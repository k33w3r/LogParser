package one.frei.domain.model;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import one.frei.domain.enums.ActionType;

import java.time.OffsetDateTime;

@Getter
@Setter
public class LogEntry {

    private OffsetDateTime timestamp;
    private String user;
    private ActionType actionType;
    @Nullable
    private String ipAddress;
    @Nullable
    private String fileName;

    @Override
    public String toString() {
        return "\n########## LogEntry ##########" +
                "\n  timestamp: " + timestamp +
                "\n  user: " + user +
                "\n  actionType: " + actionType +
                "\n  ipAddress: " + ipAddress +
                "\n  fileName: " + fileName +
                "\n#############################";
    }
}