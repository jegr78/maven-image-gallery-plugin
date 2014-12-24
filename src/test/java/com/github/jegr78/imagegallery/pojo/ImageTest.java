package com.github.jegr78.imagegallery.pojo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.LinkedHashSet;

import org.junit.Before;
import org.junit.Test;

public class ImageTest {
    
    private Image image1;
    private Image image2;
    
    @Before
    public void createImages() {
        image1 = new Image("image1.png", "image1_thumbnail.png", "Image 1", "A Image");
        image2 = new Image("image2.png", "image2_thumbnail.png", "Image 2", "Another Image");
    }

    @Test
    public void equalsObject() {
        assertEquals("image1 should be equal", image1, image1);
        assertNotEquals("image1 and image2 shound not be equal", image1, image2);
        
        Image image1Copy = new Image("image1.png", "image1_thumbnail.png", "Image 1", "A Image");
        assertEquals("image1 and image1 copy should be equal", image1, image1Copy);
        image1.setLink("Link");
        image1Copy.setLink("Copy Link");
        assertNotEquals("image1 and copy shound not be equal because of different link", image1, image1Copy);
        image1Copy.setLink(null);
        assertNotEquals("image1 and copy shound not be equal because of no link", image1, image1Copy);
        image1Copy.setLink("Link");
        assertEquals("image1 and copy shound be equal also with link", image1, image1Copy);
        
        assertNotEquals("null should not be equal with image1", image1, null);
        assertNotEquals("String should not be equal with image1", image1, "A String");
    }

    @Test
    public void compareToObject() {
        int result = image1.compareTo(image2);
        assertTrue("image1 should be less than image2", result < 0);
        result = image1.compareTo(null);
        assertTrue("image1 should be equal with null image", result == 0);
    }
    
    @Test
    public void hashSetOperations() {
        LinkedHashSet<Image> images = new LinkedHashSet<>();
        images.add(image1);
        images.add(image2);
        Iterator<Image> iterator = images.iterator();
        Image firstImage = iterator.next();
        assertEquals("first image should be image1: " + firstImage, image1, firstImage);
        Image secondImage =  iterator.next();
        assertEquals("second image should be image2: " + secondImage, image2, secondImage);
    }

}
