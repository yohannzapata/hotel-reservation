import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Client {

    static Scanner sc = Main.sc;

    private static final String CLIENT_FILE = "CLIENTS.txt";
    private static final String RESERVE_FILE = "RESERVE.txt";

    /*
     * notes:
     * - make random id for client or reserve
     * - check if id already used first
     * - use time value if random fail
     * - try to keep ids not same
     */
    private static String generateUniqueId(String prefix, String fileName, int keyIndex) {
        Random random = new Random();
        for (int attempt = 0; attempt < 50; attempt++) {
            String candidate = prefix + (1000 + random.nextInt(9000));
            if (FileHandler.searchRecord(fileName, candidate, keyIndex).isEmpty()) {
                return candidate;
            }
        }

        String candidate = prefix + (System.currentTimeMillis() % 100000);
        return candidate;
    }
    
    /*
     * notes:
     * - show client menu
     * - wait user pick option
     * - go back to main menu if user want
     */
    public static void displayMenu() {
        boolean exit = false;

        while (exit == false) {
            System.out.println("\n=========================================================");
            System.out.println("||                     CLIENT MENU                     ||");
            System.out.println("=========================================================");
            System.out.println("\n               Welcome to DLSL hotel!");
            System.out.println("             Please choose an option below.\n");
            
            System.out.println("  [1] Register Client    - Create a new client account.");
            System.out.println("  [2] Create Reservation - Make a new reservation.");
            System.out.println("  [3] Main Menu          - Return to Main Menu");

            System.out.println("\n---------------------------------------------------------");
            System.out.print("Select an option: ");

            String option = sc.nextLine().trim();

            if (option.equals("1")) {
                registerClient();
            } else if (option.equals("2")) {
                createReservation();
            } else if (option.equals("3")) {
                exit = true;
                return;
            } else {
                System.out.println("\nInvalid input. Please try again.");
            }
        }
    }

    /*
     * notes:
     * - collect personal info
     * - check name, contact, email
     * - save new client if user say yes
     * - print new id after saving
     */
    public static void registerClient() {
        System.out.println("\n=========================================================");
        System.out.println("<-                 REGISTER NEW CLIENT                 ->");
        System.out.println("=========================================================");
        System.out.println("\n[ Guidelines ]");
        System.out.println(" - Full name: letters and spaces only");
        System.out.println(" - Password: ");
        System.out.println(" - Contact number: exactly 11 digits");
        System.out.println(" - Email: must end with @gmail.com");
        System.out.println(" - Contact number and email must be unique\n");
        System.out.println("---------------------------------------------------------");
        System.out.println("Client Information\n");

        /*
         * NOTES:
         * - ask for the client name first
         * - check that the name uses letters and spaces only
         * - reject empty input
         */
        String fullName = "";
        while (true) {
            System.out.print("Full Name *: ");
            fullName = sc.nextLine();
            if (fullName.isEmpty() == false && fullName.matches("[a-zA-Z ]+")) {
                break;
            } else {
                System.out.println("  -> Invalid: Letters/spaces only (no '|').");
            }
        }

        System.out.print("Password *: ");
        String password = sc.nextLine();

        /*
         * NOTES:
         * - ask for the address
         * - make sure the field is not blank
         * - reject empty strings
         */
        String address = "";
        while (true) {
            System.out.print("Address *: ");
            address = sc.nextLine();
            if (address.isEmpty() == false) {
                break;
            } else {
                System.out.println("  -> Invalid: Address cannot be empty (no '|').");
            }
        }

        /*
         * NOTES:
         * - check that the contact number has 11 digits
         * - make sure it only uses numbers
         * - reject duplicates already registered
         */
        String contactNum = "";
        while (true) {
            System.out.print("Contact Number (09xx-xxx-xxxx) *: ");
            contactNum = sc.nextLine();
            if (contactNum.length() == 11 && contactNum.matches("[0-9]+")) {
                if (FileHandler.searchRecord(CLIENT_FILE, contactNum, 4).size() == 0) {
                    System.out.println("  -> Valid & Available");
                    break;
                } else {
                    System.out.println("  -> Error: Contact number already registered.");
                }
            } else {
                System.out.println("  -> Invalid: Must be exactly 11 digits.");
            }
        }

        /*
         * NOTES:
         * - check that the email ends with gmail.com
         * - reject if email is already registered
         */
        String email = "";
        while (true) {
            System.out.print("Email (@gmail.com) *: ");
            email = sc.nextLine();
            if (email.toLowerCase().endsWith("@gmail.com")) {
                if (FileHandler.searchRecord(CLIENT_FILE, email, 5).size() == 0) {
                    System.out.println("  -> Valid & Available");
                    break;
                } else {
                    System.out.println("  -> Error: Email already registered.");
                }
            } else {
                System.out.println("  -> Invalid: Email must end with @gmail.com.");
            }
        }

        System.out.println("\n---------------------------------------------------------");
        /*
         * Notes:
         * - ask for final confirmation
         * - stop here if the user cancels
         * - only save after the user agrees
         */
        System.out.print("Confirm registration? [Y] Yes, Confirm / [N] No, Cancel: ");
        String confirm = sc.nextLine();
        
        if (confirm.equalsIgnoreCase("Y")) {
            /*
             * NOTES:
             * - build a new client ID
             * - combine every field into one record line
             * - use | to match the file format
             */
            String clientID = generateUniqueId("C-", CLIENT_FILE, 0);
            
            String finalData = clientID + "|" + password + "|" + fullName + "|" + address + "|" + contactNum + "|" + email;
            
            /*
             * NOTES:
             * - save the new record to the client file
             * - show the generated client ID after saving
             */
            boolean saved = FileHandler.saveToFile(CLIENT_FILE, finalData); 
            if (saved == true) {
                System.out.println("\n*********************************************************");
                System.out.println("*            Client successfully registered!!           *");
                System.out.println("*         Please save your Client ID for future use.    *");
                System.out.println("*********************************************************");
                System.out.println("\n                  YOUR CLIENT ID IS:                     ");
                System.out.println("                     [ " + clientID + " ]                        \n");
                System.out.print("Press Enter to go Back to Client Menu...");
                sc.nextLine();
            }
        } else {
            System.out.println("\nRegistration cancelled.");
            System.out.print("Press Enter to go Back to Main Menu...");
            sc.nextLine();
        }
    }

    /*
     * notes:
     * - find the client first
     * - check password and date
     * - count total price before save
     * - save reservation with pay status
     */
    public static void createReservation() {
        System.out.println("\n=========================================================");
        System.out.println("<-                 CREATE RESERVATION                  ->");
        System.out.println("=========================================================");
        
        /*
         * NOTES:
         * - start by finding the client record
         * - use the client ID as the first search key
         * - stop early if the ID is empty
         */
        System.out.print("\nEnter Client ID *: ");
        String clientID = sc.nextLine().trim();
        while (clientID.isEmpty()) {
            System.out.println("  -> Client ID is required.");
            System.out.print("Enter Client ID *: ");
            clientID = sc.nextLine().trim();
        }

        List<String> clientRecord = FileHandler.searchRecord(CLIENT_FILE, clientID, 0);
        if (clientRecord.size() == 0) {
            System.out.println("  -> Error: Client ID not found. Please register first.");
            System.out.print("\nPress Enter to go Back to Main Menu...");
            sc.nextLine();
            return; 
        }
        
        /*
         * Notes:
         * - read the saved client details
         * - check if the saved password exists in the record
         * - pull the client name from the right column
         */
        String[] clientData = clientRecord.get(0).split("\\|");
        boolean hasSavedPassword = clientData.length >= 6;
        String savedPassword = hasSavedPassword ? clientData[1] : "";
        String clientName = hasSavedPassword ? clientData[2] : (clientData.length > 1 ? clientData[1] : "");

        System.out.print("Enter Client Password *: ");
        String password = sc.nextLine();
        if (hasSavedPassword && !password.equals(savedPassword)) {
            System.out.println("  -> Error: Password does not match the client record.");
            System.out.print("\nPress Enter to go Back to Main Menu...");
            sc.nextLine();
            return;
        }

        if (!hasSavedPassword) {
            System.out.println("  -> Client record found.");
        }

        System.out.println("  -> Client found: " + clientName);

        System.out.println("\n---------------------------------------------------------");
        /*
         * Notes:
         * - ask for the reservation date
         * - reject dates in the past
         * - accept only the yyyy-MM-dd format
         */
        System.out.println("1. Reservation Date");
        LocalDate resDate = null;
        while (true) {
            System.out.print("Enter Reservation Date (YYYY-MM-DD) *: ");
            String dateInput = sc.nextLine();
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                resDate = LocalDate.parse(dateInput, formatter);
                
                if (resDate.isBefore(LocalDate.now())) {
                    System.out.println("  -> Invalid: Date cannot be in the past.");
                } else {
                    System.out.println("  -> Valid future date");
                    break; 
                }
            } catch (Exception e) {
                System.out.println("  -> Invalid format. Please use YYYY-MM-DD.");
            }
        }

        System.out.println("\n---------------------------------------------------------");
        /*
         * NOTES:
         * - show the facility choice in table style
         * - store the room price and pax limit
         */
        System.out.println("2. Choose Facility");
        System.out.printf("  %-5s %-15s %-15s %-10s\n", "[#]", "Facility", "Price Per Unit", "Max # Pax");
        System.out.printf("  %-5s %-15s %-15s %-10s\n", "[1]", "Single", "P1,500.00", "2");
        System.out.printf("  %-5s %-15s %-15s %-10s\n", "[2]", "Double", "P2,000.00", "3");
        System.out.printf("  %-5s %-15s %-15s %-10s\n", "[3]", "King", "P3,000.00", "4");
        System.out.printf("  %-5s %-15s %-15s %-10s\n", "[4]", "Suite", "P4,000.00", "6");

        int roomChoice = 0;
        double roomPrice = 0;
        int maxPaxLimit = 0;
        String facilityName = "";
        
        while (true) {
            System.out.print("\nSelect facility (1-4) *: ");
            try {
                roomChoice = Integer.parseInt(sc.nextLine());
                if (roomChoice == 1) { facilityName = "Single Room"; roomPrice = 1500; maxPaxLimit = 2; break; }
                else if (roomChoice == 2) { facilityName = "Double"; roomPrice = 2000; maxPaxLimit = 3; break; }
                else if (roomChoice == 3) { facilityName = "King"; roomPrice = 3000; maxPaxLimit = 4; break; }
                else if (roomChoice == 4) { facilityName = "Suite"; roomPrice = 4000; maxPaxLimit = 6; break; }
                else { System.out.println("  -> Invalid choice."); }
            } catch (Exception e) {
                System.out.println("  -> Please enter a number.");
            }
        }

        System.out.println("\n---------------------------------------------------------");
        /*
         * NOTES:
         * - collect the number of rooms
         * - collect the number of guests
         */
        System.out.println("3. Rooms & Guests");
        int numOfRooms = 0;
        while (true) {
            System.out.print("Number of Rooms *: ");
            try {
                numOfRooms = Integer.parseInt(sc.nextLine());
                if (numOfRooms > 0) break; 
                else System.out.println("  -> Must be greater than 0.");
            } catch (Exception e) {
                System.out.println("  -> Please enter a valid number.");
            }
        }

        int numOfGuests = 0;
        while (true) {
            System.out.print("Number of Guests *: ");
            try {
                numOfGuests = Integer.parseInt(sc.nextLine());
                if (numOfGuests > 0) break; 
                else System.out.println("  -> Must be greater than 0.");
            } catch (Exception e) {
                System.out.println("  -> Please enter a valid number.");
            }
        }

        System.out.println("\n---------------------------------------------------------");
        /*
         * NOTES:
         * - ask for the meal choice
         */
        System.out.println("4. Meal Options");
        System.out.println("  [1] None   (P0)");
        System.out.println("  [2] Lunch  (P200)");
        System.out.println("  [3] Dinner (P300)");
        System.out.println("  [4] Both   (P500)");
        
        int mealChoice = 0;
        String mealName = "";
        while (true) {
            System.out.print("\nSelect meal option (1-4) *: ");
            try {
                mealChoice = Integer.parseInt(sc.nextLine());
                if (mealChoice == 1) { mealName = "None (P0 per pax)"; break; }
                else if (mealChoice == 2) { mealName = "Lunch (P200 per pax)"; break; }
                else if (mealChoice == 3) { mealName = "Dinner (P300 per pax)"; break; }
                else if (mealChoice == 4) { mealName = "Both (P500 per pax)"; break; }
                else System.out.println("  -> Invalid choice.");
            } catch (Exception e) {
                System.out.println("  -> Please enter a number.");
            }
        }

        System.out.println("\n---------------------------------------------------------");
        /*
         * NOTES:
         * - ask for the payment option
         * - store the chosen payment label
         * - use it later for the cost breakdown
         */
        System.out.println("5. Payment Options");
        System.out.println("  [1] 30% Downpayment");
        System.out.println("  [2] 50% Downpayment");
        System.out.println("  [3] 100% Full Payment");
        
        int payChoice = 0;
        String payName = "";
        while (true) {
            System.out.print("\nSelect payment option (1-3) *: ");
            try {
                payChoice = Integer.parseInt(sc.nextLine());
                if (payChoice == 1) { payName = "30% Downpayment"; break; }
                else if (payChoice == 2) { payName = "50% Downpayment"; break; }
                else if (payChoice == 3) { payName = "100% Full Payment"; break; }
                else System.out.println("  -> Invalid choice.");
            } catch (Exception e) {
                System.out.println("  -> Please enter a number.");
            }
        }

        /*
         * NOTES:
         * - compute the base room cost
         * - add extra guest fees
         * - add the meal cost to the total
         */
        double baseCost = roomPrice * numOfRooms;
        double totalCost = baseCost;

        int totalMaxPax = maxPaxLimit * numOfRooms;
        double extraFee = 0;
        if (numOfGuests > totalMaxPax) {
            int extraPeople = numOfGuests - totalMaxPax;
            extraFee = extraPeople * 500.0; 
            totalCost = totalCost + extraFee;
        }

        double mealCost = 0;
        if (mealChoice == 2) {
            mealCost = 200.0 * numOfGuests; 
        } else if (mealChoice == 3) {
            mealCost = 300.0 * numOfGuests;
        } else if (mealChoice == 4) {
            mealCost = 500.0 * numOfGuests;
        }
        totalCost = totalCost + mealCost;

        /*
         * NOTES:
         * - calculate the amount to pay now
         * - leave the rest as the remaining balance
         * - use the payment choice percentage
         */
        double amountToPay = 0;
        if (payChoice == 1) {
            amountToPay = totalCost * 0.30;
        } else if (payChoice == 2) {
            amountToPay = totalCost * 0.50;
        } else {
            amountToPay = totalCost * 1.00;
        }

        double remainingBalance = totalCost - amountToPay;

        System.out.println("\n=========================================================");
        System.out.println("<-                 REVIEW RESERVATION                  ->");
        System.out.println("=========================================================");
        
        System.out.println("\nReservation Summary:");
        System.out.printf("  %-20s %s\n", "Client ID:", clientID);
        System.out.printf("  %-20s %s\n", "Client Name:", clientName);
        System.out.printf("  %-20s %s\n", "Reservation Date:", resDate.toString());
        System.out.printf("  %-20s %s\n", "Facility:", facilityName);
        System.out.printf("  %-20s %d\n", "Number of Rooms:", numOfRooms);
        System.out.printf("  %-20s %d\n", "Number of Guests:", numOfGuests);
        System.out.printf("  %-20s %s\n", "Meal Option:", mealName);
        System.out.printf("  %-20s %s\n", "Payment Option:", payName);

        System.out.println("\nFULL BREAKDOWN:");
        System.out.printf("  %-25s P%.2f\n", "Base Cost (Rooms):", baseCost);
        System.out.printf("  %-25s P%.2f\n", "Extra Pax Fee:", extraFee);
        System.out.printf("  %-25s P%.2f\n", "Meal Cost:", mealCost);
        System.out.println("  ---------------------------------------");
        System.out.printf("  %-25s P%.2f\n", "Total Cost:", totalCost);
        System.out.printf("  %-25s P%.2f\n", "Amount to Pay Now:", amountToPay);
        System.out.printf("  %-25s P%.2f\n", "Remaining Balance:", remainingBalance);

        System.out.println("\n---------------------------------------------------------");
        /*
         * NOTES:
         * - ask for reservation confirmation
         * - stop here if the user cancels
         * - save only when the user agrees
         */
        System.out.print("Confirm Reservation? [Y] Yes, Confirm / [N] No, Cancel: ");
        String confirm = sc.nextLine();

        if (confirm.equalsIgnoreCase("Y")) {
            /*
             * NOTES:
             * - build a reservation ID
             * - set the payment status
             * - save the final reservation record
             */
            String resID = generateUniqueId("R-", RESERVE_FILE, 0);
            
            String status = "";
            if (remainingBalance > 0) {
                status = "PaidWithBalance";
            } else {
                status = "Paid";
            }
            
            String finalData = resID + "|" + clientID + "|" + resDate.toString() + "|" + roomChoice + "|" + numOfRooms + "|" + numOfGuests + "|" + mealChoice + "|" + amountToPay + "|" + remainingBalance + "|" + status;

            boolean saved = FileHandler.saveToFile(RESERVE_FILE, finalData);
            if (saved == true) {
                System.out.println("\n*********************************************************");
                System.out.println("*           Reservation completed successfully!         *");
                System.out.println("*                Thank you, " + clientName + ".                *");
                System.out.println("*********************************************************");
                System.out.print("\nPress Enter to go Back to Main Menu...");
                sc.nextLine();
            }
        } else {
            System.out.println("\nReservation cancelled.");
            System.out.print("Press Enter to go Back to Main Menu...");
            sc.nextLine();
        }
    }
}
