package folletto.toyproject.global.casbin;

public enum SubjectType {
    group,
    user;

    public static SubjectType from(String subjectName) {
        for (SubjectType subjectType : SubjectType.values()) {
            if (subjectType.name().equals(subjectName)) {
                return subjectType;
            }
        }
        throw new IllegalArgumentException("Unknown subject type: " + subjectName);
    }
}
