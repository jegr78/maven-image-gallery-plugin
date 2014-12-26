package com.github.jegr78.imagegallery.mojo;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.mockito.Mockito;

import com.github.jegr78.imagegallery.GalleryCreator;

public class ImageGalleryMojoTest {

    @Test
    public void execute() throws Exception {
        ImageGalleryMojo mojo = new ImageGalleryMojo();
        mojo.setImagesRootDirectory(new File("src/test/resources/gallery"));
        File outputDirectory = new File("target/gallery");
        mojo.setOutputDirectory(outputDirectory);
        mojo.setLog(Mockito.mock(Log.class));
        mojo.execute();
        assertTrue("no gallery created: " + outputDirectory, outputDirectory.isDirectory());
        File indexFile = new File(outputDirectory, GalleryCreator.HTML_FILENAME);
        assertTrue("no " + indexFile + " file created", indexFile.isFile());
    }

}
