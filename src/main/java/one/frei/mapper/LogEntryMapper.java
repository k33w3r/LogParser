package one.frei.mapper;

import jakarta.annotation.Nullable;
import one.frei.domain.enums.ActionType;
import one.frei.domain.model.LogEntry;
import one.frei.domain.model.LogEntryContainer;
import one.frei.domain.model.vo.login.UserLoginSummary;
import one.frei.domain.model.vo.suspicious.AttemptInfo;
import one.frei.domain.model.vo.suspicious.SuspiciousIpAttempt;
import one.frei.domain.model.vo.upload.UserUploadSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LogEntryMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogEntryMapper.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private LogEntryMapper() {
    }

    @Nullable
    public static LogEntry mapstringToLogEntry(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 3) {
            LOGGER.warn("Invalid log entry line: {}", line);
            return null;
        }

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

        String timestampString = parts[0].trim();
        logEntry.setTimestamp(OffsetDateTime.parse(timestampString, FORMATTER));

        String user = parts[1].trim();
        logEntry.setUser(user);

        String actionStr = parts[2].trim();
        logEntry.setActionType(ActionType.fromValue(actionStr));

        LOGGER.debug(logEntry.toString());
        return logEntry;
    }

    public static List<UserLoginSummary> logEntryContainerMapToUserLoginSummaries(Map<String, LogEntryContainer> logEntryContainerMap) {
        List<UserLoginSummary> userLoginSummaries = new ArrayList<>();

        for (Map.Entry<String, LogEntryContainer> stringLogEntryContainerEntry : logEntryContainerMap.entrySet()) {
            LogEntryContainer logEntryContainer = stringLogEntryContainerEntry.getValue();
            UserLoginSummary userLoginSummary = logEntryContainerToUserLoginSummary(logEntryContainer);
            userLoginSummaries.add(userLoginSummary);
        }

        return userLoginSummaries;
    }

    private static UserLoginSummary logEntryContainerToUserLoginSummary(LogEntryContainer logEntryContainer) {
        UserLoginSummary userLoginSummary = new UserLoginSummary();

        userLoginSummary.setUsername(logEntryContainer.getUser());
        userLoginSummary.setLoginSuccessCount(logEntryContainer.getSuccessfulLogins().size());
        userLoginSummary.setLoginFailureCount(logEntryContainer.getFailedLogins().size());

        return userLoginSummary;
    }

    public static List<UserUploadSummary> logEntryContainerMapToUserUploadSummaries(Map<String, LogEntryContainer> logEntryContainerMap) {
        List<UserUploadSummary> userUploadSummaries = new ArrayList<>();

        for (Map.Entry<String, LogEntryContainer> stringLogEntryContainerEntry : logEntryContainerMap.entrySet()) {
            LogEntryContainer logEntryContainer = stringLogEntryContainerEntry.getValue();
            UserUploadSummary userUploadSummary = logEntryContainerToUserUploadSummary(logEntryContainer);
            userUploadSummaries.add(userUploadSummary);
        }

        return userUploadSummaries;
    }

    private static UserUploadSummary logEntryContainerToUserUploadSummary(LogEntryContainer logEntryContainer) {
        UserUploadSummary userUploadSummary = new UserUploadSummary();

        userUploadSummary.setUsername(logEntryContainer.getUser());
        userUploadSummary.setFileUploads(logEntryContainer.getFileUploads());
        userUploadSummary.setFileUploadCount(logEntryContainer.getFileUploads().size());

        return userUploadSummary;
    }


    public static List<SuspiciousIpAttempt> logEntryListToSuspiciousIpAttempts(Map<String, List<LogEntry>> suspiciousIpMap) {
        List<SuspiciousIpAttempt> suspiciousIpAttempts = new ArrayList<>();

        for (Map.Entry<String, List<LogEntry>> entry : suspiciousIpMap.entrySet()) {
            SuspiciousIpAttempt suspiciousIpAttempt = logEntryContainerToSuspiciousIpAttempt(entry);
            suspiciousIpAttempts.add(suspiciousIpAttempt);
        }

        return suspiciousIpAttempts;
    }

    private static SuspiciousIpAttempt logEntryContainerToSuspiciousIpAttempt(Map.Entry<String, List<LogEntry>> entry) {
        SuspiciousIpAttempt suspiciousIpAttempt = new SuspiciousIpAttempt();

        suspiciousIpAttempt.setIpAddress(entry.getKey());
        List<AttemptInfo> attemptInfos = logEntriesToAttemptInfoList(entry.getValue());
        suspiciousIpAttempt.setAttempts(attemptInfos);

        return suspiciousIpAttempt;
    }

    private static List<AttemptInfo> logEntriesToAttemptInfoList(List<LogEntry> logEntries) {
        List<AttemptInfo> attemptInfos = new ArrayList<>();

        for (LogEntry logEntry : logEntries) {
            AttemptInfo attemptInfo = new AttemptInfo();
            attemptInfo.setTimestamp(logEntry.getTimestamp());
            attemptInfo.setActionType(logEntry.getActionType());
            attemptInfo.setUser(logEntry.getUser());
            attemptInfos.add(attemptInfo);
        }
        return attemptInfos;
    }
}
