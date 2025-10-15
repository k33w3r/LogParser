package one.frei.domain.model.dto.suspicious;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttemptInfo {

    private String timestamp;
    private String user;
    private String actionType;

    public AttemptInfo(String timestamp, String user, String actionType) {
        this.timestamp = timestamp;
        this.user = user;
        this.actionType = actionType;
    }
}
