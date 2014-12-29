package com.github.jegr78.imagegallery;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;


public class GalleryHTMLCreatorTest {
    
    private static final String IMAGE_DIR_NAME = "fun";
    private static final String ROOT_DIR_PATH = "src/test/resources/images/" + IMAGE_DIR_NAME;
    private static final String OUTPUT_DIR_PATH = "target/htmlcreatortest";
    
    
    private GalleryHTMLCreator htmlCreator;
    private GalleryImageCreator imageCreator;
    
    @Before
    public void initCreators() throws Exception {
        File outputDir = new File(OUTPUT_DIR_PATH);
        outputDir.mkdirs();
        htmlCreator = new GalleryHTMLCreator(outputDir);
        imageCreator = new GalleryImageCreator(outputDir);
        imageCreator.create(Arrays.asList(new File(ROOT_DIR_PATH, "fun2.png"), new File(ROOT_DIR_PATH, "fun3.png")), IMAGE_DIR_NAME);
    }
    
    @Test
    public void create() throws Exception {
        File imageDir = new File(OUTPUT_DIR_PATH, IMAGE_DIR_NAME);
        htmlCreator.create(IMAGE_DIR_NAME);
        File htmlFile = new File(imageDir, GalleryHTMLCreator.HTML_FILENAME);
        assertTrue("no html file written", htmlFile.exists());
    }
}
