package com.github.jegr78.imagegallery.pojo;

import java.util.Objects;

public class Image implements Comparable<Image> {

    private final String image;
    private final String thumb;
    private final String title;
    private final String description;
    private String link;
    
   
    public Image(String image, String thumb, String title, String description) {
        verifyParameters(image, thumb, title, description);
        this.image = image;
        this.thumb = thumb;
        this.title = title;
        this.description = description;
    }

    private void verifyParameters(String image, String thumb, String title, String description) {
        Objects.requireNonNull(image, "image may not be null");
        Objects.requireNonNull(thumb, "thumb may not be null");
        Objects.requireNonNull(title, "title may not be null");
        Objects.requireNonNull(description, "description may not be null");
    }
    
    public String getImage() {
        return image;
    }
    
    public String getThumb() {
        return thumb;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getLink() {
        return link;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((image == null) ? 0 : image.hashCode());
        result = prime * result + ((link == null) ? 0 : link.hashCode());
        result = prime * result + ((thumb == null) ? 0 : thumb.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Image)) {
            return false;
        }
        Image other = (Image) obj;
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (image == null) {
            if (other.image != null) {
                return false;
            }
        } else if (!image.equals(other.image)) {
            return false;
        }
        if (link == null) {
            if (other.link != null) {
                return false;
            }
        } else if (!link.equals(other.link)) {
            return false;
        }
        if (thumb == null) {
            if (other.thumb != null) {
                return false;
            }
        } else if (!thumb.equals(other.thumb)) {
            return false;
        }
        if (title == null) {
            if (other.title != null) {
                return false;
            }
        } else if (!title.equals(other.title)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "Image [image=" + image + ", thumb=" + thumb + ", title=" + title + ", description=" + description + ", link=" + link + "]";
    }

    @Override
    public int compareTo(Image o) {
        if (o == null) {
            return 0;
        }
        return title.compareTo(o.getTitle());
    }
    
}
