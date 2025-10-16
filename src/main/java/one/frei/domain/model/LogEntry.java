package one.frei.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import one.frei.domain.enums.ActionType;

import java.time.OffsetDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogEntry {

    private OffsetDateTime timestamp;
    private String user;
    private ActionType actionType;
    @Nullable
    private String ipAddress;
    @Nullable
    private String fileName;
}