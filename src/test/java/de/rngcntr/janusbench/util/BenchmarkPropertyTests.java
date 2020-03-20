package de.rngcntr.janusbench.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.DefaultGraphTraversal;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class BenchmarkPropertyTests {

    @Test
    public void testGetName() {
        BenchmarkProperty prop = new BenchmarkProperty("key", () -> "value");
        assertEquals("BenchmarkProperty needs to keep key unchanged", "key", prop.getName());
    }

    @Test
    public void testEvaluateString() {
        BenchmarkProperty prop = new BenchmarkProperty("key", () -> "value");
        assertEquals("BenchmarkProperty needs to keep key unchanged", "value", prop.evaluate());
    }
}
