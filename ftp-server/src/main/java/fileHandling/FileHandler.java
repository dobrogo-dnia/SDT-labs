package fileHandling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private static final String LOG_FILE_PATH = "ServerFiles\\LogFile.txt";

    public static List<String> getLogFileContent() throws IOException {
        File logFile = new File(LOG_FILE_PATH);

        try (BufferedReader fileReader = new BufferedReader(new FileReader(logFile))) {
            List<String> lastLines = new ArrayList<>();
            String newLine;

            while ((newLine = fileReader.readLine()) != null) {
                lastLines.add(newLine);
                if (lastLines.size() > 15) {
                    lastLines.remove(0);
                }
            }
            return lastLines;
        }
    }
}
