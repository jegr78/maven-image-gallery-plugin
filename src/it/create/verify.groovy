File galleryDir = new File(basedir, "target/gallery");
assert galleryDir.isDirectory();
File indexFile = new File(galleryDir, "index.html");
assert indexFile.isFile();
File sampleDir = new File(galleryDir, "sample");
assert sampleDir.isDirectory();
File sampleGalleryFile = new File(sampleDir, "gallery.html");
assert sampleGalleryFile.isFile();

