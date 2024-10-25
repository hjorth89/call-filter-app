package com.ahdevelopment.callhandlingapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private BillingManager billingManager;
    private SharedPreferences preferences;
    private Switch subscriptionSwitch;

    private static final String SERVER_URL = "https://your-server.com/setPreferences"; // Replace with your server URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize billing manager and preferences
        billingManager = new BillingManager(this);
        preferences = getSharedPreferences("app_prefs", MODE_PRIVATE);

        subscriptionSwitch = findViewById(R.id.subscriptionSwitch);
        subscriptionSwitch.setChecked(billingManager.hasSubscription());

        // Check subscription and allow access to app features
        if (!billingManager.hasSubscription()) {
            subscriptionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    billingManager.startSubscriptionPurchase();
                } else {
                    Toast.makeText(this, "Please subscribe to access the app features.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Setup Call Forwarding button
        Button setupForwardingButton = findViewById(R.id.setupForwardingButton);
        setupForwardingButton.setOnClickListener(v -> openForwardingInstructions());

        // Remove Call Forwarding button
        Button removeForwardingInstructions = findViewById(R.id.removeForwardingInstructions);
        removeForwardingInstructions.setOnClickListener(v -> removeForwardingInstructions());

        // View Call Summary button
        Button viewSummaryButton = findViewById(R.id.viewSummaryButton);
        viewSummaryButton.setOnClickListener(v -> {
            if (billingManager.hasSubscription()) {
                startActivity(new Intent(MainActivity.this, SummaryActivity.class));
            } else {
                Toast.makeText(this, "Subscription required.", Toast.LENGTH_SHORT).show();
            }
        });

        // Open Settings button
        Button settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));

        // Send user preferences to the server
        sendUserPreferencesToServer();
    }

    private void openForwardingInstructions() {
        String twilioNumber = "+4512345678"; // Replace with your Twilio number
        
        // Options for call forwarding
        String[] forwardingOptions = {
            "Forward All Calls",
            "Forward Unanswered Calls Only",
            "Forward When Busy"
        };
        
        // Create an AlertDialog to select forwarding type
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Call Forwarding Type")
                .setItems(forwardingOptions, (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    switch (which) {
                        case 0:
                            intent.setData(Uri.parse("tel:**21*" + twilioNumber + Uri.encode("#")));
                            break;
                        case 1:
                            intent.setData(Uri.parse("tel:**61*" + twilioNumber + Uri.encode("#")));
                            break;
                        case 2:
                            intent.setData(Uri.parse("tel:**67*" + twilioNumber + Uri.encode("#")));
                            break;
                    }
                    startActivity(intent);
                });
        builder.show();
    }

    private void removeForwardingInstructions() {
        // Options for removing call forwarding
        String[] removalOptions = {
            "Remove All Call Forwarding",
            "Remove Forwarding for Unanswered Calls",
            "Remove Forwarding for Busy Calls"
        };
        
        // Create an AlertDialog to select removal type
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Forwarding Removal Type")
                .setItems(removalOptions, (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    switch (which) {
                        case 0:
                            intent.setData(Uri.parse("tel:" + Uri.encode("##21#")));
                            break;
                        case 1:
                            intent.setData(Uri.parse("tel:" + Uri.encode("##61#")));
                            break;
                        case 2:
                            intent.setData(Uri.parse("tel:" + Uri.encode("##67#")));
                            break;
                    }
                    startActivity(intent);
                });
        builder.show();
    }

    // Function to send user preferences (business keywords and language) to the server
    private void sendUserPreferencesToServer() {
        // Retrieve saved preferences
        String businessKeywords = preferences.getString("business_keywords", "");
        String language = preferences.getString("language", "en");

        // Prepare POST request with OkHttp
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("business_keywords", businessKeywords)
                .add("language", language)
                .build();

        Request request = new Request.Builder()
                .url(SERVER_URL)
                .post(formBody)
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure
                Log.e("MainActivity", "Failed to send preferences", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("MainActivity", "Preferences sent successfully");
                } else {
                    Log.e("MainActivity", "Failed to send preferences: " + response.message());
                }
            }
        });
    }
}
