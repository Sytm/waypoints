package de.md5lukas.resourceindex;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

public abstract class ResourceIndexTask extends DefaultTask {

    @InputDirectory
    public abstract DirectoryProperty getResources();

    @OutputFile
    public abstract RegularFileProperty getIndexFile();

    @Inject
    public ResourceIndexTask() {
        getResources().convention(getProject().getLayout().getProjectDirectory().dir("src/main/resources"));
        getIndexFile().convention(getProject().getLayout().getBuildDirectory().file("resources/main/resourceIndex"));
    }

    @TaskAction
    void createIndex() throws IOException {
        var paths = new ArrayList<String>();
        getResources().getAsFileTree().visit(fileVisitDetails -> {
            if (!fileVisitDetails.isDirectory()) {
                paths.add(fileVisitDetails.getPath());
            }
        });
        try (var out = new FileWriter(getIndexFile().getAsFile().get(), StandardCharsets.UTF_8)) {
            for (var path : paths) {
                out.write(path);
                out.write('\n');
            }
        }
    }
}
