package com.github.jegr78.imagegallery;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.imageio.ImageIO;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ImageOperations {
    
    public static final String DEFAULT_EXTENSION = "jpg";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageOperations.class);

    private ImageOperations() {}
    
    public static void verifyDirectory(File dir) {
        if (dir == null) {
            throw new IllegalArgumentException("dir may not be null");
        }
        if (false == dir.isDirectory()) {
            throw new IllegalArgumentException("dir is not a valid directory: " + dir);
        }
    }

    public static String createHomePath(String imageDirOutputName) {
        String homePath = "../"; //images sub dir
        String[] subDirs = imageDirOutputName.split(getPathSplitter());
        for (int i = 0; i < subDirs.length; i++) {
            homePath = homePath.concat("../");
        }
        LOGGER.debug("home path for " + imageDirOutputName + ": " + homePath);
        return homePath;
    }
    
    public static String createTitlePath(String imageDirOutputName) {
        String titlePath = "";
        String[] subDirs = imageDirOutputName.split(getPathSplitter());
        for (String subDir : subDirs) {
            titlePath = titlePath.concat(subDir).concat(" - ");
        }
        titlePath = titlePath.substring(0, titlePath.lastIndexOf(" - "));
        LOGGER.debug("title path for " + imageDirOutputName + ": " + titlePath);
        return titlePath;
    }
    
    public static String getPathSplitter() {
        return File.separatorChar == '\\' ? "\\\\" : File.separator;
    }

    public static String getThumbnailFileName(File original) {
        ensureOriginalFile(original);
        String imageName = original.getName();
        String baseName = FilenameUtils.getBaseName(imageName);
        String extension = verifyExtension(FilenameUtils.getExtension(imageName));
        return baseName + "_thumbnail." + extension;
    }

    public static void ensureOriginalFile(File original) {
        if (original == null) {
            throw new IllegalArgumentException("original file may not be null");
        }
        if (false == original.isFile()) {
            throw new IllegalArgumentException("original file is not a valid file: " + original);
        }
    }
    
    public static String verifyExtension(final String extension) {
        if (extension == null || extension.isEmpty()) {
            return DEFAULT_EXTENSION;
        }
        List<String> writerFormatNames = Arrays.asList(ImageIO.getWriterFormatNames());
        if (false == writerFormatNames.contains(extension)) {
            return DEFAULT_EXTENSION;
        }
        return extension;
    }
    
    public static boolean isValidDir(File dir) {
        return dir != null && dir.isDirectory() && false == "target".equals(dir.getName());
    }
    
    
    public static boolean isValidImageFile(File file) {
        if (file == null || false == file.isFile()) {
            LOGGER.debug("file " + file + " is not a file -> not valid");
            return false;
        }
        try {
            Image image = ImageIO.read(file);
            if (image == null) {
                LOGGER.debug("file: " + file + " cannot be read be imageio -> not valid");
                return false;
            }
        } catch (IOException e) {
            LOGGER.debug("file: " + file + " threw exception during imageio -> not valid", e);
            return false;
        }
        LOGGER.debug("file: " + file + " is a valid image file");
        return true;
    }
    
    public static String readResource(String resource) throws IOException {
        return IOUtils.toString(ImageOperations.class.getClassLoader().getResourceAsStream(resource), Charsets.toCharset(Charset.defaultCharset()));
    }
    
    public static void copyResource(String source, File outputDirectory) throws IOException {
        File targetDir = new File(outputDirectory, source);
        LOGGER.debug("copy resource " + source + " to " + targetDir);
        try {
            copyFromDisk(source, targetDir);
            LOGGER.debug("copied resource from disk");
        } catch (IOException ignore) {
            JarURLConnection jarConnection = (JarURLConnection) ImageOperations.class.getClassLoader().getResource(source).openConnection();
            copyFromJar(targetDir, jarConnection);
            LOGGER.debug("copied resource from jar");
        }
    }

    private static void copyFromDisk(String source, File targetDir) throws IOException {
        File resourceFile = new File(ImageOperations.class.getClassLoader().getResource(source).getFile());
        if (resourceFile.isFile()) {
            FileUtils.copyFileToDirectory(resourceFile, targetDir);
        } else {
            FileUtils.copyDirectory(resourceFile, targetDir);
        }
    }

    private static void copyFromJar(File targetDir, JarURLConnection jarConnection) throws IOException {
        JarFile jarFile = jarConnection.getJarFile();
        for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
            JarEntry jarEntry = e.nextElement();
            String jarEntryName = jarEntry.getName();
            String jarConnectionEntryName = jarConnection.getEntryName();
            if (jarEntryName.startsWith(jarConnectionEntryName)) {
                String filename = jarEntryName.startsWith(jarConnectionEntryName) ? jarEntryName.substring(jarConnectionEntryName.length()) : jarEntryName;
                File currentFile = new File(targetDir, filename);
                if (jarEntry.isDirectory()) {
                    checkCreatedDirectory(currentFile);
                } else {
                    InputStream is = jarFile.getInputStream(jarEntry);
                    OutputStream out = FileUtils.openOutputStream(currentFile);
                    try {
                        IOUtils.copy(is, out);
                    } finally {
                        IOUtils.closeQuietly(is);
                        IOUtils.closeQuietly(out);
                    }
                }
            }
        }
    }
    
    public static void checkCreatedDirectory(File dir) {
        if (dir.isDirectory()) {
            return;
        }
        boolean dirsCreated = dir.mkdirs();
        if (dirsCreated) {
            LOGGER.debug("directory newly created:" + dir);
        }
    }
}
