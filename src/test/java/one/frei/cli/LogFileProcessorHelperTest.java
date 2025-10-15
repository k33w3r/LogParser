package one.frei.cli;

import one.frei.domain.enums.ActionType;
import one.frei.domain.model.LogEntry;
import one.frei.domain.model.LogEntryContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogFileProcessorHelperTest {


    private LogFileProcessorHelper processorHelper;

    @BeforeEach
    void setUp() {
        processorHelper = new LogFileProcessorHelper();
    }

    @Test
    void testHasNonAsciiCharacters() {

        assertFalse(processorHelper.hasNonAsciiCharacters(""));
        assertFalse(processorHelper.hasNonAsciiCharacters("Normal ASCII text"));

        assertTrue(processorHelper.hasNonAsciiCharacters("Text with non-ascii char: \u0100"));
        assertTrue(processorHelper.hasNonAsciiCharacters("Text with control char: \u0001"));
    }

    @Test
    void testAddLogEntry() {
        LogEntryContainer container = new LogEntryContainer();
        LogEntry loginSuccess = new LogEntry();
        loginSuccess.setActionType(ActionType.LOGIN_SUCCESS);

        LogEntry loginFailure = new LogEntry();
        loginFailure.setActionType(ActionType.LOGIN_FAILURE);

        LogEntry fileUpload = new LogEntry();
        fileUpload.setActionType(ActionType.FILE_UPLOAD);

        processorHelper.addLogEntry(loginSuccess, container);
        processorHelper.addLogEntry(loginFailure, container);
        processorHelper.addLogEntry(fileUpload, container);

        assertEquals(1, container.getSuccessfulLogins().size());
        assertEquals(loginSuccess, container.getSuccessfulLogins().get(0));
        assertEquals(1, container.getFailedLogins().size());
        assertEquals(loginFailure, container.getFailedLogins().get(0));
        assertEquals(1, container.getFileUploads().size());
        assertEquals(fileUpload, container.getFileUploads().get(0));
    }

    @Test
    void testCreateLogEntryContainerMap() {
        LogEntry entry1 = new LogEntry();
        entry1.setUser("user1");
        entry1.setActionType(ActionType.LOGIN_SUCCESS);

        LogEntry entry2 = new LogEntry();
        entry2.setUser("user1");
        entry2.setActionType(ActionType.FILE_UPLOAD);

        LogEntry entry3 = new LogEntry();
        entry3.setUser("user2");
        entry3.setActionType(ActionType.LOGIN_FAILURE);

        List<LogEntry> entries = List.of(entry1, entry2, entry3);

        Map<String, LogEntryContainer> map = processorHelper.createLogEntryContainerMap(entries);

        assertEquals(2, map.size());
        LogEntryContainer user1Container = map.get("user1");
        assertNotNull(user1Container);
        assertEquals("user1", user1Container.getUser());
        assertEquals(1, user1Container.getSuccessfulLogins().size());
        assertEquals(1, user1Container.getFileUploads().size());

        LogEntryContainer user2Container = map.get("user2");
        assertNotNull(user2Container);
        assertEquals(1, user2Container.getFailedLogins().size());
    }

    @Test
    void testGetTopUsersByFileUploads() {
        LogEntryContainer user1 = new LogEntryContainer();
        user1.setUser("user1");
        user1.getFileUploads().add(new LogEntry());

        LogEntryContainer user2 = new LogEntryContainer();
        user2.setUser("user2");
        user2.getFileUploads().add(new LogEntry());
        user2.getFileUploads().add(new LogEntry());

        LogEntryContainer user3 = new LogEntryContainer();
        user3.setUser("user3");

        Map<String, LogEntryContainer> map = Map.of(
                user1.getUser(), user1,
                user2.getUser(), user2,
                user3.getUser(), user3
        );

        List<LogEntryContainer> top = processorHelper.getTopUsersByFileUploads(map, 2);
        assertEquals(2, top.size());
        assertEquals("user2", top.get(0).getUser());
        assertEquals("user1", top.get(1).getUser());
    }

    @Test
    void testDetectSuspiciousLogEntries() {
        LogEntryContainer user1 = new LogEntryContainer();
        user1.setUser("user1");

        OffsetDateTime baseTime = OffsetDateTime.now();

        LogEntry firstFailure = new LogEntry();
        firstFailure.setActionType(ActionType.LOGIN_FAILURE);
        firstFailure.setIpAddress("10.0.0.1");
        firstFailure.setTimestamp(baseTime.plusMinutes(1));
        user1.getFailedLogins().add(firstFailure);

        LogEntry secondFailure = new LogEntry();
        secondFailure.setActionType(ActionType.LOGIN_FAILURE);
        secondFailure.setIpAddress("10.0.0.1");
        secondFailure.setTimestamp(baseTime.plusMinutes(1));
        user1.getFailedLogins().add(secondFailure);

        LogEntry thirdFailure = new LogEntry();
        thirdFailure.setActionType(ActionType.LOGIN_FAILURE);
        thirdFailure.setIpAddress("10.0.0.1");
        thirdFailure.setTimestamp(baseTime.plusMinutes(1));
        user1.getFailedLogins().add(thirdFailure);


        Map<String, LogEntryContainer> map = Map.of(user1.getUser(), user1);

        List<LogEntry> suspicious = processorHelper.detectSuspiciousLogEntries(map);

        assertEquals(3, suspicious.size());
    }
}
