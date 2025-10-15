package one.frei.domain.model.dto.upload;

import lombok.Getter;
import lombok.Setter;
import one.frei.domain.model.LogEntry;

import java.util.List;

@Getter
@Setter
public class UserUploadSummary {

    private String username;
    private int fileUploadCount;
    private List<LogEntry> fileUploads;

    public UserUploadSummary(String username, List<LogEntry> fileUploads) {
        this.username = username;
        this.fileUploadCount = fileUploads.size();
        this.fileUploads = fileUploads;
    }
}
