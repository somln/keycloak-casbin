package folletto.toyproject.domain.user.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import folletto.toyproject.domain.user.dto.SignupRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@Getter
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String userUUID;
    private String username; //아이디
    private String name;  //이룸
    private String email;

    private Long groupId;
    private boolean isMasterUser;

    @Builder
    public UserEntity(String userUUID, String username, String name, String email, Long groupId, boolean isMasterUser) {
        this.userUUID = userUUID;
        this.username = username;
        this.name = name;
        this.email = email;
        this.groupId = groupId;
        this.isMasterUser = isMasterUser;
    }

    public static UserEntity from(SignupRequest signupRequest, String userUUID) {
        return UserEntity.builder()
                .userUUID(userUUID)
                .username(signupRequest.username())
                .username(signupRequest.username())
                .name(signupRequest.username())
                .email(signupRequest.email())
                .groupId(signupRequest.groupId())
                .isMasterUser(false)
                .build();
    }

    public void setMasterUser() {
        this.isMasterUser = true;
    }

    public void unsetMasterUser() {
        this.isMasterUser = false;
    }
}
