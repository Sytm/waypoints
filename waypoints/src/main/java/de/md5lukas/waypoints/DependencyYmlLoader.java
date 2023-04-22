package de.md5lukas.waypoints;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;

public class DependencyYmlLoader implements PluginLoader {

    @SuppressWarnings("unchecked")
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        var resolver = new MavenLibraryResolver();

        var declaration = (Map<String, ?>) new Yaml().load(this.getClass().getClassLoader().getResourceAsStream("dependencies.yml"));

        for (var repository : (List<Map<String, String>>) declaration.get("repositories")) {
            resolver.addRepository(new RemoteRepository.Builder(
                    repository.get("id"),
                    repository.get("type"),
                    repository.get("url")
            ).build());
        }

        for (var dependency : (List<String>) declaration.get("dependencies")) {
            resolver.addDependency(new Dependency(new DefaultArtifact(dependency), null));
        }

        classpathBuilder.addLibrary(resolver);
    }
}
