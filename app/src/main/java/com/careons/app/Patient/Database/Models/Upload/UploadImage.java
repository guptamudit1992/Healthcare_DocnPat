package com.careons.app.Patient.Database.Models.Upload;

import com.orm.SugarRecord;

public class UploadImage extends SugarRecord {

    private String albumId;
    private String imageId;
    private String imagePath;
    private String azureServerUrl;

    public UploadImage() {
        // Default Constructor
    }

    public UploadImage(String albumId, String imageId, String imagePath, String azureServerUrl) {

        this.albumId = albumId;
        this.imageId = imageId;
        this.imagePath = imagePath;
        this.azureServerUrl = azureServerUrl;
    }


    // Getter and Setter
    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
    public String getImagePath() {
        return imagePath;
    }

    public String getAzureServerUrl() {
        return azureServerUrl;
    }


}

