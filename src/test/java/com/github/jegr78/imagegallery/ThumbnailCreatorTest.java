package com.github.jegr78.imagegallery;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class ThumbnailCreatorTest {
    
    private ThumbnailCreator thumbnailCreator;
    private File outputDir;
    
    @Before
    public void initThumbnailCreator() {
        outputDir = new File("target/thumbs");
        outputDir.mkdirs();
        thumbnailCreator = new ThumbnailCreator(outputDir);
    }

    @Test
    public void createThumbnail() throws Exception {
        File outputDir = new File("target/thumbs");
        outputDir.mkdirs();

        assertThumbnail("images/logos", "Logo1.jpg");
        assertThumbnail("images/fun", "fun2.png");
    }
    
    private void assertThumbnail(String dirName, String imageName) throws IOException {
        File imageFile = new File(ThumbnailCreatorTest.class.getClassLoader().getResource(dirName + "/" + imageName).getFile());
        thumbnailCreator.create(imageFile);
        String thumbnailFileName = ImageOperations.getThumbnailFileName(imageFile);
        File thumbnailFile = new File(outputDir, thumbnailFileName);        
        assertTrue("thumbnail was not created: " + thumbnailFile, thumbnailFile.exists());
    }

}
