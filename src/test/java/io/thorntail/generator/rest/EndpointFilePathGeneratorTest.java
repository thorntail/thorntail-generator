package io.thorntail.generator.rest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EndpointFilePathGeneratorTest {
    @Test
    public void shouldGeneratePathForValidGroupAndArtifact() {
        EndpointFilePathGenerator endpointFilePathGenerator = new EndpointFilePathGenerator("com.test", "example", null);

        assertEquals("src/main/java/com/test/example/rest/HelloWorldEndpoint.java",
                endpointFilePathGenerator.getEndpointFilePath());
    }

    @Test
    public void shouldStripNonAlphaNumericFromGroupAndArtifact() {
        EndpointFilePathGenerator endpointFilePathGenerator = new EndpointFilePathGenerator("com.my-test", "example-app", null);

        assertEquals("src/main/java/com/mytest/exampleapp/rest/HelloWorldEndpoint.java",
                endpointFilePathGenerator.getEndpointFilePath());
    }

    @Test
    public void shouldGenerateEndpointPackageForValidGroupAndArtifact() {
        EndpointFilePathGenerator endpointFilePathGenerator = new EndpointFilePathGenerator("com.test", "example", null);

        assertEquals("com.test.example.rest",
                endpointFilePathGenerator.getEndpointPackage());
    }

    @Test
    public void shouldStripNonAlphaNumericFromEndpointPackage() {
        EndpointFilePathGenerator endpointFilePathGenerator = new EndpointFilePathGenerator("com.my-test", "example-app", null);

        assertEquals("com.mytest.exampleapp.rest",
                endpointFilePathGenerator.getEndpointPackage());
    }

    @Test
    public void shouldGenerateRestApplicationPathForValidGroupAndArtifact() {
        EndpointFilePathGenerator endpointFilePathGenerator = new EndpointFilePathGenerator("com.test", "example", null);

        assertEquals("src/main/java/com/test/example/rest/RestApplication.java",
                endpointFilePathGenerator.getApplicationPath());
    }
}
