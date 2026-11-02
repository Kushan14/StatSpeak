# StatSpeak — The Student Feedback Quantifier

## Project Overview

StatSpeak is a Java 17 desktop application that analyzes student feedback from CSV files and provides insights into frequently occurring words and topics. It features a Swing-based interface with real-time visualization and report generation capabilities.

## Features

- Import and analyze CSV files containing student feedback
- Clean and normalize text data
- Count and rank keywords by frequency
- Display interactive charts and statistics
- Export results to CSV, text, and PDF formats
- Handle errors gracefully with informative messages

## Requirements

- Java 17 or later
- Gradle 8.0 or later

## Building and Running

### Prerequisites

1. Install Java 17: https://adoptium.net/
2. Download Gradle: https://gradle.org/ (8.5 or later)

### Build Instructions

```bash
# On Windows:
gradlew.bat assemble

# On macOS/Linux:
./gradlew assemble
```

This will create the executable JAR file at `build/libs/statspeak-1.0.0.jar`.

### Running the Application

```bash
# On Windows:
gradlew.bat run

# On macOS/Linux:
./gradlew run
```

### Sample Data

Sample feedback data is available in the `sample-data/` directory. These files demonstrate the application's capabilities with synthetic data.

## File Structure

```
StatSpeak/
├── build.gradle
├── gradlew, gradlew.bat
├── src/
│   ├── main/
│   │   ├── java/com/statspeak/
│   │   │   ├── app/
│   │   │   ├── model/
│   │   │   ├── importdata/
│   │   │   ├── analysis/
│   │   │   ├── ui/
│   │   │   ├── visualization/
│   │   │   └── reports/
│   │   └── resources/
│   └── test/
│       └── java/com/statspeak/
├── sample-data/
├── docs/
└── README.md
```

## Development Team

This project was developed using a collaborative Agent Team consisting of six specialized teammates:

- **CSV Import and Data Management**: Handles CSV file parsing and validation
- **Text Cleaning and Keyword Analysis**: Processes and analyzes text data
- **Desktop GUI and User Experience**: Builds the Swing interface
- **Visualization and Analytics**: Creates charts and visualizations
- **Report Generation and Sample Data**: Handles export functionality
- **Testing, Build, and Documentation**: Manages testing and documentation

Each teammate operates as an independent Claude instance with specialized responsibilities, communicating directly through a shared task management system.

## Privacy

All processing happens locally. No student feedback data is transmitted to external services. The application operates entirely offline after the build dependencies are downloaded.

## License

This project is developed as part of a demonstration of collaborative AI development using Claude's multi-agent capabilities.