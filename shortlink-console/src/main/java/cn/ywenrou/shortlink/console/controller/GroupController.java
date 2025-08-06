package cn.ywenrou.shortlink.console.controller;

import cn.ywenrou.shortlink.common.core.domain.AjaxResult;
import cn.ywenrou.shortlink.common.core.web.controller.BaseController;
import cn.ywenrou.shortlink.console.dto.req.GroupCreateReqDTO;
import cn.ywenrou.shortlink.console.dto.req.GroupUpdateReqDTO;
import cn.ywenrou.shortlink.console.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/group")
public class GroupController extends BaseController {
    private final GroupService groupService;

    @PostMapping("/create")
    public AjaxResult createGroup(@RequestBody GroupCreateReqDTO requestParam) {
        groupService.createGroup(requestParam);
        return success("分组创建成功");
    }

    @PutMapping("/update")
    public AjaxResult updateGroup(@RequestBody GroupUpdateReqDTO requestParam) {
        groupService.updateGroup(requestParam);
        return success("分组更新成功");
    }

    @DeleteMapping("/delete/{gid}")
    public AjaxResult deleteGroup(@PathVariable("gid") String gid) {
        groupService.deleteGroup(gid);
        return success("分组删除成功");
    }

    @GetMapping("/list")
    public AjaxResult listGroups() {
        return success(groupService.listGroups());
    }
    
    /**
     * 验证分组是否存在且属于当前用户
     */
    @GetMapping("/validate/{gid}")
    public AjaxResult validateGroup(@PathVariable("gid") String gid) {
        return success(groupService.validateGroup(gid));
    }
    
    /**
     * 获取用户默认分组ID
     */
    @GetMapping("/default")
    public AjaxResult getDefaultGroup() {
        return success(groupService.getDefaultGroup());
    }
}
