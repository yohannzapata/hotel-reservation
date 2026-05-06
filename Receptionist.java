import java.util.*;
import java.io.*;

public class Receptionist {

    static Scanner sc = Main.sc;

    private static final String CLIENT_FILE = "CLIENTS.txt";
    private static final String RESERVE_FILE = "RESERVE.txt";
    private static final String CHECKED_IN_FILE = "CHECKED-IN.txt";
    private static final String RECEPTIONIST_FILE = "RECEPTIONIST.txt";

    public static void displayMenu() {
        if (!loginReceptionist()) {
            System.out.println("\nAccess denied. Returning to main menu...");
            return;
        }
        
        boolean exit = false;
        while (exit == false) {
            System.out.println("\n=========================================================");
            System.out.println("||                  RECEPTIONIST MENU                  ||");
            System.out.println("=========================================================");
            System.out.println("\n                        Welcome!");
            System.out.println("             Please choose an option below.\n");
            
            System.out.println("  [1] View Clients          - Search and view client records.");
            System.out.println("  [2] View Reservations     - View all reservations with filters.");
            System.out.println("  [3] Check-in Guest        - Mark guest as checked-in.");
            System.out.println("  [4] Logout                - Exit to login screen.");
            System.out.println("\n---------------------------------------------------------");
            System.out.print("Select an option: ");
            
            String option = sc.nextLine();
            
            if (option.equals("1")) {
                viewClients();
            } else if (option.equals("2")) {
                viewReservations();
            } else if (option.equals("3")) {
                checkInGuest();
            } else if (option.equals("4")) {
                System.out.println("\n Logging out...");
                break;
            } else {
                System.out.println("\nInvalid input. Please try again.");
            }
        }
    }

    public static boolean loginReceptionist() {
        System.out.println("\n=========================================================");
        System.out.println("<-                  RECEPTIONIST LOGIN                 ->");
        System.out.println("=========================================================");

        File staffFile = FileHandler.resolveFilePath(RECEPTIONIST_FILE);
        ensureDefaultReceptionistAccount(staffFile);

        String username, password;
        while (true) {
            System.out.print("\nUsername *: ");
            username = sc.nextLine().trim();
            System.out.print("Password *: ");
            password = sc.nextLine().trim();

            if (validateCredentials(username, password)) {
                System.out.println("\nLogin successful!");
                System.out.print("Press Enter to continue...");
                sc.nextLine();
                return true;
            } else {
                System.out.println("-> Invalid credentials!");
            }
            
            System.out.print("\nTry again? [Y/N]: ");
            if (!sc.nextLine().equalsIgnoreCase("Y")) {
                return false;
            }
        }
    }

