package com.careons.app.Patient.Database.Handlers.Upload;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.careons.app.Patient.Activity.Main.UploadedDocuments.GalleryDetailActivity;
import com.careons.app.Patient.Activity.Main.UploadedDocuments.GalleryGridActivity;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Upload.UploadImage;
import com.careons.app.R;

public class GalleryGridHandler {

    private Context context;
    private UploadImage uploadImage;
    private ViewDataBinding dataCard;

    private CardView cardView;

    // Parametrized Constructor
    public GalleryGridHandler(final UploadImage uploadImage, final ViewDataBinding dataCard) {

        this.uploadImage = uploadImage;
        this.dataCard = dataCard;

        // Get context
        context = dataCard.getRoot().getContext();

        // Initialize layouts
        cardView = (CardView) dataCard.getRoot().findViewById(R.id.card_gallery_grid);


        /**
         * Function - Delete Album
         */
        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                // Initialize dialog
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater =  LayoutInflater.from(context);
                final View dialogView = inflater.inflate(R.layout.dialog_delete, null);
                builder.setView(dialogView);
                final AlertDialog dialog = builder.create();
                dialog.show();


                CardView acceptCardView = (CardView) dialogView.findViewById(R.id.cta_accept);
                CardView cancelCardView = (CardView) dialogView.findViewById(R.id.cta_cancel);
                ImageView cancelImage = (ImageView) dialogView.findViewById(R.id.cancel);

                // Delete entry
                acceptCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Call Delete API
                        GalleryGridActivity.deleteImage(uploadImage.getImageId());

                        dialog.dismiss();
                    }
                });


                // Cancel
                cancelCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });

                cancelImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });
                return true;
            }
        });
    }


    /**
     * Function to handle click event on show album
     * @param view
     */
    public void handleClick(View view) {

        final Context context = view.getContext();

        ImageView gridGalleryImageView = (ImageView) view.findViewById(R.id.image);
        String url = gridGalleryImageView.getTag().toString();


        /**
         * Check Calling Activity
         */
        if(GalleryGridActivity.via == StaticConstants.CHAT_ADAPTER) {

            /**
             * Case - Chat
             */
            Toolbar toolbar = (Toolbar) view.getRootView().findViewById(R.id.toolbar);
            String title = toolbar.getTitle().toString();

            Intent intent = new Intent();
            intent.putExtra("selectedTitle", title);
            intent.putExtra("selectedUrl", url);
            GalleryGridActivity.getInstance().setResult(Activity.RESULT_OK, intent);
            GalleryGridActivity.getInstance().finish();

        } else {

            /**
             * Case - Gallery
             */
            Intent intent = new Intent(context, GalleryDetailActivity.class);
            intent.putExtra(StringConstants.KEY_ALBUM_ID, GalleryGridActivity.albumId);
            intent.putExtra(StringConstants.KEY_URL, url);
            context.startActivity(intent);
        }
    }
}
