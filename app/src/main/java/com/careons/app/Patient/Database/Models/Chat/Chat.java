package com.careons.app.Patient.Database.Models.Chat;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.careons.app.R;
import com.careons.app.Shared.Utils.Validation;
import com.squareup.picasso.Picasso;

public class Chat {

    private String id;
    private String message;
    private String url;
    private String type;
    private String time;
    private Boolean isSendSelf;



    public Chat() {
        // Default Constructor
    }

    public Chat(String id, String message, String url, String type, String time, Boolean isSendSelf) {
        this.id = id;
        this.message = message;
        this.url = url;
        this.type = type;
        this.time = time;
        this.isSendSelf = isSendSelf;
    }



    /**
     * Getter and Setter
     */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getSendSelf() {
        return isSendSelf;
    }

    public void setSendSelf(Boolean sendSelf) {
        isSendSelf = sendSelf;
    }




    @BindingAdapter({"bind:imagePath"})
    public static void setImagePath(ImageView view, String imagePath) {

        if(!Validation.isEmpty(imagePath)) {
            Picasso.with(view.getContext())
                    .load(imagePath)
                    .placeholder(R.drawable.dp_man)
                    //.error()
                    .into(view);
        }
    }

}

