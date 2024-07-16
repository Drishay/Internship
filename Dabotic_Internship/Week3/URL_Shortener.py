# install - pip install pyshorteners

# Note - Connect to the internet and check stable connection

# import pyshorteners
import pyshorteners

# Create a Shortener object
s = pyshorteners.Shortener()

# Function to shorten the URL and handle errors.
def shorten_url(long_url):
    try:
        # Check if the URL starts with 'https'
        if not long_url.startswith("https://"):
            raise ValueError("URL must start with 'https://'")
        
        # Shorten the URL using the TinyURL service
        short_url = s.tinyurl.short(long_url)
        return short_url
    
    except ValueError:
        print(f"Error: {ValueError}")
        return None
    except pyshorteners.exceptions.ShorteningErrorException:
        print(f"Shortening error: {pyshorteners.exceptions.ShorteningErrorException}")
        return None
    except Exception:
        print(f"An unexpected error occurred: {Exception}")
        return None

# Function to expand the shortened URL and handle errors.
def expand_url(short_url):
    try:
        # Expand the shortened URL back to its original form
        expanded_url = s.tinyurl.expand(short_url)
        return expanded_url
    
    except pyshorteners.exceptions.ExpandingErrorException:
        print(f"Expanding error: {pyshorteners.exceptions.ExpandingErrorException}")
        return None
    except Exception:
        print(f"An unexpected error occurred: {Exception}")
        return None

if __name__ == "__main__":
    long_url = input("Enter the URL to shorten it: ")
    print("Original URL:", long_url)

    # Shorten the URL
    short_url = shorten_url(long_url)
    if short_url:
        print("Short URL:", short_url)

        # Expand the shortened URL back to its original form
        expanded_url = expand_url(short_url)
        if expanded_url:
            print("Expanded URL:", expanded_url)