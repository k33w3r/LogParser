package one.frei.service;

import one.frei.domain.model.LogEntry;
import one.frei.domain.model.LogEntryContainer;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface LogFileProcessorService {

    List<LogEntry> readLogFile(InputStream logFile);
    Map<String, LogEntryContainer> createLogEntryContainerMap(List<LogEntry> logEntries);
    List<LogEntryContainer> getTopUsersByFileUploads(Map<String, LogEntryContainer> logEntryContainerMap, int resultsAmount);
    List<LogEntry> detectSuspiciousLogEntries(Map<String, LogEntryContainer> containers);
}