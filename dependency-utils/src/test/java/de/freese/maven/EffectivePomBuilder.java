// // Created: 27 März 2025
// package de.freese.maven;
//
// import java.io.File;
// import java.lang.reflect.Constructor;
// import java.net.URI;
// import java.net.http.HttpClient;
// import java.net.http.HttpRequest;
// import java.net.http.HttpResponse;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.time.Duration;
// import java.util.Arrays;
// import java.util.List;
//
// import org.apache.maven.model.Model;
// import org.apache.maven.model.building.DefaultModelBuilderFactory;
// import org.apache.maven.model.building.DefaultModelBuildingRequest;
// import org.apache.maven.model.building.ModelBuilder;
// import org.apache.maven.model.building.ModelBuildingRequest;
// import org.apache.maven.model.building.ModelBuildingResult;
// import org.apache.maven.model.resolution.ModelResolver;
// import org.apache.maven.project.ProjectBuildingRequest;
// import org.apache.maven.project.ProjectModelResolver;
// import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
// import org.eclipse.aether.DefaultRepositorySystemSession;
// import org.eclipse.aether.RepositorySystem;
// import org.eclipse.aether.RepositorySystemSession;
// import org.eclipse.aether.RequestTrace;
// import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
// import org.eclipse.aether.impl.ArtifactResolver;
// import org.eclipse.aether.impl.DefaultServiceLocator;
// import org.eclipse.aether.impl.RemoteRepositoryManager;
// import org.eclipse.aether.impl.VersionRangeResolver;
// import org.eclipse.aether.internal.impl.DefaultRepositorySystem;
// import org.eclipse.aether.repository.LocalRepository;
// import org.eclipse.aether.repository.RemoteRepository;
// import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
// import org.eclipse.aether.spi.connector.transport.TransporterFactory;
// import org.eclipse.aether.transport.file.FileTransporterFactory;
// import org.eclipse.aether.transport.http.HttpTransporterFactory;
// import org.eclipse.aether.transport.wagon.WagonTransporterFactory;
//
// /**
//  * https://github.com/sahilm/maven-resolver-test/blob/master/pom.xml
//  * https://github.com/indrabasak/stackoverflow-indra/blob/master/effective-pom/pom.xml
//  *
//  * @author Thomas Freese
//  */
// @SuppressWarnings("all")
// public final class EffectivePomBuilder {
//     static void main() throws Exception {
//         final DefaultServiceLocator locator = serviceLocator();
//         final RepositorySystem system = locator.getService(RepositorySystem.class);
//         final DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
//         final LocalRepository localRepo = new LocalRepository(System.getProperty("java.io.tmpdir") + "/m2");
//         session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
//
//         final RequestTrace requestTrace = new RequestTrace(null);
//
//         // final Constructor<ModelResolver> c = defaultModelResolverConstructor();
//         // final ArtifactResolver artifactResolver = locator.getService(ArtifactResolver.class);
//         // final VersionRangeResolver versionRangeResolver = locator.getService(VersionRangeResolver.class);
//         // final RemoteRepositoryManager remoteRepositoryManager = locator.getService(RemoteRepositoryManager.class);
//         // final List<RemoteRepository> repos = List.of(new RemoteRepository.Builder("central", "default", "https://repo.maven.apache.org/maven2/").build());
//         // final ModelResolver modelResolver = c.newInstance(session, requestTrace, "context", artifactResolver, versionRangeResolver, remoteRepositoryManager, repos);
//
//         final RemoteRepositoryManager remoteRepositoryManager = locator.getService(RemoteRepositoryManager.class);
//         final List<RemoteRepository> repositories = Arrays.asList(new RemoteRepository.Builder("central", "default", "https://repo.maven.apache.org/maven2/").build());
//         final DefaultRepositorySystem repositorySystem = new DefaultRepositorySystem();
//         repositorySystem.initService(locator);
//         final ModelResolver modelResolver = new ProjectModelResolver(
//                 session,
//                 requestTrace,
//                 repositorySystem,
//                 remoteRepositoryManager,
//                 repositories,
//                 ProjectBuildingRequest.RepositoryMerging.POM_DOMINANT,
//                 null);
//
//         final String pomUrl = "https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot/3.4.4/spring-boot-3.4.4.pom";
//         // final String pomUrl = "https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-dependencies/3.4.4/spring-boot-dependencies-3.4.4.pom";
//         final File springBootPOM = downloadPom(pomUrl, HttpClient.newBuilder().build());
//
//         final ModelBuildingRequest modelBuildingRequest = new DefaultModelBuildingRequest();
//         modelBuildingRequest.setPomFile(springBootPOM);
//         modelBuildingRequest.setModelResolver(modelResolver);
//
//         final ModelBuilder modelBuilder = new DefaultModelBuilderFactory().newInstance();
//         ModelBuildingResult modelBuildingResult = modelBuilder.build(modelBuildingRequest);
//
//         Model effectiveModel = modelBuildingResult.getEffectiveModel();
//
//         effectiveModel.getDependencies().forEach(System.out::println);
//     }
//
//     private static Constructor<ModelResolver> defaultModelResolverConstructor() throws ClassNotFoundException, NoSuchMethodException {
//         final Class<ModelResolver> modelResolverClass = (Class<ModelResolver>) Class.forName("org.apache.maven.repository.internal.DefaultModelResolver");
//
//         final Constructor<ModelResolver> c = modelResolverClass.getDeclaredConstructor(
//                 RepositorySystemSession.class,
//                 RequestTrace.class,
//                 String.class,
//                 ArtifactResolver.class,
//                 VersionRangeResolver.class,
//                 RemoteRepositoryManager.class,
//                 List.class);
//         c.setAccessible(true);
//
//         return c;
//     }
//
//     private static File downloadPom(final String pomUrl, final HttpClient httpClient) throws Exception {
//         final HttpRequest httpRequest = HttpRequest.newBuilder()
//                 .uri(URI.create(pomUrl))
//                 .GET()
//                 .timeout(Duration.ofSeconds(5))
//                 .build();
//
//         Path path = Path.of(System.getProperty("java.io.tmpdir"), "mvn-demo.pom");
//
//         Files.deleteIfExists(path);
//
//         final HttpResponse<Path> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofFile(path));
//
//         return httpResponse.body().toFile();
//     }
//
//     private static DefaultServiceLocator serviceLocator() {
//         final DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
//         locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
//         locator.addService(TransporterFactory.class, FileTransporterFactory.class);
//         locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
//         locator.addService(TransporterFactory.class, WagonTransporterFactory.class);
//
//         return locator;
//     }
//
//     private EffectivePomBuilder() {
//         super();
//     }
// }
