package one.frei.impl;

import one.frei.domain.enums.ActionType;
import one.frei.domain.model.LogEntry;
import one.frei.domain.model.LogEntryContainer;
import one.frei.domain.model.vo.suspicious.SuspiciousIpAttempt;
import one.frei.domain.model.vo.upload.UserUploadSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class LogFileProcessorImplTest {


    private LogFileProcessorImpl processorHelper;


    @BeforeEach
    void setUp() {
        processorHelper = new LogFileProcessorImpl();
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
    void testGetTopUsersByFileUploads() {
        List<UserUploadSummary> top = processorHelper.retrieveTopUsersByFileUploads(2);

        assertEquals(2, top.size());
        assertEquals("USER014", top.get(0).getUsername());
        assertEquals("USER025", top.get(1).getUsername());
    }

    @Test
    void testDetectSuspiciousLogEntries() {

        List<SuspiciousIpAttempt> suspicious = processorHelper.detectSuspiciousLogEntries();
        assertEquals(5, suspicious.size());
    }
}
