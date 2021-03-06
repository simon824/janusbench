package de.rngcntr.janusbench.backend;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class ConnectionTests {

    private static final int JG_PORT = 8182;

    private static final File COMPOSE_FILE = new File("docker/configurations/janusgraph-inmemory.yml");

    private static final File REMOTE_PROPERTIES = new File("conf/remote-graph.properties");

    @Container
    public static DockerComposeContainer<?> environment =
        new DockerComposeContainer<>(COMPOSE_FILE)
            .withExposedService("janusgraph", JG_PORT)
            .waitingFor("janusgraph", Wait.forLogMessage(".*Channel started at port.*", 1))
            .withLocalCompose(true);

    @Test
    public void testEstablishValidConnection() throws Exception {
        final Connection connStarted = new Connection(REMOTE_PROPERTIES);
        connStarted.open();
        assertNotNull("Graph traversal source should be available", connStarted.g());
        connStarted.close();
        environment.stop();
        final Connection connStopped = new Connection(REMOTE_PROPERTIES);
        connStopped.setTimeout(3000);
        Exception exception = assertThrows(ConfigurationException.class, () -> connStopped.open());

        assertTrue("ConfigurationException should be thrown",
                   exception instanceof ConfigurationException);
        assertTrue("ConfigurationException should contain unreachable statement",
                   exception.getMessage().contains("Unable to reach cluster"));
    }

    @Test
    public void testEstablishInvalidConnection() {
        Connection conn = new Connection(new File("/dev/null"));
        Exception exception = assertThrows(ConfigurationException.class, () -> conn.open());

        assertTrue("ConfigurationException should be thrown",
                   exception instanceof ConfigurationException);
    }
}