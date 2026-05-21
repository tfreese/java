// Created: 03.03.2019
package de.freese.dependency.version;

//
// import java.util.List;
// import java.util.Objects;
// import org.springframework.web.client.RestTemplate;
// import com.fasterxml.jackson.databind.DeserializationFeature;
// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
//
/// **
// * @author Thomas Freese
// */
// class VersionQueryWithRestTemplate extends AbstractVersionQuery {
// private final ObjectMapper mapper;

// private final RestTemplate restTemplate;
//
// VersionQueryWithRestTemplate(final RestTemplate restTemplate) {
// super();
//
// this.restTemplate = Objects.requireNonNull(restTemplate, "restTemplate required");
//
// mapper = new ObjectMapper();
// mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
// }
//
// protected String getUrlSearchMaven() {
// return VersionQueryMavenSearch.URL_SEARCH_MAVEN;
// }
//
// @Override
// public List<String> getVersions(final String groupId, final String artifactId) throws Exception {
// // // Durch das Encoding werden die '+' Zeichen in der Query kodiert, was NICHT gewünscht ist.
// // // String url = "http://search.maven.org/solrsearch/select?q=g:\"{groupID}\"+AND+a:\"{artifactID}\"&core=gav&rows=20&wt=json";
// // // String jsonResult = restTemplate.getForObject(url, String.class, dependency.getGroup(), dependency.getArtifact());
// // // '+' in der Query bleiben erhalten.
/// /        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://search.maven.org/solrsearch/select")
/// /            .queryParam("q", String.format("g:\"%s\"+AND+a:\"%s\"", groupID, artifactID))
/// /            .queryParam("core", "gav")
/// /            .queryParam("rows", "10")
/// /            .queryParam("wt", "json");
// //
// // URI repository = builder.build().toUri();
// // String jsonResult = restTemplate.getForObject(repository, String.class);
// // List<String> versions = JsonPath.read(jsonResult, "$.response.docs[*].v");
// // // !Pattern.compile(".*[bB][eE][tT][aA].*").matcher(v).matches()
//
// getLogger().info("query {}:{}", groupId, artifactId);
//
// final String url = getUrlSearchMaven().replace("{groupId}", groupId).replace("{artifactId}", artifactId);
// getLogger().debug("url: {}", url);
//
// String jsonResult = restTemplate.getForObject(url, String.class);
//
// JsonNode jsonNode = mapper.readValue(jsonResult, JsonNode.class);
// // jsonNode = jsonNode.findPath("docs");
//
// List<String> versions = jsonNode.findValuesAsText("v");
//
// return versions;
// }
// }
