package de.jegr.imagegallery;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;


public class ImageOperationsTest {
    
    private ImageOperations imageOps;
    
    @Before
    public void initImageOps() throws Exception {
        File rootDir = new File(ImageOperationsTest.class.getClassLoader().getResource(".").getFile());
        File outputDir = new File(rootDir, "gallery");
        outputDir.mkdirs();
        imageOps = new ImageOperations(rootDir, outputDir);
    }

    @Test
    public void scanImagesRootDir() throws Exception {
        Map<File, List<File>> imagesPerDir = imageOps.scanImagesRootDir();
        Set<File> directories = imagesPerDir.keySet();
        assertEquals("wrong number of directories", 3, directories.size());
    }
    
    @Test
    public void getImageDirOutputName() throws Exception {
        assertImageDirOutputName("fun");
        assertImageDirOutputName("nested/1");
    }
    
    private void assertImageDirOutputName(String path) {
        File imageDir = new File(ImageOperationsTest.class.getClassLoader().getResource("gallery/" + path).getFile());
        String name = imageOps.getImageDirOutputName(imageDir);
        assertEquals("wrong name", path, name);
    }
    
}
