package one.frei.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Component
public class LogFileReader implements CommandLineRunner {

    Logger LOGGER = LoggerFactory.getLogger(LogFileReader.class);

    @Override
    public void run(String... args) {
        String logFile = "/home/keenan/Downloads/system_logs.log";
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (hasNonAsciiCharacters(line)) {
                    LOGGER.warn("Non asci detected, ignoring content: {} ", line);
                } else {
                    LOGGER.info(line);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read file: {}", logFile, e);
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
