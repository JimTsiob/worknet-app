# worknet-app
LinkedIn clone application for our E-commerce technologies MSc course. It was built using Spring Boot 3 and Android Studio with API 34 (Vanilla Java).

## Installation

In order to install and run our app you need to have the following:
- MySQL Workbench (would be good but optional)
- MySQL Server (default installation on server port 3306 is optimal)
- Java 17 SDK on Intellij
- Android Studio Jellyfish Edition (2023.3.1 Patch 1) with minimum SDK: API 34
(“UpsideDownCake”; Android 14.0).

Then you have to do the following:
1. Go to MySQL workbench or mysql console and create a database. Then add a schema
called “worknet”.
2. Open the project (worknet) in Intellij.
3. In the application.properties add the username and password you use to connect to your
database, through the workbench or the console.
4. In the same file, change spring.jpa.hibernate.ddl-auto=update to spring.jpa.hibernate.ddl-auto=create and run the app.
5. Once the application ends, close it and change it back to update, then re-run the app.
6. Now the server works!
7. Open the worknet-android folder with Android Studio
8. Create an AVD and add some photos, videos and mp3 files by using the given
instructions here: add_files_to_phone_instructions.txt.
9. Run the app and you’re good to go.
