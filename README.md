# AccessMaster 🚀

AccessMaster is an **offline SMS-based command app** that allows users to retrieve contacts, location, and send SMS messages remotely. It works without an internet connection and ensures secure access to important data via SMS commands.

## 📌 Features

✅ Retrieve contacts via SMS commands  
✅ Get the device's current location via SMS  
✅ Send SMS commands remotely  
✅ Works **offline** without internet  
✅ Secure access with authentication  
✅ Lightweight and battery-efficient  

## 📂 Project Structure

```
AccessMaster/
│── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/accessmaster/
│   │   │   │   ├── MainActivity.java
│   │   │   │   ├── SmsReceiver.java
│   │   │   │   ├── LocationService.java
│   │   │   │   ├── DatabaseHelper.java
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   ├── drawable/
│   │   │   │   ├── values/
│── README.md
│── AndroidManifest.xml
```

## ⚙️ How It Works

1. **Send an SMS command** (e.g., `GET_CONTACTS`) from another phone.
2. The app **receives the SMS** and processes the command.
3. If authorized, it retrieves the requested data and **sends it back via SMS**.
4. Works even without an **internet connection**.

## 📲 Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/AccessMaster.git
   ```
2. Open the project in **Android Studio**.
3. Connect your phone or use an emulator.
4. Run the app! 🚀

## 🔑 Permissions Required

To function properly, the app needs the following permissions:

- `READ_CONTACTS` – To access contacts.
- `SEND_SMS` – To send responses via SMS.
- `RECEIVE_SMS` – To listen for SMS commands.
- `ACCESS_FINE_LOCATION` – To get device location.

## 🛠️ Tech Stack

- **Java** (Android Development)
- **Room Database** (Data storage)
- **BroadcastReceiver** (For listening to SMS)
- **Android Services** (For background tasks)

## 🛡️ Security Measures

🔒 Only **authorized users** can send SMS commands.  
🔒 Uses **SharedPreferences encryption** for storing sensitive data.  
🔒 Ensures **data privacy** by handling permissions securely.  

## 🏆 Future Improvements

🚀 Add a user-friendly **settings screen** for configuring commands.  
🚀 Implement **end-to-end encryption** for better security.  
🚀 Improve **UI/UX** for a better experience.  

## 🤝 Contribution

Want to improve AccessMaster? Feel free to contribute!
1. **Fork** the repository
2. **Create a new branch** (`feature-xyz`)
3. **Commit your changes**
4. **Create a Pull Request**

## 📧 Contact
For any queries, feel free to reach out!  
✉️ [Your Email]  
🌍 [Your GitHub Profile](https://github.com/yourusername)

---
🚀 **Access your phone’s data securely via SMS commands!**
