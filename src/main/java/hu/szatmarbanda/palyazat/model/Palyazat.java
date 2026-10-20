package hu.szatmarbanda.palyazat.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Palyazat {

    @Id
    private String urlHash;
    private String title;
    private String link;
    private LocalDateTime discoveredAt;

    public Palyazat() {}

    public Palyazat(String urlHash, String title, String link, LocalDateTime discoveredAt) {
        this.urlHash = urlHash;
        this.title = title;
        this.link = link;
        this.discoveredAt = discoveredAt;
    }

    public String getUrlHash() { return urlHash; }
    public String getTitle() { return title; }
    public String getLink() { return link; }
}