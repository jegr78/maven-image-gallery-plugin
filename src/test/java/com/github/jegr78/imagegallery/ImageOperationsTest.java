package com.github.jegr78.imagegallery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.github.jegr78.imagegallery.ImageOperations;
import com.github.jegr78.imagegallery.pojo.Image;


public class ImageOperationsTest {
    
    private static final String ROOT_DIR_PATH = "src/test/resources/gallery";
    private static final String OUTPUT_DIR_PATH = "target/gallery";
    
    private ImageOperations imageOps;
    
    @Before
    public void initImageOps() throws Exception {
        File rootDir = new File(ROOT_DIR_PATH);
        File outputDir = new File(OUTPUT_DIR_PATH);
        outputDir.mkdirs();
        imageOps = new ImageOperations(rootDir, outputDir);
    }

    @Test
    public void scanImagesRootDir() throws Exception {
        Map<String, List<File>> imagesPerDir = imageOps.scanImagesRootDir();
        Set<String> directories = imagesPerDir.keySet();
        assertEquals("wrong number of directories", 4, directories.size());
    }
    
    @Test
    public void getImageDirOutputName() throws Exception {
        assertImageDirOutputName("fun");
        assertImageDirOutputName("nested/1");
    }
    
    private void assertImageDirOutputName(String path) {
        File imageDir = new File(ROOT_DIR_PATH + "/" + path);
        String name = imageOps.getImageDirRelativePath(imageDir);
        assertEquals("wrong name", path, name);
    }
    
    @Test
    public void createHTMLForImageDirectory() throws Exception {
        File imageDir = new File(OUTPUT_DIR_PATH + "/fun");
        imageOps.createHTMLForImageDirectory("fun", "../", "fun");
        File htmlFile = new File(imageDir, "gallery.html");
        assertTrue("no html file written", htmlFile.exists());
    }
    
    @Test
    public void createHomePath() throws Exception {
        String homePath = imageOps.createHomePath("fun");
        assertEquals("wrong home path", "../", homePath);
        homePath = imageOps.createHomePath("nested/1");
        assertEquals("wrong home path", "../../", homePath);
    }
    
    @Test
    public void createTitlePath() throws Exception {
        String titlePath = imageOps.createTitlePath("fun");
        assertEquals("wrong title path", "fun", titlePath);
        titlePath = imageOps.createTitlePath("nested/1");
        assertEquals("wrong title path", "nested - 1", titlePath);
    }
    
    @Test
    public void createGalleriesHtml() throws Exception {
        List<Image> galleries = new ArrayList<Image>();
        Image galleryFun = new Image("fun1.png", "fun1_thumbnail.png", "fun", "fun");
        galleryFun.setLink("fun/gallery.html");
        galleries.add(galleryFun);
        Image galleryNested1 = new Image("Image1.jpg", "Image1_thumbnail.jpg", "nested - 1", "nested - 1");
        galleryNested1.setLink("nested/1/gallery.html");
        galleries.add(galleryNested1);
        String html = imageOps.createGalleriesHtml(galleries);
        assertNotNull("galleries html may not be null", html);
        assertFalse("galleries html may not be empty", html.isEmpty());
    }
    
    @Test
    public void copyToOutputDirectory() throws Exception {
        imageOps.copyGalleriaFiles();
        List<File> errors = imageOps.copyToOutputDirectory(imageOps.scanImagesRootDir());
        assertTrue("there should be no errors: " + errors.toString(), errors.isEmpty());
    }
}
