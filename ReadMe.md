# NKA Grant Monitor (Pályázatfigyelő)

A Spring Boot-based background service developed as a useful hobby and practice project, tailored for a real-world use case: monitoring cultural grants for my band (Szatmár Banda) and cultural association.

The application periodically monitors the National Cultural Fund (NKA) website, filters newly published grants using a multi-layered keyword system and a strict blacklist, and sends automated email alerts to ensure we never miss an opportunity for instrument acquisition, logistics, or cultural projects.

## Features

- Automated Web Scraping: Uses Jsoup to parse and extract newly published grants from the NKA portal.
- Smart Filtering: Multi-layered keyword analysis combined with a strict blacklist to filter out irrelevant news, decisions, and archive links.
- Database Persistence: Utilizes a local H2 database with Spring Data JPA to remember processed URLs via SHA-256 hashing, avoiding duplicate alerts.
- Email Notifications: Integrated JavaMailSender for real-time alerts.
- Linux Daemon Ready: Configured to run seamlessly as a background systemd service.

## Tech Stack

- Java 21
- Spring Boot 3.2.0 (Data JPA, Mail, Scheduling)
- H2 Database (File-based)
- Jsoup (HTML Parsing)
- Maven

## Configuration

The application relies on environment variables for sensitive data. Before running, ensure the following are set:
- MAIL_USERNAME: Your SMTP email address
- MAIL_PASSWORD: Your SMTP App Password (e.g., Google App Password)
- NOTIFY_EMAIL: The recipient's email address

## Building from Source

Since compiled files (like the target folder and jar binaries) are excluded from version control, you need to build the project locally:

1. Clone the repository and navigate into the project directory.
2. Package the application using Maven with the following command: mvn clean package
   This will generate the executable jar file inside the target directory.

## Running & Deployment (Linux systemd)

To run the application as a permanent background service on Linux: 

1. Create a deployment directory (~/palyazatfigyelo) and copy the built jar file there.
2. Create a systemd service file at /etc/systemd/system/palyazatfigyelo.service containing your user configuration, WorkingDirectory, ExecStart pointing to the jar, and the Environment variables for MAIL_USERNAME, MAIL_PASSWORD, and NOTIFY_EMAIL.
3. Enable and start the service using systemctl daemon-reload, systemctl enable palyazatfigyelo, and systemctl start palyazatfigyelo.