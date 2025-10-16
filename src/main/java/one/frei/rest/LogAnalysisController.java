package one.frei.rest;

import one.frei.processor.LogFileProcessor;
import one.frei.domain.model.LogEntry;
import one.frei.domain.model.LogEntryContainer;
import one.frei.domain.model.dto.login.UserLoginSummary;
import one.frei.domain.model.dto.suspicious.AttemptInfo;
import one.frei.domain.model.dto.suspicious.SuspiciousIpAttempts;
import one.frei.domain.model.dto.upload.UserUploadSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/logs")
public class LogAnalysisController {

    private final LogFileProcessor logFileProcessor;

    private final List<LogEntry> logEntries;

    @Autowired
    public LogAnalysisController(LogFileProcessor logFileProcessor) {
        this.logFileProcessor = logFileProcessor;
        InputStream logStream = getClass().getClassLoader().getResourceAsStream("system_logs.log");
        logEntries = logFileProcessor.readLogFile(logStream);

    }

    @GetMapping("/login-summary")
    public List<UserLoginSummary> getUserLoginSummary() {
        Map<String, LogEntryContainer> logEntryContainerMap = logFileProcessor.createLogEntryContainerMap(logEntries);

        return logEntryContainerMap.values().stream()
                .map(container -> new UserLoginSummary(
                        container.getUser(),
                        container.getSuccessfulLogins().size(),
                        container.getFailedLogins().size()))
                .sorted((a, b) -> Integer.compare(
                        (b.getLoginSuccessCount() + b.getLoginFailureCount()),
                        (a.getLoginSuccessCount() + a.getLoginFailureCount())))
                .toList();
    }

    @GetMapping("/login-summary/download")
    public ResponseEntity<List<UserLoginSummary>> downloadUserLoginSummary() {
        Map<String, LogEntryContainer> logEntryContainerMap = logFileProcessor.createLogEntryContainerMap(logEntries);
        List<UserLoginSummary> result = logEntryContainerMap.values().stream()
                .map(container -> new UserLoginSummary(
                        container.getUser(),
                        container.getSuccessfulLogins().size(),
                        container.getFailedLogins().size()))
                .sorted((a, b) -> Integer.compare(
                        (b.getLoginSuccessCount() + b.getLoginFailureCount()),
                        (a.getLoginSuccessCount() + a.getLoginFailureCount())))
                .toList();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=login-summary.json")
                .body(result);
    }

    @GetMapping("/top-file-uploads")
    public List<UserUploadSummary> getTopUsersByFileUploads(@RequestParam(defaultValue = "3") int count) {
        Map<String, LogEntryContainer> logEntryContainerMap = logFileProcessor.createLogEntryContainerMap(logEntries);
        List<LogEntryContainer> topUsers = logFileProcessor.getTopUsersByFileUploads(logEntryContainerMap, count);

        return topUsers.stream()
                .map(container -> new UserUploadSummary(
                        container.getUser(),
                        container.getFileUploads())).toList();
    }

    @GetMapping("/top-file-uploads/download")
    public ResponseEntity<List<UserUploadSummary>> downloadTopUsersByFileUploads(@RequestParam(defaultValue = "3") int count) {
        Map<String, LogEntryContainer> logEntryContainerMap = logFileProcessor.createLogEntryContainerMap(logEntries);
        List<LogEntryContainer> topUsers = logFileProcessor.getTopUsersByFileUploads(logEntryContainerMap, count);

        List<UserUploadSummary> result = topUsers.stream()
                .map(container -> new UserUploadSummary(
                        container.getUser(),
                        container.getFileUploads())).toList();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=top-file-uploads.json")
                .body(result);
    }

    @GetMapping("/suspicious")
    public List<SuspiciousIpAttempts> getSuspiciousLogEntriesGrouped() {
        Map<String, LogEntryContainer> logEntryContainerMap = logFileProcessor.createLogEntryContainerMap(logEntries);

        List<LogEntry> suspiciousRaw = logFileProcessor.detectSuspiciousLogEntries(logEntryContainerMap);

        Map<String, List<LogEntry>> byIp = suspiciousRaw.stream()
                .filter(entry -> entry.getIpAddress() != null)
                .collect(Collectors.groupingBy(LogEntry::getIpAddress));

        return byIp.entrySet().stream()
                .map(e -> new SuspiciousIpAttempts(
                        e.getKey(),
                        e.getValue().stream()
                                .map(entry -> new AttemptInfo(
                                        entry.getTimestamp().toString(),
                                        entry.getUser(),
                                        entry.getActionType().name()))
                                .toList()
                ))
                .toList();
    }

    @GetMapping("/suspicious/download")
    public ResponseEntity<List<SuspiciousIpAttempts>> downloadSuspiciousLogEntriesGrouped() {
        Map<String, LogEntryContainer> logEntryContainerMap = logFileProcessor.createLogEntryContainerMap(logEntries);
        List<LogEntry> suspiciousRaw = logFileProcessor.detectSuspiciousLogEntries(logEntryContainerMap);
        Map<String, List<LogEntry>> byIp = suspiciousRaw.stream()
                .filter(entry -> entry.getIpAddress() != null)
                .collect(Collectors.groupingBy(LogEntry::getIpAddress));
        List<SuspiciousIpAttempts> result = byIp.entrySet().stream()
                .map(e -> new SuspiciousIpAttempts(
                        e.getKey(),
                        e.getValue().stream()
                                .map(entry -> new AttemptInfo(
                                        entry.getTimestamp().toString(),
                                        entry.getUser(),
                                        entry.getActionType().name()))
                                .toList()
                ))
                .toList();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=suspicious.json")
                .body(result);
    }
}
