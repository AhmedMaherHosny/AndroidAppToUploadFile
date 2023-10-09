# Android File Upload App with Jetpack Compose

This Android application is designed to upload very large files by splitting them into chunks, using streams for efficient upload. It utilizes modern Android development technologies like Android Jetpack Compose, Uncle Bob's Clean Architecture, Coroutines, Retrofit 2, Dagger Hilt, and MVVM architecture. The app also includes a foreground service to handle file uploads and provides real-time progress updates.

https://github.com/AhmedMaherHosny/AndroidAppToUploadFile/assets/55118681/dfa8bea3-2737-4206-ab6e-2df3d9ae9a10

## Features and Tools

### 1. Android Jetpack Compose
- The user interface of the application is built using Jetpack Compose, which allows for a more declarative and efficient way to create UI components.

### 2. Uncle Bob's Clean Architecture
- The application follows Clean Architecture principles, separating concerns into layers: Presentation, Domain, and Data. This ensures a clear and maintainable codebase.

### 3. Coroutines
- Coroutines are used to manage asynchronous operations, such as file uploads, in a concise and readable way.

### 4. Retrofit 2
- Retrofit 2 is used for making HTTP requests to a remote server, facilitating the file upload process.

### 5. Foreground Service
- The app utilizes a foreground service to ensure that file uploads continue even when the app is in the background or closed, providing a seamless user experience.

### 6. MVVM Architecture
- The application employs the MVVM (Model-View-ViewModel) architectural pattern, separating the UI logic from the data handling and business logic.

### 7. Dagger Hilt
- Dagger Hilt is used for dependency injection, making it easier to manage and provide dependencies throughout the application.

## Getting Started
1. Clone the repository to your local machine.
2. Open the project in Android Studio.
3. Build and run the app on an Android emulator or physical device.

## Usage
1. Launch the app on your Android device.
2. Select a file you want to upload.
3. The app will split the file into chunks and start the upload process.
4. Monitor the progress, upload speed, and estimated time remaining in real-time on the UI.
5. The foreground service ensures that the upload continues even if you navigate away from the app.

## Contributing
Contributions to this project are welcome. Please feel free to fork the repository and submit pull requests to improve or extend the application.

## License
This project is licensed under the MIT License. See the [LICENSE](https://github.com/AhmedMaherHosny/AndroidAppToUploadFile/blob/master/LICENSE) file for details.

## Acknowledgments
- Thanks to Uncle Bob for Clean Architecture guidelines.
- The project utilizes various open-source libraries and tools, which are credited in the codebase.

---

Enjoy using the Android File Upload App with Jetpack Compose! If you have any questions or encounter any issues, please don't hesitate to [create an issue](https://github.com/AhmedMaherHosny/AndroidAppToUploadFile/issues) on the GitHub repository.
