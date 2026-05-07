import java.io.*;
import java.util.*;

public class FileHandler {

    private static final String SAVE_DIR_NAME = "SAVES";
    
    /*
     * NOTES:
     * - gets the save folder from the current working directory
     * - makes the folder if it is missing
     * - used for all file read and write work
     */
    private static File getSaveDirPath() {
        String userDir = System.getProperty("user.dir");
        File dir = new File(userDir, SAVE_DIR_NAME);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /*
     * NOTES:
     * - builds the full path for a save file
     * - keeps folder logic away from the other classes
         * - helps every file use the same save folder
     */
    public static File resolveFilePath(String fileName) {
        return new File(getSaveDirPath(), fileName);
    }

    /*
     * NOTES:
     * - appends one record line to a file
     * - makes sure the parent folder exists first
     * - adds a newline so each record stays separate
         * - used for saving client, reservation, and staff data
     */
    public static boolean saveToFile(String fileName, String data) {
        File file = resolveFilePath(fileName);

        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
        } catch (Exception e) {
            System.out.println("Error creating save directory: " + e.getMessage());
            return false;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(data);
            bw.newLine();
            return true;
        } catch (IOException e) {
            System.out.println("Error saving to file: " + e.getMessage());
            return false;
        }
    }

    /*
     * NOTES:
     * - scans a file line by line
     * - splits each line using the | 
     * - returns records that match the chosen keyIndex/column
     */
    public static List<String> searchRecord(String fileName, String searchKey, int keyIndex) {
        List<String> foundList = new ArrayList<>(); 
        File file = resolveFilePath(fileName);
        
        if (!file.exists()) {
            return foundList; 
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|"); 
                
                if (data.length > keyIndex) {
                    if (data[keyIndex].equals(searchKey)) {
                        foundList.add(line);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Read Error: " + e.getMessage()); 
        }
        
        return foundList; 
    }

    /*
     * NOTES:
     * - reads the source file line by line
     * - copies the matching record to the target file
     * - saves the other lines in a temp file first
     * - replaces the old file after the moving records is done
         * - used for cancel and check-in file moves
     */
    public static boolean moveOrUpdateRecord(String sourceFileName, String targetFileName, String searchKey, int keyIndex) {
        File sourceFile = resolveFilePath(sourceFileName);
        File tempFile = resolveFilePath(sourceFileName + ".tmp");

        if (!sourceFile.exists()) {
            return false;
        }

        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(sourceFile));
             BufferedWriter bwTemp = new BufferedWriter(new FileWriter(tempFile))) {
             
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");
                
                if (data.length > keyIndex && data[keyIndex].equals(searchKey)) {
                    saveToFile(targetFileName, line);
                    found = true;
                } else {
                    bwTemp.write(line);
                    bwTemp.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }

        if (sourceFile.exists() && !sourceFile.delete()) {
            System.out.println("Error finalizing record move: could not replace original file.");
            return false;
        }

        if (!tempFile.renameTo(sourceFile)) {
            System.out.println("Error finalizing record move: could not rename temp file.");
            return false;
        }
        
        return found; 
    }

    /*
     * NOTES:
     * - loads every non-empty line into a list
     * - useful for showing all records at once
     * - also helps when filtering in memory later
         * - gives the menu code a simple list to work with
     */
    public static List<String> getAllRecords(String fileName) {
        List<String> records = new ArrayList<>();
        File file = resolveFilePath(fileName);

        if (!file.exists()) {
            return records;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    records.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Read Error: " + e.getMessage());
        }

        return records;
    }
}
