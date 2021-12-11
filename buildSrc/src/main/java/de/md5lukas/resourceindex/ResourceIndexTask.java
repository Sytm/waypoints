package de.md5lukas.resourceindex;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class ResourceIndexTask extends DefaultTask {

    @InputDirectory
    abstract DirectoryProperty getResources();

    @OutputFile
    abstract RegularFileProperty getIndexFile();

    @Inject
    public ResourceIndexTask() {
        getResources().convention(getProject().getLayout().getProjectDirectory().dir("src/main/resources"));
        getIndexFile().convention(getProject().getLayout().getBuildDirectory().file("resources/main/resourceIndex"));
    }

    @TaskAction
    void createIndex() throws IOException {
        List<String> paths = new ArrayList<>();
        getResources().getAsFileTree().visit(fileVisitDetails -> {
            if (!fileVisitDetails.isDirectory()) {
                paths.add(fileVisitDetails.getPath());
            }
        });
        try (FileWriter out = new FileWriter(getIndexFile().getAsFile().get(), StandardCharsets.UTF_8)) {
            for (String path : paths) {
                out.write(path);
                out.write('\n');
            }
        }
    }
}
