package com.github.jegr78.imagegallery;

import java.io.File;

final class NormalizedCreator extends ImageCreator {
	
    NormalizedCreator(File outputDirectory) {
        super(outputDirectory);
    }

    @Override
    protected int getTargetSize() {
        return 600;
    }
    
    @Override
    protected String getOutputName(File original) {
        return ImageOperations.getNormalizedFileName(original);
    }

}
