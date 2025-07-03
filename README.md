# D-accounts
My system

# D-Accounts Application Documentation

## Overview
D-Accounts is a Windows desktop application (Java/JavaFX) to help you manage your finances, study subjects, courses, and daily discipline. It features:
- Finance tracking and automatic splitting
- Subject and course management
- Personalized timetable generation
- Daily enforcement for study discipline
- Settings for free time and semester/season
- MySQL database backend

## Setup Instructions
1. **Install Java (JDK 17+) and MySQL Server**
2. **Add dependencies:**
   - JavaFX SDK (controls, fxml)
   - MySQL JDBC Driver
3. **Import the database schema:**
   - Use the provided `resources/daccounts_schema.sql` to create the required tables in your MySQL server.
4. **Add your user to the `users` table** (no registration in-app).
5. **Run the application:**
   - On first run, enter your MySQL connection details.
   - Set your free time and semester/season in Settings.

## Usage
- **Login:** Use your credentials from the `users` table.
- **Dashboard:** Access finance, subjects, courses, timetable, and settings.
- **Finance Tracker:** Enter payment amounts; splits are saved and viewable.
- **Subject/Course Manager:** Add and view your subjects/courses.
- **Timetable Manager:** Generate a 7-day schedule. Double-click a row to mark a study task as completed.
- **Enforcement:** If you have incomplete study tasks for today, the dashboard is locked until you complete them.
- **Settings:** Set your daily free time and semester/season dates.

## Customization
- The timetable algorithm always schedules language subjects (e.g., Espanol) daily.
- All settings are stored in `settings.properties`.

## Security Note
- Passwords are stored as plain text for demo. Use proper hashing in production.

## Support
For issues or feature requests, update the code or contact the developer.
