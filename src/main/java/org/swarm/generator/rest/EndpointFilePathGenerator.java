package org.swarm.generator.rest;

/**
 * Generates the file path and package of a sample JAXRS service based upon the
 * Maven groupId and artifactId.
 *
 * The JAXRS service is created at groupid.artifactId.rest.HelloWorldEndpoint.java
 *
 * Non alpha-numeric characters are stripped from the generated package name
 */
class EndpointFilePathGenerator {

    private static final String SRC_PATH = "/src/main/java";
    private static final String REST_CLASS = "/rest/HelloWorldEndpoint.java";
    private static final String REST_PACKAGE = "rest";

    private final String endpointFilePath;
    private final String endpointPackage;

    EndpointFilePathGenerator(String groupId, String artifactId) {

        groupId = groupId.replaceAll("[^A-Za-z0-9.]", "");
        artifactId = artifactId.replaceAll("[^A-Za-z0-9]", "");

        endpointFilePath = String.format("%s/%s/%s%s",
                SRC_PATH,
                groupId.replace(".", "/"),
                artifactId,
                REST_CLASS);

        endpointPackage = String.format("%s.%s.%s",
                groupId,
                artifactId,
                REST_PACKAGE);
    }

    String getEndpointFilePath() {
        return endpointFilePath;
    }

    String getEndpointPackage() {
        return endpointPackage;
    }
}
