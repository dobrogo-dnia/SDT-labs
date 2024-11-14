package logger;

import enums.LogType;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class Logger {
    private static Logger logger;
    private final String LOG_FILE_PATH = "ServerFiles\\LogFile.txt";
    private final BufferedWriter fileWriter;

    private Logger() throws IOException {
        fileWriter = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true));
    }

    public static Logger getLogger() throws IOException{
        if(logger == null)
            logger = new Logger();
        return logger;
    }

    public void writeLogInEventToFile(String clientAddress, String username) throws IOException {
        LogMessage newLog = new LogMessage
                .ConnectionLogBuilder(LogType.LOG_IN_TYPE)
                .setClientAddress(clientAddress)
                .setUsername(username)
                .setEventTime(LocalDateTime.now().withSecond(0).withNano(0))
                .build();

        String logMessageText = newLog.getResultMessage();
        fileWriter.write(logMessageText);
        fileWriter.newLine();
        fileWriter.flush();
    }

}
