package com.github.jegr78.imagegallery;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;


public class ImageScannerTest {
    
    private static final String ROOT_DIR_PATH = "src/test/resources/gallery";
    
    private ImageScanner imageScanner;
    
    @Before
    public void initImageScanner() throws Exception {
        File rootDir = new File(ROOT_DIR_PATH);
        imageScanner = new ImageScanner(rootDir);
    }

    @Test
    public void scanImagesRootDir() throws Exception {
        Map<String, List<File>> imagesPerDir = imageScanner.scan();
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
        String name = imageScanner.getImageDirRelativePath(imageDir);
        assertEquals("wrong name", path, name);
    }
    
}
