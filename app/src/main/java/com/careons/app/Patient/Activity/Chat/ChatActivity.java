package com.careons.app.Patient.Activity.Chat;

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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.careons.app.Patient.Activity.Main.UploadedDocuments.GalleryActivity;
import com.careons.app.Patient.Adapters.Adapter;
import com.careons.app.Patient.Commons.BuildProperties;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Chat.Chat;
import com.careons.app.Patient.Database.Models.Chat.ConsultationRecord;
import com.careons.app.R;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.databinding.CardChatBinding;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private Firebase firebaseReference;
    private RecyclerView messageRecyclerView;
    private ImageView sendChatTextView, chatUploadDocImageView, chatEndImageView;
    private EditText chatBoxEditText;

    private static Adapter<Chat, CardChatBinding> cardChatBindingAdapter;
    private ArrayList<Chat> patientMessageList = new ArrayList<>();
    private ConsultationRecord consultationRecord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        /**
         * Firebase Setup
         */
        Firebase.setAndroidContext(this);
        //Firebase.getDefaultConfig().setPersistenceEnabled(true);
        firebaseReference = new Firebase(BuildProperties.firebaseURL);


        /**
         * Initialize Chat Box
         */
        messageRecyclerView = (RecyclerView) findViewById(R.id.chat_recyclerview);
        chatBoxEditText = (EditText) findViewById(R.id.chat_text);
        chatUploadDocImageView = (ImageView) findViewById(R.id.chat_upload_doc);
        chatEndImageView = (ImageView) findViewById(R.id.chat_end);
        sendChatTextView = (ImageView) findViewById(R.id.send_button);


        /**
         * Attach click listener
         */
        chatUploadDocImageView.setOnClickListener(this);
        chatEndImageView.setOnClickListener(this);
        sendChatTextView.setOnClickListener(this);


        /**
         * Check source
         */
        Intent intent = getIntent();
        String source = intent.getStringExtra("source");
        if(source.equalsIgnoreCase("live")) {
            // Fetch parcelable record from intent
            Bundle data = getIntent().getExtras();
            consultationRecord = data.getParcelable("consultationRecord");

            /**
             * Send Initial Message to Firebase
             */
            sendPatientInitialMessage();
        }


        /**
         * Patient Chat Listener Attach
         */
        readPatientMessage();



        /**
         * Set adapter
         */
        cardChatBindingAdapter =
                new Adapter<>(patientMessageList, R.layout.card_chat,
                        StaticConstants.CHAT_ADAPTER);

        messageRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        messageRecyclerView.setAdapter(cardChatBindingAdapter);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.send_button:
                sendPatientMessage();
                break;

            case R.id.chat_upload_doc:
                selectImage();
                break;

            case R.id.chat_end:
                endChat();
                break;
        }
    }




    /**
     * Function to send initial message to Firebase
     */
    public void sendPatientInitialMessage() {

        // Construct Description
        String description = "";
        if(!consultationRecord.getDescription().isEmpty()) {

            description = ", \nDescription: "
                    .concat(consultationRecord.getDescription());
        }


        // Construct Symptoms string
        String symptoms = "";
        if(consultationRecord.getSymptoms().size() > 0) {
            symptoms = consultationRecord.getSymptoms().get(0);
            for (int i = 1; i < consultationRecord.getSymptoms().size(); i++) {
                symptoms = symptoms
                        .concat(", ")
                        .concat(consultationRecord.getSymptoms().get(i));
            }

            symptoms = ", \nSymptoms: "
                    .concat(symptoms);
        }



        // Construct initial message
        String initMessage =
                "Name: "
                        .concat(consultationRecord.getName())
                        .concat(", \nAge: ")
                        .concat(consultationRecord.getAge())
                        .concat(", \nGender: ")
                        .concat(consultationRecord.getGender())
                        .concat(symptoms)
                        .concat(description);





        Chat chat = new Chat(
                "1",
                initMessage,
                "",
                "text",
                DateTimeUtils.getChatTime(getApplicationContext()),
                true
        );

        /**
         * Upload Message to Firebase - Publish
         */
        firebaseReference.child("12").child("messages").push().setValue(chat);
        chatBoxEditText.setText(null);
    }


    /**
     * Function to send message to Firebase
     */
    public void sendPatientMessage() {

        if(!chatBoxEditText.getText().toString().isEmpty()) {
            Chat chat = new Chat(
                    "1",
                    chatBoxEditText.getText().toString(),
                    "",
                    "text",
                    DateTimeUtils.getChatTime(getApplicationContext()),
                    true
            );

            /**
             * Upload Message to Firebase - Publish
             */
            firebaseReference.child("12").child("messages").push().setValue(chat);
            chatBoxEditText.setText(null);

        } else {

            // Cannot send blank chat
            Toast.makeText(getApplicationContext(), "Cannot send blank message", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Function to read message
     */
    public void readPatientMessage() {

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
                    if(chat.getId().equalsIgnoreCase("1")) {
                        chat.setSendSelf(true);
                    } else {

                        //sendMessageNotification(chat);
                        chat.setSendSelf(false);
                    }

                    patientMessageList.add(chat);
                    cardChatBindingAdapter.notifyDataSetChanged();

                    /**
                     * Scroll Recycler View to position
                     */
                    messageRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            //call smooth scroll
                            messageRecyclerView.smoothScrollToPosition(cardChatBindingAdapter.getItemCount());
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

        final CharSequence[] items = {getString(R.string.choose_upload_doc), getString(R.string.choose_gallery), getString(R.string.camera)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                switch (item) {

                    case 0:
                        // Upload from Previously uploaded documents
                        Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
                        intent.putExtra(StringConstants.KEY_VIA, StaticConstants.CHAT_ADAPTER);
                        intent.putExtra(StringConstants.KEY_CATEGORY, StringConstants.KEY_BILLS);
                        startActivityForResult(intent, StaticConstants.SEND_CHAT_IMAGE_REQUEST);
                        break;

                    case 1:
                        // Upload from Gallery
                        selectImageViaGallery();
                        break;


                    case 2:
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
                        "1",
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
                chatBoxEditText.setText(null);

            }

        } else {

            // TODO: Comment in case upload error
        }
    }


    /**
     * Function to end chat
     */
    public void endChat() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to end this session with your doctor?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // End Session
                        Intent intent = new Intent(ChatActivity.this, RateChatActivity.class);
                        startActivity(intent);

                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                    }
                });

        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
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
