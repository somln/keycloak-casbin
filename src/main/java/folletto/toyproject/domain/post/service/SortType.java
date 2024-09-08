package folletto.toyproject.domain.post.service;

import com.sun.jdi.InvalidTypeException;
import folletto.toyproject.global.exception.ApplicationException;
import folletto.toyproject.global.exception.ErrorCode;

public enum SortType {
    DESC("desc"),
    ASC("asc");

    private final String description;

    SortType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static SortType fromDescription(String description) {
        for (SortType type : SortType.values()) {
            if (type.description.equals(description)) {
                return type;
            }
        }
        throw new ApplicationException(ErrorCode.INVALID_SORT_TYPE);
    }
}