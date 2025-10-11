package one.frei.domain.enums;

import lombok.Getter;

@Getter
public enum ActionType {
    LOGIN_FAILURE("LOGIN_FAILURE"),
    LOGIN_SUCCESS("LOGIN_SUCCESS"),
    LOGOUT("LOGOUT"),
    FILE_DOWNLOAD("FILE_DOWNLOAD"),
    FILE_UPLOAD("FILE_UPLOAD");

    private final String value;

    ActionType(String value) {
        this.value = value;
    }


    public static ActionType fromValue(String value) {
        for (ActionType type : ActionType.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown action: " + value);
    }
}