package folletto.toyproject.domain.group.dto;

import folletto.toyproject.domain.group.entity.GroupEntity;

public record GroupResponse(
        Long groupId,
        String groupName,
        String description
) {
    public static GroupResponse from(GroupEntity groupEntity) {
        return new GroupResponse(groupEntity.getGroupId(), groupEntity.getGroupName(), groupEntity.getDescription());
    }
}
