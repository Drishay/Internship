import tkinter as tk
from tkinter import messagebox
from datetime import datetime
import time
import threading

# Global list to keep track of alarm times and their titles
alarms = []

# Function to update the clock display
def update_clock():
    now = datetime.now().strftime("%H:%M:%S")
    clock_label.config(text=now)
    root.after(1000, update_clock)

# Function to check the alarm time
def check_alarm(alarm_time, alarm_title, alarm_frame):
    while alarm_time in [alarm[0] for alarm in alarms]:
        now = datetime.now().strftime("%H:%M")
        if now == alarm_time:
            messagebox.showinfo("Alarm", f"Time to wake up! {alarm_title}")
            remove_alarm(alarm_time, alarm_frame)
            break
        time.sleep(1)  # Check every second for more immediate response

# Function to set the alarm
def set_alarm():
    alarm_time = entry.get()
    alarm_title = title_entry.get()
    if alarm_time in [alarm[0] for alarm in alarms]:
        messagebox.showerror("Alarm Exists", "An alarm for this time already exists.")
        return

    try:
        valid_time = datetime.strptime(alarm_time, "%H:%M")
        alarms.append((alarm_time, alarm_title))
        alarms.sort()  # Sort alarms in ascending order
        update_alarm_list()
        messagebox.showinfo("Alarm Set", f"Alarm set for {alarm_time} with title: {alarm_title}")
        threading.Thread(target=check_alarm, args=(alarm_time, alarm_title, None)).start()  # Start a thread for the new alarm
    except ValueError:
        messagebox.showerror("Invalid Time", "Please enter a valid time in HH:MM format")

# Function to remove the alarm
def remove_alarm(alarm_time, alarm_frame):
    global alarms
    alarms = [alarm for alarm in alarms if alarm[0] != alarm_time]
    if alarm_frame:
        alarm_frame.destroy()  # Destroy the alarm frame containing the label and buttons
    update_alarm_list()

# Function to update the alarm list display
def update_alarm_list():
    for widget in alarm_list_frame.winfo_children():
        widget.destroy()  # Clear current alarms displayed

    for alarm_time, alarm_title in alarms:
        alarm_frame = tk.Frame(alarm_list_frame, bg="#333333", pady=2, padx=5)
        alarm_frame.pack(fill='x', pady=2)

        alarm_label = tk.Label(alarm_frame, text=f"Alarm set for {alarm_time} - {alarm_title}", font=("Helvetica", 12), bg="#333333", fg="#ffffff")
        alarm_label.pack(side=tk.LEFT)

        button_frame = tk.Frame(alarm_frame, bg="#333333")
        button_frame.pack(side=tk.RIGHT)

        edit_button = tk.Button(button_frame, text="Edit", font=("Helvetica", 10), command=lambda at=alarm_time, atitle=alarm_title, al=alarm_label, af=alarm_frame: edit_alarm(at, atitle, al, af), bg="#ffc107", fg="white")
        edit_button.pack(side=tk.RIGHT, padx=5)

        remove_button = tk.Button(button_frame, text="Remove", font=("Helvetica", 10), command=lambda at=alarm_time, af=alarm_frame: remove_alarm(at, af), bg="#ff4c4c", fg="white")
        remove_button.pack(side=tk.RIGHT, padx=5)

    alarm_count_label.config(text=f"Alarms set: {len(alarms)}")

# Function to edit the alarm
def edit_alarm(old_alarm_time, old_alarm_title, alarm_label, alarm_frame):
    def save_new_alarm():
        new_alarm_time = edit_time_entry.get()
        new_alarm_title = edit_title_entry.get()
        if new_alarm_time in [alarm[0] for alarm in alarms if alarm[0] != old_alarm_time]:
            messagebox.showerror("Alarm Exists", "An alarm for this time already exists.")
            return
        try:
            valid_time = datetime.strptime(new_alarm_time, "%H:%M")
            remove_alarm(old_alarm_time, alarm_frame)
            alarms.append((new_alarm_time, new_alarm_title))
            alarms.sort()  # Sort alarms in ascending order
            update_alarm_list()
            edit_window.destroy()
            threading.Thread(target=check_alarm, args=(new_alarm_time, new_alarm_title, None)).start()  # Start a thread for the new alarm
        except ValueError:
            messagebox.showerror("Invalid Time", "Please enter a valid time in HH:MM format")

    edit_window = tk.Toplevel(root)
    edit_window.title("Edit Alarm")
    edit_window.geometry("250x150")
    edit_window.configure(bg="#f0f0f0")

    edit_label = tk.Label(edit_window, text="Edit alarm time (HH:MM):", font=("Helvetica", 12), bg="#f0f0f0", fg="#333")
    edit_label.pack(pady=5)
    edit_time_entry = tk.Entry(edit_window, font=("Helvetica", 12))
    edit_time_entry.pack(pady=5)
    edit_time_entry.insert(0, old_alarm_time)

    edit_title_label = tk.Label(edit_window, text="Edit alarm title:", font=("Helvetica", 12), bg="#f0f0f0", fg="#333")
    edit_title_label.pack(pady=5)
    edit_title_entry = tk.Entry(edit_window, font=("Helvetica", 12))
    edit_title_entry.pack(pady=5)
    edit_title_entry.insert(0, old_alarm_title)

    save_button = tk.Button(edit_window, text="Save", font=("Helvetica", 12), command=save_new_alarm, bg="#4caf50", fg="white")
    save_button.pack(pady=5)

