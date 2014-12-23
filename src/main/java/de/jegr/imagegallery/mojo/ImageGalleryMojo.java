package de.jegr.imagegallery.mojo;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import de.jegr.imagegallery.ImageOperations;

/**
 * Goal which creates a HTML image gallery.
 *
 */
@Mojo(name = "create", defaultPhase = LifecyclePhase.PACKAGE)
public class ImageGalleryMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project.build.directory}/gallery", property = "outputDir", required = true)
	private File outputDirectory;
	
	@Parameter(defaultValue = "${basedir}", property = "imagesRootDir", required = true)
	private File imagesRootDirectory;

	private ImageOperations imageOps;
	
	public void execute() throws MojoExecutionException {
	    getLog().info("creating image gallery from: " + imagesRootDirectory + " to: " + outputDirectory);
		ensureDirectories();
		initImageOperations();
		copyGalleriaFiles();
		copyToOutputDirectory(scanImagesRootDir());
	}

	private void ensureDirectories() {
	    getLog().debug("ensure src/dest directories existence");
	    outputDirectory.mkdirs();
	    imagesRootDirectory.mkdirs();
	}

    private void initImageOperations() {
        imageOps = new ImageOperations(imagesRootDirectory, outputDirectory);
    }
    
    private void copyGalleriaFiles() throws MojoExecutionException {
        getLog().debug("copy galleria files to destination");
        try {
            imageOps.copyGalleriaFiles();
        } catch (IOException e) {
            throw new MojoExecutionException("unable to copy galleria files", e);
        }
    }

	private Map<String, List<File>> scanImagesRootDir() {
	    getLog().debug("scanning destination directory for images");
		return imageOps.scanImagesRootDir();
	}

	private void copyToOutputDirectory(Map<String, List<File>> imageFilesPerDirectory) throws MojoExecutionException {
	    getLog().debug("copying found images to destination");
        try {
            List<File> errors = imageOps.copyToOutputDirectory(imageFilesPerDirectory);
            for (File error : errors) {
                getLog().warn("unable to copy file: " + error);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("unable to copy images to output directory", e);   
        }
	}

}
