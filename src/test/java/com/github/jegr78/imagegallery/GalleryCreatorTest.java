package com.github.jegr78.imagegallery;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.jegr78.imagegallery.pojo.Image;


public class GalleryCreatorTest {
    
    private static final String ROOT_DIR_PATH = "src/test/resources/images";
    private static final String OUTPUT_DIR_PATH = "target/gallerycreatortest";
    
    private GalleryCreator galleryCreator;
    
    @Before
    public void initCreator() throws Exception {
        File rootDir = new File(ROOT_DIR_PATH);
        File outputDir = new File(OUTPUT_DIR_PATH);
        outputDir.mkdirs();
        galleryCreator = new GalleryCreator(rootDir, outputDir, false);
    }
    
    @Test
    public void create() throws Exception {
        List<File> errors = galleryCreator.create();
        assertTrue("there should be no errors", errors.isEmpty());
        File copiedStaticDir = new File(OUTPUT_DIR_PATH, "static");
        assertTrue("static dir not copied", copiedStaticDir.isDirectory());
        File indexFile = new File(OUTPUT_DIR_PATH, GalleryCreator.HTML_FILENAME);
        assertTrue("no " + indexFile + " file written", indexFile.isFile());
    }
    
    @Test
    public void createGalleriesHtml() throws Exception {
        List<Image> galleries = new ArrayList<Image>();
        Image galleryFun = new Image("fun1_normalized.png", "fun1.png", "fun1_thumbnail.png", "fun", "fun");
        galleryFun.setLink("fun/gallery.html");
        galleries.add(galleryFun);
        Image galleryNested1 = new Image("Image1_normalized.jpg", "Image1.jpg", "Image1_thumbnail.jpg", "nested - 1", "nested - 1");
        galleryNested1.setLink("nested/1/gallery.html");
        galleries.add(galleryNested1);
        String html = galleryCreator.createGalleriesHtml(galleries);
        assertNotNull("galleries html may not be null", html);
        assertFalse("galleries html may not be empty", html.isEmpty());
    }
}
