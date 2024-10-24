package com.ahdevelopment.callhandlingapp;

import android.app.Activity;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;

import java.util.List;

public class BillingManager implements PurchasesUpdatedListener {

    private BillingClient billingClient;
    private Activity activity;

    public interface SubscriptionStatusListener {
        void onSubscriptionStatusChecked(boolean isSubscribed);
    }

    public BillingManager(Activity activity) {
        this.activity = activity;
        billingClient = BillingClient.newBuilder(activity)
                .setListener(this)
                .enablePendingPurchases()
                .build();
        startConnection();
    }

    private void startConnection() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(int billingResult) {
                if (billingResult == BillingClient.BillingResponseCode.OK) {
                    checkSubscriptionStatus();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Retry connection
                startConnection();
            }
        });
    }

    public void checkSubscriptionStatus(SubscriptionStatusListener listener) {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS, (billingResult, purchases) -> {
            boolean isSubscribed = false;
            for (Purchase purchase : purchases) {
                if (purchase.getSku().equals("premium_subscription")) {
                    isSubscribed = purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED;
                    break;
                }
            }
            listener.onSubscriptionStatusChecked(isSubscribed);
        });
    }

    @Override
    public void onPurchasesUpdated(int billingResult, List<Purchase> purchases) {
        // Handle purchase updates if needed
    }
}
