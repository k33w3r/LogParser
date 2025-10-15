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
        var resource = getClass().getClassLoader().getResource("system_logs.log");
        if (resource == null) {
            throw new IllegalStateException("Unable to find file: system_logs.log");
        }
        String logFilePath = resource.getFile();
        logFileProcessorHelper.processLogEntry(logFilePath);
    }
}
