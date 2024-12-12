package org.example;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


class FileProcess {
    private static final String INPUT_FOLDER = "resources/Input";
    private static final String OUTPUT_FOLDER = "resources/Output";
    private static final String ERROR_FOLDER = "resources/Error";

    public File[] getInputFiles() {
        File folder = new File(INPUT_FOLDER);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

        if (files == null || files.length == 0) {
            System.out.println("[INFO] Aucun fichier trouvé dans le dossier input.");
        } else {
            System.out.println("[INFO] " + files.length + " fichier(s) trouvé(s) dans le dossier input.");
        }

        return files;
    }

    public void moveFileToOutput(File file) throws IOException {
        Path target = Paths.get(OUTPUT_FOLDER, file.getName());
        Files.move(file.toPath(), target);
        System.out.println("[INFO] Fichier déplacé vers Output : " + file.getName());
    }

    public void moveFileToError(File file) throws IOException {
        Path target = Paths.get(ERROR_FOLDER, file.getName());
        Files.move(file.toPath(), target);
        System.out.println("[INFO] Fichier déplacé vers Error : " + file.getName());
    }
}