# Function to set gradient background
def set_gradient_background(widget, start_color, end_color):
    width = widget.winfo_width()
    height = widget.winfo_height()

    gradient = tk.PhotoImage(width=width, height=height)

    for y in range(height):
        color = f"#{int(start_color[1:3], 16) + (int(end_color[1:3], 16) - int(start_color[1:3], 16)) * y // height:02x}{int(start_color[3:5], 16) + (int(end_color[3:5], 16) - int(start_color[3:5], 16)) * y // height:02x}{int(start_color[5:7], 16) + (int(end_color[5:7], 16) - int(start_color[5:7], 16)) * y // height:02x}"
        gradient.put(color, (0, y, width, y + 1))

    widget.create_image(0, 0, image=gradient, anchor="nw")
    widget.gradient = gradient

# Setting up the GUI
root = tk.Tk()
root.title("Alarm Clock")

# Calculate screen dimensions
screen_width = root.winfo_screenwidth()
screen_height = root.winfo_screenheight()

# Calculate half of the screen width and set frame dimensions
bg_width = screen_width // 2
bg_height = screen_height
bg_x = screen_width // 4  # Position at the center horizontally
bg_y = 0  # Start from the top

# Background frame
bg_frame = tk.Canvas(root, width=bg_width, height=bg_height)
bg_frame.place(x=bg_x, y=bg_y)
set_gradient_background(bg_frame, "#000000", "#333333")

# Clock and input frame dimensions
clock_width = bg_width - 40  # Adjust for padding
clock_height = 100
clock_x = bg_x + 20
clock_y = bg_y + 20

# Title label
title_label = tk.Label(root, text="Python Alarm Clock", font=("Helvetica", 16, "bold"), bg="#333333", fg="#ffffff")
title_label.place(x=clock_x, y=clock_y, width=clock_width)

# Digital clock display
clock_label = tk.Label(root, text="", font=("Helvetica", 48), bg="#333333", fg="#ffffff")
clock_label.place(x=clock_x, y=clock_y + 40, width=clock_width)
update_clock()

# Alarm time input dimensions
input_width = bg_width - 40
input_height = 30
input_x = bg_x + 20
input_y = clock_y + 120

# Alarm title input
title_label = tk.Label(root, text="Enter alarm title:", font=("Helvetica", 12), bg="#333333", fg="#ffffff")
title_label.place(x=input_x, y=input_y, width=input_width)
title_entry = tk.Entry(root, font=("Helvetica", 12))
title_entry.place(x=input_x, y=input_y + 30, width=input_width)

# Alarm time input
label = tk.Label(root, text="Enter alarm time (HH:MM):", font=("Helvetica", 12), bg="#333333", fg="#ffffff")
label.place(x=input_x, y=input_y + 60, width=input_width)
entry = tk.Entry(root, font=("Helvetica", 12))
entry.place(x=input_x, y=input_y + 90, width=input_width)

# Set alarm button dimensions
button_width = 100
button_height = 30
button_x = bg_x + bg_width - 120  # Align to the right of the background frame
button_y = input_y + 120

# Set alarm button
button = tk.Button(root, text="Set Alarm", font=("Helvetica", 12), command=set_alarm, bg="#4caf50", fg="white")
button.place(x=button_x, y=button_y, width=button_width, height=button_height)

# Frame to display the number of alarms set dimensions
count_width = bg_width - 40
count_height = 30
count_x = bg_x + 20
count_y = input_y + 170

# Frame to display the number of alarms set
alarm_count_bg_frame = tk.Frame(root, bg="#333333")
alarm_count_bg_frame.place(x=count_x, y=count_y, width=count_width, height=count_height)
alarm_count_label = tk.Label(alarm_count_bg_frame, text="Alarms set: 0", font=("Helvetica", 12), bg="#333333", fg="#ffffff")
alarm_count_label.pack()

# Frame to display the list of alarms set dimensions
list_width = bg_width - 40
list_height = bg_height - 320  # Adjust remaining height
list_x = bg_x + 20
list_y = count_y + 50

# Frame to display the list of alarms set
alarm_list_frame = tk.Frame(root, bg="#333333")
alarm_list_frame.place(x=list_x, y=list_y, width=list_width, height=list_height)

# Run the GUI loop
root.geometry(f"{screen_width}x{screen_height}+0+0")  # Set full screen size
root.mainloop()
