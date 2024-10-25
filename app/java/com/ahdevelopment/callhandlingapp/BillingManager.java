package com.ahdevelopment.callhandlingapp;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import com.android.billingclient.api.*;

public class BillingManager implements PurchasesUpdatedListener {

    private BillingClient billingClient;
    private Context context;

    public BillingManager(Context context) {
        this.context = context;
        billingClient = BillingClient.newBuilder(context)
                .setListener(this)
                .enablePendingPurchases()
                .build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    checkSubscriptionStatus();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
            }
        });
    }

    public void startSubscriptionPurchase() {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(/* Retrieve and provide your SKU details here */)
                .build();
        billingClient.launchBillingFlow((Activity) context, billingFlowParams);
    }

    public boolean hasSubscription() {
        // Perform check on stored subscription status, assume shared preferences or similar storage
        return /* Retrieve stored subscription status */;
    }

    private void checkSubscriptionStatus() {
        Purchase.PurchasesResult result = billingClient.queryPurchases(BillingClient.SkuType.SUBS);
        for (Purchase purchase : result.getPurchasesList()) {
            if (purchase.getSku().equals("your_subscription_sku")) {
                // Update subscription status as active
            }
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                if (purchase.getSku().equals("your_subscription_sku")) {
                    // Mark subscription as active
                    Toast.makeText(context, "Subscription activated!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(context, "Subscription failed.", Toast.LENGTH_SHORT).show();
        }
    }
}
