import java.util.*;
import java.io.*;

public class Receptionist {

    static Scanner sc = Main.sc;

    private static final String CLIENT_FILE = "CLIENTS.txt";
    private static final String RESERVE_FILE = "RESERVE.txt";
    private static final String CHECKED_IN_FILE = "CHECKED-IN.txt";
    private static final String RECEPTIONIST_FILE = "RECEPTIONIST.txt";

    /*
     * NOTES:
     * - show receptionist menu
     */
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

    /*
     * NOTES:
     * - ask for receptionist username and password
     * - check saved staff file 
     * - let user retry if fail
     */
    public static boolean loginReceptionist() {
        System.out.println("\n=========================================================");
        System.out.println("<-                  RECEPTIONIST LOGIN                 ->");
        System.out.println("=========================================================");

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

    /*
     * NOTES:
     * - check saved receptionist credential
     * - split each line by comma
     * - compare saved username and password
     */
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

    /*
     * NOTES:
     * - load client records
     * - can show all or just searched ones
     * - search by client id 
     */
    public static void viewClients() {
        System.out.println("\n=========================================================");
        System.out.println("<-                    VIEW CLIENTS                     ->");
        System.out.println("=========================================================");
        
        System.out.print("\nEnter Client ID or Name (blank=ALL): ");
        String searchKey = sc.nextLine().trim();

        /*
         * NOTES:
         * - load all clients first
         * - narrow it down if search text exist
         * - one method for all and search view
         */
        List<String> allClients = FileHandler.getAllRecords(CLIENT_FILE);
        if (allClients.isEmpty()) {
            System.out.println("\n  -> No data yet. No clients registered.");
        } else if (searchKey.isEmpty()) {
            displayClientTable(allClients);
        } else {
            List<String> found = new ArrayList<>();
            for (String line : allClients) {
                if (line == null || line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split("\\|");
                if (data.length >= 6) {
                    if (data[0].equals(searchKey) || data[2].equals(searchKey)) {
                        found.add(line);
                    }
                } else if (data.length >= 5) {
                    if (data[0].equals(searchKey) || data[1].equals(searchKey)) {
                        found.add(line);
                    }
                }
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

    /*
     * Loads reservation records and applies a display filter.
     * The user can view all records, only paid records, records with balance,
     * or a date-sorted list.
     */
        /*
         * Notes:
         * - loads reservation records
         * - applies the selected filter
         * - can show paid, balance, or date-sorted results
         */
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
            /*
             * NOTES:
             * - keep only fully paid reservations
             * - remove anything with a different status
             */
            filtered.removeIf(line -> {
                String[] data = line.split("\\|");
                return data.length < 10 || !data[9].equals("Paid");
            });
        } else if (filter.equals("3")) {
            /*
             * NOTES:
             * - keep only reservations that still have a balance
             * - show the PaidWithBalance records only
             * - hide the fully paid ones
             */
            filtered.removeIf(line -> {
                String[] data = line.split("\\|");
                return data.length < 10 || !data[9].equals("PaidWithBalance");
            });
        } else if (filter.equals("4")) {
            /*
             * NOTES:
             * - sort the records by reservation date
             * - show the oldest date first
             * - keep the list ordered for readability
             */
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

    /*
     * NOTES:
     * - marks a guest as checked in
     * - looks up the reservation first
     * - moves the record into the checked-in file
     */
    public static void checkInGuest() {
        System.out.print("\nEnter Res ID/Client ID: ");
        String searchKey = sc.nextLine().trim();

        /*
         * Notes:
         * - check reservation ID first
         * - check client ID second
         * - use whichever search finds a record
         */
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
        
        /* Notes: move the selected reservation after check-in confirmation. */
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

    /*
     * NOTES:
     * - prints client records in a table
     * - handles newer and older record layouts
     * - table style
     */
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

            if (data.length >= 6) {
                System.out.printf("  %-10s %-20s %-12s %s\n",
                        data[0], truncate(data[2]), data[4], data[5]);
            } else if (data.length >= 5) {
                System.out.printf("  %-10s %-20s %-12s %s\n",
                        data[0], truncate(data[1]), data[3], data[4]);
            }
        }

        System.out.println("---------------------------------------------------------");
    }

    /*
     * NOTES:
     * - prints full client details
     * - used when one client is found
     * - shows each field one line at a time
     */
    private static void displayClientDetails(List<String> records) {
        System.out.println("\n---------------------------------------------------------");
        System.out.println("                    CLIENT RECORD(s) FOUND:");
        System.out.println("---------------------------------------------------------");

        for (String record : records) {
            if (record == null || record.trim().isEmpty()) continue;
            String[] data = record.split("\\|");
            /* Print the full client details one record at a time. */
            if (data.length >= 6) {
                System.out.println("ID *: " + data[0]);
                System.out.println("NAME *: " + data[2]);
                System.out.println("CONTACT *: " + data[4]);
                System.out.println("EMAIL *: " + data[5]);
                System.out.println("ADDRESS *: " + data[3]);
                System.out.println("---------------------------------------------------------");
            } else if (data.length >= 5) {
                System.out.println("ID *: " + data[0]);
                System.out.println("NAME *: " + data[1]);
                System.out.println("CONTACT *: " + data[3]);
                System.out.println("EMAIL *: " + data[4]);
                System.out.println("ADDRESS *: " + data[2]);
                System.out.println("---------------------------------------------------------");
            }
        }
    }

    /*
     * NOTES:
     * - prints reservation records in table style
     * - keeps the reservation output simple for staff
     */
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

    /*
     * NOTES:
     * - shortens text for table display
     * - keeps columns aligned
     * - adds ellipsis "..." when the text is too long
     */
    private static String truncate(String value) {
        return truncate(value, 20);
    }

    /*
     * NOTES:
     * - shortens a string to a fixed length
     * - returns the original text if it is already short enough
     * - used by the table display
     */
    private static String truncate(String value, int maxLen) {
        if (value == null) return "";
        if (maxLen <= 3) return value;
        if (value.length() <= maxLen) return value;
        return value.substring(0, maxLen - 3) + "...";
    }

    /*
     * NOTES:
     * - changes a room choice number into a room name
     * - used when reading reservations from file
     */
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
