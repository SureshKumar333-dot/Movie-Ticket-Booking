package com.cineverse.config;

import com.cineverse.model.*;
import com.cineverse.repository.*;
import com.cineverse.util.JsonUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final TheatreRepository theatreRepository;
    private final HallRepository hallRepository;
    private final ShowRepository showRepository;
    private final BookingRepository bookingRepository;
    private final CancellationRepository cancellationRepository;
    private final BookingChangeRepository changeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            MovieRepository movieRepository,
            TheatreRepository theatreRepository,
            HallRepository hallRepository,
            ShowRepository showRepository,
            BookingRepository bookingRepository,
            CancellationRepository cancellationRepository,
            BookingChangeRepository changeRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.theatreRepository = theatreRepository;
        this.hallRepository = hallRepository;
        this.showRepository = showRepository;
        this.bookingRepository = bookingRepository;
        this.cancellationRepository = cancellationRepository;
        this.changeRepository = changeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            seedUsers();
        }
        if (movieRepository.count() < 10) {
            seedMovies();
        }
        if (theatreRepository.count() < 14 || hallRepository.count() < 23) {
            seedTheatresAndHalls();
        }
        if (showRepository.count() == 0) {
            seedShows();
        }
        if (bookingRepository.count() == 0) {
            seedBookings();
        }
        if (cancellationRepository.count() == 0) {
            seedCancellations();
        }
        if (changeRepository.count() == 0) {
            seedChanges();
        }
    }

    private void seedUsers() {
        saveUser("A001", "Admin User", "admin@cineverse.com", "admin123", "9000000001", "CBE", Role.ROLE_ADMIN);
        saveUser("M001", "Theatre Manager", "manager@cineverse.com", "manager123", "9000000002", "CBE", Role.ROLE_MANAGER);
        saveUser("C001", "Arjun Kumar", "arjun@gmail.com", "arjun123", "9876543210", "CBE", Role.ROLE_CUSTOMER);
        saveUser("C002", "Priya Lakshmi", "priya@gmail.com", "priya123", "9123456789", "CHN", Role.ROLE_CUSTOMER);
        saveUser("C003", "Karthik Raj", "karthik@gmail.com", "karthik123", "9345678901", "CBE", Role.ROLE_CUSTOMER);
    }

    private void saveUser(String id, String name, String email, String rawPassword, String phone, String city, Role role) {
        if (userRepository.existsById(id)) return;
        User u = new User();
        u.setId(id);
        u.setName(name);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setPhone(phone);
        u.setCity(city);
        u.setAddress("");
        u.setAvatar(name.substring(0, 1));
        u.setJoinedOn(LocalDate.of(2024, 1, 10));
        u.setRole(role);
        userRepository.save(u);
    }

    private void seedMovies() {
        saveMovie("M01", "Leader", "Tamil", "Action,Drama", "2h 30m", "7.9", "new",
                "A young leader rises against corruption and fights to bring justice and change to society through bold political moves.",
                "Aadhi, Nassar, Ramya Krishnan ...", "K. Selva", "./leader.webp", 180, 280);

        saveMovie("M02", "Dhurandhar: The Revenge", "Tamil", "Action,Thriller", "3h 10m", "8.3", "hot",
                "Dhurandhar The Revenge introduces Jaskirat Singh Rangi, tracing the chain of events that compel him to become Hamza Ali Mazari, and follows his rise as he operates deep inside Pakistan.",
                "Ranveer Singh, Sanjay Dutt, Madhavan ...", "Lokesh Kanagaraj", "./dhurandhar.webp", 170, 260);

        saveMovie("M03", "Happyraj", "Tamil", "Comedy,Drama", "2h 20m", "7.2", "",
                "Happy Raj is a Tamil movie starring G.V. Prakash Kumar, Abbas, Sri Gouri Priya and George Maryan in prominent roles. It is written and directed by Maria Raja Elanchezian.",
                "G.V Prakash, Sri Gouri Priya, Abbas ...", "Maria Raj", "./happyraj.webp", 200, 320);

        saveMovie("M04", "Kaalidas 2", "Tamil", "Crime,Action,Thriller", "2h 15m", "7.8", "new",
                "After battling inner demons, Kaalidas, Part 2, turns more complicated as cop Kaalidas faces eerie murders striking across the city. Veiled provocateur Stephen unleashes deadly chaos inside and out, forcing a twisted hunt for crime, punishment, and the hidden truth.",
                "Bharath, Ajay Karthi, Abarnathi ....", "Sri Senthil", "./kaalidas.webp", 160, 240);

        saveMovie("M05", "Biker", "Tamil", "Action,Thriller", "2h 10m", "7.0", "",
                "A family's pride and conflict collide as one man chases motocross glory, driven by grit, spirit, and the edge of danger.",
                "Sharwanand, Malavika Nair ...", "Abhilash Reddy", "./biker.webp", 180, 280);

        saveMovie("M06", "Thaai Kizhavi", "Tamil", "Drama,Family", "2h 25m", "8.1", "new",
                "Greedy sons, initially awaiting their paralysed mother's death, frantically try to keep her alive after discovering she holds a valuable treasure.",
                "Raadhika Sarathkumar, Singam Puli ...", "Sivakumar", "thaai.webp", 170, 260);

        saveMovie("M07", "Satan", "Tamil", "Horror,Thriller", "2h 45m", "8.0", "hot",
                "During the era of the East India Company, a failed witchcraft ritual in Asthinapuram unleashes a dark curse that haunts the village for generations. Years later, a young man must confront this terrifying past to save the woman he loves from its deadly grip.",
                "Fredrick John, Ayraa Palak ...", "Manikandan", "satan.webp", 200, 320);

        saveMovie("M08", "Youth", "Tamil", "Romance,Drama", "2h 05m", "7.4", "",
                "Youth follows 15-year-old Praveen as he enters adolescence determined to find true love before school ends. Through a series of relationships and heartbreaks, he gradually discovers the real meaning of love, shaping his maturity and outlook on life.",
                "Ken Karunas, Anishma Anilkumar, Meenakshi Dinesh ...", "Ken Karunas", "youth.webp", 160, 240);

        saveMovie("M09", "Project Hail Mary", "English", "Sci-Fi,Adventure", "2h 40m", "8.5", "new",
                "Science teacher Ryland Grace wakes up alone on a spaceship, light-years from Earth. As his memory returns, he uncovers a mission to stop a mysterious substance killing the Sun and save Earth. An unexpected friendship may be the key.",
                "Ryan Gosling, Sandra Huller ...", "Phil Lord, Christopher Miller", "./projecthail.webp", 150, 230);

        saveMovie("M10", "Vaazha 2", "Tamil", "Comedy,Drama", "2h 30m", "7.7", "",
                "Four friends, Hashir, Alan, Ajin and Vinayak are considered losers and troublemakers by parents, family, and the school management. They face immense social pressure as they reach adulthood, which embarks them on an emotional journey of self-discovery and acceptance.",
                "Hashir H, Alan Bin Siraj, Ajin Joy ...", "Savin Sa", "./vaazha2.webp", 200, 320);
    }

    private void saveMovie(String id, String title, String lang, String genre, String duration, String rating, String badge,
                           String description, String cast, String director, String poster, int std, int prem) {
        Movie m = movieRepository.findById(id).orElseGet(Movie::new);
        m.setId(id);
        m.setTitle(title);
        m.setLanguage(lang);
        m.setGenre(genre);
        m.setDuration(duration);
        m.setRating(rating);
        m.setBadge(badge);
        m.setDescription(description);
        m.setCastMembers(cast);
        m.setDirector(director);
        m.setPoster(poster);
        m.setPriceStandard(std);
        m.setPricePremium(prem);
        movieRepository.save(m);
    }

    private void seedTheatresAndHalls() {
        // CBE
        saveTheatre("T01", "PVR Cinemas", "CBE", "Brookefields Mall, Neelikonampalayam", "0422-4567890");
        saveTheatre("T02", "INOX", "CBE", "Fun Republic Mall, Avinashi Road", "0422-3456789");
        saveTheatre("T03", "KG Cinemas", "CBE", "DB Road, R.S. Puram", "0422-2345678");
        saveTheatre("T04", "Cinepolis", "CBE", "Prozone Mall, Avinashi Road", "0422-1234567");

        // CHN
        saveTheatre("T05", "SPI Sathyam", "CHN", "Royapettah High Road", "044-4567890");
        saveTheatre("T06", "PVR Phoenix", "CHN", "Phoenix Mall, Velachery", "044-3456789");
        saveTheatre("T07", "Rohini Silver Screens", "CHN", "Koyambedu, Chennai", "044-2345678");
        saveTheatre("T08", "AGS Cinemas", "CHN", "Villivakkam, Chennai", "044-1234567");

        // Other cities
        saveTheatre("T09", "Rajshree Cinemas", "MDU", "Bypass Road, Anna Nagar", "0452-4567890");
        saveTheatre("T10", "PVR Trichy", "TRY", "Cauvery Mall, Thillai Nagar", "0431-4567890");
        saveTheatre("T11", "PVR Salem", "SLM", "Salem Junction Mall", "0427-4567890");
        saveTheatre("T12", "PVR Tirunelveli", "TNV", "Tirunelveli Junction Mall", "0462-4567890");
        saveTheatre("T13", "PVR Vellore", "VLR", "VGP Arcade, Katpadi Road", "0416-4567890");
        saveTheatre("T14", "PVR Erode", "ERD", "ESS Forum Mall, Erode", "0424-4567890");

        // Halls
        saveHall("H01", "IMAX Hall", "T01", 120, List.of("IMAX", "Dolby Atmos"), List.of("A", "B", "C", "D", "E", "F", "G", "H"), 15);
        saveHall("H02", "4DX Hall", "T01", 80, List.of("4DX", "Vibration"), List.of("A", "B", "C", "D", "E"), 16);
        saveHall("H03", "Gold Class", "T01", 40, List.of("Recliner", "Premium"), List.of("A", "B", "C", "D"), 10);

        saveHall("H04", "Screen 1", "T02", 100, List.of("4K", "Dolby"), List.of("A", "B", "C", "D", "E", "F"), 17);
        saveHall("H05", "Screen 2", "T02", 80, List.of("3D"), List.of("A", "B", "C", "D", "E"), 16);

        saveHall("H06", "Hall 1", "T03", 90, List.of("Dolby"), List.of("A", "B", "C", "D", "E", "F"), 15);
        saveHall("H07", "Hall 2", "T03", 70, List.of("3D"), List.of("A", "B", "C", "D", "E"), 14);

        saveHall("H08", "Screen 1", "T04", 110, List.of("VIP", "3D"), List.of("A", "B", "C", "D", "E", "F", "G"), 16);

        saveHall("H09", "IMAX Hall", "T05", 150, List.of("IMAX", "Premium"), List.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J"), 15);
        saveHall("H10", "Screen 2", "T05", 100, List.of("4K", "Dolby Atmos"), List.of("A", "B", "C", "D", "E", "F"), 17);

        saveHall("H11", "Screen 1", "T06", 120, List.of("IMAX", "4DX"), List.of("A", "B", "C", "D", "E", "F", "G", "H"), 15);
        saveHall("H12", "Gold Hall", "T06", 50, List.of("Recliner", "Bar"), List.of("A", "B", "C", "D", "E"), 10);

        saveHall("H13", "4K/ATMOS Hall", "T07", 90, List.of("4K", "Dolby Atmos"), List.of("A", "B", "C", "D", "E", "F"), 15);
        saveHall("H14", "RGB ATMOS Hall", "T08", 100, List.of("RGB", "Dolby Atmos"), List.of("A", "B", "C", "D", "E", "F"), 17);

        saveHall("H15", "Hall 1", "T09", 100, List.of("Dolby Atmos"), List.of("A", "B", "C", "D", "E", "F"), 17);
        saveHall("H16", "Hall 2", "T09", 80, List.of("3D"), List.of("A", "B", "C", "D", "E"), 16);

        saveHall("H17", "Screen 1", "T10", 100, List.of("4DX", "Dolby"), List.of("A", "B", "C", "D", "E", "F"), 17);
        saveHall("H18", "Screen 2", "T10", 80, List.of("3D"), List.of("A", "B", "C", "D", "E"), 16);

        saveHall("H19", "Screen 1", "T11", 110, List.of("IMAX", "Dolby"), List.of("A", "B", "C", "D", "E", "F", "G"), 16);
        saveHall("H20", "Screen 2", "T11", 80, List.of("3D"), List.of("A", "B", "C", "D", "E"), 16);

        saveHall("H21", "Screen 1", "T12", 90, List.of("4K", "Dolby Atmos"), List.of("A", "B", "C", "D", "E", "F"), 15);
        saveHall("H22", "Screen 1", "T13", 80, List.of("Dolby"), List.of("A", "B", "C", "D", "E"), 16);
        saveHall("H23", "Screen 1", "T14", 100, List.of("IMAX", "Recliner"), List.of("A", "B", "C", "D", "E", "F"), 17);
    }

    private void saveTheatre(String id, String name, String city, String location, String phone) {
        Theatre t = theatreRepository.findById(id).orElseGet(Theatre::new);
        t.setId(id);
        t.setName(name);
        t.setCity(city);
        t.setLocation(location);
        t.setPhone(phone);
        theatreRepository.save(t);
    }

    private void saveHall(String id, String name, String theatreId, int seats, List<String> features, List<String> rows, int seatsPerRow) {
        Hall h = hallRepository.findById(id).orElseGet(Hall::new);
        h.setId(id);
        h.setName(name);
        h.setTheatreId(theatreId);
        h.setTotalSeats(seats);
        h.setFeatures(JsonUtil.toJson(features));
        h.setRows(JsonUtil.toJson(rows));
        h.setSeatsPerRow(seatsPerRow);
        hallRepository.save(h);
    }

    private void seedShows() {
        LocalDate today = LocalDate.now();
        int showSeq = 1;

        // Seed 15 days of shows starting from today
        for (int dayOffset = 0; dayOffset < 15; dayOffset++) {
            LocalDate date = today.plusDays(dayOffset);
            String dateStr = date.toString();

            // M01 (Leader)
            saveShow(String.format("S%04d", showSeq++), "M01", "T01", "H01", "CBE", dateStr, "10:00 AM", 112, "Tamil", "IMAX");
            saveShow(String.format("S%04d", showSeq++), "M01", "T01", "H01", "CBE", dateStr, "02:15 PM", 120, "Tamil", "IMAX");
            saveShow(String.format("S%04d", showSeq++), "M01", "T01", "H01", "CBE", dateStr, "07:45 PM", 98,  "Tamil", "IMAX");
            saveShow(String.format("S%04d", showSeq++), "M01", "T02", "H04", "CBE", dateStr, "03:25 PM", 85,  "Tamil", "4K Dolby");
            saveShow(String.format("S%04d", showSeq++), "M01", "T05", "H09", "CHN", dateStr, "10:00 AM", 145, "Tamil", "IMAX");
            saveShow(String.format("S%04d", showSeq++), "M01", "T05", "H09", "CHN", dateStr, "03:30 PM", 130, "Tamil", "IMAX");
            saveShow(String.format("S%04d", showSeq++), "M01", "T07", "H13", "CHN", dateStr, "02:45 PM", 80,  "Tamil", "4K ATMOS");
            saveShow(String.format("S%04d", showSeq++), "M01", "T09", "H15", "MDU", dateStr, "10:30 AM", 95,  "Tamil", "Dolby");
            saveShow(String.format("S%04d", showSeq++), "M01", "T10", "H17", "TRY", dateStr, "11:00 AM", 90,  "Tamil", "4DX Dolby");
            saveShow(String.format("S%04d", showSeq++), "M01", "T11", "H19", "SLM", dateStr, "10:00 AM", 100, "Tamil", "IMAX");

            // M02 (Dhurandhar: The Revenge)
            saveShow(String.format("S%04d", showSeq++), "M02", "T01", "H02", "CBE", dateStr, "11:00 AM", 75,  "Tamil", "4DX");
            saveShow(String.format("S%04d", showSeq++), "M02", "T01", "H02", "CBE", dateStr, "06:30 PM", 60,  "Tamil", "4DX");
            saveShow(String.format("S%04d", showSeq++), "M02", "T02", "H05", "CBE", dateStr, "09:30 AM", 70,  "Tamil", "3D");
            saveShow(String.format("S%04d", showSeq++), "M02", "T05", "H09", "CHN", dateStr, "01:30 PM", 140, "Tamil", "IMAX");
            saveShow(String.format("S%04d", showSeq++), "M02", "T06", "H11", "CHN", dateStr, "10:30 AM", 110, "Tamil", "IMAX 4DX");
            saveShow(String.format("S%04d", showSeq++), "M02", "T09", "H16", "MDU", dateStr, "11:30 AM", 78,  "Tamil", "3D");

            // M03 (Happyraj)
            saveShow(String.format("S%04d", showSeq++), "M03", "T03", "H06", "CBE", dateStr, "10:00 AM", 88,  "Tamil", "Dolby");
            saveShow(String.format("S%04d", showSeq++), "M03", "T03", "H06", "CBE", dateStr, "02:00 PM", 85,  "Tamil", "Dolby");
            saveShow(String.format("S%04d", showSeq++), "M03", "T06", "H11", "CHN", dateStr, "02:00 PM", 115, "Tamil", "IMAX 4DX");
            saveShow(String.format("S%04d", showSeq++), "M03", "T07", "H13", "CHN", dateStr, "11:59 PM", 85,  "Tamil", "4K ATMOS");

            // M04 (Kaalidas 2) — PRIMARY TARGET
            saveShow(String.format("S%04d", showSeq++), "M04", "T04", "H08", "CBE", dateStr, "10:30 AM", 102, "Tamil", "VIP 3D");
            saveShow(String.format("S%04d", showSeq++), "M04", "T04", "H08", "CBE", dateStr, "02:30 PM", 110, "Tamil", "VIP 3D");
            saveShow(String.format("S%04d", showSeq++), "M04", "T04", "H08", "CBE", dateStr, "05:45 PM", 95,  "Tamil", "VIP 3D");
            saveShow(String.format("S%04d", showSeq++), "M04", "T04", "H08", "CBE", dateStr, "09:30 PM", 80,  "Tamil", "VIP 3D");
            saveShow(String.format("S%04d", showSeq++), "M04", "T03", "H06", "CBE", dateStr, "10:30 AM", 85,  "Tamil", "Dolby");
            saveShow(String.format("S%04d", showSeq++), "M04", "T03", "H06", "CBE", dateStr, "03:00 PM", 90,  "Tamil", "Dolby");
            saveShow(String.format("S%04d", showSeq++), "M04", "T03", "H06", "CBE", dateStr, "07:30 PM", 72,  "Tamil", "Dolby");
            saveShow(String.format("S%04d", showSeq++), "M04", "T02", "H04", "CBE", dateStr, "11:00 AM", 96,  "Tamil", "4K Dolby");
            saveShow(String.format("S%04d", showSeq++), "M04", "T02", "H04", "CBE", dateStr, "04:30 PM", 88,  "Tamil", "4K Dolby");
            saveShow(String.format("S%04d", showSeq++), "M04", "T01", "H03", "CBE", dateStr, "01:00 PM", 38,  "Tamil", "Gold Class");
            saveShow(String.format("S%04d", showSeq++), "M04", "T01", "H03", "CBE", dateStr, "07:00 PM", 35,  "Tamil", "Gold Class");
            saveShow(String.format("S%04d", showSeq++), "M04", "T05", "H10", "CHN", dateStr, "10:30 AM", 92,  "Tamil", "4K Dolby Atmos");
            saveShow(String.format("S%04d", showSeq++), "M04", "T05", "H10", "CHN", dateStr, "03:30 PM", 88,  "Tamil", "4K Dolby Atmos");
            saveShow(String.format("S%04d", showSeq++), "M04", "T08", "H14", "CHN", dateStr, "01:15 PM", 95,  "Tamil", "RGB ATMOS");
            saveShow(String.format("S%04d", showSeq++), "M04", "T08", "H14", "CHN", dateStr, "06:30 PM", 90,  "Tamil", "RGB ATMOS");
            saveShow(String.format("S%04d", showSeq++), "M04", "T09", "H15", "MDU", dateStr, "11:00 AM", 95,  "Tamil", "Dolby");
            saveShow(String.format("S%04d", showSeq++), "M04", "T09", "H15", "MDU", dateStr, "03:30 PM", 98,  "Tamil", "Dolby");
            saveShow(String.format("S%04d", showSeq++), "M04", "T10", "H17", "TRY", dateStr, "11:00 AM", 92,  "Tamil", "4DX Dolby");
            saveShow(String.format("S%04d", showSeq++), "M04", "T11", "H19", "SLM", dateStr, "10:00 AM", 105, "Tamil", "IMAX");
            saveShow(String.format("S%04d", showSeq++), "M04", "T12", "H21", "TNV", dateStr, "11:30 AM", 85,  "Tamil", "4K Dolby");
            saveShow(String.format("S%04d", showSeq++), "M04", "T13", "H22", "VLR", dateStr, "10:00 AM", 76,  "Tamil", "Dolby");
            saveShow(String.format("S%04d", showSeq++), "M04", "T14", "H23", "ERD", dateStr, "11:00 AM", 95,  "Tamil", "IMAX");

            // M05 (Biker)
            saveShow(String.format("S%04d", showSeq++), "M05", "T03", "H07", "CBE", dateStr, "10:00 AM", 68,  "Tamil", "3D");
            saveShow(String.format("S%04d", showSeq++), "M05", "T03", "H07", "CBE", dateStr, "02:00 PM", 65,  "Tamil", "3D");
            saveShow(String.format("S%04d", showSeq++), "M05", "T06", "H12", "CHN", dateStr, "11:00 AM", 46,  "Tamil", "Recliner");
            saveShow(String.format("S%04d", showSeq++), "M05", "T08", "H14", "CHN", dateStr, "11:00 AM", 90,  "Tamil", "RGB ATMOS");

            // M06 (Thaai Kizhavi)
            saveShow(String.format("S%04d", showSeq++), "M06", "T01", "H02", "CBE", dateStr, "09:30 AM", 75,  "Tamil", "4DX");
            saveShow(String.format("S%04d", showSeq++), "M06", "T01", "H02", "CBE", dateStr, "01:00 PM", 78,  "Tamil", "4DX");
            saveShow(String.format("S%04d", showSeq++), "M06", "T05", "H10", "CHN", dateStr, "06:00 PM", 94,  "Tamil", "4K Dolby");
            saveShow(String.format("S%04d", showSeq++), "M06", "T09", "H16", "MDU", dateStr, "10:00 AM", 76,  "Tamil", "3D");

            // M07 (Satan)
            saveShow(String.format("S%04d", showSeq++), "M07", "T01", "H03", "CBE", dateStr, "12:00 PM", 38,  "Tamil", "Recliner");
            saveShow(String.format("S%04d", showSeq++), "M07", "T01", "H03", "CBE", dateStr, "06:00 PM", 36,  "Tamil", "Recliner");
            saveShow(String.format("S%04d", showSeq++), "M07", "T05", "H10", "CHN", dateStr, "10:30 AM", 90,  "Tamil", "4K Dolby");
            saveShow(String.format("S%04d", showSeq++), "M07", "T07", "H13", "CHN", dateStr, "09:30 PM", 85,  "Tamil", "4K ATMOS");

            // M08 (Youth)
            saveShow(String.format("S%04d", showSeq++), "M08", "T02", "H04", "CBE", dateStr, "10:30 AM", 92,  "Tamil", "4K Dolby");
            saveShow(String.format("S%04d", showSeq++), "M08", "T02", "H04", "CBE", dateStr, "02:00 PM", 95,  "Tamil", "4K Dolby");
            saveShow(String.format("S%04d", showSeq++), "M08", "T06", "H12", "CHN", dateStr, "01:00 PM", 48,  "Tamil", "Gold Hall");

            // M09 (Project Hail Mary)
            saveShow(String.format("S%04d", showSeq++), "M09", "T01", "H01", "CBE", dateStr, "09:00 AM", 110, "English", "IMAX");
            saveShow(String.format("S%04d", showSeq++), "M09", "T01", "H01", "CBE", dateStr, "05:00 PM", 105, "English", "IMAX");
            saveShow(String.format("S%04d", showSeq++), "M09", "T05", "H09", "CHN", dateStr, "10:00 AM", 140, "English", "IMAX");
            saveShow(String.format("S%04d", showSeq++), "M09", "T05", "H09", "CHN", dateStr, "07:00 PM", 132, "English", "IMAX");
            saveShow(String.format("S%04d", showSeq++), "M09", "T11", "H19", "SLM", dateStr, "10:00 AM", 108, "English", "IMAX");

            // M10 (Vaazha 2)
            saveShow(String.format("S%04d", showSeq++), "M10", "T02", "H05", "CBE", dateStr, "11:30 AM", 75,  "Tamil", "3D");
            saveShow(String.format("S%04d", showSeq++), "M10", "T07", "H13", "CHN", dateStr, "10:00 AM", 88,  "Tamil", "4K ATMOS");
            saveShow(String.format("S%04d", showSeq++), "M10", "T09", "H15", "MDU", dateStr, "10:00 AM", 92,  "Tamil", "Dolby");
        }
    }

    private void saveShow(String id, String movieId, String theatreId, String hallId, String cityId,
                          String date, String time, int seats, String lang, String format) {
        Show s = showRepository.findById(id).orElseGet(Show::new);
        s.setId(id);
        s.setMovieId(movieId);
        s.setTheatreId(theatreId);
        s.setHallId(hallId);
        s.setCityId(cityId);
        s.setShowDate(LocalDate.parse(date));
        s.setShowTime(time);
        s.setAvailableSeats(seats);
        s.setLanguage(lang);
        s.setFormat(format);
        showRepository.save(s);
    }

    private void seedBookings() {
        LocalDate today = LocalDate.now();
        saveBooking("B001", "C001", "S0001", "M01", "T01", "H01", "Leader", "PVR Cinemas", "IMAX Hall",
                "CBE", today.toString(), "10:00 AM", List.of("C6", "C7"), "Standard", 360, 60, 420, "UPI", "CONFIRMED");
        saveBooking("B002", "C001", "S0011", "M02", "T01", "H02", "Dhurandhar: The Revenge", "PVR Cinemas", "4DX Hall",
                "CBE", today.toString(), "11:00 AM", List.of("A3", "A4"), "Premium", 520, 60, 580, "Credit Card", "CANCELLED");
        saveBooking("B003", "C001", "S0021", "M04", "T04", "H08", "Kaalidas 2", "Cinepolis", "Screen 1",
                "CBE", today.toString(), "10:30 AM", List.of("B4", "B5"), "Premium", 480, 60, 540, "UPI", "CONFIRMED");
    }

    private void saveBooking(String id, String customerId, String showId, String movieId, String theatreId, String hallId,
                             String movieTitle, String theatreName, String hallName, String cityId, String showDate,
                             String showTime, List<String> seats, String seatType, int ticket, int fee, int total,
                             String payment, String status) {
        if (bookingRepository.existsById(id)) return;
        Booking b = new Booking();
        b.setId(id);
        b.setCustomerId(customerId);
        b.setShowId(showId);
        b.setMovieId(movieId);
        b.setTheatreId(theatreId);
        b.setHallId(hallId);
        b.setMovieTitle(movieTitle);
        b.setTheatreName(theatreName);
        b.setHallName(hallName);
        b.setCityId(cityId);
        b.setShowDate(LocalDate.parse(showDate));
        b.setShowTime(showTime);
        b.setSeats(JsonUtil.toJson(seats));
        b.setSeatType(seatType);
        b.setTicketAmount(ticket);
        b.setConvenienceFee(fee);
        b.setTotalAmount(total);
        b.setPaymentMethod(payment);
        b.setPaymentStatus("SUCCESS");
        b.setStatus(status);
        b.setBookedOn(LocalDateTime.now().minusDays(1));
        bookingRepository.save(b);
    }

    private void seedCancellations() {
        if (cancellationRepository.existsById("CAN001")) return;
        Cancellation c = new Cancellation();
        c.setId("CAN001");
        c.setBookingId("B002");
        c.setCustomerId("C001");
        c.setMovieTitle("Dhurandhar: The Revenge");
        c.setTheatreName("PVR Cinemas");
        c.setShowDate(LocalDate.now());
        c.setShowTime("11:00 AM");
        c.setSeats(JsonUtil.toJson(List.of("A3", "A4")));
        c.setTotalAmount(580);
        c.setRefundAmount(522);
        c.setRefundStatus("PROCESSED");
        c.setRefundMethod("Credit Card");
        c.setReason("Personal reasons");
        c.setCancelledAt(LocalDateTime.now().minusHours(4));
        cancellationRepository.save(c);
    }

    private void seedChanges() {
        if (changeRepository.existsById("CHG001")) return;
        BookingChange ch = new BookingChange();
        ch.setId("CHG001");
        ch.setBookingId("B001");
        ch.setCustomerId("C001");
        ch.setChangeType("SEAT_CHANGE");
        ch.setDescription("Seat change from C4,C5 to C6,C7");
        ch.setBeforeState("{\"seats\":[\"C4\",\"C5\"],\"showDate\":\"" + LocalDate.now() + "\",\"showTime\":\"10:00 AM\"}");
        ch.setAfterState("{\"seats\":[\"C6\",\"C7\"],\"showDate\":\"" + LocalDate.now() + "\",\"showTime\":\"10:00 AM\"}");
        ch.setFeePaid(50);
        ch.setChangedAt(LocalDateTime.now().minusHours(2));
        changeRepository.save(ch);
    }
}
