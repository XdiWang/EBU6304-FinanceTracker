# AI-Empowered Personal Finance Tracker

This is a Java Swing-based desktop application designed to help users track their personal finances, featuring AI-assisted financial advice.

## Key Features

*   **User Management**: Supports user registration and login with hashed password security.
*   **Account Management**: Add, edit, and delete various types of financial accounts (e.g., bank accounts, Alipay, WeChat Pay).
*   **Transaction Logging**: Record income and expense transactions, linking them to specific accounts and categories.
*   **Category Management**: Built-in common transaction categories and support for user-defined categories.
*   **Data Visualization**:
    *   **Dashboard**: Displays monthly income/expense summaries and a daily income/expense trend chart.
    *   **Overview**: Shows categorized monthly expenses with a pie chart illustrating spending structure.
*   **AI Financial Assistant**:
    *   Automatic transaction categorization via local keyword matching (simulated AI).
    *   Provides saving suggestions, monthly expense predictions, and budget allocation advice (simulated AI).
    *   Integrates with **DeepSeek API** for advanced AI-powered financial chat consultations.
*   **Data Import/Export**: Supports importing transaction data from CSV files and exporting data to CSV or PDF formats.
*   **Multi-language Support**: Switch between Chinese and English interfaces.
*   **Font Optimization**: Includes an embedded Chinese font (`simhei.ttf`) to ensure proper display on various systems.

## Prerequisites

*   **JDK**: Version 11 or higher.
*   **IDE**: IntelliJ IDEA (Recommended).
*   **Maven**: Version 3.6.x or higher (often bundled with IntelliJ IDEA).
*   **Internet Connection**: Required for Maven to download dependencies and for the DeepSeek API functionality.

## IntelliJ IDEA Project Setup (with Maven)

1.  **Obtain Project Code**:
    *   If the project uses Git, clone the repository.
    *   Otherwise, extract the project code to a local folder.

2.  **Open Project in IntelliJ IDEA**:
    *   Launch IntelliJ IDEA.
    *   Select `File` -> `Open...` (or `Open Project...`).
    *   Navigate to the project's root directory (the one containing the `pom.xml` file) and select it.
    *   IntelliJ IDEA should automatically detect it as a Maven project. If prompted, choose "Open as Project" and trust the project.

3.  **Configure JDK**:
    *   Go to `File` -> `Project Structure...`.
    *   Under `Project Settings` -> `Project`, ensure a compatible JDK (11+) is selected for `SDK`.
    *   If no suitable JDK is listed, you can add one under `Platform Settings` -> `SDKs` by clicking `+`.

4.  **Maven Dependencies**:
    *   Maven will automatically download and manage the project dependencies defined in the `pom.xml` file.
    *   If dependencies are not resolved automatically, or if you modify `pom.xml`, you can force a re-import:
        *   Open the `Maven` tool window (usually on the right side of IDEA).
        *   Click the `Reload All Maven Projects` button (often a circular arrows icon).
    *   The key dependencies include: JFreeChart, Apache Commons CSV, Apache PDFBox, and Jackson libraries. These are specified in your `pom.xml`.

5.  **Configure Resource Folders**:
    *   For a standard Maven project, resources like fonts and images should be placed in `src/main/resources`.
    *   IntelliJ IDEA usually marks `src/main/resources` as a "Resources Root" automatically for Maven projects.
    *   The application expects the font at `/resources/fonts/simhei.ttf` and images like `/resources/images/user.png` relative to the classpath root. Ensure your `src/main/resources` directory contains `fonts/simhei.ttf` and `images/user.png` etc.

## Configuration

1.  **DeepSeek API Key**:
    *   To use the integrated DeepSeek AI chat feature, a valid API key is required.
    *   Open the file: `src/main/java/com/financetracker/service/DeepSeekAPIService.java`
    *   Locate the line:
        ```java
        private static final String API_KEY = "xxxxxxxxx"; // Replace with your API key
        ```
    *   Replace `"sk-a504d43d17754171bc4aecbc6a4e0dd2"` with your actual DeepSeek API key.
    *   **Note**: If this key is not configured or is invalid, the DeepSeek AI chat feature will not work correctly and may fall back to the local simulated AI.

