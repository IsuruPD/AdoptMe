# AdoptMe - Pet Adoption App

## Overview

**AdoptMe** is a mobile application built using **Kotlin** and **Firebase** that helps users find and adopt animals that need a caring home. It enables users to view a wide range of pets classified under different categories and users can browse available pets, view detailed information, and initiate the adoption process for a pet they wish to adopt. 

The project is aimed at reducing the number of stray and abandoned animals, while also facilitating the public to contribute to the cause by giving them a modern and accessible way to connect the animals in need of care with potential adopters. Users can create listings for their own pets, as well as any stray or abandoned animals they come across, providing their detailed information and tagging the animals' precise location on a map. This allows potential adopters to easily track and find these animals. Additionally, welfare organizations can use the platform to identify the relevant areas, allowing them to more effectively deploy their aid programs, such as rescue missions, medical assistance, or adoption drives. Pet control organizations can also leverage the location data to plan and execute neutering, vaccination, and other population control initiatives in targeted areas.

Through this platform, we strive to make the world a kinder place for pets and the people who adopt them.

## Features

- **User Registration & Login**: Secure authentication with **Firebase Authentication**.
- **Listings by Category**: Browse pets categorized by different types.
- **Add/Update Listings**: Logged-in users can add, update, or delete listings with location tags.
- **Detailed View**: View detailed information for each pet, including name, breed, age, description, and etc.
- **Search and Filter**: Search for specific pets and filter by categories.
- **Search by Area**: View listings specific to an area marked on a map.
- **Adoption Process**: Users can initiate the adoption process after confirmation of details.

## Technologies Used

- **Programming Language**: Kotlin
- **Backend**: Firebase Firestore, Firebase Storage
- **Authentication**: Firebase Authentication

## Getting Started

Follow these steps to set up and run the project on your local machine.
  
### Download the Project

1. Open a terminal and clone the repository:

```bash
git clone https://github.com/IsuruPD/AdoptMe.git
```

2. Open the project in **Android Studio**.

### Firebase Setup

To set up Firebase, follow these steps:

1. **Create a Firebase Project**: Go to [Firebase Console](https://console.firebase.google.com/), create a new project, and add your Android app.
2. **Add the Firebase Configuration**: Download the `google-services.json` file from your Firebase Console and place it in the `app/` directory of your Android project.
3. **Enable Firebase Authentication**: Enable Email/Password sign-in method in Firebase Authentication.

### Setup Instructions

1. **Sync Gradle**: After cloning the project, open it in Android Studio and let it sync the required dependencies automatically.
2. **Configure Firebase in the project**:
   - Make sure that the `google-services.json` is in the `app/` folder.
   - In `build.gradle`, ensure Firebase dependencies are added:
3. **Run the App**: Once everything is set up, run the project on an Android Emulator or on your Android device.
