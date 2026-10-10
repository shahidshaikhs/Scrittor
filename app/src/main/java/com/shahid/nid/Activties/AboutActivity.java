package com.shahid.nid.Activties;

import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shahid.nid.R;
import com.shahid.nid.WebViewActivity;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        LinearLayout profileIcons = findViewById(R.id.profile_icons);
        ImageView backButton = findViewById(R.id.back_button);
        ImageView twitter = findViewById(R.id.click_twitter);
        ImageView facebook = findViewById(R.id.click_facebook);
        LinearLayout changelog = findViewById(R.id.changelog);
        LinearLayout privacy_policy = findViewById(R.id.privacy_policy);
        LinearLayout linearGson = findViewById(R.id.gson);
        LinearLayout circularImageView = findViewById(R.id.circularImage);
        LinearLayout drive = findViewById(R.id.googleDrive);
        TextView junaidClick = findViewById(R.id.junaid);

        junaidClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLinks("https://play.google.com/store/apps/dev?id=6428398715878309895");
            }
        });

        changelog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AboutActivity.this, WebViewActivity.class).putExtra("url", "https://shahidshaikh.com/Changelog/index.html"));
            }
        });

        linearGson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLinks("https://github.com/google/gson");
            }
        });

        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLinks("https://github.com/hdodenhof/CircleImageView");
            }
        });

        drive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLinks("https://developers.google.com/drive/");
            }
        });

        profileIcons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLinks("https://www.flaticon.com/authors/monkik");
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLinks("https://x.com/letshahid");
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/shahid.shaikh.3386")));
            }
        });

        privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLinks("https://www.shahidshaikh.com/scrittor-privacy-policy");
            }
        });
    }

    public void openLinks(String link) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
        } catch (ActivityNotFoundException exception) {
            android.widget.Toast.makeText(this, R.string.no_browser_available, android.widget.Toast.LENGTH_SHORT).show();
        }
    }
}
