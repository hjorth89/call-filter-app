package com.ahdevelopment.callhandlingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            // Check if phone is ringing
            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.d("CallReceiver", "Incoming call from: " + incomingNumber);
                if (incomingNumber != null) {
                    if (!isNumberInContacts(context, incomingNumber)) {
                        Log.d("CallReceiver", "Redirecting call to AI for number: " + incomingNumber);
                        redirectCallToAI(context, incomingNumber);
                        // Trigger AI handling logic here (e.g., Dialogflow integration)
                    }
                }
            }

            // Check if call is answered
            if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
                Log.d("CallReceiver", "Call answered.");
            }

            // Check if call has ended
            if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
                Log.d("CallReceiver", "Call ended.");
            }
        }
    }

    private boolean isNumberInContacts(Context context, String phoneNumber) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                new String[]{phoneNumber}, null);
        boolean isInContacts = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }
        return isInContacts;
    }

    // Example code for triggering Dialogflow for AI handling
    public void redirectCallToAI(Context context, String phoneNumber) {
        // Send phoneNumber to AI and initiate conversation handling
        Log.d("CallReceiver", "Triggering AI assistant for number: " + phoneNumber);

        // Example Dialogflow integration
        // String aiResponse = dialogflowHelper.getAIResponse(phoneNumber);
        // processAIResponse(aiResponse);
    }

}
