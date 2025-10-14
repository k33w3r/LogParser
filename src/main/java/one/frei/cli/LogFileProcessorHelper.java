package one.frei.cli;

import one.frei.domain.model.LogEntry;
import one.frei.domain.model.LogEntryContainer;
import one.frei.mapper.LogEntryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LogFileProcessorHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogFileProcessorHelper.class);

    public void processLogEntry(String logFile) {
        List<LogEntry> logEntries = readLogFile(logFile);
        Map<String, LogEntryContainer> logEntryContainerMap = createLogEntryContainerMap(logEntries);
        List<LogEntryContainer> topUsersByFileUploads = getTopUsersByFileUploads(logEntryContainerMap, 3);
        List<LogEntry> suspiciousLogEntries = detectSuspiciousLogEntries(logEntryContainerMap);
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

    private Map<String, LogEntryContainer> createLogEntryContainerMap(List<LogEntry> logEntries) {
        Map<String, LogEntryContainer> logEntryContainerMap = new HashMap<>();

        for (LogEntry logEntry : logEntries) {

            if (logEntry == null) {
                continue;
            }

            String user = logEntry.getUser();
            if (logEntryContainerMap.containsKey(user)) {
                LogEntryContainer logEntryContainer = logEntryContainerMap.get(user);
                populateLogEntryContainer(logEntry, logEntryContainer);
            } else {
                LogEntryContainer logEntryContainer = populateLogEntryContainer(logEntry);
                logEntryContainerMap.put(user, logEntryContainer);
            }
        }
        return logEntryContainerMap;
    }

    private LogEntryContainer populateLogEntryContainer(LogEntry logEntry) {
        LogEntryContainer logEntryContainer = new LogEntryContainer();
        logEntryContainer.setUser(logEntry.getUser());
        addLogEntry(logEntry, logEntryContainer);
        return logEntryContainer;
    }

    private void populateLogEntryContainer(LogEntry logEntry, LogEntryContainer logEntryContainer) {
        addLogEntry(logEntry, logEntryContainer);
    }

    /*
     * P.S. Although the spec only said to check for the following three scenarios:
     * 1. Count LOGINSUCCESS and LOGINFAILURE per user.
     * 2. Identify top 3 users with the most FILEUPLOAD events.
     * 3. Detect “suspicious” activity: >3 LOGINFAILUREs from the same IP within 5 minutes.
     *
     * I wanted to use all the data presented and might remove this at a later stage.
     */
    private void addLogEntry(LogEntry logEntry, LogEntryContainer logEntryContainer) {
        switch (logEntry.getActionType()) {
            case LOGIN_SUCCESS -> logEntryContainer.getSuccessfulLogins().add(logEntry);
            case LOGIN_FAILURE -> logEntryContainer.getFailedLogins().add(logEntry);
            case FILE_UPLOAD -> logEntryContainer.getFileUploads().add(logEntry);
            case FILE_DOWNLOAD -> logEntryContainer.getFileDownloads().add(logEntry);
            case LOGOUT -> logEntryContainer.getLogouts().add(logEntry);
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

    private List<LogEntryContainer> getTopUsersByFileUploads(Map<String, LogEntryContainer> logEntryContainerMap, int resultsAmount) {
        return logEntryContainerMap.entrySet().stream()
                .sorted((a, b) -> Integer.compare(
                        b.getValue().getFileUploads().size(),
                        a.getValue().getFileUploads().size()))
                .limit(resultsAmount)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    private List<LogEntry> detectSuspiciousLogEntries(Map<String, LogEntryContainer> containers) {
        Map<String, List<LogEntry>> ipFailures = new HashMap<>();

        for (LogEntryContainer container : containers.values()) {
            for (LogEntry entry : container.getFailedLogins()) {
                if (entry.getIpAddress() != null && entry.getTimestamp() != null) {
                    ipFailures.computeIfAbsent(entry.getIpAddress(), ip -> new ArrayList<>()).add(entry);
                }
            }
        }

        List<LogEntry> suspiciousEntries = new ArrayList<>();
        for (List<LogEntry> logEntries : ipFailures.values()) {
            logEntries.sort(Comparator.comparing(LogEntry::getTimestamp));
            for (int i = 0; i <= logEntries.size() - 3; i++) {
                OffsetDateTime first = logEntries.get(i).getTimestamp();
                OffsetDateTime third = logEntries.get(i + 2).getTimestamp();
                if (Duration.between(first, third).toMinutes() <= 5) {
                    suspiciousEntries.add(logEntries.get(i));
                    suspiciousEntries.add(logEntries.get(i + 1));
                    suspiciousEntries.add(logEntries.get(i + 2));
                    break;
                }
            }
        }
        return suspiciousEntries;
    }
}