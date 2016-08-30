package com.careons.app.Patient.Database.Handlers.Upload;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.careons.app.Patient.Activity.Main.UploadedDocuments.GalleryActivity;
import com.careons.app.Patient.Activity.Main.UploadedDocuments.GalleryGridActivity;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Upload.Album;
import com.careons.app.R;

public class AlbumHandler {

    private Context context;
    private Album album;
    private ViewDataBinding dataCard;

    private CardView cardView;


    // Parameterized Constructor
    public AlbumHandler(final Album album, final ViewDataBinding dataCard) {

        this.album = album;
        this.dataCard = dataCard;

        // Get context
        context = dataCard.getRoot().getContext();

        // Initialize layouts
        cardView = (CardView) dataCard.getRoot().findViewById(R.id.gallery_card);


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
                        GalleryActivity.deleteAlbum(album.getAlbumId());

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
    public void showAlbum(View view) {

        final Context context = view.getContext();

        TextView albumIdTextView = (TextView) view.findViewById(R.id.album_id);
        final String albumId = albumIdTextView.getText().toString();

        /**
         * Navigate to the detail page
         */
        Intent intent = new Intent(context, GalleryGridActivity.class);
        intent.putExtra(StringConstants.KEY_ALBUM_ID, albumId);
        intent.putExtra(StringConstants.KEY_VIA, GalleryActivity.via);
        GalleryActivity.galleryActivity.startActivityForResult(intent, StaticConstants.SEND_CHAT_IMAGE_REQUEST);
    }
}
