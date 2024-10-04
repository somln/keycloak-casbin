package folletto.toyproject.domain.group.service;

import folletto.toyproject.domain.group.dto.GroupRequest;
import folletto.toyproject.domain.group.dto.GroupResponse;
import folletto.toyproject.domain.group.entity.GroupEntity;
import folletto.toyproject.domain.group.repository.GroupRepository;
import folletto.toyproject.domain.post.respository.PostRepository;
import folletto.toyproject.domain.user.repository.UserRepository;
import folletto.toyproject.global.casbin.AuthorizationManager;
import folletto.toyproject.global.exception.ApplicationException;
import folletto.toyproject.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static folletto.toyproject.global.dto.ActionType.CREATE;
import static folletto.toyproject.global.dto.ActionType.DELETE;
import static folletto.toyproject.global.dto.ActionType.READ;
import static folletto.toyproject.global.dto.ActionType.UPDATE;
import static folletto.toyproject.global.dto.ObjectType.GROUP;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final AuthorizationManager authorizationManager;

    public void createGroup(GroupRequest groupRequest) {
        authorizationManager.verify(GROUP, CREATE, findMasterGroup().getGroupId());
        validateDuplicateGroup(groupRequest.groupName());
        groupRepository.save(GroupEntity.from(groupRequest));
    }

    private void validateDuplicateGroup(String groupName) {
        if(groupRepository.existsByGroupName(groupName)){
            throw new ApplicationException(ErrorCode.GROUP_NAME_ALREADY_EXISTS);
        }
    }

    public void updateGroup(Long groupId, GroupRequest groupRequest) {
        GroupEntity group = findGroupById(groupId);
        authorizationManager.verify(GROUP, UPDATE, groupId);
        group.update(groupRequest);
    }

    private GroupEntity findGroupById(Long groupId) {
        return groupRepository.findById(groupId)
                 .orElseThrow(() -> new ApplicationException(ErrorCode.GROUP_NOT_FOUND));
    }

    public void deleteGroup(Long groupId) {
        authorizationManager.verify(GROUP, DELETE, groupId);
        groupRepository.deleteById(groupId);
        userRepository.deleteAllByGroupId(groupId);
        postRepository.deleteAllByGroupId(groupId);
    }

    public GroupResponse findGroup(Long groupId) {
        authorizationManager.verify(GROUP, READ, groupId);
        GroupEntity group = findGroupById(groupId);
        return GroupResponse.from(group);
    }

    public List<GroupResponse> findGroups() {
        authorizationManager.verify(GROUP, READ, findMasterGroup().getGroupId());
        return groupRepository.findAll().stream().map(GroupResponse::from).toList();
    }

    private GroupEntity findMasterGroup(){
        return groupRepository.findByIsMasterGroup(true);
    }
}


