import time
from chatterbot import ChatBot
from chatterbot.trainers import ChatterBotCorpusTrainer

# Create a ChatBot instance
bot = ChatBot('FriendlyBot')

# Create a new trainer for the ChatBot
trainer = ChatterBotCorpusTrainer(bot)

# Train the bot based on the English corpus
trainer.train('chatterbot.corpus.english')

# Start a conversation
print("Welcome to FriendlyBot. How can I assist you today?")

# Use time.perf_counter() instead of time.clock()
time_func = time.perf_counter

while True:
    user_input = input("You: ")
    if user_input.lower() == 'bye':
        print("FriendlyBot: Goodbye!")
        quit()

    start_time = time_func()
    try:
        response = bot.get_response(user_input)
    except Exception as e:
        print(f"An error occurred: {e}")
        response = "I'm having trouble processing that."
    end_time = time_func()
    
    print("FriendlyBot:", response)
    print(f"Response time: {end_time - start_time:.4f} seconds")


# '''
# # pip install chatterbot
# pip install chatterbot==1.0.0   ->instal the specific version i.e 1.0.0 of chatterbot
# -- Lib\site-packages\sqlalchemy\util\compat.py, line 264, in <module>
# > # Replace this line:
# time_func = time.clock

# # With this line:
# time_func = time.perf_counter

# -- Lib\site-packages\yaml\constructor.py, line 126, in construct_mapping
# # Replace this line:
# if not isinstance(key, collections.Hashable):

# # With this line:
# if not isinstance(key, collections.abc.Hashable):

# # pip install chatterbot_corpus --> this for traning data
# # pip install pytz  --> pytz is a Python library that provides timezone definitions and timezone-aware datetime object
# '''

# '''The message "No value for search_text was available on the provided input" indicates that ChatterBot 
# couldn't find a suitable response based on the input it received. This can often be improved by refining the 
# training data to cover a wider range of user queries and scenarios.

# Training of bot is to be done with lots of prompt, for the sake of working bot it knows about simple and direct
# questions and answers. But if you want to make it more intelligent then you have to train it with more data.

# Sometimes bot replies are not great as it is not trained well.
# '''