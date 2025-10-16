package one.frei.mapper;

import one.frei.domain.enums.ActionType;
import one.frei.domain.model.LogEntry;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LogEntryMapperTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private static final String TIMESTAMP = "2025-10-14T20:52:00+02:00";


    @Test
    void testMapStringToLogEntry_ValidInputWithIpAddress() {

        String logLine = TIMESTAMP + "|USER1|LOGIN_SUCCESS|IP=192.168.1.1";

        LogEntry result = LogEntryMapper.mapstringToLogEntry(logLine);

        assertNotNull(result);
        assertEquals(OffsetDateTime.parse(TIMESTAMP, FORMATTER), result.getTimestamp());
        assertEquals("USER1", result.getUser());
        assertEquals(ActionType.fromValue("LOGIN_SUCCESS"), result.getActionType());
        assertEquals("192.168.1.1", result.getIpAddress());
        assertNull(result.getFileName());
    }

    @Test
    void testMapStringToLogEntry_ValidInputWithFileName() {

        String logLine = TIMESTAMP + "|USER2|FILE_UPLOAD|FILE=document.pdf";

        LogEntry result = LogEntryMapper.mapstringToLogEntry(logLine);

        assertNotNull(result);
        assertEquals(OffsetDateTime.parse(TIMESTAMP, FORMATTER), result.getTimestamp());
        assertEquals("USER2", result.getUser());
        assertEquals(ActionType.fromValue("FILE_UPLOAD"), result.getActionType());
        assertEquals("document.pdf", result.getFileName());
        assertNull(result.getIpAddress());
    }

    @Test
    void testMapStringToLogEntry_ValidInputWithoutOptionalInfo() {

        String logLine = TIMESTAMP + "|USER3|LOGOUT";

        LogEntry result = LogEntryMapper.mapstringToLogEntry(logLine);

        assertNotNull(result);
        assertEquals(OffsetDateTime.parse(TIMESTAMP, FORMATTER), result.getTimestamp());
        assertEquals("USER3", result.getUser());
        assertEquals(ActionType.fromValue("LOGOUT"), result.getActionType());
        assertNull(result.getIpAddress());
        assertNull(result.getFileName());
    }

    @Test
    void testMapStringToLogEntry_ValidInputWithUnknownInfoType() {
        String logLine = TIMESTAMP + "|user123|ACCESS|UNKNOWN=value";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> LogEntryMapper.mapstringToLogEntry(logLine)
        );

        assertEquals("Unknown action: ACCESS", exception.getMessage());
    }

    @Test
    void testMapStringToLogEntry_InvalidInputWithFewerThanThreeParts() {
        String logLine = "2025-10-14T20:52:00+02:00|USER1";
        LogEntry result = LogEntryMapper.mapstringToLogEntry(logLine);
        assertNull(result);
    }


    @Test
    void testMapStringToLogEntry_ValidInputWithWhitespace() {

        String logLine = "  " + TIMESTAMP + "  |  USER1  |  LOGIN_SUCCESS  |  IP=192.168.1.1  ";

        LogEntry result = LogEntryMapper.mapstringToLogEntry(logLine);

        assertNotNull(result);
        assertEquals(OffsetDateTime.parse(TIMESTAMP, FORMATTER), result.getTimestamp());
        assertEquals("USER1", result.getUser());
        assertEquals(ActionType.fromValue("LOGIN_SUCCESS"), result.getActionType());
        assertEquals("192.168.1.1", result.getIpAddress());
    }

    @Test
    void testMapStringToLogEntry_ValidInputWithMultiplePipeDelimiters() {

        String logLine = TIMESTAMP + "|USER1|LOGIN_SUCCESS|IP=192.168.1.1|extra|data";

        LogEntry result = LogEntryMapper.mapstringToLogEntry(logLine);

        assertNotNull(result);
        assertEquals(OffsetDateTime.parse(TIMESTAMP, FORMATTER), result.getTimestamp());
        assertEquals("USER1", result.getUser());
        assertEquals(ActionType.fromValue("LOGIN_SUCCESS"), result.getActionType());
        assertEquals("192.168.1.1", result.getIpAddress());
    }

}