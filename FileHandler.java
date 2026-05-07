import java.util.*;

public class Main {
    public static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        displayMenu();
    }

    public static void displayMenu() {
        boolean exit = false;
        
        while(!exit) {
            System.out.println("\n=========================================================");
            System.out.println("||                      MAIN MENU                      ||");
            System.out.println("=========================================================");
            System.out.println("\n              Welcome to DLSL Hotel!");
            System.out.println("             Please choose an option below.\n");
            
            System.out.println("  [1] Client             - Are you a client?");
            System.out.println("  [2] Receptionist       - Are you a Receptionist?");
            System.out.println("  [3] Manager            - Are you a Manager?");
            System.out.println("  [4] Exit               - Exit the system.");
            System.out.println("\n---------------------------------------------------------");
            System.out.print("Select an option: ");
            String option = sc.nextLine().trim();

            if (option.equals("1")) {
                Client.displayMenu();
            } else if (option.equals("2")) {
                Receptionist.displayMenu();
            } else if (option.equals("3")) {
                Manager manager = new Manager();
                manager.loginManager();
            } else if (option.equals("4")) {
                System.out.println("\n=================================================================================");
                System.out.println("||                  Thank you for using DLSL Hotel System!                       ||");
                System.out.println("||                             Have a great day!                                 ||");
                System.out.println("=================================================================================");
                exit = true;
            } else {
                System.out.println("\n Invalid input. Please select 1-4.");
            }
        }
    }
}   


