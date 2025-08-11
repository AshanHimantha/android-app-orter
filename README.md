# Orter - Full Stack Ordering Application 🍔📱

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-8+-orange.svg?logo=java)](https://www.java.com)
[![Laravel](https://img.shields.io/badge/Laravel-11.x-FF2D20.svg?logo=laravel)](https://laravel.com)

A complete, full-stack ordering application built with a Java Android client and a robust Laravel backend. This project serves as a demonstration of building a real-world mobile application from the ground up.

- **📱 Android Client Repository:** [AshanHimantha/android-app-orter](https://github.com/AshanHimantha/android-app-orter)
- **⚙️ Backend API Repository:** [AshanHimantha/orter-android-backend](https://github.com/AshanHimantha/orter-android-backend)

## ✨ Features

### Client (Android App)
- **User Authentication:** Secure user registration and login.
- **Product Browsing:** View a list of available products with details.
- **Search & Filter:** Easily find products.
- **Shopping Cart:** Add/remove items and manage quantities.
- **Order Placement:** A simple and intuitive checkout process.
- **Order History:** View past orders and their status.
- **Modern UI:** A clean and user-friendly interface.

### Server (Backend API)
- **Secure REST APIs:** Well-defined endpoints for all application functionalities.
- **Token-based Authentication:** Stateless and secure authentication using Firebase Auth.
- **Data Persistence:** Manages users, products, and orders in a relational database using the Eloquent ORM.
- **Scalable Architecture:** Built with the powerful Laravel framework.
- **Data Seeding:** Includes seeders to populate the database with initial product data for quick setup.

---

## 📸 Screenshots

| Login Screen | Home Screen | Product Details |
| :----------: | :---------: | :-------------: |
| <img src="https://github.com/user-attachments/assets/9f2e6f9e-8479-47d0-819a-50a14f8fe871" alt="Login Screen" width="270"/> | <img src="https://github.com/user-attachments/assets/ab50f16a-5261-4528-9d8d-75cd3a44a218" alt="Home Screen" width="270"/> | <img src="https://github.com/user-attachments/assets/a6ebc556-f526-44b8-9ce6-e972440d5d1a" alt="Product Details" width="270"/> |

| Cart | Order History | User Profile |
| :--: | :-----------: | :--: |
| <img src="https://github.com/user-attachments/assets/7fdfc087-9fbd-42e9-98c8-f312cd13d4f4" alt="Cart Screen" width="270"/> | <img src="https://github.com/user-attachments/assets/534a6b88-426e-4e86-8980-3c1de06056c9" alt="Order History Screen" width="270"/> | <img width="270" alt="Screenshot_1740358841" src="https://github.com/user-attachments/assets/044927f9-a997-4989-8185-3cbd23bae8d0" />
 |

---

## 🛠️ Tech Stack & Architecture

### 📱 Android Application (Frontend)
- **Language:** [Java](https://www.java.com/)
- **Architecture:** [MVVM (Model-View-ViewModel)](https://developer.android.com/topic/architecture)
- **Networking:** [Retrofit 2](https://square.github.io/retrofit/) & [OkHttp 3](https://square.github.io/okhttp/) for making API calls.
- **JSON Parsing:** [Gson](https://github.com/google/gson)
- **UI:** [Android Jetpack (ViewModel)](https://developer.android.com/topic/libraries/architecture/viewmodel) & [Material Design Components](https://material.io/develop/android).
- **Image Loading:** [Glide](https://github.com/bumptech/glide)

### ⚙️ Backend API (Backend)
- **Framework:** [Laravel 8](https://laravel.com/)
- **Language:** [PHP](https://www.php.net/)
- **Security:** [Laravel Sanctum](https://laravel.com/docs/8.x/sanctum) for API token authentication.
- **Database:** [Eloquent ORM](https://laravel.com/docs/8.x/eloquent) with a relational database (MySQL is configured).
- **Dependency Manager:** [Composer](https://getcomposer.org/)
- **API Specification:** RESTful principles.

---

## 🚀 Getting Started

To get this project up and running on your local machine, follow these steps.

### Prerequisites

- **PHP:** Version 8.0 or higher.
- **Composer:** [PHP Dependency Manager](https://getcomposer.org/download/).
- **Database:** A running instance of MySQL.
- **IDE:**
    - [Android Studio](https://developer.android.com/studio) for the mobile app.
    - [VS Code](https://code.visualstudio.com/) with PHP extensions or [PhpStorm](https://www.jetbrains.com/phpstorm/) for the backend.
- **Git:** [Version Control System](https://git-scm.com/).

### 1. Backend Setup (`orter-android-backend`)

First, set up and run the Laravel backend server.

1.  **Clone the backend repository:**
    ```bash
    git clone https://github.com/AshanHimantha/orter-android-backend.git
    cd orter-android-backend
    ```

2.  **Install PHP dependencies:**
    ```bash
    composer install
    ```

3.  **Configure the environment:**
    - Copy the example environment file.
      ```bash
      cp .env.example .env
      ```
    - Generate an application key.
      ```bash
      php artisan key:generate
      ```

4.  **Set up the database:**
    - Create a new database in your MySQL instance (e.g., `orter_db`).
    - Open the `.env` file and update the `DB_*` variables with your credentials:
      ```env
      DB_CONNECTION=mysql
      DB_HOST=127.0.0.1
      DB_PORT=3306
      DB_DATABASE=orter_db
      DB_USERNAME=your_db_user
      DB_PASSWORD=your_db_password
      ```

5.  **Run database migrations and seeders:**
    - This will create the necessary tables and populate the `products` table with sample data.
      ```bash
      php artisan migrate --seed
      ```

6.  **Run the development server:**
    ```bash
    php artisan serve
    ```
    The backend API should now be running on `http://localhost:8000`.

### 2. Android App Setup (`android-app-orter`)

Now, set up and run the Android client.

1.  **Clone the Android app repository:**
    ```bash
    git clone https://github.com/AshanHimantha/android-app-orter.git
    cd android-app-orter
    ```

2.  **Open in Android Studio:**
    - Launch Android Studio.
    - Select `File > Open...` and choose the cloned `android-app-orter` directory.

3.  **Configure the API Base URL:**
    - The Android app needs to know where the backend is running.
    - Navigate to `app/src/main/java/com/ashan/orter/api/RetrofitInstance.kt`.
    - **Crucially**, if you are running the app on the Android Emulator, you must use the special IP `10.0.2.2` to refer to your host machine's `localhost`.
    - Make sure the `BASE_URL` constant points to the Laravel server's address:
      ```
      // in RetrofitInstance.kt
      const val BASE_URL = "http://10.0.2.2:8000/" 
      ```
      > Note: The default Laravel port is `8000`.

4.  **Sync and Run:**
    - Let Android Studio sync the Gradle files.
    - Select an emulator or connect a physical device.
    - Click the `Run 'app'` button.

The app should now be running and able to communicate with your local Laravel backend!

---

## 📝 API Endpoints Overview

The backend exposes several RESTful endpoints. Here are some of the key ones:

| Method | Endpoint                    | Description                                | Protected |
| :----- | :-------------------------- | :----------------------------------------- | :-------- |
| `POST` | `/api/register`             | Register a new user.                       | No        |
| `POST` | `/api/login`                | Authenticate a user and get a Sanctum token.| No        |
| `GET`  | `/api/products`             | Get a list of all available products.      | Yes       |
| `GET`  | `/api/products/{id}`        | Get details for a single product.          | Yes       |
| `POST` | `/api/orders`               | Place a new order with items from the cart.| Yes       |
| `GET`  | `/api/orders`               | Get the authenticated user's order history.| Yes       |
| `POST` | `/api/logout`               | Log the user out and revoke the token.     | Yes       |

---

## 📧 Contact

Ashan Himantha - [@Ashan_Himantha](https://twitter.com/Ashan_Himantha) - ashanhimantha321@gmail.com

Project Link: [https://github.com/AshanHimantha/android-app-orter](https://github.com/AshanHimantha/android-app-orter)
