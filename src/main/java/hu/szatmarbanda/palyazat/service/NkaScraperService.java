package hu.szatmarbanda.palyazat.service;

import hu.szatmarbanda.palyazat.model.Palyazat;
import hu.szatmarbanda.palyazat.repository.PalyazatRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Base64;

@Service
public class NkaScraperService {

    private final PalyazatRepository palyazatRepository;
    private final NotificationService notificationService;

    // A konkrét kollégiumi aloldalak listája, amit a program sorra jár
    private final List<String> kollgiumUrls = Arrays.asList(
            "https://nka.hu/kiemelt-kategoriak/palyaztatas/kollegiumok-felhivasai/anyanyelvi-kultura-kollegiuma/",
            "https://nka.hu/kiemelt-kategoriak/palyaztatas/kollegiumok-felhivasai/eloado-muveszetek-kollegiuma/",
            "https://nka.hu/kiemelt-kategoriak/palyaztatas/kollegiumok-felhivasai/epitett-orokseg-kollegiuma/",
            "https://nka.hu/kiemelt-kategoriak/palyaztatas/kollegiumok-felhivasai/hagyomany-es-ismeretatadas-kollegiuma/",
            "https://nka.hu/kiemelt-kategoriak/palyaztatas/kollegiumok-felhivasai/kozgyujtemenyek-kollegiuma-2/",
            "https://nka.hu/kiemelt-kategoriak/palyaztatas/kollegiumok-felhivasai/kozossegi-programok-es-fesztivalok-kollegiuma/",
            "https://nka.hu/kiemelt-kategoriak/palyaztatas/kollegiumok-felhivasai/vizualis-muveszetek-kollegiuma-2/",
            "https://nka.hu/kiemelt-kategoriak/palyaztatas/kollegiumok-felhivasai/halmos-bela-program-kollegium/",
            "https://nka.hu/kiemelt-kategoriak/palyaztatas/kollegiumok-felhivasai/hangfoglalo-konnyuzene-tamogato-program-kollegiuma/",
            "https://nka.hu/kiemelt-kategoriak/palyaztatas/kollegiumok-felhivasai/kiemelt-kulturalis-programok-ideiglenes-kollegiuma/"
    );

    public NkaScraperService(PalyazatRepository palyazatRepository, NotificationService notificationService) {
        this.palyazatRepository = palyazatRepository;
        this.notificationService = notificationService;
    }

    // 1 óránként fut le a háttérben
    @Scheduled(fixedRate = 3600000)
    public void scrapeNka() {
        System.out.println("NKA Pályázatfigyelő keresés indul a kijelölt kollégiumi oldalakon...");

        for (String url : kollgiumUrls) {
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .get();

                Elements links = doc.select("a[href]");

                for (Element linkElement : links) {
                    String title = linkElement.text().trim();
                    String link = linkElement.attr("abs:href");

                    // Csak a releváns hosszúságú és nem üres linkeket nézzük
                    if (title.length() < 10 || link.isEmpty()) {
                        continue;
                    }

                    String lowerTitle = title.toLowerCase();
                    String lowerLink = link.toLowerCase();

                    // Ha archív vagy irreleváns admin hír, átugorjuk
                    if (isBlacklisted(lowerTitle, lowerLink)) {
                        continue;
                    }

                    String id = generateHash(link);

                    // Mivel eleve releváns kollégiumban vagyunk, minden aktív felhívást és eszközt/turnét szűrünk
                    if (!palyazatRepository.existsById(id) && isGrantOrRelevant(lowerTitle)) {
                        Palyazat newGrant = new Palyazat(id, title, link, LocalDateTime.now());
                        palyazatRepository.save(newGrant);
                        notificationService.sendAlert(newGrant);
                    }
                }
            } catch (Exception e) {
                System.err.println("Hiba a következő URL feldolgozása közben: " + url + " -> " + e.getMessage());
            }
        }
    }

    private boolean isBlacklisted(String lowerTitle, String lowerLink) {
        return lowerTitle.contains("kinevezték") ||
                lowerTitle.contains("változások") ||
                lowerTitle.contains("döntés") ||
                lowerTitle.contains("közlemény") ||
                lowerTitle.contains("kérdőív") ||
                lowerLink.contains("archiv") ||
                lowerLink.contains("dontesek") ||
                lowerLink.contains("hirek");
    }

    private boolean isGrantOrRelevant(String lower) {
        // Mivel eleve zenei/kulturális kollégium oldalán vagyunk, szinte minden pályázati kiírás jó nekünk,
        // de kiszűrjük a teljesen idegen (pl. tisztán építészeti vagy könyvkiadási) dolgokat,
        // miközben a járműveket, turnékat, külföldöt és eszközöket preferáljuk.

        boolean isNotCompletelyIrrelevant = !lower.contains("építészeti") &&
                !lower.contains("képzőművészeti kiállítás");

        boolean isVehicleOrGear = lower.contains("kisbusz") ||
                lower.contains("gépjármű") ||
                lower.contains("gépkocsi") ||
                lower.contains("eszköz") ||
                lower.contains("hangtechnika");

        boolean isGeneralGrant = lower.contains("pályázat") ||
                lower.contains("felhívás") ||
                lower.contains("támogatás") ||
                lower.contains("program") ||
                lower.contains("turné") ||
                lower.contains("fesztivál") ||
                lower.contains("külföld") ||
                lower.contains("nemzetközi");

        return isNotCompletelyIrrelevant && (isVehicleOrGear || isGeneralGrant);
    }

    private String generateHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Hiba a hash generálásakor", e);
        }
    }
}