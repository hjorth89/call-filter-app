
# AI Call Assistant Android App

This is an Android app that integrates with Twilio and OpenAI to forward calls to an AI assistant and provide real-time responses in either Danish or English. The app includes subscription-based access and allows the user to configure business keywords, manage call forwarding, and receive call summaries.

## Features

- **Subscription Management**: Only accessible with an active subscription.
- **Call Forwarding Setup**: Guides the user to set up call forwarding to a Twilio number.
- **Settings**: Allows users to set preferred language (English or Danish) and define business-related keywords.
- **Call Summary Notifications**: Receives a notification after each AI-handled call with a summary.

---

## Project Structure

```
app/
 ├── java/com/ahdevelopment/callhandlingapp/
 │   ├── MainActivity.java               # Main screen with subscription check and forwarding setup
 │   ├── SummaryActivity.java            # Screen to view call interaction summaries
 │   ├── SettingsActivity.java           # Screen to configure language and keywords
 │   ├── BillingManager.java             # Manages subscription and billing
 │   └── NotificationReceiver.java       # Handles notifications from the server
 ├── res/
 │   ├── layout/
 │   │   ├── activity_main.xml           # Layout for the main activity
 │   │   ├── activity_summary.xml        # Layout for the summary activity
 │   │   └── activity_settings.xml       # Layout for the settings activity
 │   ├── values/strings.xml              # String resources
 └── AndroidManifest.xml
```

---

## Prerequisites

- **Twilio Account**: Set up a Twilio account and phone number for call forwarding.
- **OpenAI API Key**: Obtain an OpenAI API key to enable AI-driven responses.
- **Google Play Console**: Access to Google Play Console for subscription setup (if distributing via Google Play).

---

## How to Build and Run the App

### 1. Clone the Repository

Clone this repository to your local machine:

```bash
git clone https://github.com/your-repo/ai-call-assistant.git
cd ai-call-assistant
```

### 2. Configure the API and Twilio Settings

1. **Set the Twilio Number in `MainActivity.java`**:
   Replace `"YOUR_TWILIO_NUMBER"` with your actual Twilio number in the `openForwardingInstructions()` method.

2. **OpenAI API Key**:
   - The API key should be set in your server environment that the app communicates with.

### 3. Add In-App Billing for Subscription

1. **Configure Google Play Billing**:
   - Set up the subscription in Google Play Console.
   - Update the SKU and subscription check logic in `BillingManager.java` with the relevant product ID.

### 4. Build the App

1. **Open the Project in Android Studio**:
   - Launch Android Studio.
   - Open the project folder.

2. **Sync Gradle**:
   - Sync Gradle to ensure all dependencies are installed.

3. **Build the APK**:
   - Go to **Build > Build Bundle(s)/APK(s) > Build APK(s)**.
   - Install the APK on an Android device for testing.

### 5. Run the App

- Open the app on the Android device.
- Configure the settings (language and keywords) in **Settings**.
- Set up call forwarding to the Twilio number.
- Ensure you have an active subscription to access the app features.

---

## Additional Notes

- **Call Forwarding**: This app requires users to manually set up call forwarding to a Twilio number.
- **Subscription Requirement**: Access is restricted without an active subscription.
- **Business Keywords and Language**: These are configurable by the user in the app's settings and used to customize the AI response.

## License

This project is licensed under the MIT License.
