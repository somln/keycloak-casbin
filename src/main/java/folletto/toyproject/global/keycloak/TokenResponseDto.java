package folletto.toyproject.global.keycloak;

import java.util.List;
import java.util.Map;

public class TokenResponseDto {
    private long exp; // 만료 시간
    private long iat; // 발급 시간
    private String jti; // JWT ID
    private String iss; // 발급자
    private List<String> aud; // 대상 클라이언트
    private String sub; // 주체 (사용자 ID)
    private String typ; // 토큰 유형
    private String azp; // 승인된 클라이언트
    private String session_state; // 세션 상태
    private String preferred_username; // 선호 사용자 이름
    private boolean email_verified; // 이메일 확인 여부
    private String acr; // 인증 컨텍스트 클래스
    private RealmAccess realm_access; // Realm 접근 역할
    private Map<String, ResourceAccess> resource_access; // 리소스 접근 역할
    private String scope; // 스코프
    private String sid; // 세션 ID
    private String client_id; // 클라이언트 ID
    private String username; // 사용자 이름
    private boolean active; // 활성 상태

    // Getters and Setters

    public static class RealmAccess {
        private List<String> roles; // 역할 리스트

        // Getters and Setters
    }

    public static class ResourceAccess {
        private List<String> roles; // 역할 리스트

        // Getters and Setters
    }

    // Getters and Setters for TokenResponseDto
}
