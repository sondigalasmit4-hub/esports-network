package com.esports;

import jakarta.persistence.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepo) {
        return args -> {
            if (userRepo.count() == 0) {
                UserAccount p1 = new UserAccount();
                p1.setFullName("Aarav 'Vortex' Sharma");
                p1.setGamertag("Vortex_OP");
                p1.setEmail("vortex@esports.gg");
                p1.setPasswordHash(AuthUtil.hash("password123"));
                p1.setRole("GAMER");
                p1.setBio("Tier-1 Entry Fragger & Sniper. Looking for a salaried organization for upcoming Split.");
                p1.setDiscordHandle("Vortex#9901");

                p1.getGameStats().add(new GameStat("Valorant", "Radiant #142", 1.48, 64.5, "Duelist / Operator", 1250));
                p1.getGameStats().add(new GameStat("Call of Duty: Mobile", "Legendary (Top 500)", 2.85, 78.0, "Main Sniper / Slayer", 2400));
                userRepo.save(p1);

                UserAccount p2 = new UserAccount();
                p2.setFullName("Rohan 'Krypton' Patel");
                p2.setGamertag("KryptonIGL");
                p2.setEmail("krypton@esports.gg");
                p2.setPasswordHash(AuthUtil.hash("password123"));
                p2.setRole("GAMER");
                p2.setBio("Experienced In-Game Leader (IGL) with 3x LAN tournament appearances.");
                p2.setDiscordHandle("Krypton#1122");

                p2.getGameStats().add(new GameStat("Counter-Strike 2", "FACEIT Level 10 (2950 ELO)", 1.29, 58.2, "IGL / Support", 3100));
                p2.getGameStats().add(new GameStat("Valorant", "Immortal 3", 1.18, 55.4, "Controller / IGL", 900));
                userRepo.save(p2);

                UserAccount recruiter = new UserAccount();
                recruiter.setFullName("Vikram Singh (Team Soul Scout)");
                recruiter.setGamertag("OrgScout_Vikram");
                recruiter.setEmail("scout@org.gg");
                recruiter.setPasswordHash(AuthUtil.hash("password123"));
                recruiter.setRole("RECRUITER");
                recruiter.setBio("Head of Esports Scouting. Recruiting high-K/D rosters for international qualifiers.");
                recruiter.setDiscordHandle("ScoutVikram#0001");
                userRepo.save(recruiter);
            }
        };
    }
}

@Entity
@Table(name = "users")
class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String passwordHash;
    private String fullName;
    private String gamertag;
    private String role;
    private String bio;
    private String discordHandle;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private List<GameStat> gameStats = new ArrayList<>();

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getGamertag() { return gamertag; }
    public void setGamertag(String gamertag) { this.gamertag = gamertag; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getDiscordHandle() { return discordHandle; }
    public void setDiscordHandle(String discordHandle) { this.discordHandle = discordHandle; }
    public List<GameStat> getGameStats() { return gameStats; }
    public void setGameStats(List<GameStat> gameStats) { this.gameStats = gameStats; }
}

@Entity
@Table(name = "game_stats")
class GameStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String gameName;
    private String highestRank;
    private double kdRatio;
    private double winRate;
    private String mainRole;
    private int matchesPlayed;

    public GameStat() {}

    public GameStat(String gameName, String highestRank, double kdRatio, double winRate, String mainRole, int matchesPlayed) {
        this.gameName = gameName;
        this.highestRank = highestRank;
        this.kdRatio = kdRatio;
        this.winRate = winRate;
        this.mainRole = mainRole;
        this.matchesPlayed = matchesPlayed;
    }

    public Long getId() { return id; }
    public String getGameName() { return gameName; }
    public void setGameName(String gameName) { this.gameName = gameName; }
    public String getHighestRank() { return highestRank; }
    public void setHighestRank(String highestRank) { this.highestRank = highestRank; }
    public double getKdRatio() { return kdRatio; }
    public void setKdRatio(double kdRatio) { this.kdRatio = kdRatio; }
    public double getWinRate() { return winRate; }
    public void setWinRate(double winRate) { this.winRate = winRate; }
    public String getMainRole() { return mainRole; }
    public void setMainRole(String mainRole) { this.mainRole = mainRole; }
    public int getMatchesPlayed() { return matchesPlayed; }
    public void setMatchesPlayed(int matchesPlayed) { this.matchesPlayed = matchesPlayed; }
}

