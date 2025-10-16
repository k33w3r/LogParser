package one.frei.domain.model.vo.suspicious;

import lombok.Getter;
import lombok.Setter;
import one.frei.domain.enums.ActionType;

import java.time.OffsetDateTime;

@Getter
@Setter
public class AttemptInfo {

    private OffsetDateTime timestamp;
    private String user;
    private ActionType actionType;
}

