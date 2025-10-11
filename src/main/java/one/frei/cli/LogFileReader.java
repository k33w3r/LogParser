package one.frei.cli;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class LogFileReader implements CommandLineRunner {

    private final LogFileReaderHelper logFileReaderHelper;

    public LogFileReader(LogFileReaderHelper logFileReaderHelper) {
        this.logFileReaderHelper = logFileReaderHelper;
    }

    @Override
    public void run(String... args) {
        String logFile = "/home/keenan/Downloads/system_logs.log";
        logFileReaderHelper.readLogFile(logFile);
    }
}
