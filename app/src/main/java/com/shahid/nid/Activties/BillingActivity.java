package com.shahid.nid.Activties;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.shahid.nid.R;



public class BillingActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    private BillingProcessor bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefsTheme = getSharedPreferences(getResources().getString(R.string.MY_PREFS_THEME), MODE_PRIVATE);
        String theme = prefsTheme.getString("theme", "not_defined");
        if(theme.equals("dark")){
            getTheme().applyStyle(R.style.OverlayPrimaryColorDark, true);
        }else if(theme.equals("light")){
            getTheme().applyStyle(R.style.OverlayPrimaryColorLight, true);
        }else if(theme.equals("amoled")){
            getTheme().applyStyle(R.style.OverlayPrimaryColorAmoled, true);
        }else{
            getTheme().applyStyle(R.style.OverlayPrimaryColorDark, true);
        }

        setContentView(R.layout.activity_billing);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        LinearLayout DONATE_COOKIES = findViewById(R.id.donate_cookies);
        LinearLayout DONATE_PEPSI = findViewById(R.id.donate_pepsi);
        LinearLayout DONATE_COFFEE = findViewById(R.id.donate_coffee);
        LinearLayout DONATE_BURGER = findViewById(R.id.donate_burger);
        LinearLayout DONATE_PIZZA = findViewById(R.id.donate_pizza);
        LinearLayout DONATE_MEAL = findViewById(R.id.donate_meal);
        LinearLayout DONATE_SHIRT = findViewById(R.id.donate_shirt);
        LinearLayout DONATE_WATCH = findViewById(R.id.donate_watch);
        TextView DONATE_SYRIA = findViewById(R.id.donate_syria);
        ImageView backButton = findViewById(R.id.back_button);
        ImageView messageButton = findViewById(R.id.message_button);

        bp = new BillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl7Y9hSHFIJDGmiS7MP06+hUGY86jLdJTEdRZvtrj5VzCN8lFe5g+tnKINvJyKTKXXoWCr0aVUsIOH61/cPGUYg0MZqkhUEIgl5fI11QjnRALXv1WGTVJiTpJY2HvwMiqYyrVDXUr7zP4aP9HLg5kLnqWBHBC4UuDsimvRSs9sFwqOP3q7FtD1mq97ocxo/lgqpLRlJaSNxvCEF9Be/dgG3CDZGuFjJVJ0tAEy6LF1Kb4biWKVc5zVgnBpA3//1nrjEfi/axnL1/yY2+n6DDiHPt9jc48tdLRmAgi7glXUW9ro3X3wHnK+3a5fmvlOEr1pVAeQYZSyFExcgyxt36xvQIDAQAB", this);


        DONATE_COOKIES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bp.purchase(BillingActivity.this, "cookies");
            }
        });

        DONATE_PEPSI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bp.purchase(BillingActivity.this, "pepsi");
            }
        });
        DONATE_COFFEE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bp.purchase(BillingActivity.this, "coffee");
            }
        });
        DONATE_BURGER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bp.purchase(BillingActivity.this, "burger");
            }
        });
        DONATE_PIZZA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bp.purchase(BillingActivity.this, "pizza");
            }
        });
        DONATE_MEAL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bp.purchase(BillingActivity.this, "meal");
            }
        });
        DONATE_SHIRT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bp.purchase(BillingActivity.this, "shirts");
            }
        });
        DONATE_WATCH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bp.purchase(BillingActivity.this, "watch");
            }
        });

        DONATE_SYRIA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.savethechildren.org/site/c.8rKLIXMGIpI4E/b.7998857/k.D075/Syria.htm"));
                startActivity(browserIntent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog billingDialog = new Dialog(BillingActivity.this);
                billingDialog.setContentView(R.layout.dialog_basic);
                billingDialog.setTitle(null);

                billingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                billingDialog.show();

                TextView agreeView = billingDialog.findViewById(R.id.agree);
                TextView title = billingDialog.findViewById(R.id.dialogHeadText);
                TextView content = billingDialog.findViewById(R.id.dialogSubText);

                agreeView.setText("COOL");
                title.setText("Why do I need money?");
                content.setText("I\'ve been working on this app for months. I have been listening to users and adding features as requested. The app is free to download and has no ads as well. I hate ads, they destroy the whole user experience. However, I need to make something out of this. If you like my work, donate whatever you like. I would really appreciate your support. Thank you :)");

                agreeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        billingDialog.dismiss();
                    }
                });

            }
        });
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        Dialog thankyouDialog = new Dialog(BillingActivity.this);
        thankyouDialog.setContentView(R.layout.dialog_basic);
        thankyouDialog.setTitle(null);

        thankyouDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        thankyouDialog.show();

        TextView agreeView = thankyouDialog.findViewById(R.id.agree);

        agreeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thankyouDialog.dismiss();
            }
        });
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
