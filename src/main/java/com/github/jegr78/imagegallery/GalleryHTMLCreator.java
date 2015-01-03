package com.github.jegr78.imagegallery;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jegr78.imagegallery.pojo.Image;

final class GalleryHTMLCreator {

    static final String HTML_FILENAME = "gallery.html";

    private final File outputDirectory;

    private final Logger logger;

    GalleryHTMLCreator(final File outputDirectory) {
        ImageOperations.verifyDirectory(outputDirectory);
        this.outputDirectory = outputDirectory;
        logger = LoggerFactory.getLogger(getClass());
    }

    void create(String imageDirName) throws IOException {
        logger.debug("create html for directory " + imageDirName);
        File imageDir = new File(outputDirectory, imageDirName);
        ImageOperations.checkCreatedDirectory(imageDir);
        JSONArray imageJsons = createImageJsonData(imageDir);
        writeHTML(imageDir, ImageOperations.createHomePath(imageDirName), ImageOperations.createTitlePath(imageDirName), imageJsons);
    }

    JSONArray createImageJsonData(File imageDir) {
        Collection<File> imageFiles = determineImageFiles(imageDir);
        JSONArray imageJsons = new JSONArray();
        for (File imageFile : imageFiles) {
            String fileName = imageFile.getName();
            if (fileName.contains("_thumbnail")) {
                continue;
            }
            String thumbnailImageName = ImageOperations.getThumbnailFileName(imageFile);
            Image image = new Image(fileName, thumbnailImageName, FilenameUtils.getBaseName(fileName), imageDir.getName());
            JSONObject imageJson = new JSONObject(image);
            imageJsons.put(imageJson);
        }
        logger.debug("json data for image dir " + imageDir + ": " + imageJsons.toString());
        return imageJsons;
    }

    private Collection<File> determineImageFiles(File imageDir) {
        File[] listFiles = imageDir.listFiles(new ImageFileFilter());
        List<File> imagesFiles = Arrays.asList(listFiles);
        Collections.sort(imagesFiles, new ImageFileCreationDateComparator());
        return imagesFiles;
    }


    void writeHTML(File imageDir, String homePath, String titlePath, JSONArray imageJsons) throws IOException {
        String data = "var data = ".concat(imageJsons.toString());
        String html = ImageOperations.readResource(HTML_FILENAME);
        html = html.replace("%DATA%", data).replace("%HOME_PATH%", homePath).replace("%TITLE%", titlePath);
        logger.debug("write " + HTML_FILENAME + " for image dir " + imageDir + ": " + html);
        File galleryHtmlFile = new File(imageDir, HTML_FILENAME);
        FileUtils.write(galleryHtmlFile, html);
    }
    
    private static class ImageFileFilter implements FileFilter {
        
        @Override
        public boolean accept(File file) {
            return ImageOperations.isValidImageFile(file);
        }
    }
}
