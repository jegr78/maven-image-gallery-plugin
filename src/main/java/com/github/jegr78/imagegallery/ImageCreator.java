package com.github.jegr78.imagegallery;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

abstract class ImageCreator {
    
    private final File outputDirectory;
	
    ImageCreator(File outputDirectory) {
        ImageOperations.checkCreatedDirectory(outputDirectory);
        this.outputDirectory = outputDirectory;
    }
	
	final void create(File original) throws IOException {
		BufferedImage image = perform(original);
		writeToDisk(image, original);
	}

    protected final BufferedImage perform(File original) throws IOException {
        BufferedImage image = ImageIO.read(original);
        int width = image.getWidth();
        int targetSize = getTargetSize();
        if (width <= targetSize) {
            return image;
        }
        return Scalr.resize(image, Method.SPEED, targetSize, Scalr.OP_ANTIALIAS);
    }

    protected abstract int getTargetSize();

    final void writeToDisk(BufferedImage image, File original) throws IOException {
        String name = getOutputName(original);
        File output = new File(outputDirectory, name);
		FileUtils.deleteQuietly(output);
        ImageIO.write(image, FilenameUtils.getExtension(name), output);
    }
    
    protected abstract String getOutputName(File original);

}
