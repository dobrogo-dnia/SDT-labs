package logger;

import enums.LogType;

import java.text.MessageFormat;
import java.time.LocalDateTime;

public class LogMessage {
    private final String resultMessage;

    private LogMessage(ConnectionLogBuilder builder) {
        this.resultMessage = builder.resultMessage;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public static class ConnectionLogBuilder {
        private String clientAddress;
        private String username;
        private LocalDateTime eventTime;
        private String additionalInfo;
        LogType logType;
        private String resultMessage;

        public ConnectionLogBuilder(LogType logType) {
            this.logType = logType;
        }

        public ConnectionLogBuilder setClientAddress(String clientAddress) {
            this.clientAddress = clientAddress;
            return this;
        }

        public ConnectionLogBuilder setUsername(String username) {
            this.username = username;
            return this;
        }

        public ConnectionLogBuilder setEventTime(LocalDateTime eventTime) {
            this.eventTime = eventTime;
            return this;
        }

        public ConnectionLogBuilder setAdditionalInfo(String additionalInfo) {
            this.additionalInfo = additionalInfo;
            return this;
        }

        public LogMessage build() {
            if(logType == LogType.ERROR_TYPE)
                resultMessage = MessageFormat.format("ERROR: At {0} client {1} logged as {2} faced this error: {3}", eventTime, clientAddress, username, additionalInfo);
            else if(logType == LogType.LOG_IN_TYPE)
                resultMessage = MessageFormat.format("LOG IN: Client {0} loged in as {1} at {2}.", clientAddress, username, eventTime);
            else if(logType == LogType.LOG_OUT_TYPE)
                resultMessage = MessageFormat.format("LOG OUT: Client {0} logged out as {1} at {2}.", clientAddress, username, eventTime);

            return new LogMessage(this);
        }
    }
}
