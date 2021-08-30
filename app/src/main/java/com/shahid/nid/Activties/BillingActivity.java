package com.shahid.nid.Activties;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.material.snackbar.Snackbar;
import com.shahid.nid.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class BillingActivity extends BaseActivity implements BillingClientStateListener, PurchasesUpdatedListener {

    // IAB v4
    private BillingClient billingClient;
    private static final String SKU_COOKIES = "cookies";
    private static final String SKU_PEPSI = "pepsi";
    private static final String SKU_COFFEE = "coffee";
    private static final String SKU_BURGER = "burger";
    private static final String SKU_PIZZA = "pizza";
    private static final String SKU_MEAL = "meal";
    private static final String SKU_SHIRTS = "shirts";
    private static final String SKU_WATCH = "watch";

    private static final ArrayList<String> skuNameList = new ArrayList<>(Arrays.asList("cookies", "pepsi", "coffee", "burger", "pizza", "meal", "shirts", "watch"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

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

        billingClient = BillingClient.newBuilder(this).setListener(this).enablePendingPurchases().build();
        billingClient.startConnection(this);


        DONATE_COOKIES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                purchasePack(productsList.get(SKU_COOKIES));
            }
        });

        DONATE_PEPSI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                purchasePack(productsList.get(SKU_PEPSI));
            }
        });
        DONATE_COFFEE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                purchasePack(productsList.get(SKU_COFFEE));
            }
        });
        DONATE_BURGER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                purchasePack(productsList.get(SKU_BURGER));
            }
        });
        DONATE_PIZZA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                purchasePack(productsList.get(SKU_PIZZA));
            }
        });
        DONATE_MEAL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                purchasePack(productsList.get(SKU_MEAL));
            }
        });
        DONATE_SHIRT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                purchasePack(productsList.get(SKU_SHIRTS));
            }
        });
        DONATE_WATCH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                purchasePack(productsList.get(SKU_WATCH));
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
    public void onBillingServiceDisconnected() {

    }

    @Override
    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            // The BillingClient is ready. You can query purchases here.
            getProductList();

        }
    }

    private HashMap<String, SkuDetails> productsList = new HashMap<>();
    private void getProductList() {
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuNameList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        // Process the result.
                        for (SkuDetails sku : skuDetailsList) {
                            productsList.put(skuNameList.get(skuNameList.indexOf(sku.getSku())), sku);
                        }

                    }
                });
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
            for (Purchase purchase : list) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Toast.makeText(this, "Purchase Cancelled!", Toast.LENGTH_SHORT).show();
        } else {
            // Handle any other error codes.
            Snackbar
                    .make(findViewById(android.R.id.content), "An unexpected error occurred. Please try again later.", Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * Updates the purchase in app class ionstance too
     *
     * @param purchase
     */
    private void handlePurchase(Purchase purchase) {
        billingClient.acknowledgePurchase(AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build(), new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                billingClient.consumeAsync(ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build(), new ConsumeResponseListener() {
                    @Override
                    public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showThankYouDialog("Purchase Complete!", "Thank you for your supporting me in further development of Orphic!");
                            }
                        });
                    }
                });
            }
        });
    }

    private void purchasePack(SkuDetails sku) {
        // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(sku)
                .build();
        int responseCode = billingClient.launchBillingFlow(this, billingFlowParams).getResponseCode();
    }

    // We're being destroyed. It's important to dispose of the helper here!
    @Override
    public void onDestroy() {
        billingClient.endConnection();
        super.onDestroy();
    }


    public void showThankYouDialog(@NonNull String title, String body) {
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

}
