package one.frei.impl;

import one.frei.domain.model.LogEntry;
import one.frei.domain.model.LogEntryContainer;
import one.frei.mapper.LogEntryMapper;
import one.frei.service.LogFileProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LogFileProcessorImpl implements LogFileProcessorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogFileProcessorImpl.class);

    /**
     * Reads a system log file and maps each valid line to a {@link LogEntry}.
     * Lines containing non-ASCII characters are ignored.
     *
     * @param logFile path to the log file
     * @return a list of parsed {@link LogEntry} objects
     */
    public List<LogEntry> readLogFile(InputStream logFile) {
        List<LogEntry> logEntries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (hasNonAsciiCharacters(line)) {
                    LOGGER.warn("Non ascii detected, ignoring content: {} ", line);
                } else {
                    LogEntry logEntry = LogEntryMapper.mapstringToLogEntry(line);
                    logEntries.add(logEntry);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read log stream", e);
        }
        return logEntries;
    }


    /**
     * Builds a map of {@link LogEntryContainer} objects keyed by username.
     * Each container groups log entries belonging to a specific user.
     *
     * @param logEntries list of parsed log entries
     * @return a map where keys are usernames and values are user-specific log containers
     */
    public Map<String, LogEntryContainer> createLogEntryContainerMap(List<LogEntry> logEntries) {
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

    /**
     * Retrieves the top N users who have performed the most file uploads.
     *
     * @param logEntryContainerMap a map of user log containers
     * @param resultsAmount        the number of top users to retrieve
     * @return a list of top users sorted by descending upload count
     */
    public List<LogEntryContainer> getTopUsersByFileUploads(Map<String, LogEntryContainer> logEntryContainerMap, int resultsAmount) {
        return logEntryContainerMap.entrySet().stream()
                .sorted((a, b) -> Integer.compare(
                        b.getValue().getFileUploads().size(),
                        a.getValue().getFileUploads().size()))
                .limit(resultsAmount)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    /**
     * Detects suspicious login patterns by identifying IPs with three or more
     * failed login attempts within a five-minute time window.
     *
     * @param containers collection of user log containers
     * @return a list of suspicious {@link LogEntry} objects indicating risky login behavior
     */
    public List<LogEntry> detectSuspiciousLogEntries(Map<String, LogEntryContainer> containers) {
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

    /**
     * Creates a new {@link LogEntryContainer} for a given {@link LogEntry}
     * and initializes it with the entry’s data.
     *
     * @param logEntry a log entry
     * @return a new populated {@link LogEntryContainer} for the user
     */
    protected LogEntryContainer populateLogEntryContainer(LogEntry logEntry) {
        LogEntryContainer logEntryContainer = new LogEntryContainer();
        logEntryContainer.setUser(logEntry.getUser());
        addLogEntry(logEntry, logEntryContainer);
        return logEntryContainer;
    }

    /**
     * Adds a {@link LogEntry} to an existing {@link LogEntryContainer}.
     *
     * @param logEntry          the log entry to add
     * @param logEntryContainer target container associated with the entry’s user
     */
    protected void populateLogEntryContainer(LogEntry logEntry, LogEntryContainer logEntryContainer) {
        addLogEntry(logEntry, logEntryContainer);
    }

    /**
     * Adds a {@link LogEntry} to the correct list in the {@link LogEntryContainer}
     * based on the action type (LOGIN, FILE_UPLOAD, FILE_DOWNLOAD, etc.).
     *
     * @param logEntry          the log entry being processed
     * @param logEntryContainer the user-specific container to add it to
     */
    protected void addLogEntry(LogEntry logEntry, LogEntryContainer logEntryContainer) {
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
     * Checks whether a given input string contains any non-printable ASCII characters.
     *
     * @param input the string to check
     * @return {@code true} if any non-ASCII characters are found, {@code false} otherwise
     */
    protected boolean hasNonAsciiCharacters(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        return input.chars().anyMatch(c -> c < 32 || c > 127);
    }
}