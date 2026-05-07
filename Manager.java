// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Manager {
   private final String RECEPTIONIST_FILE = "RECEPTIONIST.txt";
   private final String CHECKIN_FILE = "CHECKED-IN.txt";
   private final String RESERVE_FILE = "RESERVE.txt";
   private final String CANCELLED_FILE = "CANCELLED.txt";
   private static final String DEFAULT_MANAGER_USERNAME = "admin";
   private static final String DEFAULT_MANAGER_PASSWORD = "password123";
   private Scanner sc;

   public Manager() {
      this.sc = Main.sc;
   }

   private File resolveFile(String var1) {
      return FileHandler.resolveFilePath(var1);
   }

   public boolean loginManager() {
      String var4;
      do {
         System.out.println("\n=========================================================");
         System.out.println("<-                    MANAGER LOGIN                    ->");
         System.out.println("=========================================================");
         System.out.print("\nEnter username: ");
         String var1 = this.sc.nextLine();
         System.out.print("Enter password: ");
         String var2 = this.sc.nextLine();
         boolean var3 = var1.equals("admin") && var2.equals("password123");
         if (var3) {
            System.out.println("\n  -> Access granted.");
            this.displayMenu();
            return true;
         }

         System.out.println("\n  -> Access denied.");
         System.out.print("Do you want to try again? (Y/N): ");
         var4 = this.sc.nextLine().trim().toLowerCase();
      } while(var4.equals("y"));

      return false;
   }

   public void createReceptionistAccount() {
      System.out.println("\n=========================================================");
      System.out.println("<-             CREATE RECEPTIONIST ACCOUNT             ->");
      System.out.println("=========================================================");
      System.out.print("Enter new username: ");
      String var1 = this.sc.nextLine();
      System.out.print("Enter password: ");
      String var2 = this.sc.nextLine();
      File var3 = this.resolveFile("RECEPTIONIST.txt");

      try {
         BufferedWriter var4 = new BufferedWriter(new FileWriter(var3, true));

         try {
            var4.write(var1 + "," + var2);
            var4.newLine();
            System.out.println("\n*********************************************************");
            System.out.println("*      Receptionist account created successfully!       *");
            System.out.println("*********************************************************");
         } catch (Throwable var8) {
            try {
               var4.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }

            throw var8;
         }

         var4.close();
      } catch (IOException var9) {
         System.out.println("Error saving account.");
      }

      System.out.print("\nPress Enter to go Back to Manager Menu...");
      this.sc.nextLine();
   }

   public void viewCheckIn() {
      System.out.println("\n=========================================================");
      System.out.println("<-                 VIEW CHECK-IN RECORDS              ->");
      System.out.println("=========================================================");
      File var1 = this.resolveFile("CHECKED-IN.txt");
      if (var1.exists() && var1.length() != 0L) {
         System.out.println("\n---------------------------------------------------------");

         try {
            BufferedReader var2 = new BufferedReader(new FileReader(var1));

            try {
               boolean var4 = false;

               String var3;
               while((var3 = var2.readLine()) != null) {
                  if (!var3.trim().isEmpty()) {
                     System.out.println(var3);
                     var4 = true;
                  }
               }

               if (!var4) {
                  System.out.println("  -> No data yet. No check-in records found.");
               }
            } catch (Throwable var6) {
               try {
                  var2.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }

               throw var6;
            }

            var2.close();
         } catch (IOException var7) {
            System.out.println("Error reading check-in file.");
         }

         System.out.println("---------------------------------------------------------");
         System.out.print("\nPress Enter to go Back to Manager Menu...");
         this.sc.nextLine();
      } else {
         System.out.println("\n  -> No data yet. No check-in records found.");
         System.out.print("\nPress Enter to go Back to Manager Menu...");
         this.sc.nextLine();
      }
   }

   public void cancelReservation() {
      System.out.println("\n=========================================================");
      System.out.println("<-                 CANCEL RESERVATION                 ->");
      System.out.println("=========================================================");
      System.out.print("Enter Reservation ID (R-xxxx) or Client ID (C-xxxx): ");
      String var1 = this.sc.nextLine().trim();
      if (var1.isEmpty()) {
         System.out.println("\n  -> Cancel aborted (blank input).");
         System.out.print("\nPress Enter to go Back to Manager Menu...");
         this.sc.nextLine();
      } else if (var1.toUpperCase().startsWith("R-")) {
         System.out.print("Confirm cancel reservation '" + var1 + "'? (Y/N): ");
         if (this.sc.nextLine().trim().equalsIgnoreCase("Y")) {
            boolean var9 = FileHandler.moveOrUpdateRecord("RESERVE.txt", "CANCELLED.txt", var1, 0);
            if (var9) {
               System.out.println("\n*********************************************************");
               System.out.println("*          Reservation cancelled successfully!          *");
               System.out.println("*********************************************************");
            } else {
               System.out.println("\n  -> Reservation not found.");
            }
         }

         System.out.print("\nPress Enter to go Back to Manager Menu...");
         this.sc.nextLine();
      } else {
         List var2 = FileHandler.searchRecord("RESERVE.txt", var1, 1);
         if (var2.isEmpty()) {
            System.out.println("\n  -> No reservations found for Client ID: " + var1);
            System.out.print("\nPress Enter to go Back to Manager Menu...");
            this.sc.nextLine();
         } else if (var2.size() == 1) {
            String[] var11 = ((String)var2.get(0)).split("\\|");
            String var13 = var11.length > 0 ? var11[0] : "";
            System.out.print("Confirm cancel reservation '" + var13 + "'? (Y/N): ");
            if (this.sc.nextLine().trim().equalsIgnoreCase("Y")) {
               boolean var14 = FileHandler.moveOrUpdateRecord("RESERVE.txt", "CANCELLED.txt", var13, 0);
               if (var14) {
                  System.out.println("\n*********************************************************");
                  System.out.println("*          Reservation cancelled successfully!          *");
                  System.out.println("*********************************************************");
               } else {
                  System.out.println("\n  -> Reservation not found.");
               }
            }

            System.out.print("\nPress Enter to go Back to Manager Menu...");
            this.sc.nextLine();
         } else {
            System.out.println("\nMultiple reservations found. Enter the Reservation ID to cancel:");

            for(String var4 : var2) {
               String[] var5 = var4.split("\\|");
               String var6 = var5.length > 0 ? var5[0] : "";
               String var7 = var5.length > 2 ? var5[2] : "";
               String var8 = var5.length > 9 ? var5[9] : "";
               System.out.println("  - " + var6 + " | " + var7 + " | " + var8);
            }

            System.out.print("Reservation ID: ");
            String var10 = this.sc.nextLine().trim();
            if (var10.isEmpty()) {
               System.out.println("\n  -> Cancel aborted.");
               System.out.print("\nPress Enter to go Back to Manager Menu...");
               this.sc.nextLine();
            } else {
               System.out.print("Confirm cancel reservation '" + var10 + "'? (Y/N): ");
               if (this.sc.nextLine().trim().equalsIgnoreCase("Y")) {
                  boolean var12 = FileHandler.moveOrUpdateRecord("RESERVE.txt", "CANCELLED.txt", var10, 0);
                  if (var12) {
                     System.out.println("\n*********************************************************");
                     System.out.println("*          Reservation cancelled successfully!          *");
                     System.out.println("*********************************************************");
                  } else {
                     System.out.println("\n  -> Reservation not found.");
                  }
               }

               System.out.print("\nPress Enter to go Back to Manager Menu...");
               this.sc.nextLine();
            }
         }
      }
   }

   public void displayMenu() {
      boolean var1 = false;

      while(!var1) {
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
         String var2 = this.sc.nextLine().trim();
         if (var2.equals("1")) {
            this.createReceptionistAccount();
         } else if (var2.equals("2")) {
            this.viewCheckIn();
         } else if (var2.equals("3")) {
            this.cancelReservation();
         } else if (var2.equals("4")) {
            var1 = true;
            System.out.println("\n=========================================================");
            System.out.println("                   Exiting Manager Menu.                 ");
            System.out.println("=========================================================");
         } else {
            System.out.println("\nInvalid input. Please try again.");
         }
      }

   }
}
