import java.io.*;
import java.util.*;

public class Manager {

    private final String RECEPTIONIST_FILE = "RECEPTIONIST.txt";
    private final String CHECKIN_FILE = "CHECKED-IN.txt";
    private final String RESERVE_FILE = "RESERVE.txt";
    private final String CANCELLED_FILE = "CANCELLED.txt";

    private static final String DEFAULT_MANAGER_USERNAME = "admin";
    private static final String DEFAULT_MANAGER_PASSWORD = "password123";


    private Scanner sc = Main.sc;

    /*
     * notes:
     * - turn file name to real save path
     * - use FileHandler so folder stay same
     * - keep manager file work in one place
     * - reuse save folder 
     */
    private File resolveFile(String fileName) {
        return FileHandler.resolveFilePath(fileName);
    }

    /*
     * notes:
     * - check manager username and password
     * - compare with default login
     * - let user try again if fail
     * - open menu only after success
     */
    public boolean loginManager() {
    while (true) {
        System.out.println("\n=========================================================");
        System.out.println("<-                    MANAGER LOGIN                    ->");
        System.out.println("=========================================================");

        System.out.print("\nEnter username: ");
        String username = sc.nextLine();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        /*
         * notes:
         * - compare typed values with default login
         * - open manager menu if match
         * - stay on login screen if not
         * - keep login flow simple
         */
        boolean success = username.equals(DEFAULT_MANAGER_USERNAME) && password.equals(DEFAULT_MANAGER_PASSWORD);

        if (success) {
            System.out.println("\n  -> Access granted.");
            displayMenu();
            return true;
        } else {
            System.out.println("\n  -> Access denied.");

            /*
             * notes:
             * - ask if user want to retry
             * - keep looping when yes
             * - stop when no
             */
            System.out.print("Do you want to try again? (Y/N): ");
            String choice = sc.nextLine().trim().toLowerCase();

            if (!choice.equals("y")) {
                return false;
            }
        }
    }
}
    
    /*
     * notes:
     * - make receptionist account
     * - store username and password in file
     * - add new login data to end
     * - keep login list updated
     */
    public void createReceptionistAccount() {
        System.out.println("\n=========================================================");
        System.out.println("<-             CREATE RECEPTIONIST ACCOUNT             ->");
        System.out.println("=========================================================");

        System.out.print("Enter new username: ");
        String username = sc.nextLine();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        /*
         * notes:
         * - write new account in receptionist file
         * - save it as comma separated line
         */
        File file = resolveFile(RECEPTIONIST_FILE);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(username + "," + password);
            bw.newLine();
            System.out.println("\n*********************************************************");
            System.out.println("*      Receptionist account created successfully!       *");
            System.out.println("*********************************************************");
        } catch (IOException e) {
            System.out.println("Error saving account.");
        }

