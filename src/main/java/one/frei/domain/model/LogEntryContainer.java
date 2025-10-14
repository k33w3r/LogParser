package one.frei.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LogEntryContainer {
    private String user;
    private List<LogEntry> successfulLogins = new ArrayList<>();
    private List<LogEntry> failedLogins = new ArrayList<>();
    private List<LogEntry> fileUploads = new ArrayList<>();
    private List<LogEntry> fileDownloads = new ArrayList<>();
    private List<LogEntry> logouts = new ArrayList<>();
}