package de.jegr.imagegallery;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

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
        String thumbnailFileName = FilenameUtils.getBaseName(imageName) + "_thumbnail." + FilenameUtils.getExtension(imageName);
        File thumbnailFile = new File(outputDir, thumbnailFileName);        
        assertTrue("thumbnail was not created: " + thumbnailFile, thumbnailFile.exists());
    }

}
