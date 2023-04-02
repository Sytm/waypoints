package de.md5lukas.waypoints;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class WaypointsLoader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        var resolver = new MavenLibraryResolver();

        resolver.addRepository(new RemoteRepository.Builder("central", "default", "https://repo1.maven.org/maven2").build());

        for (var dependency : getDependencies()) {
            resolver.addDependency(new Dependency(new DefaultArtifact(dependency), null));
        }

        classpathBuilder.addLibrary(resolver);
    }

    private List<String> getDependencies() {
        try (var reader = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("dependencies")),
                        StandardCharsets.UTF_8
                )
        )) {
            return reader.lines().toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