interface UserRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmailIgnoreCase(String email);
    List<UserAccount> findByRole(String role);
}

class AuthUtil {
    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] encoded = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : encoded) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
class EsportsController {

    private final UserRepository userRepo;

    public EsportsController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || userRepo.findByEmailIgnoreCase(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Email already registered or invalid."));
        }
        UserAccount user = new UserAccount();
        user.setEmail(email.trim());
        user.setPasswordHash(AuthUtil.hash(body.getOrDefault("password", "")));
        user.setFullName(body.getOrDefault("fullName", "Pro Player"));
        user.setGamertag(body.getOrDefault("gamertag", "Player"));
        user.setRole(body.getOrDefault("role", "GAMER"));
        user.setBio(body.getOrDefault("bio", "Esports athlete open to organization offers."));
        user.setDiscordHandle(body.getOrDefault("discordHandle", "Not Set"));

        UserAccount saved = userRepo.save(user);
        saved.setPasswordHash(null);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        Optional<UserAccount> userOpt = userRepo.findByEmailIgnoreCase(email);

        if (userOpt.isPresent() && userOpt.get().getPasswordHash().equals(AuthUtil.hash(password))) {
            UserAccount user = userOpt.get();
            user.setPasswordHash(null);
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password."));
    }

    @GetMapping("/players")
    public List<UserAccount> getAllPlayers() {
        List<UserAccount> gamers = userRepo.findByRole("GAMER");
        gamers.forEach(g -> g.setPasswordHash(null));
        return gamers;
    }

    @PutMapping("/players/{id}/profile")
    public ResponseEntity<?> updateProfile(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return userRepo.findById(id).map(user -> {
            user.setFullName(body.getOrDefault("fullName", user.getFullName()));
            user.setGamertag(body.getOrDefault("gamertag", user.getGamertag()));
            user.setBio(body.getOrDefault("bio", user.getBio()));
            user.setDiscordHandle(body.getOrDefault("discordHandle", user.getDiscordHandle()));
            UserAccount saved = userRepo.save(user);
            saved.setPasswordHash(null);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/players/{id}/stats")
    public ResponseEntity<?> saveOrUpdateGameStat(@PathVariable Long id, @RequestBody GameStat incomingStat) {
        return userRepo.findById(id).map(user -> {
            Optional<GameStat> existing = user.getGameStats().stream()
                    .filter(s -> s.getGameName().equalsIgnoreCase(incomingStat.getGameName()))
                    .findFirst();

            if (existing.isPresent()) {
                GameStat s = existing.get();
                s.setHighestRank(incomingStat.getHighestRank());
                s.setKdRatio(incomingStat.getKdRatio());
                s.setWinRate(incomingStat.getWinRate());
                s.setMainRole(incomingStat.getMainRole());
                s.setMatchesPlayed(incomingStat.getMatchesPlayed());
            } else {
                user.getGameStats().add(incomingStat);
            }

            UserAccount saved = userRepo.save(user);
            saved.setPasswordHash(null);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/players/{id}/stats/{statId}")
    public ResponseEntity<?> deleteGameStat(@PathVariable Long id, @PathVariable Long statId) {
        return userRepo.findById(id).map(user -> {
            user.getGameStats().removeIf(s -> s.getId().equals(statId));
            UserAccount saved = userRepo.save(user);
            saved.setPasswordHash(null);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }
}