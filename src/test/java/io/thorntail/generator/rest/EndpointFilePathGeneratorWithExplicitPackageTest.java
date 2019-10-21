package io.thorntail.generator.rest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EndpointFilePathGeneratorWithExplicitPackageTest {
    @Test
    public void shouldUseExplicitPackageInEndpointFilePath() {
        EndpointFilePathGenerator endpointFilePathGenerator = new EndpointFilePathGenerator("com.test", "example", "com.foobar");

        assertEquals("src/main/java/com/foobar/rest/HelloWorldEndpoint.java",
                endpointFilePathGenerator.getEndpointFilePath());
    }

    @Test
    public void shouldStripNonAlphaNumericFromExplicitPackageInEndpointFilePath() {
        EndpointFilePathGenerator endpointFilePathGenerator = new EndpointFilePathGenerator("com.my-test", "example-app", "com.foo-bar");

        assertEquals("src/main/java/com/foobar/rest/HelloWorldEndpoint.java",
                endpointFilePathGenerator.getEndpointFilePath());
    }

    @Test
    public void shouldUseExplicitPackageInPackageName() {
        EndpointFilePathGenerator endpointFilePathGenerator = new EndpointFilePathGenerator("com.test", "example", "com.foobar");

        assertEquals("com.foobar.rest",
                endpointFilePathGenerator.getEndpointPackage());
    }

    @Test
    public void shouldStripNonAlphaNumericFromExplicitPackage() {
        EndpointFilePathGenerator endpointFilePathGenerator = new EndpointFilePathGenerator("com.my-test", "example-app", "com.foo-bar");

        assertEquals("com.foobar.rest",
                endpointFilePathGenerator.getEndpointPackage());
    }

    @Test
    public void shouldUseExplicitPackageInRestApplicationFilePath() {
        EndpointFilePathGenerator endpointFilePathGenerator = new EndpointFilePathGenerator("com.test", "example", "com.foobar");

        assertEquals("src/main/java/com/foobar/rest/RestApplication.java",
                endpointFilePathGenerator.getApplicationPath());
    }

    @Test
    public void shouldStripNonAlphaNumericFromExplicitPackageInRestApplicationFilePath() {
        EndpointFilePathGenerator endpointFilePathGenerator = new EndpointFilePathGenerator("com.my-test", "example-app", "com.foo-bar");

        assertEquals("src/main/java/com/foobar/rest/RestApplication.java",
                endpointFilePathGenerator.getApplicationPath());
    }
}
