package de.md5lukas.waypoints;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import java.util.List;
import java.util.Map;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public class DependencyYmlLoader implements PluginLoader {

  @SuppressWarnings("unchecked")
  @Override
  public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
    final var resolver = new MavenLibraryResolver();

    final var declaration = (Map<String, ?>)
        new Yaml().load(this.getClass().getClassLoader().getResourceAsStream("dependencies.yml"));

    for (final var repository : (List<Map<String, String>>) declaration.get("repositories")) {
      resolver.addRepository(new RemoteRepository.Builder(
              repository.get("id"), repository.get("type"), repository.get("url"))
          .build());
    }

    for (final var dependency : (List<String>) declaration.get("dependencies")) {
      resolver.addDependency(new Dependency(new DefaultArtifact(dependency), null));
    }

    classpathBuilder.addLibrary(resolver);
  }
}
