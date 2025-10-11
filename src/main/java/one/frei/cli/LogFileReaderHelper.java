package one.frei.cli;

import one.frei.mapper.LogEntryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Component
public class LogFileReaderHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogFileReaderHelper.class);

    public void readLogFile(String logFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (hasNonAsciiCharacters(line)) {
                    LOGGER.warn("Non asci detected, ignoring content: {} ", line);
                } else {
                    // TODO: use the pojo being mapped
                    LogEntryMapper.mapstringToLogEntry(line);
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