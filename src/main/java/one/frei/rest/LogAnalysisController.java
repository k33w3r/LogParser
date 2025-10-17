package one.frei.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Min;
import one.frei.config.annotation.LogAnalysisSwagger;
import one.frei.config.annotation.LoginSummaryDownloadSwagger;
import one.frei.config.annotation.LoginSummarySwagger;
import one.frei.config.annotation.SuspiciousDownloadSwagger;
import one.frei.config.annotation.SuspiciousSwagger;
import one.frei.config.annotation.TopFileUploadDownloadSwagger;
import one.frei.config.annotation.TopFileUploadSwagger;
import one.frei.domain.model.vo.login.UserLoginSummary;
import one.frei.domain.model.vo.suspicious.SuspiciousIpAttempt;
import one.frei.domain.model.vo.upload.UserUploadSummary;
import one.frei.impl.LogFileProcessorImpl;
import one.frei.service.LogFileProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@LogAnalysisSwagger
@RestController
@RequestMapping("/api/logs")
public class LogAnalysisController {

    private final LogFileProcessorService logFileProcessorService;

    @Autowired
    public LogAnalysisController(LogFileProcessorImpl logFileProcessorService) {
        this.logFileProcessorService = logFileProcessorService;
    }

    @LoginSummarySwagger
    @GetMapping("/login-summary")
    public List<UserLoginSummary> getUserLoginSummary() {

        return logFileProcessorService.retrieveUserLoginSummaries();
    }

    @LoginSummaryDownloadSwagger
    @GetMapping("/login-summary/download")
    public ResponseEntity<List<UserLoginSummary>> downloadUserLoginSummary() {
        List<UserLoginSummary> userLoginSummary = logFileProcessorService.retrieveUserLoginSummaries();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=login-summary.json")
                .body(userLoginSummary);
    }

    @Validated
    @TopFileUploadSwagger
    @GetMapping("/top-file-uploads")
    public List<UserUploadSummary> getTopUsersByFileUploads(@RequestParam(defaultValue = "3") @Min(value = 1, message = "count must be greater than 0") int count) {
        return logFileProcessorService.retrieveTopUsersByFileUploads(count);
    }

    @TopFileUploadDownloadSwagger
    @GetMapping("/top-file-uploads/download")
    public ResponseEntity<List<UserUploadSummary>> downloadTopUsersByFileUploads(@RequestParam(defaultValue = "3") int count) {
        List<UserUploadSummary> topUsers = logFileProcessorService.retrieveTopUsersByFileUploads(count);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=top-file-uploads.json")
                .body(topUsers);
    }

    @SuspiciousSwagger
    @GetMapping("/suspicious")
    public List<SuspiciousIpAttempt> getSuspiciousLogEntriesGrouped() {
        return logFileProcessorService.detectSuspiciousLogEntries();
    }

    @SuspiciousDownloadSwagger
    @GetMapping("/suspicious/download")
    public ResponseEntity<List<SuspiciousIpAttempt>> downloadSuspiciousLogEntriesGrouped() {
        List<SuspiciousIpAttempt> suspiciousIpAttempts = logFileProcessorService.detectSuspiciousLogEntries();


        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=suspicious.json")
                .body(suspiciousIpAttempts);
    }
}
