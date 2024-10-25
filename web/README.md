
# AI-Powered Call Assistant API

This is a Flask-based API that integrates Twilio and OpenAI GPT to provide AI-driven responses to forwarded phone calls. 
It supports both English and Danish responses and allows customization with user-defined business keywords.

## Prerequisites

- **Twilio Account**: Sign up at [Twilio](https://www.twilio.com/try-twilio) and obtain a phone number.
- **Google Cloud Account**: Set up a project and enable Google Cloud Run.
- **OpenAI API Key**: Sign up at [OpenAI](https://platform.openai.com/) to obtain an API key.

## Files

- **Dockerfile**: Configures the Docker container for deployment.
- **requirements.txt**: Specifies Python dependencies.
- **main.py**: The Flask server code, configured to interact with Twilio and OpenAI.

---

## Step-by-Step Deployment on Google Cloud

### 1. Build and Deploy the Container to Google Cloud Run

- **Authenticate with Google Cloud**:
  ```bash
  gcloud auth login
  ```

- **Set Google Cloud Project**:
  ```bash
  gcloud config set project [YOUR_PROJECT_ID]
  ```

- **Build and Push to Google Artifact Registry** (you’ll need to create a repository first):
  ```bash
  gcloud builds submit --tag gcr.io/[YOUR_PROJECT_ID]/ai-call-assistant
  ```

- **Deploy to Cloud Run**:
  ```bash
  gcloud run deploy ai-call-assistant --image gcr.io/[YOUR_PROJECT_ID]/ai-call-assistant --platform managed --allow-unauthenticated --region [YOUR_REGION]
  ```

Replace `[YOUR_PROJECT_ID]` and `[YOUR_REGION]` with your actual Google Cloud project ID and preferred region.

### 2. Set Environment Variables in Google Cloud Run

- Go to the **Google Cloud Console**.
- Navigate to **Cloud Run** > **Your Service** > **Edit & Deploy New Revision**.
- Under **Environment Variables**, set:
  - `OPENAI_API_KEY`: Your OpenAI API key.

### 3. Configure Twilio Webhooks

- Copy the **Cloud Run service URL** from the deployment output or Google Cloud Console.
- In the **Twilio Console**:
  - Navigate to **Phone Numbers** > **Manage** > **Active Numbers** and select your Twilio number.
  - Set the **A Call Comes In** webhook to `https://your-cloud-run-service-url/voice`.

---

## Testing and Verification

1. **Set Up Call Forwarding**: Configure call forwarding from your personal phone to the Twilio number.
2. **Make a Test Call**: Call your personal number to trigger the forwarding, and listen for AI interaction based on your setup.

---

## Customization

### Business Keywords and Language

The API uses placeholders for the user's **preferred language** (English or Danish) and **business keywords**. 
You can adjust these in the `user_settings` dictionary in `main.py` or update the app to dynamically pull these from a user profile.

---

## Additional Notes

- Ensure **Twilio's webhooks** are set correctly to point to the `/voice` endpoint.
- For security, store sensitive environment variables like `OPENAI_API_KEY` securely in Google Cloud Run's settings.

## License

This project is licensed under the MIT License.