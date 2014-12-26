package com.github.jegr78.imagegallery;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ImageScanner {
    
    private final File directory;
    
    private final Logger logger;
    
    ImageScanner(File directory) {
        ImageOperations.verifyDirectory(directory);
        this.directory = directory;
        logger = LoggerFactory.getLogger(getClass());
    }

    Map<String, List<File>> scan() {
        logger.debug("scanning images");
        Map<String, List<File>> imageFilesPerDirectory = new HashMap<>();
        scanImagesDir(directory, imageFilesPerDirectory);
        logger.debug("found " + imageFilesPerDirectory.keySet().size() + " dirs with images");
        return imageFilesPerDirectory;
    }
    
    private void scanImagesDir(File imagesDir, Map<String, List<File>> imageFilesPerDirectory) {
        File[] listFiles = imagesDir.listFiles();
        for (File file : listFiles) {
            String dirName = getImageDirRelativePath(file.getParentFile());
            if (ImageOperations.isValidImageFile(file)) {
                List<File> directoryFiles = imageFilesPerDirectory.get(dirName);
                if (directoryFiles == null) {
                    directoryFiles = new ArrayList<File>();
                    imageFilesPerDirectory.put(dirName, directoryFiles);
                }
                directoryFiles.add(file);
            } else if (ImageOperations.isValidImageDir(file)) {
                scanImagesDir(file, imageFilesPerDirectory);
            }
        }
    }
    
    String getImageDirRelativePath(File imageDir) {
        String imageDirPath = "";
        if (imageDirIsRootDirectory(imageDir)) {
            return imageDirPath;
        }
        List<String> pathParts = new LinkedList<String>();
        do {
            pathParts.add(imageDir.getName());
            imageDir = imageDir.getParentFile();
        }
        while (imageDirIsNotRootDirectory(imageDir));
        
        for (int i = pathParts.size() - 1; i >= 0; i--) {
            imageDirPath = imageDirPath.concat(pathParts.get(i)).concat(File.separator);
        }
        imageDirPath = imageDirPath.substring(0, imageDirPath.lastIndexOf(File.separator));
        logger.debug("relative image dir path for " + imageDir + ": " + imageDirPath);
        return imageDirPath;
    }
    

    private boolean imageDirIsRootDirectory(File imageDir) {
        return imageDir != null && imageDir.equals(directory);
    }

    private boolean imageDirIsNotRootDirectory(File imageDir) {
        return false == imageDirIsRootDirectory(imageDir);
    }
}
