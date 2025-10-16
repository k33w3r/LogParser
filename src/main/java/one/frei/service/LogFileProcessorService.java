package one.frei.service;

import one.frei.domain.model.LogEntryContainer;
import one.frei.domain.model.vo.login.UserLoginSummary;
import one.frei.domain.model.vo.suspicious.SuspiciousIpAttempt;
import one.frei.domain.model.vo.upload.UserUploadSummary;

import java.util.List;
import java.util.Map;

public interface LogFileProcessorService {

    Map<String, LogEntryContainer> createLogEntryContainerMap();

    List<UserLoginSummary> retrieveUserLoginSummaries();

    List<UserUploadSummary> retrieveTopUsersByFileUploads(int resultsAmount);

    List<SuspiciousIpAttempt> detectSuspiciousLogEntries();
}