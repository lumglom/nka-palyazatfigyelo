# NKA Grant Monitor (Pályázatfigyelő)

A Spring Boot-based background service developed as a useful hobby and practice project, tailored for a real-world use case: monitoring cultural grants for my band (Szatmár Banda) and cultural association.

The application periodically monitors the National Cultural Fund (NKA) website, filters newly published grants using a multi-layered keyword system and a strict blacklist, and sends automated email alerts to ensure we never miss an opportunity for instrument acquisition, logistics, or cultural projects.

## Features

* **Automated Web Scraping:** Uses `Jsoup` to parse and extract newly published grants from the NKA portal.
* **Smart Filtering:** Multi-layered keyword analysis combined with a strict blacklist to filter out irrelevant news, decisions, and archive links.
* **Database Persistence:** Utilizes a local `H2` database with `Spring Data JPA` to remember processed URLs via SHA-256 hashing, avoiding duplicate alerts.
* **Email Notifications:** Integrated `JavaMailSender` for real-time alerts.
* **Linux Daemon Ready:** Configured to run seamlessly as a background `systemd` service.

## Tech Stack

* Java 21
* Spring Boot 3.2.0 (Data JPA, Mail, Scheduling)
* H2 Database (File-based)
* Jsoup (HTML Parsing)
* Maven

## Configuration

The application relies on environment variables for sensitive data. Before running, ensure the following are set:
* `MAIL_USERNAME`: Your SMTP email address
* `MAIL_PASSWORD`: Your SMTP App Password (e.g., Google App Password)
* `NOTIFY_EMAIL`: The recipient's email address

## Building from Source

Since compiled files (like the `target/` folder and `.jar` binaries) are excluded from version control, you need to build the project locally:

1. Clone the repository:
   ```bash
   git clone https://github.com/YOUR_USERNAME/nka-palyazatfigyelo.git
   cd nka-palyazatfigyelo

2. Package the application using Maven:
mvn clean package

This will generate the executable nka-palyazatfigyelo-1.0-SNAPSHOT.jar inside the target/ directory.

Running & Deployment (Linux systemd)

To run the application as a permanent background service on Linux:

1. Create a deployment directory and copy the built .jar file:
    mkdir -p ~/palyazatfigyelo
    cp target/nka-palyazatfigyelo-1.0-SNAPSHOT.jar ~/palyazatfigyelo/
2. Create a systemd service file (/etc/systemd/system/palyazatfigyelo.service):
   [Unit]
   Description=NKA Palyazatfigyelo
   After=network.target

[Service]
User=your_username
WorkingDirectory=/home/your_username/palyazatfigyelo
ExecStart=/usr/bin/java -jar /home/your_username/palyazatfigyelo/nka-palyazatfigyelo-1.0-SNAPSHOT.jar
Restart=always
RestartSec=10
Environment="MAIL_USERNAME=your_email@gmail.com"
Environment="MAIL_PASSWORD=your_app_password"
Environment="NOTIFY_EMAIL=your_email@gmail.com"

[Install]
WantedBy=multi-user.target


3. Enable and start the service:
sudo systemctl daemon-reload
sudo systemctl enable palyazatfigyelo
sudo systemctl start palyazatfigyelo
sudo systemctl status palyazatfigyelo
