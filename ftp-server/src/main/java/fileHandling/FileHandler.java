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
        BufferedReader fileReader = new BufferedReader(new FileReader(logFile));
        List<String> fileContent = new ArrayList<>();
        String newLine;
        while((newLine = fileReader.readLine()) != null) {
            fileContent.add(newLine);
        }
        fileReader.close();

        return fileContent;
    }
}
