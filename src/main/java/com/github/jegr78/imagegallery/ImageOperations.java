package com.github.jegr78.imagegallery;

import java.awt.Image;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.imageio.ImageIO;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageOperations {

    private final File imagesRootDirectory;
    private final File outputDirectory;
    
    private final Logger logger;

    public ImageOperations(File rootDir, File outputDir) {
        verifyDirectory(rootDir);
        verifyDirectory(outputDir);
        this.imagesRootDirectory = rootDir;
        this.outputDirectory = outputDir;
        logger = LoggerFactory.getLogger(getClass());
    }
    
    private void verifyDirectory(File dir) {
        Objects.requireNonNull(dir);
        assert dir.isDirectory();
    }

    public Map<String, List<File>> scanImagesRootDir() {
        logger.debug("scanning images");
        Map<String, List<File>> imageFilesPerDirectory = new HashMap<>();
        scanImagesDir(imagesRootDirectory, imageFilesPerDirectory);
        logger.debug("found " + imageFilesPerDirectory.keySet().size() + " dirs with images");
        return imageFilesPerDirectory;
    }
    
    private void scanImagesDir(File imagesDir, Map<String, List<File>> imageFilesPerDirectory) {
        File[] listFiles = imagesDir.listFiles();
        for (File file : listFiles) {
            String dirName = getImageDirRelativePath(file.getParentFile());
            if (isValidImageFile(file)) {
                List<File> directoryFiles = imageFilesPerDirectory.get(dirName);
                if (directoryFiles == null) {
                    directoryFiles = new ArrayList<File>();
                    imageFilesPerDirectory.put(dirName, directoryFiles);
                }
                directoryFiles.add(file);
            } else if (isValidImageDir(file)) {
                scanImagesDir(file, imageFilesPerDirectory);
            }
        }
    }

    public List<File> copyToOutputDirectory(Map<String, List<File>> imageFilesPerDirectory) throws IOException {
        logger.debug("copy found images to destination");
        List<File> errors = new LinkedList<>();
        List<com.github.jegr78.imagegallery.pojo.Image> galleries = new ArrayList<>();
        for (Entry<String, List<File>> entry : imageFilesPerDirectory.entrySet()) {
            copyToOutputDirectory(errors, entry, galleries);
        }
        writeIndexHtml(galleries);
        return errors;
    }

    private void copyToOutputDirectory(List<File> errors, Entry<String, List<File>> entry, List<com.github.jegr78.imagegallery.pojo.Image> galleries) throws IOException {
        String imageDirOutputName = entry.getKey();
        File outputImageDir = ensureOutputImageDirectory(imageDirOutputName);
        copyDirectoryImageFiles(errors, entry.getValue(), outputImageDir);
        String homePath = createHomePath(imageDirOutputName);
        String titlePath = createTitlePath(imageDirOutputName);
        createHTMLForImageDirectory(imageDirOutputName, homePath, titlePath);
        addGalleryInfo(galleries, imageDirOutputName);
    }

    private File ensureOutputImageDirectory(String imageDirOutputName) {
        File outputImageDir = new File(outputDirectory, imageDirOutputName);
        checkCreatedDirectory(outputImageDir);
        return outputImageDir;
    }

    private void copyDirectoryImageFiles(List<File> errors, List<File> imageFiles, File outputImageDir) {
        for (File imageFile : imageFiles) {
            copyDirectoryImageFile(errors, outputImageDir, imageFile);
        }
    }

    private void copyDirectoryImageFile(List<File> errors, File outputImageDir, File imageFile) {
        logger.debug("copy image " + imageFile + " to destination " + outputImageDir);
        try {
            FileUtils.copyFileToDirectory(imageFile, outputImageDir);
            createThumbnail(imageFile, outputImageDir);
        } catch (IOException e) {
            errors.add(imageFile);
        }
    }

    String createHomePath(String imageDirOutputName) {
        String homePath = "";
        String[] subDirs = imageDirOutputName.split(getPathSplitter());
        for (int i=0; i<subDirs.length; i++) {
            homePath = homePath.concat("../");
        }
        logger.debug("home path for " + imageDirOutputName + ": " + homePath);
        return homePath;
    }
    
    String createTitlePath(String imageDirOutputName) {
        String titlePath = "";
        String[] subDirs = imageDirOutputName.split(getPathSplitter());
        for (String subDir : subDirs) {
            titlePath = titlePath.concat(subDir).concat(" - ");
        }
        titlePath = titlePath.substring(0, titlePath.lastIndexOf(" - "));
        logger.debug("title path for " + imageDirOutputName + ": " + titlePath);
        return titlePath;
    }
    
    private String getPathSplitter() {
        return File.separatorChar == '\\' ? "\\\\" : File.separator;
    }

    private void createThumbnail(File imageFile, File outputImageDir) throws IOException {
        logger.debug("create thumbnail for " + imageFile);
        ThumbnailCreator.create(imageFile, outputImageDir);
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
        return imageDir != null && imageDir.equals(imagesRootDirectory);
    }

    private boolean imageDirIsNotRootDirectory(File imageDir) {
        return false == imageDirIsRootDirectory(imageDir);
    }
    
    public static String getThumbnailFileName(File original) {
        String imageName = original.getName();
        String baseName = FilenameUtils.getBaseName(imageName);
        String extension = verifyExtension(FilenameUtils.getExtension(imageName));
        return baseName + "_thumbnail." + extension;
    }
    
    private static String verifyExtension(final String extension) {
        if (extension == null || extension.isEmpty()) {
            return "jpg";
        }
        List<String> writerFormatNames = Arrays.asList(ImageIO.getWriterFormatNames());
        if (false == writerFormatNames.contains(extension)) {
            return "jpg";
        }
        return extension;
    }
    
    public void copyGalleriaFiles() throws IOException {
        copyResource("galleria");
    }
    
    public void createHTMLForImageDirectory(String imageDirName, String homePath, String titlePath) throws IOException {
        logger.debug("create html for directory " + imageDirName);
        File imageDir = new File(outputDirectory, imageDirName);
        checkCreatedDirectory(imageDir);
        JSONArray imageJsons = createImageJsonData(imageDir);
        writeHTML(imageDir, homePath, titlePath, imageJsons);
    }

    private JSONArray createImageJsonData(File imageDir) {
        File[] imageFiles = imageDir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                return isValidImageFile(file);
            }
            
        });
        JSONArray imageJsons = new JSONArray();
        for (File imageFile : imageFiles) {
            String fileName = imageFile.getName();
            if (fileName.contains("_thumbnail")) {
                continue;
            }
            String thumbnailImageName = getThumbnailFileName(imageFile);
            com.github.jegr78.imagegallery.pojo.Image image = new com.github.jegr78.imagegallery.pojo.Image(fileName, thumbnailImageName, FilenameUtils.getBaseName(fileName), imageDir.getName());
            JSONObject imageJson = new JSONObject(image);
            imageJsons.put(imageJson);
        }
        logger.debug("json data for image dir " + imageDir + ": " + imageJsons.toString());
        return imageJsons;
    }

    
    private void writeHTML(File imageDir, String homePath, String titlePath, JSONArray imageJsons) throws IOException {
        String data = "var data = ".concat(imageJsons.toString());
        String html = readResource("gallery.html");
        html = html.replace("%DATA%", data).replace("%HOME_PATH%", homePath).replace("%TITLE%", titlePath);
        logger.debug("write gallery.html for image dir " + imageDir + ": " + html);
        File galleryHtmlFile = new File(imageDir, "gallery.html");
        FileUtils.write(galleryHtmlFile, html);
    }
    

    private void addGalleryInfo(List<com.github.jegr78.imagegallery.pojo.Image> galleries, String imageDirOutputName) {
        logger.debug("add gallery info for " + imageDirOutputName);
        File[] imageFiles = findImagesForGalleryInfo(imageDirOutputName);
        addImageToGalleryInfo(galleries, imageDirOutputName, imageFiles[0]);
    }

    private File[] findImagesForGalleryInfo(String imageDirOutputName) {
        File imageDir = new File(outputDirectory, imageDirOutputName);
        File[] imageFiles = imageDir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                return isValidImageFile(file)
                        && false == file.getName().contains("_thumbnail");
            }
            
        });
        return imageFiles;
    }

    private void addImageToGalleryInfo(List<com.github.jegr78.imagegallery.pojo.Image> galleries, String imageDirOutputName, File imageFile) {
        String galleryLink = imageDirOutputName.concat("/gallery.html");
        String thumbnailImageName = getThumbnailFileName(imageFile);
        String title = createTitlePath(imageDirOutputName);
        String imageName = imageDirOutputName.concat("/").concat(imageFile.getName());
        String thumbName = imageDirOutputName.concat("/").concat(thumbnailImageName);
        com.github.jegr78.imagegallery.pojo.Image image = new com.github.jegr78.imagegallery.pojo.Image(imageName, thumbName, title, title);
        image.setLink(galleryLink);
        galleries.add(image);
    }
    
    private void writeIndexHtml(List<com.github.jegr78.imagegallery.pojo.Image> galleries) throws IOException {
        String galleriesHtml = createGalleriesHtml(galleries);
        String indexHtml = readResource("index.html");
        indexHtml = indexHtml.replace("%GALLERIES%", galleriesHtml);
        logger.debug("write gallery index.html: " + indexHtml);
        File indexHtmlFile = new File(outputDirectory, "index.html");
        FileUtils.write(indexHtmlFile, indexHtml);
    }
    
    String createGalleriesHtml(List<com.github.jegr78.imagegallery.pojo.Image> galleries) {
        sortGalleries(galleries);
        String data = "var data = ";
        JSONArray galleriesJson = new JSONArray();
        for (com.github.jegr78.imagegallery.pojo.Image gallery : galleries) {
            logger.debug("create gallery html: " + gallery);
            galleriesJson.put(new JSONObject(gallery));
        }
        return data.concat(galleriesJson.toString());
    }
    
    private void sortGalleries(List<com.github.jegr78.imagegallery.pojo.Image> galleries) {
        Collections.sort(galleries);
    }

    private boolean isValidImageDir(File dir) {
        return dir != null && dir.isDirectory() && false == "galleria".equals(dir.getName());
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
    
    String readResource(String resource) throws IOException {
        return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(resource), Charsets.toCharset(Charset.defaultCharset()));
    }
    
    void copyResource(String source) throws IOException {
        File targetDir = new File(outputDirectory, source);
        logger.debug("copy resource " + source + " to " + targetDir);
        try {
            copyFromDisk(source, targetDir);
            logger.debug("copied resource from disk");
        } catch (IOException ignore) {
            JarURLConnection jarConnection = (JarURLConnection) getClass().getClassLoader().getResource(source).openConnection();
            copyFromJar(targetDir, jarConnection);
            logger.debug("copied resource from jar");
        }
    }

    private void copyFromDisk(String source, File targetDir) throws IOException {
        File resourceFile = new File(getClass().getClassLoader().getResource(source).getFile());
        if (resourceFile.isFile()) {
            FileUtils.copyFileToDirectory(resourceFile, targetDir);
        } else {
            FileUtils.copyDirectory(resourceFile, targetDir);
        }
    }

    private void copyFromJar(File targetDir, JarURLConnection jarConnection) throws IOException {
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
    
    private void checkCreatedDirectory(File dir) {
        if (false == dir.isDirectory()) {
            return;
        }
        boolean dirsCreated = dir.mkdirs();
        if (dirsCreated) {
            logger.debug("directory newly created:" + dir);
        }
    }
}
