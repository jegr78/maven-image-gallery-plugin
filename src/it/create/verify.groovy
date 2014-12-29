File galleryDir = new File(basedir, "target/gallery");
assert galleryDir.isDirectory();
File indexFile = new File(galleryDir, "index.html");
assert indexFile.isFile();
File imagesDir = new File(galleryDir, "images");
assert imagesDir.isDirectory();
File sampleDir = new File(imagesDir, "sample");
assert sampleDir.isDirectory();
File sampleGalleryFile = new File(sampleDir, "gallery.html");
assert sampleGalleryFile.isFile();
File targetDir = galleryDir.getParentFile();
File galleryZipFile = new File(targetDir, "gallery.zip");
assert galleryZipFile.isFile();

