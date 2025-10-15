package one.frei.domain.model.dto.login;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginSummary {

    private String username;
    private int loginSuccessCount;
    private int loginFailureCount;

    public UserLoginSummary(String username, int successCount, int failureCount) {
        this.username = username;
        this.loginSuccessCount = successCount;
        this.loginFailureCount = failureCount;
    }
}
