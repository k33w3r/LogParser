package one.frei.cli;

import one.frei.domain.model.LogEntry;
import one.frei.domain.model.LogEntryMetaData;
import one.frei.mapper.LogEntryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LogFileReaderHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogFileReaderHelper.class);

    public void processLogEntry(String logFile) {
        List<LogEntry> logEntries = readLogFile(logFile);
        createMetaDataMap(logEntries);
    }

    private List<LogEntry> readLogFile(String logFile) {
        List<LogEntry> logEntries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (hasNonAsciiCharacters(line)) {
                    LOGGER.warn("Non asci detected, ignoring content: {} ", line);
                } else {
                    LogEntry logEntry = LogEntryMapper.mapstringToLogEntry(line);
                    logEntries.add(logEntry);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read file: {}", logFile, e);
        }
        return logEntries;
    }

    private Map<String, LogEntryMetaData> createMetaDataMap(List<LogEntry> logEntries) {
        Map<String, LogEntryMetaData> metaDataMap = new HashMap<>();

        for (LogEntry logEntry : logEntries) {

            if (logEntry == null) {
                continue;
            }

            String user = logEntry.getUser();
            if (metaDataMap.containsKey(user)) {
                LogEntryMetaData logEntryMetaData = metaDataMap.get(user);
                populateLogEntryMetaData(logEntry, logEntryMetaData);
            } else {
                LogEntryMetaData logEntryMetaData = populateLogEntryMetaData(logEntry);
                metaDataMap.put(user, logEntryMetaData);
            }
        }

        return metaDataMap;
    }

    private LogEntryMetaData populateLogEntryMetaData(LogEntry logEntry) {
        LogEntryMetaData metaData = new LogEntryMetaData();
        incrementMetaData(logEntry, metaData);
        return metaData;
    }

    private void populateLogEntryMetaData(LogEntry logEntry, LogEntryMetaData metaData) {
        incrementMetaData(logEntry, metaData);
    }

    private void incrementMetaData (LogEntry logEntry, LogEntryMetaData metaData) {
        switch (logEntry.getActionType()) {
            case LOGIN_SUCCESS -> metaData.setSuccessfulLoginCount(metaData.getSuccessfulLoginCount() + 1);
            case LOGIN_FAILURE -> metaData.setFailedLoginCount(metaData.getFailedLoginCount() + 1);
            case FILE_UPLOAD -> metaData.setFileUploadCount(metaData.getFileUploadCount() + 1);
            case FILE_DOWNLOAD -> metaData.setFileDownloadCount(metaData.getFileDownloadCount() + 1);
            case LOGOUT -> metaData.setLogoutCount(metaData.getLogoutCount() + 1);
            default -> LOGGER.warn("Unknown action type: {}", logEntry.getActionType());
        }
    }

    /**
     * Returns true if the input contains at least one non-printable ASCII character.
     */
    private boolean hasNonAsciiCharacters(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        return input.chars().anyMatch(c -> c < 32 || c > 127);
    }
}