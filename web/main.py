from flask import Flask, request, jsonify
from twilio.twiml.voice_response import VoiceResponse
import openai
import os

app = Flask(__name__)

# Set up OpenAI API key
openai.api_key = os.getenv('OPENAI_API_KEY')

# Placeholder for user-specific settings
user_settings = {
    'preferred_language': 'en',  # Default to English; can be 'da' for Danish
    'business_keywords': 'web development, graphic design, e-commerce solutions'
}

# Track ongoing conversation context for dynamic flow
conversation_context = {}
user_settings = {}

@app.route("/setPreferences", methods=['POST'])
def set_preferences():
    """Receive user preferences from the app and store them."""
    caller_number = request.form.get('caller_number', 'Unknown')
    business_keywords = request.form.get('business_keywords', '')
    language = request.form.get('language', 'en')

    # Store preferences temporarily, using caller_number as the key
    user_settings[caller_number] = {
        'business_keywords': business_keywords,
        'preferred_language': language
    }

    return "Preferences updated", 200

@app.route("/voice", methods=['POST'])
def voice():
    """Handle the initial call and start the conversation loop"""
    caller_number = request.form.get('From', 'Unknown caller')
    
    # Retrieve user preferences based on caller number
    preferences = user_settings.get(caller_number, {'preferred_language': 'en', 'business_keywords': ''})
    
    response = VoiceResponse()
    greeting_message = {
        'en': "Hello! You're connected to the AI assistant. How can I help you today?",
        'da': "Hej! Du er forbundet til AI-assistenten. Hvordan kan jeg hjælpe dig i dag?"
    }
    response.say(greeting_message[preferences['preferred_language']])
    response.record(action="/process_voice", maxLength=30, transcribe=True)

    return str(response)


@app.route("/process_voice", methods=['POST'])
def process_voice():
    """Process each segment of the conversation, send to OpenAI, and respond dynamically"""
    transcript = request.form.get('TranscriptionText', '')
    caller_number = request.form.get('From', 'Unknown caller')

    # Check if the conversation context for the caller already exists
    if caller_number not in conversation_context:
        conversation_context[caller_number] = []

    # Add caller's latest input to their conversation history
    conversation_context[caller_number].append(f"Caller: {transcript}")

    # Generate prompt with conversation history and business keywords for context
    prompt = generate_dynamic_prompt(conversation_context[caller_number], user_settings['business_keywords'], user_settings['preferred_language'])

    # Get AI response
    ai_response_text = get_ai_response(prompt, user_settings['preferred_language'])
    conversation_context[caller_number].append(f"AI: {ai_response_text}")

    # Respond to the caller
    response = VoiceResponse()
    response.say(ai_response_text)

    # Define end condition phrases for each language
    end_phrases = {
        'en': ["goodbye", "thanks", "bye", "done"],
        'da': ["farvel", "tak", "hej hej", "færdig"]
    }

    # Continue the conversation loop or send final summary if the caller says a farewell phrase
    if any(phrase in transcript.lower() for phrase in end_phrases[user_settings['preferred_language']]):
        farewell_message = "Thank you for calling. Goodbye!" if user_settings['preferred_language'] == 'en' else "Tak for opkaldet. Farvel!"
        response.say(farewell_message)
        
        # Generate and send a short summary of the conversation
        conversation_summary = create_conversation_summary(conversation_context[caller_number])
        send_notification("Call Summary", conversation_summary)

        # Clear conversation context
        del conversation_context[caller_number]
    else:
        # Continue the conversation loop
        response.record(action="/process_voice", maxLength=30, transcribe=True)


    return str(response)

def generate_dynamic_prompt(history, business_keywords, language):
    """Generate a dynamic AI prompt with conversation history"""
    if language == 'da':
        prompt = f"Du er en AI-assistent for en virksomhed, der tilbyder følgende tjenester: {business_keywords}. " \
                 f"Samtalen indtil nu: " + " ".join(history) + ". " \
                 f"Fortsæt samtalen og svar venligt på dansk."
    else:
        prompt = f"You are an AI assistant for a business offering the following services: {business_keywords}. " \
                 f"The conversation so far: " + " ".join(history) + ". " \
                 f"Continue the conversation and respond politely in English."

    return prompt

def get_ai_response(prompt, language):
    """Send the prompt to OpenAI GPT and return the response"""
    response = openai.Completion.create(
        engine="gpt-4",
        prompt=prompt,
        max_tokens=150,
        temperature=0.7
    )
    ai_response = response.choices[0].text.strip()

    return ai_response

def create_conversation_summary(history):
    """Generate a short summary of the conversation in the preferred language using OpenAI GPT"""
    full_conversation = "\n".join(history)
    
    # Detect preferred language and adjust prompt accordingly
    if user_settings['preferred_language'] == 'da':
        prompt = (
            f"Opsummer følgende samtale kort på dansk:\n\n{full_conversation}\n\n"
            "Giv en kort og præcis opsummering."
        )
    else:  # Default to English
        prompt = (
            f"Summarize the following conversation briefly in English:\n\n{full_conversation}\n\n"
            "Provide a concise summary."
        )
    
    # Call OpenAI API for a summary
    response = openai.Completion.create(
        engine="gpt-4",
        prompt=prompt,
        max_tokens=50,  # Adjust token limit for desired summary length
        temperature=0.5
    )
    
    # Extract the summary from the OpenAI response
    summary = response.choices[0].text.strip()
    
    return summary



def send_notification(title, message):
    """Send a push notification with the full conversation summary"""
    # Example implementation of push notification
    # This would use Firebase Cloud Messaging or another service to send to the app
    print(f"Notification Sent: {title}\n{message}")

if __name__ == "__main__":
    app.run(debug=True)
