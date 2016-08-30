package com.careons.app.Patient.Activity.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.R;
import com.squareup.picasso.Picasso;

public class ViewChatImageActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_chat_image);

        /**
         * Fetch Data from intent
         */
        Intent intent = getIntent();
        String title = intent.getStringExtra(StringConstants.KEY_TITLE);
        String imagePath = intent.getStringExtra(StringConstants.KEY_URL);

        /**
         * Toolbar
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /**
         * Initialize
         */
        ImageView chatImageView = (ImageView) findViewById(R.id.chat_image);
        Picasso.with(this)
                .load(imagePath)
                // .placeholder(R.drawable.dp_man)
                .into(chatImageView);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // I do not want this...
                // Home as up button is to navigate to Home-Activity not previous acitivity
                super.onBackPressed();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
