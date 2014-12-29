package com.github.jegr78.imagegallery;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jegr78.imagegallery.pojo.Image;

public final class GalleryCreator {

    public static final String HTML_FILENAME = "index.html";
    
    private final File outputDirectory;
    private final boolean createZip;
    private final ImageScanner imageScanner;
    private final GalleryImageCreator galleryImageCreator;
    private final GalleryHTMLCreator galleryHTMLCreator;
    private final GalleryZipper galleryZipper;

    private final Logger logger;

    public GalleryCreator(final File imagesRootDirectory, final File outputDirectory, final boolean createZip) {
        ImageOperations.verifyDirectory(imagesRootDirectory);
        ImageOperations.verifyDirectory(outputDirectory);
        this.outputDirectory = outputDirectory;
        this.createZip = createZip;
        imageScanner = new ImageScanner(imagesRootDirectory);
        File galleryBaseOutputDir = ensureOutputImageDirectory("images");
        galleryImageCreator = new GalleryImageCreator(galleryBaseOutputDir);
        galleryHTMLCreator = new GalleryHTMLCreator(galleryBaseOutputDir);
        galleryZipper = new GalleryZipper(outputDirectory);
        logger = LoggerFactory.getLogger(getClass());
    }
    
    public List<File> create() throws IOException {
        copyStaticWebResourceFiles();
        Map<String, List<File>> imageFilesPerDirectory = imageScanner.scan();
        List<Image> galleries = new ArrayList<>();
        for (Entry<String, List<File>> entry : imageFilesPerDirectory.entrySet()) {
            copyToOutputDirectory(entry, galleries);
        }
        writeIndexHtml(galleries);
        checkCreateZip();
        return galleryImageCreator.getErrors();
    }
    
    void copyStaticWebResourceFiles() throws IOException {
        ImageOperations.copyResource("static", outputDirectory);
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
        File imageDir = ensureOutputImageDirectory("images/".concat(imageDirOutputName));
        return imageDir.listFiles(new ImageFileFilter());
    }
    
    void addImageToGalleryInfo(List<Image> galleries, String imageDirOutputName, File imageFile) {
        String imagesBasePath = "images/".concat(imageDirOutputName).concat("/");
        String galleryLink = imagesBasePath.concat(GalleryHTMLCreator.HTML_FILENAME);
        String thumbnailImageName = ImageOperations.getThumbnailFileName(imageFile);
        String title = ImageOperations.createTitlePath(imageDirOutputName);
        String imageName = imagesBasePath.concat(imageFile.getName());
        String thumbName = imagesBasePath.concat(thumbnailImageName);
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
    
    void checkCreateZip() throws IOException {
        if (false == createZip) {
            logger.debug("zip creation is not demanded -> skipping it");
            return;
        }
        createZip();
    }
    
    void createZip() throws IOException {
        try {
            galleryZipper.create();
        } catch (ZipException e) {
            throw new IOException("unable to create zip", e);
        }
    }

    private static class ImageFileFilter implements FileFilter {
        
        @Override
        public boolean accept(File file) {
            return ImageOperations.isValidImageFile(file)
                    && false == file.getName().contains("_thumbnail");
        }
    }
}
