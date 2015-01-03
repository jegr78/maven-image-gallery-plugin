package com.github.jegr78.imagegallery;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

public final class ImageFileCreationDateComparator implements Comparator<File> {

    @Override
    public int compare(File file1, File file2) {
        Date date1 = getCreationDate(file1);
        Date date2 = getCreationDate(file2);
        return date1.compareTo(date2);
    }
    
    public Date getCreationDate(File file) {
        if (file.isDirectory()) {
            return getCreationDateFromFile(file);
        }
        Date date = getCreationDateFromMetadata(file);
        if (date == null) {
            date = getCreationDateFromFile(file);
        }
        return date;
    }
    
    Date getCreationDateFromMetadata(File file) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            ExifSubIFDDirectory directory = metadata.getDirectory(ExifSubIFDDirectory.class);
            if (directory == null) {
                return null;
            }
            return directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
        } catch (ImageProcessingException | IOException e) {
            return null;
        }
    }
    
    Date getCreationDateFromFile(File file) {
        return new Date(file.lastModified());
    }
}