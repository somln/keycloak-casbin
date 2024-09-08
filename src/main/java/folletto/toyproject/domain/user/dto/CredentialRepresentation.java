package folletto.toyproject.domain.user.dto;

public record CredentialRepresentation(
        String type,
        String value,
        boolean temporary
) {
    public static CredentialRepresentation from(String value) {
        return new CredentialRepresentation("password", value, false);
    }
}
