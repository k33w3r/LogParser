package one.frei.domain.model.vo.login;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginSummary {

    private String username;
    private int loginSuccessCount;
    private int loginFailureCount;

}