package folletto.toyproject.domain.group.service;

import folletto.toyproject.domain.group.dto.GroupRequest;
import folletto.toyproject.domain.group.dto.GroupResponse;
import folletto.toyproject.domain.group.entity.GroupEntity;
import folletto.toyproject.domain.group.repository.GroupRepository;
import folletto.toyproject.domain.post.respository.PostRepository;
import folletto.toyproject.domain.user.repository.UserRepository;
import folletto.toyproject.global.exception.ApplicationException;
import folletto.toyproject.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.checkerframework.checker.nullness.Opt.orElseThrow;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public void createGroup(GroupRequest groupRequest) {
        groupRepository.save(GroupEntity.from(groupRequest));
    }

    public void updateGroup(Long groupId, GroupRequest groupRequest) {
        GroupEntity group = findGroupById(groupId);
        group.update(groupRequest);
    }

    private GroupEntity findGroupById(Long groupId) {
        return groupRepository.findById(groupId)
                 .orElseThrow(() -> new ApplicationException(ErrorCode.GROUP_NOT_FOUND));
    }

    public void deleteGroup(Long groupId) {
        groupRepository.deleteById(groupId);
        userRepository.deleteAllByGroupId(groupId);
        postRepository.deleteAllByGroupId(groupId);
    }

    public GroupResponse findGroup(Long groupId) {
        GroupEntity group = findGroupById(groupId);
        return GroupResponse.from(group);
    }


    public List<GroupResponse> findGroups() {
        return groupRepository.findAll().stream().map(GroupResponse::from).toList();
    }
}


