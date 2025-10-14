package one.frei.cli;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class LogFileProcessor implements CommandLineRunner {

    private final LogFileProcessorHelper logFileProcessorHelper;

    public LogFileProcessor(LogFileProcessorHelper logFileProcessorHelper) {
        this.logFileProcessorHelper = logFileProcessorHelper;
    }

    @Override
    public void run(String... args) {
        String logFile = "/home/keenan/Downloads/system_logs.log";
        logFileProcessorHelper.processLogEntry(logFile);
    }
}
