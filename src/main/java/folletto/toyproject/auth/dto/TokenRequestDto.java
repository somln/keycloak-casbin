package folletto.toyproject.auth.dto;

import lombok.Builder;

@Builder
public record TokenRequestDto(
        String client_id,
        String client_secret,
        String password,
        String grant_type,
        String username
){
}