        System.out.print("\nPress Enter to go Back to Manager Menu...");
        sc.nextLine();
    }

    /*
     * notes:
     * - read the check-in file
     * - print every non empty line
     * - let manager review check in records
     */
    public void viewCheckIn() {
        System.out.println("\n=========================================================");
        System.out.println("<-                 VIEW CHECK-IN RECORDS              ->");
        System.out.println("=========================================================");

        File file = resolveFile(CHECKIN_FILE);
        if (!file.exists() || file.length() == 0) {
            System.out.println("\n  -> No data yet. No check-in records found.");
            System.out.print("\nPress Enter to go Back to Manager Menu...");
            sc.nextLine();
            return;
        }

        System.out.println("\n---------------------------------------------------------");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean any = false;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                System.out.println(line);
                any = true;
            }
            if (!any) {
                System.out.println("  -> No data yet. No check-in records found.");
            }
        } catch (IOException e) {
            System.out.println("Error reading check-in file.");
        }

        System.out.println("---------------------------------------------------------");
        System.out.print("\nPress Enter to go Back to Manager Menu...");
        sc.nextLine();
    }

    /*
     * notes:
     * - cancel reservation record
     * - search by reservation id or client id
     * - move matched record to cancelled file
     */
    public void cancelReservation() {
        System.out.println("\n=========================================================");
        System.out.println("<-                 CANCEL RESERVATION                 ->");
        System.out.println("=========================================================");

        System.out.print("Enter Reservation ID (R-xxxx) or Client ID (C-xxxx): ");
        String searchKey = sc.nextLine().trim();

        if (searchKey.isEmpty()) {
            System.out.println("\n  -> Cancel aborted (blank input).");
            System.out.print("\nPress Enter to go Back to Manager Menu...");
            sc.nextLine();
            return;
        }

        if (searchKey.toUpperCase().startsWith("R-")) {
            System.out.print("Confirm cancel reservation '" + searchKey + "'? (Y/N): ");
            if (sc.nextLine().trim().equalsIgnoreCase("Y")) {
                boolean moved = FileHandler.moveOrUpdateRecord(RESERVE_FILE, CANCELLED_FILE, searchKey, 0);
                if (moved) {
                    System.out.println("\n*********************************************************");
                    System.out.println("*          Reservation cancelled successfully!          *");
                    System.out.println("*********************************************************");
                } else {
                    System.out.println("\n  -> Reservation not found.");
                }
            }

            System.out.print("\nPress Enter to go Back to Manager Menu...");
            sc.nextLine();
            return;
        }

        List<String> matches = FileHandler.searchRecord(RESERVE_FILE, searchKey, 1);
        if (matches.isEmpty()) {
            System.out.println("\n  -> No reservations found for Client ID: " + searchKey);
            System.out.print("\nPress Enter to go Back to Manager Menu...");
            sc.nextLine();
            return;
        }

        if (matches.size() == 1) {
            String[] data = matches.get(0).split("\\|");
            String resId = data.length > 0 ? data[0] : "";

            System.out.print("Confirm cancel reservation '" + resId + "'? (Y/N): ");
            if (sc.nextLine().trim().equalsIgnoreCase("Y")) {
                boolean moved = FileHandler.moveOrUpdateRecord(RESERVE_FILE, CANCELLED_FILE, resId, 0);
                if (moved) {
                    System.out.println("\n*********************************************************");
                    System.out.println("*          Reservation cancelled successfully!          *");
                    System.out.println("*********************************************************");
                } else {
                    System.out.println("\n  -> Reservation not found.");
                }
            }

            System.out.print("\nPress Enter to go Back to Manager Menu...");
            sc.nextLine();
            return;
        }

        System.out.println("\nMultiple reservations found. Enter the Reservation ID to cancel:");
        for (String line : matches) {
            String[] data = line.split("\\|");
            String resId = data.length > 0 ? data[0] : "";
            String date = data.length > 2 ? data[2] : "";
            String status = data.length > 9 ? data[9] : "";
            System.out.println("  - " + resId + " | " + date + " | " + status);
        }

        System.out.print("Reservation ID: ");
        String resId = sc.nextLine().trim();
        if (resId.isEmpty()) {
            System.out.println("\n  -> Cancel aborted.");
            System.out.print("\nPress Enter to go Back to Manager Menu...");
            sc.nextLine();
            return;
        }

        System.out.print("Confirm cancel reservation '" + resId + "'? (Y/N): ");
        if (sc.nextLine().trim().equalsIgnoreCase("Y")) {
            boolean moved = FileHandler.moveOrUpdateRecord(RESERVE_FILE, CANCELLED_FILE, resId, 0);
            if (moved) {
                System.out.println("\n*********************************************************");
                System.out.println("*          Reservation cancelled successfully!          *");
                System.out.println("*********************************************************");
            } else {
                System.out.println("\n  -> Reservation not found.");
            }
        }

        System.out.print("\nPress Enter to go Back to Manager Menu...");
        sc.nextLine();
    }

    /*
     * notes:
     * - show manager menu
     * - wait for user action
     * - exit only if user pick exit
     */
    public void displayMenu() {
        boolean exit = false;

        while (!exit) {
            System.out.println("\n=========================================================");
            System.out.println("||                     MANAGER MENU                    ||");
            System.out.println("=========================================================");
            System.out.println("\n                        Welcome!");
            System.out.println("             Please choose an option below.\n");

            System.out.println("  [1] Create Receptionist Account");
            System.out.println("  [2] View Check-In Records");
            System.out.println("  [3] Cancel Reservation");
            System.out.println("  [4] Exit");
            System.out.println("\n---------------------------------------------------------");
            System.out.print("Select an option: ");

            String option = sc.nextLine().trim();

            if (option.equals("1")) {
                createReceptionistAccount();
            } else if (option.equals("2")) {
                viewCheckIn();
            } else if (option.equals("3")) {
                cancelReservation();
            } else if (option.equals("4")) {
                exit = true;
                System.out.println("\n=========================================================");
                System.out.println("                   Exiting Manager Menu.                 ");
                System.out.println("=========================================================");
            } else {
                System.out.println("\nInvalid input. Please try again.");
            }
        }
    }
}
