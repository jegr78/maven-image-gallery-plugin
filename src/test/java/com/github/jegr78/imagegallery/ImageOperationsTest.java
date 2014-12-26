package com.github.jegr78.imagegallery;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;


public class ImageOperationsTest {
    
    private static final String ROOT_DIR_PATH = "src/test/resources/gallery";
    
    @Test
    public void verifyDirectorySuccess() throws Exception {
        File dir = new File(ROOT_DIR_PATH);
        ImageOperations.verifyDirectory(dir);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void verifyDirectoryFailureNoDirectory() throws Exception {
        File file = new File(ROOT_DIR_PATH, "fun/fun1.png");
        ImageOperations.verifyDirectory(file);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void verifyDirectoryFailureNull() throws Exception {
        ImageOperations.verifyDirectory(null);
    }
    
    @Test
    public void createHomePath() throws Exception {
        String homePath = ImageOperations.createHomePath("fun");
        assertEquals("wrong home path", "../", homePath);
        homePath = ImageOperations.createHomePath("nested/1");
        assertEquals("wrong home path", "../../", homePath);
    }
    
    @Test
    public void createTitlePath() throws Exception {
        String titlePath = ImageOperations.createTitlePath("fun");
        assertEquals("wrong title path", "fun", titlePath);
        titlePath = ImageOperations.createTitlePath("nested/1");
        assertEquals("wrong title path", "nested - 1", titlePath);
    }
    
    @Test
    public void getThumbnailFileName() throws Exception {
        String thumbnailFileName = ImageOperations.getThumbnailFileName(new File(ROOT_DIR_PATH, "fun/fun1.png"));
        assertEquals("wrong thumbnail file name", "fun1_thumbnail.png", thumbnailFileName);
        thumbnailFileName = ImageOperations.getThumbnailFileName(new File(ROOT_DIR_PATH, "logos/Logo2.jpg"));
        assertEquals("wrong thumbnail file name", "Logo2_thumbnail.jpg", thumbnailFileName);
    }
    
    @Test
    public void ensureOriginalImageFileSuccess() throws Exception {
        ImageOperations.ensureOriginalFile(new File(ROOT_DIR_PATH, "fun/fun1.png"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void ensureOriginalImageFileFailureFileNotExists() throws Exception {
        ImageOperations.ensureOriginalFile(new File(ROOT_DIR_PATH, "fun/fun100.png"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void ensureOriginalImageFileFailureFileNotFile() throws Exception {
        ImageOperations.ensureOriginalFile(new File(ROOT_DIR_PATH, "fun"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void ensureOriginalImageFileFailureFileNull() throws Exception {
        ImageOperations.ensureOriginalFile(null);
    }
}
