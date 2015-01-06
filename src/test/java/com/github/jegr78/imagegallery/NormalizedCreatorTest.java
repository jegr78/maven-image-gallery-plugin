package com.github.jegr78.imagegallery;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class NormalizedCreatorTest {
    
    private NormalizedCreator normalizedCreator;
    private File outputDir;
    
    @Before
    public void initNormalizedCreator() {
        outputDir = new File("target/normalizeds");
        outputDir.mkdirs();
        normalizedCreator = new NormalizedCreator(outputDir);
    }

    @Test
    public void createNormalized() throws Exception {
        assertNormalized("images/logos", "Logo1.jpg");
        assertNormalized("images/fun", "fun2.png");
    }
    
    private void assertNormalized(String dirName, String imageName) throws IOException {
        File imageFile = new File(NormalizedCreatorTest.class.getClassLoader().getResource(dirName + "/" + imageName).getFile());
        normalizedCreator.create(imageFile);
        String normalizedFileName = ImageOperations.getNormalizedFileName(imageFile);
        File normalizedFile = new File(outputDir, normalizedFileName);        
        assertTrue("normalized was not created: " + normalizedFile, normalizedFile.exists());
    }

}
