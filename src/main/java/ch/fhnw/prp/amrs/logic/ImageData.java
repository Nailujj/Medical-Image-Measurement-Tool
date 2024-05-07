package ch.fhnw.prp.amrs.logic;

public class ImageData {
    private final String description;
    private final String resolution;
    private final String resolutionUnit;
    private final String imageFile;
    private final ImageLoader imageLoader;

    public ImageData(String description, String resolution, String resolutionUnit, String imageFile, ImageLoader imageLoader) {
        this.description = description;
        this.resolution = resolution;
        this.resolutionUnit = resolutionUnit;
        this.imageFile = imageFile;
        this.imageLoader = imageLoader;
    }

    public String getDescription() {
        return description;
    }

    public String getResolution() {
        return resolution;
    }

    public String getResolutionUnit() {
        return resolutionUnit;
    }

    public String getImageFile() {
        return imageFile;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

}
