# Disaster Relief Management System

A Java console application for managing disaster victims, supplies, skills, medical records, inquiries, locations, cultural requirements, and family relationships.

This project was built for `ENSF 380` and uses a PostgreSQL database for storing application data.

## Features

- Add, view, update, soft delete, and hard delete disaster victims
- Manage medical records for victims
- Add and manage supplies, including allocation and expiry tracking
- Track victim skills and skill categories
- Record cultural or religious requirements
- Manage family relationships
- Create and manage inquiries
- Manage shelter and support locations
- Log actions to `30089446/data/action_log.txt`

## Project Structure

```text
30089446/
  src/main/java/edu/ucalgary/oop/   Java source files
  src/main/resources/                database config file
  data/                             action log output
project.sql                         main database setup script
grading.sql                         grading/test database data
UML.png / UML.pdf                   project UML files
```

## Main Components

- `Main.java`: starts the program and shows the main menu
- `Controller` classes: handle user input and menu actions
- `Service` classes: contain validation and business logic
- `Repository` classes: handle database queries
- Model classes such as `Person`, `DisasterVictim`, `Supply`, `Skill`, and `VictimSkill`

## Database Setup

The application connects to PostgreSQL using the settings in:

`30089446/src/main/resources/DatabaseCreds.properties`

Current values:

```properties
url=jdbc:postgresql://localhost:5432/ensf380project
user=oop
password=ucalgary
```

To set up the database:

1. Make sure PostgreSQL is installed and running.
2. Open `psql`.
3. Run the schema/data script:

```sql
\i '/absolute/path/to/project.sql'
```

This script creates the `ensf380project` database, creates the tables, and inserts sample data.

## How To Run

Because this repo does not include a Maven or Gradle build file, the easiest way to run it is from an IDE such as IntelliJ IDEA.

1. Open the project in your IDE.
2. Make sure PostgreSQL is running and the database has been created with `project.sql`.
3. Confirm the database credentials in `DatabaseCreds.properties`.
4. Run `30089446/src/main/java/edu/ucalgary/oop/Main.java`.

The program will open a console menu with options for:

- Victim Management
- Supply Management
- Skill Registry
- Cultural or Religious Requirements
- Inquiry Management
- Family Relationship Management
- Location Management

## Testing

The project includes JUnit test files for many model and service classes.

Examples include:

- `PersonTest`
- `MedicalRecordServiceTest`
- `SkillServiceTest`
- `VictimServiceTest`
- `SupplyTest`

These tests can be run from your IDE's test runner.

## Notes

- The project uses a layered design with controller, service, and repository classes.
- The database is reset when `project.sql` is run again.
- Some provided sample data is only for development. The grading database may use different data.

## Author

Noor Ali
