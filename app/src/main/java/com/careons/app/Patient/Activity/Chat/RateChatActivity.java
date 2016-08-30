package com.careons.app.Patient.Activity.Chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.careons.app.R;
import com.careons.app.Shared.Utils.Utils;

public class RateChatActivity extends AppCompatActivity {

    private View root;
    private CardView submitCardView;
    private LinearLayout ctaLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_chat);

        // Initialize Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Initialize fields
        ctaLayout = (LinearLayout) findViewById(R.id.cta_layout);
        submitCardView = (CardView) findViewById(R.id.submit);
        submitCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });



        /**
         * Handle Keyboard
         */
        root = findViewById(R.id.root);
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                Utils.toggleCTAKeyboard(root, ctaLayout);
            }
        });
    }
}
