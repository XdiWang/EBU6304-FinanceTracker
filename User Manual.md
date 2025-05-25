# AI-Powered FinanceTracker - User Manual

**Table of Contents**

- [AI-Powered FinanceTracker - User Manual](#ai-powered-financetracker---user-manual)
  - [1. Introduction](#1-introduction)
  - [2. Getting Started](#2-getting-started)
    - [2.1 Launching the Application](#21-launching-the-application)
    - [2.2 User Login](#22-user-login)
    - [2.3 User Registration](#23-user-registration)
    - [2.4 Language Selection (Login Interface)](#24-language-selection-login-interface)
  - [3. Main Interface Overview](#3-main-interface-overview)
    - [3.1 Title Bar](#31-title-bar)
    - [3.2 Menu Bar](#32-menu-bar)
    - [3.3 Toolbar](#33-toolbar)
  - [4. Core Function Modules](#4-core-function-modules)
    - [4.1 Dashboard](#41-dashboard)
    - [4.2 Overview](#42-overview)
    - [4.3 Account](#43-account)
    - [4.4 AI Chat](#44-ai-chat)
  - [5. Data Management](#5-data-management)
    - [5.1 Import Data (Import CSV)](#51-import-data-import-csv)
    - [5.2 Export Data](#52-export-data)
  - [6. Settings](#6-settings)
    - [6.1 Language Switching](#61-language-switching)
  - [7. Help \& About](#7-help--about)

---

## 1. Introduction

Welcome to the AI-Powered FinanceTracker! This application is designed to help you easily manage personal finances, track income and expenses, and receive intelligent financial advice through AI.

## 2. Getting Started

### 2.1 Launching the Application

To start the FinanceTracker application:
1. Open the project in IntelliJ IDEA
2. Navigate to `FinanceTrackerApp.java` in the project explorer
3. Either:
   - Click the green "Run" button next to the main class, **or**
   - Use the keyboard shortcut `Ctrl+F10` (Windows/Linux) or `⌃Control+F10` (Mac)

### 2.2 User Login

Upon launching the application, you will see the login interface.

* **Username:** Enter your registered username in the "Username" field.  
* **Password:** Enter your password in the "Password" field.  
* **Click Login:** Click the "Login" button.  

> **【Screenshot 1: Login Interface】**  
> ![alt text](image.png)  

After successful authentication, the system will prompt for two-factor authentication (2FA).  

* **Email/Phone Number:** Enter the email or phone number used during registration.  
* **Get Verification Code:** Click this button to (simulate) sending a verification code to your provided contact method. In the actual application, you would receive a real code; in this demo, the code will be displayed in a prompt.  
* **Enter Verification Code:** Input the received code in this field.  
* **Click Login:** Click this button to complete 2FA verification and enter the main program.  
* **Resend:** If you didn't receive the code or it expired, click this button to request a new one.  

> **【Screenshot 2: Two-Factor Authentication Dialog】**  
> ![alt text](image-1.png)  

### 2.3 User Registration

If you are a new user, click the "Register" button on the login screen.  

* **Username:** Enter your desired username.  
* **Password:** Set your account password.  
* **Confirm Password:** Re-enter the password for confirmation.  
* **Email:** Enter your email address.  
* **Phone Number:** Enter your phone number.  
* **Click Register:** After filling in all details, click "Register."  
* **Click Cancel:** Click "Cancel" to return to the login screen.  

> **【Screenshot 3: User Registration Dialog】**  
> ![alt text](image-2.png)  

### 2.4 Language Selection (Login Interface)

In the top-right menu bar of the login screen, you can select the interface language.  

* Click the "Language" menu.  
* Choose "Chinese" or "English."  

> **【Screenshot 4: Language Menu on Login Screen】**  
> ![alt text](image-3.png)

## 3. Main Interface Overview

After successful login, you will enter the application's main interface.  

> **【Screenshot 5: Main Interface - Dashboard】**  
> ![alt text](image-4.png)

The main interface consists of the following sections:  

### 3.1 Title Bar

Located at the top of the window, it displays the current module's title and includes macOS-style red-yellow-green control buttons (close, minimize, maximize/restore). You can drag the title bar to move the window.  

### 3.2 Menu Bar

Below the title bar, providing access to various functions:  

* **File:**  
  * **Import CSV:** Import transaction data in CSV format.  
  * **Export Data:** Export financial data as CSV or PDF.  
  * **Logout:** Securely log out and return to the login screen.  
  * **Exit:** Close the application.  
* **View:** Quickly switch between modules (Dashboard, Overview, Account, AI Chat).  
* **Language:** Toggle between Chinese and English.  
* **Help:**  
  * **About:** View version and developer information (links to GitHub README).  
  * **Help Contents:** Access documentation or the project homepage (links to GitHub).  

### 3.3 Toolbar

Below the menu bar, providing quick-access buttons for core modules:  

* **Dashboard**  
* **Overview**  
* **Account**  
* **AI Chat**  

Clicking a button switches the content area to the corresponding module. The selected button is highlighted with an underline.  

## 4. Core Function Modules

### 4.1 Dashboard

The dashboard is the default view after login, providing a quick financial overview.  

* **Greeting & Date:** Displays a welcome message and the current date.  
* **Month Selector:** Defaults to the current month (YYYY/MM). Click the date or dropdown arrow to select other months.  
* **Income/Expense Summary:** Shows total income and expenses for the selected month.  
* **Trend Chart:** A line chart displaying daily income (green) and expense (red) trends. Hover over data points to see amounts.  

> **【Screenshot 6: Dashboard Module】**  
> ![alt text](image-5.png)

### 4.2 Overview

This module helps analyze spending categories for a selected month.  

* **Month Selector:** Similar to the dashboard.  
* **Pie Chart:** Visualizes spending categories with colors for each (e.g., dining, transportation, shopping). Hover for details.  
* **Category List:** Displays category names and amounts beside the pie chart.  

> **【Screenshot 7: Overview Module】**  
> ![alt text](image-6.png)

### 4.3 Account

This module manages financial accounts (e.g., bank, Alipay, WeChat Pay) and transactions.  

* **Select Account:** Dropdown to choose an account.  
* **Account Balance:** Shows the current balance.  
* **Transaction Table:** Lists transactions with date, description, category, amount (green for income, red for expenses).  
* **Action Buttons:**  
  * **Add Income/Expense:** Opens a dialog to input transaction details.  
  * **Delete Transaction:** Removes selected transactions (with confirmation).  
  * **Add Account:** Creates a new account (name and type).  
  * **Edit Account:** Modifies account details.  
  * **Delete Account:** Removes an account and its transactions (with confirmation).  

> **【Screenshot 8: Account Module】**  
> ![alt text](image-7.png) 

> **【Screenshot 9: Add Transaction Dialog】**  
> ![alt text](image-8.png)

> **【Screenshot 10: Add Account Dialog】**  
> ![alt text](image-9.png)

### 4.4 AI Chat

This module allows conversations with an AI financial assistant for personalized advice.  

* **Chat Area:** Displays conversation history (user messages on the right, AI on the left with avatars).  
* **Input Box:** Type questions or commands at the bottom.  
* **Send Button:** Click the paper plane icon or press Enter to send.  
* **Quick Options:** Preset questions like "Suggestions," "Holiday planning," or "Future spending."  
* **DeepSeek AI Checkbox:** Toggle to use the advanced DeepSeek AI model (requires API key and internet).  

> **【Screenshot 11: AI Chat Module】**  
> ![alt text](image-10.png)  

## 5. Data Management

### 5.1 Import Data (Import CSV)

Import external transaction data via **File > Import CSV**.  

1. Select a CSV file with columns:  
   - `Date` (yyyy-MM-dd)  
   - `Type` ("Income" or "Expense")  
   - `Description`  
   - `Amount`  
   - `Category`  
2. Choose an existing account to associate the transactions.  
3. The system confirms successful imports or errors.  

> **【Screenshot 12: Data Import】**  
>![alt text](image-11.png)

### 5.2 Export Data

Backup or share data via **File > Export Data**.  

1. Choose format: CSV or PDF.  
2. Specify filename and location.  
3. **CSV:** Includes date, type, description, amount, category, and account.  
4. **PDF:** Generates a simple report with all transactions.  

> **【Screenshot 13: Data Export】**  
>![alt text](image-12.png)

## 6. Settings

### 6.1 Language Switching

Toggle the interface language via **Language > English/Chinese** in the menu bar.  

## 7. Help & About

* **About:** View version info and GitHub README link.  
* **Help Contents:** Open the project's GitHub page for documentation.  
