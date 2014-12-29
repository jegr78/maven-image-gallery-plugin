package com.github.jegr78.imagegallery.mojo;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.mockito.Mockito;

import com.github.jegr78.imagegallery.GalleryCreator;
import com.github.jegr78.imagegallery.GalleryZipper;

public class ImageGalleryMojoTest {

    @Test
    public void execute() throws Exception {
        ImageGalleryMojo mojo = new ImageGalleryMojo();
        mojo.setImagesRootDirectory(new File("src/test/resources/images"));
        mojo.setCreateZip(true);
        File galleryDirectory = new File("target/gallery");
        mojo.setOutputDirectory(galleryDirectory);
        mojo.setLog(Mockito.mock(Log.class));
        mojo.execute();
        assertTrue("no gallery created: " + galleryDirectory, galleryDirectory.isDirectory());
        File indexFile = new File(galleryDirectory, GalleryCreator.HTML_FILENAME);
        assertTrue("no " + indexFile + " file created", indexFile.isFile());
        File targetDirectory = galleryDirectory.getParentFile();
        File zipFile = new File(targetDirectory, GalleryZipper.ZIP_FILE_NAME);
        assertTrue("no " + zipFile + " file created", zipFile.isFile());
    }

}
