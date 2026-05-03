import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private static final String SAVE_DIR_NAME = "SAVES";

    private static Path getSaveDirPath() {
        String userDir = System.getProperty("user.dir");
        Path dir = Path.of(userDir, SAVE_DIR_NAME);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
        }
        return dir;
    }

    public static Path resolveFilePath(String fileName) {
        return getSaveDirPath().resolve(fileName);
    }

    public static boolean ensureFileExists(String fileName) {
        Path file = resolveFilePath(fileName);
        try {
            Files.createDirectories(file.getParent());
            if (!Files.exists(file)) {
                Files.createFile(file);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean saveToFile(String fileName, String data) {
        Path file = resolveFilePath(fileName);

        try {
            Files.createDirectories(file.getParent());
        } catch (IOException e) {
            System.out.println("Error creating save directory: " + e.getMessage());
            return false;
        }

        try (BufferedWriter bw = Files.newBufferedWriter(
                file,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.APPEND
        )) {
            bw.write(data);
            bw.newLine();
            return true;
        } catch (IOException e) {
            System.out.println("Error saving to file: " + e.getMessage());
            return false;
        }
    }

    public static List<String> searchRecord(String fileName, String searchKey, int keyIndex) {
        List<String> foundList = new ArrayList<>(); 
        Path file = resolveFilePath(fileName);
        
        if (!Files.exists(file)) {
            return foundList; 
        }

        try (BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
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

    public static boolean moveOrUpdateRecord(String sourceFileName, String targetFileName, String searchKey, int keyIndex) {
        Path sourceFile = resolveFilePath(sourceFileName);
        Path tempFile = resolveFilePath(sourceFileName + ".tmp");

        if (!Files.exists(sourceFile)) {
            return false;
        }

        boolean found = false;

        try (BufferedReader br = Files.newBufferedReader(sourceFile, StandardCharsets.UTF_8);
             BufferedWriter bwTemp = Files.newBufferedWriter(
                     tempFile,
                     StandardCharsets.UTF_8,
                     StandardOpenOption.CREATE,
                     StandardOpenOption.TRUNCATE_EXISTING,
                     StandardOpenOption.WRITE
             )) {
             
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

        try {
            Files.move(tempFile, sourceFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            try {
                Files.move(tempFile, sourceFile, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                System.out.println("Error finalizing record move: " + ex.getMessage());
                return false;
            }
        }
        
        return found; 
    }

    public static List<String> getAllRecords(String fileName) {
        List<String> records = new ArrayList<>();
        Path file = resolveFilePath(fileName);

        if (!Files.exists(file)) {
            return records;
        }

        try (BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
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

    public static boolean moveRecord(String sourceFileName, String targetFileName, String searchKey, int keyIndex) {
        return moveOrUpdateRecord(sourceFileName, targetFileName, searchKey, keyIndex);
    }
}