2.  **Fonts**:
    *   The application includes `simhei.ttf` (a Chinese HeiTi font) in `src/main/resources/fonts/` to improve Chinese character display.
    *   `FontLoader.java` attempts to load this embedded font if the system lacks suitable Chinese fonts.

3.  **Language**:
    *   The application supports Chinese and English. The default language can be seen in `LanguageUtil.java`. User preferences are applied after login.
    *   Language switching is available in the menu bar on the login screen and the main application window.

## Running the Application

1.  **Locate the Main Class**: In the IntelliJ IDEA Project view, navigate to `src/main/java` -> `com.financetracker` -> `FinanceTrackerApp.java`.
2.  **Run**:
    *   Right-click on the `FinanceTrackerApp.java` file.
    *   Select `Run 'FinanceTrackerApp.main()'`.
3.  **Login**:
    *   The application will start with a login screen.
    *   A default test account is available:
        *   **Username**: `test`
        *   **Password**: `password`
    *   You can also register a new user.

## Usage Guide

*   **Navigation**: Switch between different modules (Dashboard, Overview, Account, AI Chat) using the menu bar or the toolbar buttons at the top of the main window.
*   **Dashboard**: View summaries of income, expenses, and trend charts. Select different months by clicking on the displayed date.
*   **Overview**: See a pie chart of expenses by category and a detailed list. Select different months by clicking on the displayed date.
*   **Account Panel**: Manage your financial accounts, and add/delete transactions.
    *   Select an account to view its balance and transaction history.
    *   Add income or expense transactions.
    *   Delete selected transactions.
    *   Add, edit, or delete accounts.
*   **AI Chat Panel**: Interact with the AI financial assistant.
    *   Choose to use DeepSeek AI (if the API key is configured) or the local simulated AI.
    *   Type your questions or use the quick option buttons.
*   **Data Import/Export**:
    *   **Import**: Use the `File` -> `Import CSV` menu to import transaction data. You will be prompted to select an account to associate the imported transactions with. The CSV file should typically have columns like: `Date,Type,Description,Amount,Category`. (Date format: `yyyy-MM-dd`).
    *   **Export**: Use the `File` -> `Export Data` menu to export all transaction data to a CSV or PDF file.
*   **Language Switching**: Change the interface language via the `Language` menu in the menu bar.

## Troubleshooting

*   **Chinese Characters Display as Squares or Gibberish**:
    *   Ensure the `simhei.ttf` font file is correctly located in `src/main/resources/fonts/`.
    *   `FontLoader.java` is designed to address this. You can run `FontTester.java` (in `com.financetracker.util`) to diagnose font issues.
*   **AI Chat (DeepSeek) Not Working**:
    *   Verify that the `API_KEY` in `DeepSeekAPIService.java` is correctly configured.
    *   Ensure your computer has internet access and can reach `https://api.deepseek.com/`.
    *   If the DeepSeek API is unavailable, the AI chat panel will fall back to the local `AIService.java` (simulated AI).
*   **Charts Not Displaying or Incorrect**:
    *   Ensure Maven has successfully downloaded the JFreeChart dependencies. Check the `Maven` tool window for any errors.
*   **`ClassNotFoundException` or `NoClassDefFoundError`**:
    *   This usually indicates that Maven failed to download or link a required dependency. Try `Reload All Maven Projects` in the Maven tool window. Check your `pom.xml` for correctness.
*   **CSV Import Errors**:
    *   Ensure the CSV file format matches the program's expectations (date format `yyyy-MM-dd`, column order, etc.).
    *   Check error messages for specific line numbers and issues.

This README should help you get the project set up and running smoothly!

