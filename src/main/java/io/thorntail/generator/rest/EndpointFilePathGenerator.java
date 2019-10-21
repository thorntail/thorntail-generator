package io.thorntail.generator.rest;

/**
 * Generates the file path and package of a sample JAX-RS service based upon the
 * Maven groupId and artifactId.
 *
 * The JAX-RS service is created at {@code groupid.artifactId.rest.HelloWorldEndpoint.java}.
 *
 * Non alpha-numeric characters are stripped from the generated package name.
 *
 * Generated file paths do <i>not</i> start with {@code /}; it's the caller's responsibility
 * to prepend that when necessary.
 *
 * Alternatively, it is possible to pass in an explicitly specified base package name,
 * which will be used instead of the {@code groupId.artifactId} combination.
 */
class EndpointFilePathGenerator {

    private static final String SRC_PATH = "src/main/java";
    private static final String REST_CLASS = "/rest/HelloWorldEndpoint.java";
    private static final String APPLICATION_CLASS = "/rest/RestApplication.java";
    private static final String REST_PACKAGE = "rest";

    private final String endpointFilePath;
    private final String applicationFilePath;
    private final String endpointPackage;

    EndpointFilePathGenerator(String groupId, String artifactId, String explicitPackage) {
        groupId = groupId.replaceAll("[^A-Za-z0-9_.]", "");
        artifactId = artifactId.replaceAll("[^A-Za-z0-9_]", "");
        if (explicitPackage != null) {
            explicitPackage = explicitPackage.replaceAll("[^A-Za-z0-9_.]", "");
        } else {
            explicitPackage = groupId + "." + artifactId;
        }

        String packagePath = explicitPackage.replace(".", "/");

        endpointFilePath = String.format("%s/%s%s", SRC_PATH, packagePath, REST_CLASS);
        applicationFilePath = String.format("%s/%s%s", SRC_PATH, packagePath, APPLICATION_CLASS);
        endpointPackage = explicitPackage + "." + REST_PACKAGE;
    }

    String getEndpointFilePath() {
        return endpointFilePath;
    }

    String getEndpointPackage() {
        return endpointPackage;
    }

    String getApplicationPath() {
        return applicationFilePath;
    }
}
