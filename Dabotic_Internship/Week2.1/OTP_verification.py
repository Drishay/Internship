'''
# install pip->>> pip install google-auth google-auth-oauthlib google-auth-httplib2 google-api-python-client

Note :- "I had deleted my credential file for safety reason, Please! add your credential file."

Instructions are provided below

# Obtain credentials.json:
> Go to the Google Cloud Console.
> Create a new project or select an existing one.
> Enable the Gmail API.
> Create OAuth 2.0 Client IDs and download the credentials.json file.
> Place this file in the same directory as your script.

# Run the Script:
> The first time you run the script, it will open a browser window to authenticate your Google account and grant the required permissions.
> The OAuth tokens will be saved in token.pickle for future use.

'''


import os
import pickle
import random
import base64
from email.mime.text import MIMEText
from google.auth.transport.requests import Request
from google.oauth2.credentials import Credentials
from google_auth_oauthlib.flow import InstalledAppFlow
from googleapiclient.discovery import build

# Define the scopes for the Gmail API
SCOPES = ['https://www.googleapis.com/auth/gmail.send']

# Generate OTP
def generate_otp():
    return random.randint(100000, 999999)

# Creates a message i.e mail
def create_mail(sender, to, subject, message_text):
    message = MIMEText(message_text) #body of email
    message['to'] = to   #receiver
    message['from'] = sender  #sender
    message['subject'] = subject  #subject of email
    raw = base64.urlsafe_b64encode(message.as_bytes()).decode()
    return {'raw': raw}

# Sends the mail
def send_mail(service, user_id, message):
    try:
        message = service.users().messages().send(userId=user_id, body=message).execute()  #only me i.e I is the user in credentials.json
        print(f"Message Id: {message['id']}")
        return message
    except Exception as error:
        print(f"An error occurred: {error}")
        return None

def main(user_email, count = 0):
    count +=1

    creds = None
    # Check if token.pickle exists, which stores the user's access and refresh tokens
    if os.path.exists('token.pickle'):
        with open('token.pickle', 'rb') as token:
            creds = pickle.load(token)
    # If there are no valid credentials available, let the user log in.
    if not creds or not creds.valid:
        if creds and creds.expired and creds.refresh_token:
            creds.refresh(Request())
        else:
            flow = InstalledAppFlow.from_client_secrets_file('credentials.json', SCOPES)
            creds = flow.run_local_server(port=0)
        # Save the credentials for the next run
        with open('token.pickle', 'wb') as token:
            pickle.dump(creds, token)

    service = build('gmail', 'v1', credentials=creds)

    

    # Generate OTP
    otp = generate_otp()
    print(f"Generated OTP: {otp}")

    # Create email message
    sender_email = ""  # let it be an empty string variable as the sender email will be taken from the credential file as there is only one user in credentials
    mail_body = f"Hello!\nYour OTP code is {otp}.\nPlease verify the OTP code."
    message = create_mail(sender_email, user_email, "Your OTP Code", mail_body)

    # Send email
    result = send_mail(service, "me", message)  #me- because I added only myself in user while creating credentials.json
    if result:
        print("OTP sent successfully!")
    else:
        print("Failed to send OTP email.")
        quit()

    for i in range(1,4):  #give 3 chance to enter the correct OTP and if the loops completes it means that the OTP is not verified 

        print(f"OTP verification {i}/3 triaL")
        # Request OTP from user
        try:
            user_otp = int(input("Enter the OTP you received: "))
        except ValueError:
            print("Invalid input. Please enter a numeric OTP.")
            continue  #continue the loop i.e skip the iteration if the user input is not valid

        # Verify OTP
        if user_otp == otp:
            print("OTP verified successfully!")
            break
        else:
            print("Invalid OTP. Please try again.")
    else:  #when loop is completed successfully, else will be executed
        print("OTP not verified")
        if count == 2: #only 2 times the OTP will be generated
            quit()
        print("Re-generating OTP twice")
        main(user_email, count)  #re-generate OTP

if __name__ == '__main__':
    # Get user email
    user_email = input("Enter your email address to receive an OTP: ")
    main(user_email)