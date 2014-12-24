package com.github.jegr78.imagegallery;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.github.jegr78.imagegallery.ImageOperations;
import com.github.jegr78.imagegallery.ThumbnailCreator;

public class ThumbnailCreatorTest {

    @Test
    public void createThumbnail() throws Exception {
        File outputDir = new File("target/thumbs");
        outputDir.mkdirs();

        assertThumbnail("gallery/logos", "Logo1.jpg", outputDir);
        assertThumbnail("gallery/fun", "fun2.png", outputDir);
    }
    
    private void assertThumbnail(String dirName, String imageName, File outputDir) throws IOException {
        File imageFile = new File(ThumbnailCreatorTest.class.getClassLoader().getResource(dirName + "/" + imageName).getFile());
        ThumbnailCreator.create(imageFile, outputDir);
        String thumbnailFileName = ImageOperations.getThumbnailFileName(imageFile);
        File thumbnailFile = new File(outputDir, thumbnailFileName);        
        assertTrue("thumbnail was not created: " + thumbnailFile, thumbnailFile.exists());
    }

}
