package de.jegr.imagegallery;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

public class ImageOperations {

    private final File imagesRootDirectory;
    private final File outputDirectory;

    public ImageOperations(File rootDir, File outputDir) {
        Objects.requireNonNull(rootDir);
        Objects.requireNonNull(outputDir);
        this.imagesRootDirectory = rootDir;
        this.outputDirectory = outputDir;
    }

    public Map<File, List<File>> scanImagesRootDir() {
        Collection<File> imageFiles = FileUtils.listFiles(imagesRootDirectory, new ImageFileFilter(), new ImageDirFilter());
        return determineImagesPerDirectories(imageFiles);
    }

    private Map<File, List<File>> determineImagesPerDirectories(Collection<File> imageFiles) {
        Map<File, List<File>> imageFilesPerDirectory = new HashMap<>();
        for (File imageFile : imageFiles) {
            File imageDir = imageFile.getParentFile();
            List<File> direcoryImageFiles = imageFilesPerDirectory.get(imageDir);
            if (direcoryImageFiles == null) {
                direcoryImageFiles = new ArrayList<File>();
                imageFilesPerDirectory.put(imageDir, direcoryImageFiles);
            }
            direcoryImageFiles.add(imageFile);
        }
        return imageFilesPerDirectory;
    }

    public List<File> copyToOutputDirectory(Map<File, List<File>> imageFilesPerDirectory) {
        List<File> errors = new LinkedList<>();
        for (Entry<File, List<File>> entry : imageFilesPerDirectory.entrySet()) {
            File imageDir = entry.getKey();
            File outputImageDir = new File(outputDirectory, getImageDirOutputName(imageDir));
            outputImageDir.mkdirs();
            for (File imageFile : entry.getValue()) {
                try {
                    FileUtils.copyFileToDirectory(imageFile, outputImageDir);
                    createThumbnail(imageFile, outputImageDir);
                } catch (IOException e) {
                    errors.add(imageFile);
                }
            }
        }
        return errors;
    }

    private void createThumbnail(File imageFile, File outputImageDir) throws IOException {
        ThumbnailCreator.create(imageFile, outputImageDir);
    }

    String getImageDirOutputName(File imageDir) {
        String imageDirName = "";
        if (imageDirIsOutputDirectory(imageDir)) {
            return imageDirName;
        }
        List<String> pathParts = new LinkedList<String>();
        do {
            pathParts.add(imageDir.getName());
            imageDir = imageDir.getParentFile();
        }
        while (imageDirIsNotOutputDirectory(imageDir));
        
        for (int i = pathParts.size() - 1; i >= 0; i--) {
            imageDirName = imageDirName.concat(pathParts.get(i)).concat(File.separator);
        }
        return imageDirName.substring(0, imageDirName.lastIndexOf(File.separator));
    }
    

    private boolean imageDirIsOutputDirectory(File imageDir) {
        return imageDir != null && imageDir.equals(outputDirectory);
    }

    private boolean imageDirIsNotOutputDirectory(File imageDir) {
        return false == imageDirIsOutputDirectory(imageDir);
    }

    private static class ImageFileFilter implements IOFileFilter {

        public boolean accept(File file) {
            return isValidImageFile(file);
        }

        private boolean isValidImageFile(File file) {
            if (file == null || false == file.isFile()) {
                return false;
            }
            try {
                Image image = ImageIO.read(file);
                if (image == null) {
                    return false;
                }
            } catch (IOException ex) {
                return false;
            }
            return true;
        }

        public boolean accept(File dir, String name) {
            return true;
        }

    }

    private static class ImageDirFilter implements IOFileFilter {

        public boolean accept(File file) {
            return isValidImageDir(file);
        }

        public boolean accept(File dir, String name) {
            return isValidImageDir(dir);
        }

        private boolean isValidImageDir(File dir) {
            return dir != null && dir.isDirectory();
        }

    }
}
