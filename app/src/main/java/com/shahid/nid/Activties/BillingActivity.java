package com.shahid.nid.Activties;

import android.app.Dialog;
import com.android.billingclient.api.*;
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

        billingClient = BillingClient.newBuilder(this).setListener(this).enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
                .enableAutoServiceReconnection().build();
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
                getOnBackPressedDispatcher().onBackPressed();
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

    private HashMap<String, ProductDetails> productsList = new HashMap<>();
    private void getProductList() {
        ArrayList<QueryProductDetailsParams.Product> products = new ArrayList<>();
        for (String id : skuNameList) products.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(id).setProductType(BillingClient.ProductType.INAPP).build());
        billingClient.queryProductDetailsAsync(QueryProductDetailsParams.newBuilder().setProductList(products).build(),
                (result, details) -> {
                    if (result.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                        showSnackBar("Products are unavailable. Please try again later."); return;
                    }
                    runOnUiThread(() -> {
                        productsList.clear();
                        for (ProductDetails product : details.getProductDetailsList()) productsList.put(product.getProductId(), product);
                    });
                });
        billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(),
                (result, purchases) -> {
                    if (result.getResponseCode() == BillingClient.BillingResponseCode.OK)
                        for (Purchase purchase : purchases) handlePurchase(purchase);
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
    private final java.util.Set<String> processingTokens = java.util.Collections.synchronizedSet(new java.util.HashSet<>());

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() != Purchase.PurchaseState.PURCHASED) return;
        if (java.util.Collections.disjoint(purchase.getProducts(), skuNameList)) return;
        if (!processingTokens.add(purchase.getPurchaseToken())) return;
        // Consumption also acknowledges a consumable purchase. Never consume pending payments.
        billingClient.consumeAsync(ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build(),
                (result, token) -> {
                    processingTokens.remove(token);
                    if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        runOnUiThread(() -> {
                            if (!isFinishing() && !isDestroyed()) showThankYouDialog("Purchase complete!", "Thank you for supporting Scrittor!");
                        });
                    } else {
                        showSnackBar("Purchase confirmation failed. It will be retried when you reopen this screen.");
                    }
                });
    }

    private void purchasePack(ProductDetails product) {
        if (!billingClient.isReady() || product == null) {
            showSnackBar("Products are still loading or unavailable. Please try again."); return;
        }
        BillingFlowParams.ProductDetailsParams.Builder item = BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(product);
        ProductDetails.OneTimePurchaseOfferDetails offer = product.getOneTimePurchaseOfferDetails();
        if (offer != null && offer.getOfferToken() != null && !offer.getOfferToken().isEmpty()) item.setOfferToken(offer.getOfferToken());
        BillingFlowParams params = BillingFlowParams.newBuilder().setProductDetailsParamsList(java.util.Collections.singletonList(item.build())).build();
        if (billingClient.launchBillingFlow(this, params).getResponseCode() != BillingClient.BillingResponseCode.OK)
            showSnackBar("Unable to start purchase. Please try again later.");
    }

    // We're being destroyed. It's important to dispose of the helper here!
    @Override
    public void onDestroy() {
        if (billingClient != null) billingClient.endConnection();
        super.onDestroy();
    }


    public void showThankYouDialog(@NonNull String title, String body) {
        Dialog thankyouDialog = new Dialog(BillingActivity.this);
        thankyouDialog.setContentView(R.layout.dialog_basic);
        thankyouDialog.setTitle(null);

        thankyouDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        thankyouDialog.show();

        ((TextView) thankyouDialog.findViewById(R.id.dialogHeadText)).setText(title);
        ((TextView) thankyouDialog.findViewById(R.id.dialogSubText)).setText(body);
        TextView agreeView = thankyouDialog.findViewById(R.id.agree);

        agreeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thankyouDialog.dismiss();
            }
        });
    }

}
