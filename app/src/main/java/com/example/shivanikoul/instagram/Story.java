package com.example.shivanikoul.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class Story extends AppCompatActivity {

    ImageView story;
    private int position = 0;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story);

        Intent intent = getIntent();
        int position = intent.getIntExtra("position",0);
        story = findViewById(R.id.story);
        handler = new Handler() {
            @Override
            public void publish(LogRecord logRecord) {

            }

            @Override
            public void flush() {

            }

            @Override
            public void close() throws SecurityException {

            }
        };

        setImage(position);
        Log.d("In StoryActivity",""+position);

        startHandler(position);
    }

    private void startHandler(final int pos) {
        this.position = pos;



    }

    private void setImage(int position) {

        switch (position) {
            case 0:
                Glide.with(this).load(R.drawable.mark).into(story);

                break;

            case 1:
                Glide.with(this).load(R.drawable.hubspot).into(story);
                break;

            case 2:
                Glide.with(this).load(R.drawable.image).into(story);
                break;

            case 3:
                Glide.with(this).load(R.drawable.match).into(story);
                break;

            case 4:
                Glide.with(this).load(R.drawable.computer).into(story);
                break;
            case 5:
                Glide.with(this).load(R.drawable.weather).into(story);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        handler.removeCallbacksAndMessages(null);

        finish();

    }


}