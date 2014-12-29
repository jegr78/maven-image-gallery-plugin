package com.github.jegr78.imagegallery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class GalleryImageCreatorTest {
    
    private static final String ROOT_DIR_PATH = "src/test/resources/images/fun";
    private static final String OUTPUT_DIR_PATH = "target";
    
    private GalleryImageCreator imageCreator;
    
    @Before
    public void initImageCreator() throws Exception {
        File outputDir = new File(OUTPUT_DIR_PATH);
        outputDir.mkdirs();
        imageCreator = new GalleryImageCreator(outputDir);
    }

    
    @Test
    public void create() throws Exception {
        List<File> imageFiles = Arrays.asList(new File(ROOT_DIR_PATH, "fun1.png"), new File(ROOT_DIR_PATH, "fun4.png"));
        imageCreator.create(imageFiles, "imagecreatortest");
        assertTrue("there should be no errors", imageCreator.getErrors().isEmpty());
        File createdImagesDir = new File(OUTPUT_DIR_PATH, "imagecreatortest");
        assertEquals("not all images created", 4, createdImagesDir.listFiles().length);
    }
}
