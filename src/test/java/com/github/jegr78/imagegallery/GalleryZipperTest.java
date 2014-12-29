package com.github.jegr78.imagegallery;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;


public class GalleryZipperTest {
    
    private static final String ROOT_DIR_PATH = "src/test/resources/images";
    private static final String OUTPUT_DIR_PATH = "target/galleryzippertest";
    
    private GalleryZipper zipper;
    
    private File zipDir = null;
    
    @Before
    public void initZipper() throws Exception {
        zipDir = new File(OUTPUT_DIR_PATH, "images");
        zipDir.mkdirs();
        zipper = new GalleryZipper(zipDir);
    }
    
    @Test
    public void create() throws Exception {
        FileUtils.copyDirectory(new File(ROOT_DIR_PATH), zipDir);
        zipper.create();
        File galleryZipFile = new File(OUTPUT_DIR_PATH, GalleryZipper.ZIP_FILE_NAME);
        assertTrue("no zip file created: " + galleryZipFile, galleryZipFile.exists());
        ZipFile zipFile = new ZipFile(galleryZipFile);
        FileHeader fileHeader = zipFile.getFileHeader("images/fun/fun1.png");
        assertNotNull("fun1.png not found in zip", fileHeader);
    }
}
