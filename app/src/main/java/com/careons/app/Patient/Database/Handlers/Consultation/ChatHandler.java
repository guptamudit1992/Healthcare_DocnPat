package com.careons.app.Patient.Database.Handlers.Consultation;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.careons.app.Patient.Activity.Chat.ViewChatImageActivity;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.R;

public class ChatHandler {

    private Context context;


    /**
     * Function to handle click event on image
     * @param view
     */
    public void showImage(View view) {

        context = view.getContext();

        Intent intent = new Intent(context, ViewChatImageActivity.class);
        intent.putExtra(StringConstants.KEY_TITLE, (view.findViewById(R.id.chat_image)).getTag().toString());
        intent.putExtra(StringConstants.KEY_URL, (view.findViewById(R.id.chat_url)).getTag().toString());
        context.startActivity(intent);
    }
}
