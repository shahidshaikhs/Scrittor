package com.shahid.nid.Activties;

import android.content.ActivityNotFoundException;
import android.content.Intent;
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
        ImageView instagram = findViewById(R.id.click_instagram);
        ImageView twitter = findViewById(R.id.click_twitter);
        ImageView google = findViewById(R.id.click_google);
        ImageView facebook = findViewById(R.id.click_facebook);
        LinearLayout changelog = findViewById(R.id.changelog);
        TextView telegramClick = findViewById(R.id.textViewTelegram);
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

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGPlus();
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
                onBackPressed();
            }
        });

        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://instagram.com/_u/__shahidshaikh");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/__shahidshaikh")));
                }
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/__shahidshaikh")));
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/shahid.shaikh.3386")));
            }
        });

        telegramClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLinks("https://t.me/scrittor");
            }
        });

        privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutActivity.this, WebViewActivity.class).putExtra("url", "https://shahidshaikh.com/PrivacyPolicy"));
            }
        });
    }


    public void openGPlus() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.com.shahid.nid.GoogleDrive.android.apps.plus",
                    "com.com.shahid.nid.GoogleDrive.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", "+SHAHIDSHAIKH27595");
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/u/0/+SHAHIDSHAIKH27595")));
        }
    }

    public void openLinks(String link) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
    }
}
