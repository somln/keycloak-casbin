package folletto.toyproject.global.casbin;


public enum ActionType {
    CREATE,
    UPDATE,
    DELETE,
    READ;

    public static String from(String actionName) {
        for (ActionType actionType : ActionType.values()) {
            if (actionType.name().equals(actionName)) {
                return actionType.name();
            }
        }
        throw new IllegalArgumentException("Unknown action type: " + actionName);
    }
}