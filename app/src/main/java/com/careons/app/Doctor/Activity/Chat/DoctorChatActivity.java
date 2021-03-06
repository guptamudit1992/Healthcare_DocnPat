package com.careons.app.Doctor.Activity.Chat;


import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.careons.app.Patient.Activity.Chat.UploadChatConfirmActivity;
import com.careons.app.Patient.Adapters.Adapter;
import com.careons.app.Patient.Commons.BuildProperties;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Chat.Chat;
import com.careons.app.R;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.databinding.CardChatDoctorBinding;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

public class DoctorChatActivity extends AppCompatActivity implements View.OnClickListener {

    private Firebase firebaseReference;
    private RecyclerView doctorMessageRecyclerView;
    private ImageView doctorSendChatTextView, doctorChatUploadDocImageView;
    private EditText doctorChatBoxEditText;

    private static Adapter<Chat, CardChatDoctorBinding> doctorCardChatBindingAdapter;
    private ArrayList<Chat> doctorMessageList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_doctor);

        /**
         * Firebase Setup
         */
        Firebase.setAndroidContext(this);
        //Firebase.getDefaultConfig().setPersistenceEnabled(true);
        firebaseReference = new Firebase(BuildProperties.firebaseURL);


        /**
         * Initialize Chat Box
         */
        doctorMessageRecyclerView = (RecyclerView) findViewById(R.id.doc_chat_recyclerview);
        doctorChatBoxEditText = (EditText) findViewById(R.id.doc_chat_text);
        doctorChatUploadDocImageView = (ImageView) findViewById(R.id.chat_upload_doc);
        doctorSendChatTextView = (ImageView) findViewById(R.id.doc_send_button);


        /**
         * Attach click listener
         */
        doctorChatUploadDocImageView.setOnClickListener(this);
        doctorSendChatTextView.setOnClickListener(this);


        /**
         * Patient and Doctor Chat Listener Attach
         */
        readDoctorMessage();


        /**
         * Set adapter
         */
        doctorCardChatBindingAdapter =
                new Adapter<>(doctorMessageList, R.layout.card_chat_doctor,
                        StaticConstants.CHAT_DOCTOR_ADAPTER);

        doctorMessageRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        doctorMessageRecyclerView.setAdapter(doctorCardChatBindingAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // I do not want this...
                // Home as up button is to navigate to Home-Activity not previous acitivity
                super.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.doc_send_button:
                sendDoctorMessage();
                break;

            case R.id.chat_upload_doc:
                selectImage();
                break;
        }
    }








    /**
     * Function to send message to Firebase
     */
    public void sendDoctorMessage() {

        if(!doctorChatBoxEditText.getText().toString().isEmpty()) {
            Chat chat = new Chat(
                    "2",
                    doctorChatBoxEditText.getText().toString(),
                    "",
                    "text",
                    DateTimeUtils.getChatTime(getApplicationContext()),
                    true
            );

            /**
             * Upload Message to Firebase - Publish
             */
            firebaseReference.child("12").child("messages").push().setValue(chat);
            doctorChatBoxEditText.setText(null);

        } else {

            Toast.makeText(this, "Blank Message Cannot be Send", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Function to read message
     */
    public void readDoctorMessage() {

        /**
         * Read Data from Firebase - Subscriber
         */
        firebaseReference.child("12").child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if(dataSnapshot.getValue() != null) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    /**
                     * Check for sender
                     */
                    if(chat.getId().equalsIgnoreCase("2")) {
                        chat.setSendSelf(true);

                    } else {
                        //sendMessageNotification(chat);
                        chat.setSendSelf(false);
                    }

                    doctorMessageList.add(chat);
                    doctorCardChatBindingAdapter.notifyDataSetChanged();

                    /**
                     * Scroll Recycler View to position
                     */
                    doctorMessageRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            //call smooth scroll
                            doctorMessageRecyclerView.smoothScrollToPosition(doctorCardChatBindingAdapter.getItemCount());
                        }
                    });
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }








    /**
     * Function to select upload method
     */
    private void selectImage() {

        final CharSequence[] items = {getString(R.string.choose_gallery), getString(R.string.camera)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                switch (item) {

                    case 0:
                        // Upload from Gallery
                        selectImageViaGallery();
                        break;


                    case 1:
                        // Upload from Camera
                        selectImageViaCamera();
                        break;
                }
            }
        });
        builder.show();
    }


    /**
     * Function to uplaod image via gallery
     */
    public void selectImageViaGallery() {

        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), StaticConstants.PICK_IMAGE_REQUEST);
    }


    /**
     * Function to uplaod image via camera
     */
    public void selectImageViaCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, StaticConstants.REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * Check if resultCode is Success
         */
        if (resultCode == RESULT_OK) {
            if (requestCode == StaticConstants.PICK_IMAGE_REQUEST
                    || requestCode == StaticConstants.REQUEST_IMAGE_CAPTURE) {

                /**
                 * Navigate to confirm upload activity
                 */
                Intent intent = new Intent(this, UploadChatConfirmActivity.class);
                intent.putExtra(StringConstants.KEY_DATA, data);
                intent.putExtra(StringConstants.KEY_REQUEST_CODE, requestCode);
                startActivityForResult(intent, StaticConstants.SEND_CHAT_IMAGE_REQUEST);

            } else if(requestCode == StaticConstants.SEND_CHAT_IMAGE_REQUEST) {

                /**
                 * Send to Chat
                 */
                Chat chat = new Chat(
                        "2",
                        data.getStringExtra("selectedTitle"),
                        data.getStringExtra("selectedUrl"),
                        "image",
                        DateTimeUtils.getChatTime(getApplicationContext()),
                        true
                );

                /**
                 * Upload Message to Firebase - Publish
                 */
                firebaseReference.child("12").child("messages").push().setValue(chat);
                doctorChatBoxEditText.setText(null);

            }

        } else {

            // TODO: Comment in case upload error
        }
    }







    /**
     * Function to send new message notification
     * @param chat
     */
    public void sendMessageNotification(Chat chat) {


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        // Set Notification Priority
        builder.setPriority(StaticConstants.NOTIFICATION_PRIORITY);
        builder.setVisibility(StaticConstants.VISIBILITY_PUBLIC);
        builder.setSmallIcon(R.drawable.ic_prof_phone);

        // Notification Sound
        builder.setSound(RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        // Set the notification to auto-cancel.
        // This means that the notification will disappear after the user taps it,
        // rather than remaining until it's explicitly dismissed.
        builder.setAutoCancel(true);

        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_prof_phone));
        builder.setContentTitle(getString(R.string.title_activity_home));
        builder.setContentText(chat.getMessage());
        builder.setSubText(getString(R.string.chat_notification_hint));

        // Send the notification
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(StaticConstants.NOTIFICATION_ID, builder.build());
    }

}
