Android Task Manager Description
The Android Process Monitor is an Android-based task management application designed to monitor and manage processes running on a device. Built using the modern Jetpack Compose framework, this app provides an intuitive interface for real-time process tracking and management.

Key Features:                                                                                                             


Real-time Process Monitoring:
The app periodically fetches and updates the list of active processes, displaying their Process IDs (PIDs) and names.

Search Functionality:
Users can filter processes dynamically by entering search queries in the search bar, allowing quick navigation through the process list.

Process Termination:
Users can terminate specific processes by selecting the "Kill Process" button. This feature requires root access to execute system-level commands.

Interactive UI:
A search bar for filtering processes.
A scrollable list view displaying processes with their details.
Buttons for directly interacting with processes.

Core Functionalities:
Fetch Process List: Retrieves the active process list using the ps command executed with root permissions.
Terminate Process: Kills processes by executing the kill command with the specified PID.
Periodic Updates: Automatically refreshes the process list every 5 seconds for real-time updates.

Technologies Used:
Jetpack Compose: For UI layout and design.
Kotlin Coroutines: To handle background tasks such as fetching processes and killing them without blocking the main thread.
Material Design: Provides a polished and responsive user experience.
This app is ideal for advanced users or developers requiring insights into process management and control, offering a lightweight and efficient solution. Root access is necessary for full functionality.


 ![AndroidTaskManagerImage](https://github.com/user-attachments/assets/0036b236-4e5a-4593-9ad7-14e8aa3f2e68)
