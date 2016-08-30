package com.careons.app.Patient.Database.Models.Upload;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.careons.app.R;
import com.orm.SugarRecord;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Album extends SugarRecord implements Comparable {

    private String patientId;
    private String albumId;
    private String title;
    private long dateOfTest;
    private String tag;
    private boolean isAlbum;
    private int count;
    private String comments;
    private String imagePath;
    private String azureUrlPath;

    public Album() {
        // Default Constructor
    }


    public Album(String patientId, String albumId, String title, long dateOfTest, String tag, boolean isAlbum, int count, String comments) {

        this.patientId = patientId;
        this.albumId = albumId;
        this.title = title;
        this.dateOfTest = dateOfTest;
        this.tag = tag;
        this.isAlbum = isAlbum;
        this.count = count;
        this.comments = comments;
    }


    // Getter and Setter
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDateOfTest() {
        return dateOfTest;
    }

    public void setDateOfTest(long dateOfTest) {
        this.dateOfTest = dateOfTest;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isAlbum() {
        return isAlbum;
    }

    public void setIsAlbum(boolean isAlbum) {
        this.isAlbum = isAlbum;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getAzureUrlPath() {
        return azureUrlPath;
    }

    public void setAzureUrlPath(String azureUrlPath) {
        this.azureUrlPath = azureUrlPath;
    }


    /**
     * Fetch first uploaded image
     * @return
     */
    public List<UploadImage> getUploadImages() {

        return UploadImage.find(UploadImage.class, "album_id = ?", albumId);
    }

    public String getImagePath() {

        if(getUploadImages() != null && getUploadImages().size() > 0) {
            imagePath = getUploadImages().get(0).getAzureServerUrl().toString();
        }
        return imagePath;
    }

    @BindingAdapter({"bind:imagePath"})
    public static void setImagePath(ImageView view, String imagePath) {

        Picasso
                .with(view.getContext())
                .load(imagePath)
                .placeholder(R.drawable.dp_man)
                //.error()
                .into(view);
    }


    @Override
    public int compareTo(Object another) {

        long timestamp  = ((Album) another).getDateOfTest();

        if(timestamp > dateOfTest) {
            return 1;
        } if(timestamp == dateOfTest) {
            return 0;
        } else {
            return -1;
        }
    }
}
