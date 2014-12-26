package com.github.jegr78.imagegallery;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jegr78.imagegallery.pojo.Image;

public final class GalleryCreator {

    public static final String HTML_FILENAME = "index.html";
    
    private final File outputDirectory;
    private final ImageScanner imageScanner;
    private final GalleryImageCreator galleryImageCreator;
    private final GalleryHTMLCreator galleryHTMLCreator;

    private final Logger logger;

    public GalleryCreator(final File imagesRootDirectory, final File outputDirectory) {
        ImageOperations.verifyDirectory(imagesRootDirectory);
        ImageOperations.verifyDirectory(outputDirectory);
        this.outputDirectory = outputDirectory;
        imageScanner = new ImageScanner(imagesRootDirectory);
        galleryImageCreator = new GalleryImageCreator(outputDirectory);
        galleryHTMLCreator = new GalleryHTMLCreator(outputDirectory);
        logger = LoggerFactory.getLogger(getClass());
    }
    
    public List<File> create() throws IOException {
        copyGalleriaFiles();
        Map<String, List<File>> imageFilesPerDirectory = imageScanner.scan();
        List<Image> galleries = new ArrayList<>();
        for (Entry<String, List<File>> entry : imageFilesPerDirectory.entrySet()) {
            copyToOutputDirectory(entry, galleries);
        }
        writeIndexHtml(galleries);
        return galleryImageCreator.getErrors();
    }
    
    void copyGalleriaFiles() throws IOException {
        ImageOperations.copyResource("galleria", outputDirectory);
    }
    
    private void copyToOutputDirectory(Entry<String, List<File>> entry, List<Image> galleries) throws IOException {
        String imageDirOutputName = entry.getKey();
        List<File> imageFiles = entry.getValue();
        galleryImageCreator.create(imageFiles, imageDirOutputName);
        galleryHTMLCreator.create(imageDirOutputName);
        addGalleryInfo(galleries, imageDirOutputName);
    }
    
    File ensureOutputImageDirectory(String imageDirOutputName) {
        File outputImageDir = new File(outputDirectory, imageDirOutputName);
        ImageOperations.checkCreatedDirectory(outputImageDir);
        return outputImageDir;
    }
    
    private void addGalleryInfo(List<Image> galleries, String imageDirOutputName) {
        logger.debug("add gallery info for " + imageDirOutputName);
        File[] imageFiles = findImagesForGalleryInfo(imageDirOutputName);
        addImageToGalleryInfo(galleries, imageDirOutputName, imageFiles[0]);
    }

    private File[] findImagesForGalleryInfo(String imageDirOutputName) {
        File imageDir = ensureOutputImageDirectory(imageDirOutputName);
        File[] imageFiles = imageDir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                return ImageOperations.isValidImageFile(file)
                        && false == file.getName().contains("_thumbnail");
            }
            
        });
        return imageFiles;
    }
    
    void addImageToGalleryInfo(List<Image> galleries, String imageDirOutputName, File imageFile) {
        String galleryLink = imageDirOutputName.concat("/").concat(GalleryHTMLCreator.HTML_FILENAME);
        String thumbnailImageName = ImageOperations.getThumbnailFileName(imageFile);
        String title = ImageOperations.createTitlePath(imageDirOutputName);
        String imageName = imageDirOutputName.concat("/").concat(imageFile.getName());
        String thumbName = imageDirOutputName.concat("/").concat(thumbnailImageName);
        Image image = new Image(imageName, thumbName, title, title);
        image.setLink(galleryLink);
        galleries.add(image);
    }
    
    void writeIndexHtml(List<Image> galleries) throws IOException {
        String galleriesHtml = createGalleriesHtml(galleries);
        String indexHtml = ImageOperations.readResource(HTML_FILENAME);
        indexHtml = indexHtml.replace("%GALLERIES%", galleriesHtml);
        logger.debug("write gallery " + HTML_FILENAME + ": " + indexHtml);
        File indexHtmlFile = new File(outputDirectory, HTML_FILENAME);
        FileUtils.write(indexHtmlFile, indexHtml);
    }
    
    String createGalleriesHtml(List<Image> galleries) {
        sortGalleries(galleries);
        String data = "var data = ";
        JSONArray galleriesJson = new JSONArray();
        for (Image gallery : galleries) {
            logger.debug("create gallery html: " + gallery);
            galleriesJson.put(new JSONObject(gallery));
        }
        return data.concat(galleriesJson.toString());
    }
    
    void sortGalleries(List<Image> galleries) {
        Collections.sort(galleries);
    }
}
