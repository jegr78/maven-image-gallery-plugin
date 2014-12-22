package de.jegr.imagegallery;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

public final class ThumbnailCreator {
	
	private ThumbnailCreator() {}
	
	public static void create(File original, File outputDir) throws IOException {
		BufferedImage image = resize(original);
		writeToDisk(outputDir, image, original);
	}

    private static BufferedImage resize(File original) throws IOException {
        BufferedImage image = ImageIO.read(original);
		return Scalr.resize(image, Method.SPEED, 125, Scalr.OP_ANTIALIAS);
    }

    private static void writeToDisk(File outputDir, BufferedImage image, File original) throws IOException {
        String name = ImageOperations.getThumbnailFileName(original);
        File output = new File(outputDir, name);
		FileUtils.deleteQuietly(output);
        ImageIO.write(image, FilenameUtils.getExtension(name), output);
    }

}
