package de.jegr.imagegallery;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

public final class ThumbnailCreator {
	
	private ThumbnailCreator() {}
	
	public static void create(File original, File outputDir) throws IOException {
	    String imageName = original.getName();
	    String baseName = FilenameUtils.getBaseName(imageName);
	    String extension = verifyExtension(FilenameUtils.getExtension(imageName));
		BufferedImage image = resize(original);
		writeToDisk(outputDir, image, baseName, extension);
	}

    private static BufferedImage resize(File original) throws IOException {
        BufferedImage image = ImageIO.read(original);
		return Scalr.resize(image, Method.SPEED, 125, Scalr.OP_ANTIALIAS, Scalr.OP_BRIGHTER);
    }

    private static String verifyExtension(final String extension) {
        if (extension == null || extension.isEmpty()) {
		    return "jpg";
		}
		List<String> writerFormatNames = Arrays.asList(ImageIO.getWriterFormatNames());
		if (false == writerFormatNames.contains(extension)) {
		    return "jpg";
		}
        return extension;
    }

    private static void writeToDisk(File outputDir, BufferedImage image, String baseName, String extension) throws IOException {
        File output = new File(outputDir, baseName + "_thumbnail." + extension);
		FileUtils.deleteQuietly(output);
        ImageIO.write(image, extension, output);
    }

}
