package de.rngcntr.janusbench.backend;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class GraphOperationTests {

    private static int JG_PORT = 8182;

    private static final File COMPOSE_FILE = new File("docker/configurations/janusgraph-inmemory.yml");
    private static final File INIT_SCRIPT = new File("conf/initialize-graph.groovy");

    private static final File REMOTE_PROPERTIES = new File("conf/remote-graph.properties");

    private Connection conn;

    @Container
    public static DockerComposeContainer<?> environment =
        new DockerComposeContainer<>(COMPOSE_FILE)
            .withExposedService("janusgraph", JG_PORT)
            .waitingFor("janusgraph", Wait.forLogMessage(".*Channel started at port.*", 1))
            .withLocalCompose(true);

    @BeforeEach
    public void openConnectionAndCleanGraph() throws Exception {
        conn = new Connection(REMOTE_PROPERTIES);
        conn.open();
        conn.submit("g.V().drop()");
        conn.submit("g.tx().commit()");
    }

    @AfterEach
    public void closeConnectionAndCleanGraph() throws Exception {
        conn.close();
    }

    @Test
    public void testCreateSchema() throws Exception {
        final String initRequest = new String(Files.readAllBytes(INIT_SCRIPT.toPath()));
        conn.submit(initRequest);
    }

    @Test
    public void testInsertVertex() throws Exception {
        conn.submit("g.addV().next()");
        conn.submit("g.tx().commit()");
        assertEquals("One vertex should exist in graph", 1,
                     conn.submit("g.V().count().next()").one().getInt());
    }

    @Test
    public void testInsertEdge() throws Exception {
        Vertex a = conn.submit("g.addV().next()").one().getVertex();
        Vertex b = conn.submit("g.addV().next()").one().getVertex();
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("a", a);
        parameters.put("b", b);
        conn.submit("g.tx().commit()");
        conn.submit("g.addE('mylabel').from(a).to(b).next()", parameters);
        conn.submit("g.tx().commit()");
        assertEquals("Two vertices should exist in graph", 2,
                     conn.submit("g.V().count().next()").one().getInt());
        assertEquals("One edge should exist in graph", 1,
                     conn.submit("g.E().count().next()").one().getInt());
    }

    @Test
    public void testCypherQuery() throws Exception {
        conn.submitCypher("CREATE (p1:Person {name: 'Tom', age: 24})").all();
        conn.submitCypher("CREATE (p2:Person {name: 'Jerry', age: 24})").all();
        Object count = conn.submitCypher("MATCH (n) RETURN count(n) as count").all().get(0).get("count");
        assertTrue(count instanceof Long);
        assertEquals(new Long(2L), (Long) count);
    }
}