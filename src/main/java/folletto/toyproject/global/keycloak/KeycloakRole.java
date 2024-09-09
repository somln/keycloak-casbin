package folletto.toyproject.global.keycloak;

public record KeycloakRole(
        String id,
        String name
){
    public static KeycloakRole of(String id, String name) {
        return new KeycloakRole(id, name);
    }
}
