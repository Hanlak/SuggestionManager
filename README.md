# Stock Suggestion Manager

Stock Suggestion Manager is a Spring-based web application designed to assist stock market analysts and enthusiasts in
managing stock suggestions. The application allows users to track buy or sell suggestions and provides an archive of
share suggestions, catering especially to naive retail traders.

## Features

### User Management

- Group Admins and Members can register themselves in the system.
- Admins and Members can log in to the system and maintain an account profile.
- Both roles can edit their account information.

### Group Management

- Users can create a group and become the default admin.
- Admins and Members can view access requests on the Requests tab.
- Members can send a request for data access.

### Suggestions Data

- Admins and Members can view the data.
- Only users with Admin role can delete, update, or add data.
- Users with Member roles are restricted to viewing the data.

### Vote on Suggestions

- Users with Member roles can vote(like) or down vote(unlike) a suggestion.
- Confidence levels are generated based on votes, used to sort and highlight the data.
- Top-rated suggestions are displayed by default.

## Project Board

For a detailed view of the project board with user stories, tasks, and progress, you can visit
our [GitHub Project Board](https://github.com/users/Hanlak/projects/2).

## Prerequisites

Make sure you have the following tools installed on your machine:

- [IntelliJ IDEA](https://www.jetbrains.com/idea/) - Integrated Development Environment
- [Git](https://git-scm.com/) - Version Control System
- [MySQL Workbench 8.0](https://www.mysql.com/products/workbench/) - Database Design and Administration Tool
- [Java 1.8](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html) - Java Development Kit
- [Maven 3.6](https://maven.apache.org/download.cgi) or higher - Dependency Management

## Technologies Used

- **Java 8:** Programming language for the backend development.
- **Maven:** Dependency management for the project.
- **Spring MVC:** Framework for building web applications.
- **HTML, CSS, JavaScript:** Frontend technologies for building the user interface.
- **Spring JPA:** Part of the Spring Data project, simplifying data access using JPA.
- **Spring Security:** Provides authentication and access control for the application.
- **MapStruct:** Library for mapping Java beans.
- **Lombok:** Library to reduce boilerplate code in Java classes.
- **Spring Boot:** Framework for creating standalone, production-grade Spring-based applications.

## Clone the Project to IntelliJ IDEA

To clone the project to IntelliJ IDEA, follow these steps:

1. Open IntelliJ IDEA.

2. Click on `File` in the top-left corner.

3. Select `New` and then choose `Project from Version Control`.

4. Choose `Git`.

5. In the "Git Repository URL" field, enter the repository URL.

### Lombok Annotation Processing

To use Lombok features in IntelliJ IDEA, follow these steps:

1. **Install the Lombok Plugin:**

    - Open IntelliJ IDEA.
    - Navigate to `File` > `Settings`.
    - In the Settings dialog, select `Plugins`.
    - In the Plugins window, click on `Marketplace`.
    - Search for "Lombok" and click `Install` next to the Lombok plugin.

2. **Enable Annotation Processing:**

    - Navigate to `File` > `Project Structure`.
    - Select `Modules` on the left.
    - Under the `Sources` tab, check the box for `Enable annotation processing`.

Now, you should be able to use Lombok annotations in your project.

## Configuration

### Database Configuration

Assuming the default database password is `root`, no additional configuration is required for the database. The
application will use this password to connect to the MySQL database.

- run this query > create database stocksuggestions;

### Mail Configuration

For mail configuration, set the following environment variables in your run configuration:

- `mail_sender_user` - Your email address for sending emails.
- `mail_sender_pass` - Your email password(Create App password from 2-factor authentication from gmail account)
