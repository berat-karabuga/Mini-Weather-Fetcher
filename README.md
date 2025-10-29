# ☀️ Mini Weather Fetcher App

!(https://user-images.githubusercontent.com/9587600/220275525-24c58f00-36b1-4f11-92be-3f3f5a04e90d.png)

## 🎯 Project Overview

This is a **practice mini-project** developed to solidify the knowledge gained from the **Meta Android Developer Professional Certificate** program, specifically within the "Interacting with REST APIs in Android" module of the "Working with Data in Android" course.

The primary goal of this application is to demonstrate proficiency in **internet communication (REST API)** and **image loading** using modern Android development tools and libraries. The app retrieves current weather data for a city entered by the user and dynamically displays an image (currently a cat image) on the UI.

### Core Objectives

* Implementing **data fetching** from the internet (establishing Web/API communication).
* **Parsing** the retrieved JSON data into appropriate Kotlin data classes.
* Displaying both the data and images **asynchronously** on the UI.

## 🛠️ Technologies Used

The project utilizes the following key technologies and libraries for data communication, asynchronous processing, and modern Android UI development:

| Technology / Library | Purpose | Key Benefit |
| :--- | :--- | :--- |
| **Kotlin / Android Compose** | User Interface (UI) | Used for modern, declarative, and rapid UI development. |
| **Ktor Client** | HTTP Client / API Communication | A lightweight, Kotlin-native coroutine-powered HTTP client for fetching data from REST APIs. |
| **Gson** | JSON Parsing | Used for easily converting the JSON response from the REST API into structured Kotlin `data class` objects. |
| **Glide (Compose Integration)** | Image Loading | Handles efficient and smooth loading and display of images from remote URLs. |
| **LiveData / State** | Data Management | Ensures safe and reactive delivery of asynchronously fetched data to the UI layer. |
| **Coroutines** | Asynchronous Operation Management | Enables running network requests securely in the background without blocking the Main Thread. |

## ⚙️ Application Functionality

The app performs two main functions:

1.  **Weather Data Fetching:**
    * The user inputs a city name into the text field.
    * Tapping the button triggers an HTTP GET request to `weatherapi.com`.
    * The incoming JSON response is parsed into `WeatherResponse` data classes.
    * The City Name, Weather Condition Text, and Temperature (°C) are displayed on the screen.
2.  **Dynamic Image Display:**
    * An image is fetched from an external image URL (e.g., `picsum.photos`) using the **Glide** library and displayed with appropriate scaling.

## 📝 Setup and Testing

The application is fully functional and successfully executes all necessary network operations. To test it:

1.  Clone or download the project.
2.  Open it in Android Studio.
3.  Run on an emulator (API 24+) or a physical device.
4.  Enter a valid city name to check the data retrieved from the API.





# ☀️ Mini Weather Fetcher App (Hava Durumu Veri Çekme Uygulaması)

!(https://user-images.githubusercontent.com/9587600/220275525-24c58f00-36b1-4f11-92be-3f3f5a04e90d.png)

## 🇹🇷 Proje Hakkında (About the Project)

Bu proje, **Meta Android Developer Professional Certificate** programının "Working with Data in Android" kursunun "Interacting with Rest APIs in Android" modülünde öğrenilen bilgileri pekiştirmek amacıyla geliştirilmiş bir **pratik mini-projedir**.

Temel amaç, modern Android mimarisi ve kütüphaneleri kullanarak internet üzerinden **veri iletişimi (REST API)** ve **görsel yükleme** yeteneklerini uygulamaktır. Uygulama, kullanıcı tarafından girilen şehir için anlık hava durumu verilerini çeker ve arayüzde dinamik olarak bir görsel (kedi görseli) gösterir.

### Ana Hedefler

* İnternet üzerinden **veri çekme** (Web/API iletişimini kurma).
* Çekilen JSON verisini Kotlin veri sınıflarına **ayıştırma (parsing)**.
* Uygulama arayüzünde (UI) alınan verileri ve görselleri **eşzamansız (asenkron)** olarak gösterme.

## 🛠️ Kullanılan Teknolojiler (Technologies Used)

Bu projede veri iletişimi, asenkron işlemler ve modern Android UI geliştirmesi için aşağıdaki temel teknolojiler ve kütüphaneler kullanılmıştır:

| Teknoloji / Kütüphane | Amaç (Purpose) | Neden Kullanıldı? (Why Used?) |
| :--- | :--- | :--- |
| **Kotlin / Android Compose** | Kullanıcı Arayüzü (UI) | Modern, deklaratif ve hızlı UI geliştirmesi için tercih edildi. |
| **Ktor Client** | HTTP İstemcisi / API İletişimi | REST API'lerinden veri çekmek için hafif ve Kotlin tabanlı asenkron HTTP istemcisi. |
| **Gson** | JSON Ayrıştırma (Parsing) | REST API'den gelen JSON yanıtını Kotlin `data class` yapılarına kolayca dönüştürmek için. |
| **Glide (Compose Integration)** | Görsel Yükleme | İnternet üzerindeki görselleri (URLs) verimli ve sorunsuz bir şekilde yükleyip göstermek için. |
| **LiveData / State** | Veri Yönetimi | Asenkron olarak çekilen verinin UI'a güvenli ve reaktif bir şekilde iletilmesini sağlamak için. |
| **Coroutines** | Eşzamansız İşlem Yönetimi | Ağ isteklerini ana iş parçacığını (Main Thread) tıkamadan, arka planda güvenli bir şekilde çalıştırmak için. |

## ⚙️ Uygulama Fonksiyonu (Application Functionality)

Uygulama, temel olarak iki ana işlevi yerine getirir:

1.  **Hava Durumu Verisi Çekme:**
    * Kullanıcı, metin alanına bir şehir adı girer.
    * "Hava nası olcak" butonuna tıklandığında, `weatherapi.com` adresine bir HTTP GET isteği gönderilir.
    * Gelen JSON yanıtı `WeatherResponse` veri sınıflarına ayrıştırılır.
    * Şehir Adı, Hava Durumu Açıklaması ve Sıcaklık (C) bilgisi ekranda gösterilir.
2.  **Görsel Çekme ve Gösterme:**
    * Uygulama arayüzünde, **Glide** kütüphanesi kullanılarak harici bir görsel URL'sinden (örneğin `picsum.photos`) dinamik bir görsel çekilir ve ekranda uygun boyutta gösterilir.

## 📝 Kurulum ve Test (Installation and Testing)

Uygulama, gerekli tüm ağ işlemlerini başarıyla gerçekleştirmektedir ve tamamen çalışır durumdadır. Test etmek için:

1.  Projeyi klonlayın veya indirin.
2.  Android Studio'da açın.
3.  Bir emülatör (API 24+) veya fiziksel cihazda çalıştırın.
4.  Geçerli bir şehir adı girerek API'den gelen veriyi kontrol edin.
