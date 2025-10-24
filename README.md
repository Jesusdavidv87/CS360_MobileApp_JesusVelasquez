# CS360_MobileApp_JesusVelasquez
Android Inventory App project developed for CS 360 – Mobile Architecture and Programming.

# CS360 – Mobile Architecture and Programming
### Inventory App by Jesús Velásquez

This repository contains my final Android application for SNHU CS360.  
It demonstrates:
- User login and persistent SQLite database
- CRUD functionality for inventory management
- SMS permission handling
- Best coding practices and app launch plan

**Technologies used:** Java, SQLite, Android Studio.

**Instructor collaborator:** heusserm


# 📱 CS 360 – Mobile Architecture and Programming  
### **Artifact:** Inventory App – Jesus Velasquez  

---

## 🧩 Overview  
This project demonstrates my ability to design and develop a fully functional Android mobile application using Java and Android Studio. The app was created to help users manage an inventory system with features that allow them to add, edit, update, and delete items while also receiving SMS alerts when stock levels fall below a defined threshold.  

The goal was to provide a simple, user-centered tool for small business owners or warehouse workers to stay organized and maintain control over product availability.  

---

## 1️⃣ Requirements and Goals  
The app’s main requirements included:  
- A login system that stores new users in a database.  
- A persistent SQLite database to track inventory items.  
- CRUD functionality (Create, Read, Update, Delete).  
- SMS notifications for low-stock alerts, with runtime permission handling.  

The primary user need was **simplicity** — users needed a quick, efficient way to manage inventory without unnecessary complexity.  

---

## 2️⃣ User Interface and Design  
The app includes a **Login screen** and an **Inventory List screen**.  
- The **LoginActivity** allows users to sign in or register new credentials.  
- The **InventoryActivity** displays all items in a grid layout, with buttons for adding, editing, and deleting.  
- The **Item Dialog** enables quick edits or additions using input fields for name, SKU, quantity, location, and threshold.  
- The **SMS Settings Dialog** lets users toggle alerts and enter a phone number.  

The design followed a *clean and minimal* approach with consistent colors, spacing, and clearly labeled buttons. The use of dialogs and a scrollable layout kept the interface intuitive and accessible.  

---

## 3️⃣ Coding Approach and Strategies  
I used an **object-oriented structure** and separated concerns into logical classes:  
- `Item`, `ItemDao`, and `DBRepository` handled database logic.  
- `InventoryActivity` managed app behavior and UI interactions.  
- `ViewItemDialog` handled add/edit item pop-ups.  

I coded iteratively — writing small, testable chunks, then running the Android Emulator frequently to verify behavior. This incremental approach helped isolate bugs early and ensured that each feature worked before moving on.  

---

## 4️⃣ Testing and Functionality  
Testing was done using the **Android Emulator** to simulate real devices. I tested:  
- Database persistence after app restarts.  
- CRUD operations for reliability.  
- Permission handling for SMS notifications (both granted and denied cases).  

Testing is essential because it confirms that the app meets functional and user expectations before release. It also revealed small logic errors, such as missing permission checks and incorrect view bindings, which were corrected.  

---

## 5️⃣ Challenges and Innovation  
One challenge was implementing SMS functionality correctly while ensuring the app remained stable when users denied permissions.  
To solve this, I used the `ActivityResultLauncher` API with runtime permission handling — allowing smooth, modern permission requests without interrupting the app flow.  

Another challenge was maintaining data persistence across sessions.  
I resolved this by properly structuring the SQLite database and ensuring all CRUD methods were transactional and tested.  

---

## 6️⃣ Key Strengths and Successes  
My strongest component was the integration of the **SQLite database and SMS permissions**, which required combining multiple Android frameworks into a cohesive system.  

This demonstrated my growing skills in:  
- Database management  
- User interface design  
- Mobile software engineering best practices  

Overall, this project represents a full end-to-end app development cycle — from UI design and coding to testing and deployment preparation — and serves as a strong portfolio piece that showcases both technical skill and user-centered design thinking.  

---

## ✅ Included Files  
- **`MyFirstApp_JesusVelasquez.zip`** – Full Android Studio Project  

---

### 🔗 Repository Information  
This artifact is part of my Computer Science portfolio for Southern New Hampshire University (SNHU).  
It highlights my ability to apply **object-oriented programming, database management, and user-centered design** to develop a functional mobile application using **Java and Android Studio**.  


