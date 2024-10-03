package folletto.toyproject.domain.group.api;

import folletto.toyproject.domain.group.dto.GroupRequest;
import folletto.toyproject.domain.group.dto.GroupResponse;
import folletto.toyproject.domain.group.service.GroupService;
import folletto.toyproject.global.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
public class GroupApi {

    private final GroupService groupService;

    @PostMapping()
    @RolesAllowed({"USER"})
    public ResponseDto<Void> createGroup(
            @RequestBody GroupRequest groupRequest
    ) {
        groupService.createGroup(groupRequest);
        return ResponseDto.created();
    }

    @PutMapping("/{groupId}")
    @RolesAllowed({"USER"})
    public ResponseDto<Void> updateGroup(@PathVariable Long groupId,
                                         @RequestBody GroupRequest groupRequest) {
        groupService.updateGroup(groupId, groupRequest);
        return ResponseDto.created();
    }

    @DeleteMapping("/{groupId}")
    @RolesAllowed({"USER"})
    public ResponseDto<Void> deleteGroup(@PathVariable Long groupId) {
        groupService.deleteGroup(groupId);
        return ResponseDto.created();
    }

    @GetMapping("/{groupId}")
    @RolesAllowed("USER")
    public ResponseDto<GroupResponse> findGroup(@PathVariable Long groupId) {
        return ResponseDto.okWithData(groupService.findGroup(groupId));
    }

    @GetMapping()
    @RolesAllowed("USER")
    public ResponseDto<List<GroupResponse>> findGroups() {
        return ResponseDto.okWithData(groupService.findGroups());
    }
}
