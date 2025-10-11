package one.frei.mapper;

import jakarta.annotation.Nullable;
import one.frei.domain.enums.ActionType;
import one.frei.domain.model.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class LogEntryMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogEntryMapper.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private LogEntryMapper() {
    }

    @Nullable
    public static LogEntry mapstringToLogEntry(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 3) {
            // TODO: add actual handling logic so I can keep track of lines that could not be mapped
            //  and if they need to be logged as suspicious
            LOGGER.warn("Invalid log entry line: {}", line);
            return null;
        }

        String timestampString = parts[0].trim();
        String user = parts[1].trim();
        String actionStr = parts[2].trim();

        LogEntry logEntry = new LogEntry();
        if (parts.length > 3) {
            String info = parts[3].trim();
            if (info.startsWith("IP=")) {
                String ipAddress = info.substring(3);
                logEntry.setIpAddress(ipAddress);
            } else if (info.startsWith("FILE=")) {
                String fileName = info.substring(5);
                logEntry.setFileName(fileName);
            }
        }

        logEntry.setTimestamp(OffsetDateTime.parse(timestampString, FORMATTER));
        logEntry.setUser(user);
        logEntry.setActionType(ActionType.fromValue(actionStr));

        LOGGER.info(logEntry.toString());
        return logEntry;
    }
}
