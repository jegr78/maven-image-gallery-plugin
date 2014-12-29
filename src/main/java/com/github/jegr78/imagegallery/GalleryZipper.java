package com.github.jegr78.imagegallery;

import java.io.File;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class GalleryZipper {
    
    public static final String ZIP_FILE_NAME = "gallery.zip";
    
    private final File galleryDirectory;
    private final File outputDirectory;
    
    GalleryZipper(File galleryDirectory) {
        ImageOperations.verifyDirectory(galleryDirectory);
        this.galleryDirectory = galleryDirectory;
        this.outputDirectory = galleryDirectory.getParentFile();
    }
    
    void create() throws ZipException {
        ZipFile zipFile = new ZipFile(new File(outputDirectory, ZIP_FILE_NAME));
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_MAXIMUM);
        zipFile.addFolder(galleryDirectory, parameters);
    }
}
