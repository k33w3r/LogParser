package one.frei.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogEntryMetaData {
    private int successfulLoginCount;
    private int failedLoginCount;
    private int fileUploadCount;
    private int fileDownloadCount;
    private int logoutCount;
}