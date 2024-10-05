package folletto.toyproject.global.casbin;

public enum ObjectType {
    GROUP,
    BOARD,
    USER,
    ROLE;

    public static String from(String objectName) {
        for (ObjectType objectType : ObjectType.values()) {
            if (objectType.name().equalsIgnoreCase(objectName)) {
                return objectType.name();
            }
        }
        throw new IllegalArgumentException("Unknown object type: " + objectName);
    }
}