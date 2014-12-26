package com.github.jegr78.imagegallery;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class GalleryImageCreator {

    private final File outputDirectory;
    private final List<File> errors = new LinkedList<>();

    private final Logger logger;

    GalleryImageCreator(final File outputDirectory) {
        ImageOperations.verifyDirectory(outputDirectory);
        this.outputDirectory = outputDirectory;
        logger = LoggerFactory.getLogger(getClass());
    }
    
    void create(List<File> imageFiles, String imageDirName) {
        File imageDir = new File(outputDirectory, imageDirName);
        ImageOperations.checkCreatedDirectory(imageDir);
        for (File imageFile : imageFiles) {
            copyDirectoryImageFile(imageDir, imageFile);
        }
    }

    private void copyDirectoryImageFile(File imageDir, File imageFile) {
        logger.debug("copy image " + imageFile + " to destination " + imageDir);
        try {
            FileUtils.copyFileToDirectory(imageFile, imageDir);
            new ThumbnailCreator(imageDir).create(imageFile);
        } catch (IOException e) {
            errors.add(imageFile);
        }
    }
    
    List<File> getErrors() {
        return new ArrayList<>(errors);
    }
}
