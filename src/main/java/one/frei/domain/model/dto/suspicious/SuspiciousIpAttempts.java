package one.frei.domain.model.dto.suspicious;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SuspiciousIpAttempts {

    private String ipAddress;
    private List<AttemptInfo> attempts;

    public SuspiciousIpAttempts(String ipAddress, List<AttemptInfo> attempts) {
        this.ipAddress = ipAddress;
        this.attempts = attempts;
    }
}
