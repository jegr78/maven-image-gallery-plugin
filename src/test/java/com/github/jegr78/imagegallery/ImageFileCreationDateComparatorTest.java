package com.github.jegr78.imagegallery;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;


public class ImageFileCreationDateComparatorTest {
    
    private static final String ROOT_DIR_PATH = "src/test/resources";
    
    private ImageFileCreationDateComparator comparator;
    
    @Before
    public void initComparator() throws Exception {
        comparator = new ImageFileCreationDateComparator();
    }

    @Test
    public void getCreationDateFromMetadata() throws Exception {
        File imageFile = new File(ROOT_DIR_PATH, "image_with_exif_data.jpg");
        Date date = comparator.getCreationDateFromMetadata(imageFile);
        assertNotNull("image " + imageFile + " should have date in exif data", date);
    }
    
}
