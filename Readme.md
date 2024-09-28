# Yoober - JDBC Project
---
Developed by
- [Irina Ignatenok](https://github.com/irinaignatenok)
- [Livio Reinoso](https://github.com/LivioDR)
---
## Project Overview

Yoober is an academic project developed for the Introduction to Databases course of the Mobile Application Development program (MAP1) at Fanshawe College, that simulates the backend database management system of a ride-sharing application, similar to Uber. 

This project implements various database operations using the JDBC library to interact with a SQL database.

## Features

- Manage passenger information
- Handle driver data and availability
- Process ride requests
- Store and retrieve ride history
- Implement a rating system for both drivers and passengers
- Generate reports and analytics

More information about the implemented features can be found in the [Progress and Pending](https://github.com/LivioDR/Yoober_Databases_JDBC_Project/blob/main/src/database/ProgressAndPending.md) file that was used as a guideline throughout the development of this project.

## Technology Stack

- Java
- JDBC (Java Database Connectivity)
- SQLite

## Setup and Installation

1. Clone the repository:
   ```
   git clone https://github.com/LivioDR/Yoober_Databases_JDBC_Project.git
   ```
2. Set up your SQL database and execute the `createFreshDatabase.sql` script to create the necessary tables.
3. Update the `App.java` file with your database connection details.
4. Ensure you have the correct JDBC driver for your database in the `lib/` directory.
5. Build the project using your preferred build tool.

## Contributing

This is an academic project and is not open to external contributions. However, feedback and suggestions are welcome.
