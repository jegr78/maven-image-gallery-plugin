package de.jegr.imagegallery;

import java.awt.Image;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class ImageOperations {

    private final File imagesRootDirectory;
    private final File outputDirectory;

    public ImageOperations(File rootDir, File outputDir) {
        verifyDirectory(rootDir);
        verifyDirectory(outputDir);
        this.imagesRootDirectory = rootDir;
        this.outputDirectory = outputDir;
    }
    
    private void verifyDirectory(File dir) {
        Objects.requireNonNull(dir);
        assert(dir.isDirectory());
    }

    public Map<String, List<File>> scanImagesRootDir() {
        Map<String, List<File>> imageFilesPerDirectory = new HashMap<>();
        scanImagesDir(imagesRootDirectory, imageFilesPerDirectory);
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
        List<File> errors = new LinkedList<>();
        List<de.jegr.imagegallery.pojo.Image> galleries = new ArrayList<>();
        for (Entry<String, List<File>> entry : imageFilesPerDirectory.entrySet()) {
            copyToOutputDirectory(errors, entry, galleries);
        }
        writeIndexHtml(galleries);
        return errors;
    }

    private void copyToOutputDirectory(List<File> errors, Entry<String, List<File>> entry, List<de.jegr.imagegallery.pojo.Image> galleries) throws IOException {
        String imageDirOutputName = entry.getKey();
        File outputImageDir = ensureOutputImageDirectory(imageDirOutputName);
        copyDirectoryImageFiles(errors, entry.getValue(), outputImageDir);
        String homePath = createHomePath(imageDirOutputName);
        String titlePath = createTitlePath(imageDirOutputName);
        createHTMLForImageDirectory(imageDirOutputName, homePath, titlePath);
        addGalleryInfo(galleries, imageDirOutputName, titlePath);
    }

    private File ensureOutputImageDirectory(String imageDirOutputName) {
        File outputImageDir = new File(outputDirectory, imageDirOutputName);
        outputImageDir.mkdirs();
        return outputImageDir;
    }

    private void copyDirectoryImageFiles(List<File> errors, List<File> imageFiles, File outputImageDir) {
        for (File imageFile : imageFiles) {
            copyDirectoryImageFile(errors, outputImageDir, imageFile);
        }
    }

    private void copyDirectoryImageFile(List<File> errors, File outputImageDir, File imageFile) {
        try {
            FileUtils.copyFileToDirectory(imageFile, outputImageDir);
            createThumbnail(imageFile, outputImageDir);
        } catch (IOException e) {
            errors.add(imageFile);
        }
    }

    String createHomePath(String imageDirOutputName) {
        String homePath = "";
        String[] subDirs = imageDirOutputName.split(File.separator);
        for (int i=0; i<subDirs.length; i++) {
            homePath = homePath.concat("../");
        }
        return homePath;
    }
    
    String createTitlePath(String imageDirOutputName) {
        String titlePath = "";
        String[] subDirs = imageDirOutputName.split(File.separator);
        for (String subDir : subDirs) {
            titlePath = titlePath.concat(subDir).concat(" - ");
        }
        return titlePath.substring(0, titlePath.lastIndexOf(" - "));
    }

    private void createThumbnail(File imageFile, File outputImageDir) throws IOException {
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
        return imageDirPath.substring(0, imageDirPath.lastIndexOf(File.separator));
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
        File galleriaDir = new File(getClass().getClassLoader().getResource("galleria").getFile());
        FileUtils.copyDirectory(galleriaDir, new File(outputDirectory, "galleria"));
    }
    
    public void createHTMLForImageDirectory(String imageDirName, String homePath, String titlePath) throws IOException {
        File imageDir = new File(outputDirectory, imageDirName);
        imageDir.mkdirs();
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
            de.jegr.imagegallery.pojo.Image image = new de.jegr.imagegallery.pojo.Image(fileName, thumbnailImageName, FilenameUtils.getBaseName(fileName), imageDir.getName());
            JSONObject imageJson = new JSONObject(image);
            imageJsons.put(imageJson);
        }
        return imageJsons;
    }

    
    private void writeHTML(File imageDir, String homePath, String titlePath, JSONArray imageJsons) throws IOException {
        String data = "var data = ".concat(imageJsons.toString());
        String html = FileUtils.readFileToString(new File(getClass().getClassLoader().getResource("gallery.html").getFile()));
        html = html.replace("%DATA%", data).replace("%HOME_PATH%", homePath).replace("%TITLE%", titlePath);
        File galleryHtmlFile = new File(imageDir, "gallery.html");
        FileUtils.write(galleryHtmlFile, html);
    }
    

    private void addGalleryInfo(List<de.jegr.imagegallery.pojo.Image> galleries, String imageDirOutputName, String titlePath) {
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

    private void addImageToGalleryInfo(List<de.jegr.imagegallery.pojo.Image> galleries, String imageDirOutputName, File imageFile) {
        String galleryLink = imageDirOutputName.concat("/gallery.html");
        String thumbnailImageName = getThumbnailFileName(imageFile);
        String title = createTitlePath(imageDirOutputName);
        String imageName = imageDirOutputName.concat("/").concat(imageFile.getName());
        String thumbName = imageDirOutputName.concat("/").concat(thumbnailImageName);
        de.jegr.imagegallery.pojo.Image image = new de.jegr.imagegallery.pojo.Image(imageName, thumbName, title, title);
        image.setLink(galleryLink);
        galleries.add(image);
    }
    
    private void writeIndexHtml(List<de.jegr.imagegallery.pojo.Image> galleries) throws IOException {
        String galleriesHtml = createGalleriesHtml(galleries);
        String indexHtml = FileUtils.readFileToString(new File(getClass().getClassLoader().getResource("index.html").getFile()));
        indexHtml = indexHtml.replace("%GALLERIES%", galleriesHtml);
        File indexHtmlFile = new File(outputDirectory, "index.html");
        FileUtils.write(indexHtmlFile, indexHtml);
    }

    String createGalleriesHtml(List<de.jegr.imagegallery.pojo.Image> galleries) {
        sortGalleries(galleries);
        String data = "var data = ";
        JSONArray galleriesJson = new JSONArray();
        for (de.jegr.imagegallery.pojo.Image gallery : galleries) {
            galleriesJson.put(new JSONObject(gallery));
        }
        return data.concat(galleriesJson.toString());
    }
    
    private void sortGalleries(List<de.jegr.imagegallery.pojo.Image> galleries) {
        Collections.sort(galleries, new Comparator<de.jegr.imagegallery.pojo.Image>() {

            @Override
            public int compare(de.jegr.imagegallery.pojo.Image o1, de.jegr.imagegallery.pojo.Image o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });
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
}