    private static void ensureDefaultReceptionistAccount(File staffFile) {
        try {
            if (!staffFile.exists()) {
                staffFile.getParentFile().mkdirs();
                staffFile.createNewFile();
            }
        } catch (IOException e) {
            return;
        }

        boolean hasDefault = false;
        try (BufferedReader br = new BufferedReader(new FileReader(staffFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 1 && data[0].trim().equals("receptionist")) {
                    hasDefault = true;
                    break;
                }
            }
        } catch (IOException e) {
            return;
        }

        if (!hasDefault) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(staffFile, true))) {
                bw.write("receptionist,receptionist123");
                bw.newLine();
            } catch (IOException e) {}
        }
    }

    public static void createReceptionistAccount() {
        System.out.println("\n=========================================================");
        System.out.println("<-               CREATE RECEPTIONIST ACCOUNT            ->");
        System.out.println("=========================================================");

        String username;
        while (true) {
            System.out.print("New Username *: ");
            username = sc.nextLine().trim();
            if (username.isEmpty() || !isUsernameUnique(username)) {
                System.out.println("  -> Username invalid or exists.");
                continue;
            }
            break;
        }

        String password;
        while (true) {
            System.out.print("New Password (min 6 chars) *: ");
            password = sc.nextLine().trim();
            if (password.length() < 6) {
                System.out.println("-> Password too short.");
                continue;
            }
            break;
        }

        System.out.print("Confirm? [Y/N]: ");
        if (sc.nextLine().equalsIgnoreCase("Y")) {
            File staffFile = FileHandler.resolveFilePath(RECEPTIONIST_FILE);
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(staffFile, true))) {
                bw.write(username + "," + password);
                bw.newLine();
                System.out.println("\nAccount created!");
            } catch (IOException e) {
                System.out.println("Save error.");
            }
        }
        System.out.print("\nPress Enter...");
        sc.nextLine();
    }

    private static boolean validateCredentials(String username, String password) {
        File file = FileHandler.resolveFilePath(RECEPTIONIST_FILE);
        if (!file.exists()) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");  
                if (data.length >= 2 && data[0].equals(username) && data[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {}
        return false;
    }

    private static boolean isUsernameUnique(String username) {
        File file = FileHandler.resolveFilePath(RECEPTIONIST_FILE);
        if (!file.exists()) return true;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 1 && data[0].trim().equals(username)) {
                    return false;
                }
            }
        } catch (IOException e) {}

        return true;
    }

    public static void viewClients() {
        System.out.println("\n=========================================================");
        System.out.println("<-                    VIEW CLIENTS                     ->");
        System.out.println("=========================================================");
        
        System.out.print("\nEnter Client ID or Name (blank=ALL): ");
        String searchKey = sc.nextLine().trim();

        List<String> allClients = FileHandler.getAllRecords(CLIENT_FILE);
        if (allClients.isEmpty()) {
            System.out.println("\n  -> No data yet. No clients registered.");
        } else if (searchKey.isEmpty()) {
            displayClientTable(allClients);
        } else {
            List<String> found = FileHandler.searchRecord(CLIENT_FILE, searchKey, 0);
            if (found.isEmpty()) {
                found = FileHandler.searchRecord(CLIENT_FILE, searchKey, 1);
            }

            if (found.isEmpty()) {
                System.out.println("  -> No clients found matching '" + searchKey + "'.");
            } else {
                displayClientDetails(found);
            }
        }
        
        System.out.print("\nPress Enter...");
        sc.nextLine();
    }

    public static void viewReservations() {
        System.out.println("\n=========================================================");
        System.out.println("<-                   VIEW RESERVATIONS                 ->");
        System.out.println("=========================================================");
        
        List<String> allReservations = FileHandler.getAllRecords(RESERVE_FILE);
        
        if (allReservations.isEmpty()) {
            System.out.println("\n  -> No data yet. No reservations recorded.");
            pause();
            return;
        }

        System.out.println("\nFilter: [1]All [2]Paid [3]Balance [4]Date");
        System.out.print("Select filter (1-4): ");
        String filter = sc.nextLine().trim();

        List<String> filtered = new ArrayList<>(allReservations);
        if (filter.equals("2")) {
            filtered.removeIf(line -> {
                String[] data = line.split("\\|");
                return data.length < 10 || !data[9].equals("Paid");
            });
        } else if (filter.equals("3")) {
            filtered.removeIf(line -> {
                String[] data = line.split("\\|");
                return data.length < 10 || !data[9].equals("PaidWithBalance");
            });
        } else if (filter.equals("4")) {
            filtered.sort((a, b) -> {
                String[] dataA = a.split("\\|");
                String[] dataB = b.split("\\|");
                String dateA = dataA.length > 2 ? dataA[2] : "";
                String dateB = dataB.length > 2 ? dataB[2] : "";
                return dateA.compareTo(dateB);
            });
        }

        if (filtered.isEmpty()) {
            System.out.println("\n -> No data yet for this filter.");
            pause();
            return;
        }

        displayReservationTable(filtered);
        pause();
    }

    public static void checkInGuest() {
        System.out.print("\nEnter Res ID/Client ID: ");
        String searchKey = sc.nextLine().trim();

        List<String> reservations = FileHandler.searchRecord(RESERVE_FILE, searchKey, 0);
        if (reservations.isEmpty()) {
            reservations = FileHandler.searchRecord(RESERVE_FILE, searchKey, 1);
        }

        if (reservations.isEmpty()) {
            System.out.println("Not found.");
            pause();
            return;
        }

        String[] data = reservations.get(0).split("\\|");
        String resID = data.length > 0 ? data[0] : "";
        
        System.out.print("Confirm check-in? [Y/N]: ");
        if (sc.nextLine().equalsIgnoreCase("Y")) {
            if (resID.isEmpty()) {
                System.out.println("Invalid reservation record.");
                pause();
                return;
            }
            FileHandler.moveOrUpdateRecord(RESERVE_FILE, CHECKED_IN_FILE, resID, 0);
            System.out.println("Checked in!");
        }
        pause();
    }

    private static void pause() {
        System.out.print("\nPress Enter...");
        sc.nextLine();
    }

    private static void displayClientTable(List<String> clients) {
        if (clients == null || clients.isEmpty()) {
            System.out.println("\n  -> No data yet. No clients registered.");
            return;
        }

        System.out.println("\n---------------------------------------------------------");
        System.out.printf("  %-10s %-20s %-12s %s\n", "CLIENT-ID", "NAME", "CONTACT", "EMAIL");
        System.out.println("  -------------------------------------------------------");

        for (String line : clients) {
            if (line == null || line.trim().isEmpty()) continue;
            String[] data = line.split("\\|");
            if (data.length >= 5) {
                System.out.printf("  %-10s %-20s %-12s %s\n",
                        data[0], truncate(data[1]), data[3], data[4]);
            }
        }

        System.out.println("---------------------------------------------------------");
    }

    private static void displayClientDetails(List<String> records) {
        System.out.println("\n---------------------------------------------------------");
        System.out.println("                    CLIENT RECORD(s) FOUND:");
        System.out.println("---------------------------------------------------------");

        for (String record : records) {
            if (record == null || record.trim().isEmpty()) continue;
            String[] data = record.split("\\|");
            if (data.length >= 5) {
                System.out.println("ID *: " + data[0]);
                System.out.println("NAME *: " + data[1]);
                System.out.println("CONTACT *: " + data[3]);
                System.out.println("EMAIL *: " + data[4]);
                System.out.println("ADDRESS *: " + data[2]);
                System.out.println("---------------------------------------------------------");
            }
        }
    }

    private static void displayReservationTable(List<String> reservations) {
        System.out.println("\n---------------------------------------------------------");
        System.out.printf("  %-8s %-10s %-12s %-10s %-8s %-8s %s\n",
                "RES-ID", "CLIENT-ID", "DATE", "FACILITY", "ROOMS", "GUESTS", "BALANCE");
        System.out.println("  ------------------------------------------------------------------------------------------------");

        for (String record : reservations) {
            if (record == null || record.trim().isEmpty()) continue;
            String[] data = record.split("\\|");
            if (data.length >= 10) {
                String facility;
                try {
                    facility = getFacilityName(Integer.parseInt(data[3]));
                } catch (NumberFormatException e) {
                    facility = "Unknown";
                }
                System.out.printf("  %-8s %-10s %-12s %-10s %-8s %-8s P%s\n",
                        data[0], data[1], data[2], facility, data[4], data[5], data[8]);
            }
        }

        System.out.println("---------------------------------------------------------");
    }

    private static String truncate(String value) {
        return truncate(value, 20);
    }

    private static String truncate(String value, int maxLen) {
        if (value == null) return "";
        if (maxLen <= 3) return value;
        if (value.length() <= maxLen) return value;
        return value.substring(0, maxLen - 3) + "...";
    }

    private static String getFacilityName(int choice) {
        return switch (choice) {
            case 1 -> "Single Room";
            case 2 -> "Double";
            case 3 -> "King";
            case 4 -> "Suite";
            default -> "Unknown";
        };
    }
}
