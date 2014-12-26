package com.github.jegr78.imagegallery;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

final class ThumbnailCreator {
    
    private final File outputDirectory;
	
    ThumbnailCreator(File outputDirectory) {
        ImageOperations.checkCreatedDirectory(outputDirectory);
        this.outputDirectory = outputDirectory;
    }
	
	void create(File original) throws IOException {
		BufferedImage image = resize(original);
		writeToDisk(image, original);
	}

    private BufferedImage resize(File original) throws IOException {
        BufferedImage image = ImageIO.read(original);
		return Scalr.resize(image, Method.SPEED, 125, Scalr.OP_ANTIALIAS);
    }

    void writeToDisk(BufferedImage image, File original) throws IOException {
        String name = ImageOperations.getThumbnailFileName(original);
        File output = new File(outputDirectory, name);
		FileUtils.deleteQuietly(output);
        ImageIO.write(image, FilenameUtils.getExtension(name), output);
    }

}
