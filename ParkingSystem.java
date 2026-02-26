import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ParkingSystem {
    private ArrayList<User> users = new ArrayList<>();
    private char[][] slots = new char[5][50];
    private int dailyReservationCounter = 1;

    private final String USER_FILE = "parking_users.txt";
    private Scanner sc = new Scanner(System.in);

    public ParkingSystem() {
        for (int i = 0; i < 5; i++)
            Arrays.fill(slots[i], '0');

        loadUsersFromFile();
    }

    public void run() {
        while (true) mainMenu();
    }

    // MAIN MENU
    void mainMenu() {
        System.out.println("\nWelcome to Smart Parking System!");
        System.out.println("1. Admin");
        System.out.println("2. User");
        System.out.println("3. Exit");
        System.out.print("Enter choice: ");

        int ch = sc.nextInt();

        switch (ch) {
            case 1 -> adminLogin();
            case 2 -> userMenu();
            case 3 -> {
                System.out.println("Exiting...");
                System.exit(0);
            }
            default -> System.out.println("Invalid Choice!");
        }
    }

    // MASKED PASSWORD INPUT
    String inputPassword() {
        Console console = System.console();
        if (console != null) {
            char[] pass = console.readPassword();
            return new String(pass);
        }
        return sc.next(); // fallback
    }

    // SIGNUP
    void signUp() {
        System.out.print("\nEnter name: ");
        String name = sc.next();

        for (User u : users) {
            if (u.name.equals(name)) {
                System.out.println("User already exists!");
                return;
            }
        }

        System.out.print("Enter password: ");
        String pwd = inputPassword();
        System.out.print("Confirm password: ");
        String cpwd = inputPassword();

        if (!pwd.equals(cpwd)) {
            System.out.println("Passwords do not match!");
            return;
        }

        users.add(new User(name, pwd));
        saveUsersToFile();
        System.out.println("User registered successfully!");
    }

    // USER MENU
    void userMenu() {
        System.out.println("\n1. Sign Up");
        System.out.println("2. Login");
        System.out.println("3. Back");
        System.out.print("Enter choice: ");

        int ch = sc.nextInt();
        switch (ch) {
            case 1 -> signUp();
            case 2 -> userLogin();
            case 3 -> mainMenu();
            default -> System.out.println("Invalid choice!");
        }
    }

    // USER LOGIN
    void userLogin() {
        System.out.print("\nEnter name: ");
        String name = sc.next();
        System.out.print("Enter password: ");
        String pwd = inputPassword();

        for (User u : users) {
            if (u.name.equals(name) && u.password.equals(pwd)) {
                System.out.println("Login successful!");
                userActions(u);
                return;
            }
        }

        System.out.println("Invalid credentials!");
    }

    // USER ACTIONS
    void userActions(User user) {
        while (true) {
            System.out.println("\n1. Reserve Slot");
            System.out.println("2. View Reservation");
            System.out.println("3. Logout");
            System.out.print("Enter choice: ");
            int ch = sc.nextInt();

            switch (ch) {
                case 1 -> reserveSlot(user);
                case 2 -> user.displayInfo();
                case 3 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid!");
            }
        }
    }

    // DISPLAY SLOTS
    void displaySlots(boolean showOnlyBooked) {
        for (int i = 0; i < 5; i++) {
            System.out.println("\nFloor " + (char) ('A' + i) + ":");
            for (int j = 0; j < 50; j++) {
                if (slots[i][j] == '0') {
                    if (!showOnlyBooked) System.out.print((j + 1) + " ");
                } else {
                    System.out.print(slots[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    // RESERVE SLOT
    void reserveSlot(User user) {
        System.out.println("\nAvailable Slots:");
        displaySlots(false);

        int slot;
        char floor;

        while (true) {
            System.out.print("Enter slot and floor (e.g. 12 A): ");
            slot = sc.nextInt();
            floor = sc.next().charAt(0);

            if (slot < 1 || slot > 50 || floor < 'A' || floor > 'E') {
                System.out.println("Invalid input!");
                continue;
            }

            int floorIndex = floor - 'A';
            if (slots[floorIndex][slot - 1] != '0') {
                System.out.println("Slot already booked!");
            } else {
                break;
            }
        }

        System.out.print("Enter vehicle type (Car/Bike/Truck): ");
        user.vehicleType = sc.next();

        if (user.vehicleType.equals("Car")) user.vehicleSymbol = "C";
        else if (user.vehicleType.equals("Bike")) user.vehicleSymbol = "B";
        else if (user.vehicleType.equals("Truck")) user.vehicleSymbol = "T";
        else user.vehicleSymbol = "C";

        slots[floor - 'A'][slot - 1] = user.vehicleSymbol.charAt(0);

        user.reservedSlot = slot;
        user.reservedFloor = floor;

        System.out.print("Enter registration number: ");
        user.registrationNumber = sc.next();

        // Entry Time
        System.out.print("Enter entry time (HH:MM): ");
        String e = sc.next();
        user.desiredEntryTime = parseTime(e);

        // Exit Time
        System.out.print("Enter exit time (HH:MM): ");
        String ex = sc.next();
        user.desiredExitTime = parseTime(ex);

        user.reservationCode = generateReservationCode();

        System.out.println("Reservation successful. Code: " + user.reservationCode);

        saveUsersToFile();
    }

    LocalDateTime parseTime(String timeStr) {
        LocalDateTime now = LocalDateTime.now();
        String[] parts = timeStr.split(":");

        return now.withHour(Integer.parseInt(parts[0]))
                .withMinute(Integer.parseInt(parts[1]))
                .withSecond(0);
    }

    String generateReservationCode() {
        LocalDateTime now = LocalDateTime.now();

        String code = String.format("%02d%02d%04d%02d",
                now.getDayOfMonth(),
                now.getMonthValue(),
                now.getYear(),
                dailyReservationCounter++);
        return code;
    }

    // ADMIN LOGIN
    void adminLogin() {
        System.out.print("\nEnter admin name: ");
        String name = sc.next();
        System.out.print("Enter password: ");
        String pwd = inputPassword();

        if ((name.equals("dushyanth") || name.equals("akhil") || name.equals("hemanath") || name.equals("tejasri"))
                && pwd.equals("5484")) {
            System.out.println("Admin login successful!");
            adminActions();
        } else {
            System.out.println("Invalid admin credentials!");
        }
    }

    // ADMIN ACTIONS
    void adminActions() {
        while (true) {
            System.out.println("\n1. Check Available Slots");
            System.out.println("2. Check Booked Slots");
            System.out.println("3. Billing");
            System.out.println("4. Logout");
            System.out.print("Enter choice: ");

            int ch = sc.nextInt();

            switch (ch) {
                case 1 -> displaySlots(false);
                case 2 -> displaySlots(true);
                case 3 -> processBilling();
                case 4 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid!");
            }
        }
    }

    // BILLING
    void processBilling() {
        System.out.print("Enter reservation code: ");
        String code = sc.next();

        for (User u : users) {
            if (u.reservationCode.equals(code)) {
                Duration duration = Duration.between(u.desiredEntryTime, u.desiredExitTime);
                double hours = duration.toMinutes() / 60.0;

                int fee = calculateFee(hours, u.vehicleType);

                System.out.println("Total hours: " + hours);
                System.out.println("Parking Fee: ₹" + fee);

                return;
            }
        }

        System.out.println("Reservation not found!");
    }

    int calculateFee(double hours, String type) {
        int baseRate = switch (type) {
            case "Car" -> 20;
            case "Bike" -> 10;
            case "Truck" -> 30;
            default -> 15;
        };

        if (hours <= 2) return (int) (baseRate * hours);

        double extra = hours - 2;
        return (int) (baseRate * 2 + extra * baseRate * 1.5);
    }

    // SAVE USERS
    void saveUsersToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(USER_FILE))) {
            for (User u : users) pw.println(u.toFileString());
        } catch (Exception e) {
            System.out.println("Error saving file!");
        }
    }

    // LOAD USERS
    void loadUsersFromFile() {
        File file = new File(USER_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    User u = User.fromFileString(line);
                    users.add(u);

                    if (u.reservedSlot != -1) {
                        slots[u.reservedFloor - 'A'][u.reservedSlot - 1] = u.vehicleSymbol.charAt(0);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading file!");
        }
    }
}
