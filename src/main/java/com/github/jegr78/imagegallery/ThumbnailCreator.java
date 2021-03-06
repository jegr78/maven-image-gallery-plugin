package com.github.jegr78.imagegallery;

import java.io.File;

final class ThumbnailCreator extends ImageCreator {

    
    ThumbnailCreator(File outputDirectory) {
        super(outputDirectory);
    }

    @Override
    protected int getTargetSize() {
        return 125;
    }

    @Override
    protected String getOutputName(File original) {
        return ImageOperations.getThumbnailFileName(original);
    }
}
