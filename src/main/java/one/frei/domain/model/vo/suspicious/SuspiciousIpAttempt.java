package one.frei.domain.model.vo.suspicious;

import lombok.Getter;
import lombok.Setter;
import one.frei.domain.model.LogEntry;

import java.util.List;

@Getter
@Setter
public class SuspiciousIpAttempt {

    private String ipAddress;
    private List<AttemptInfo> attempts;
}
