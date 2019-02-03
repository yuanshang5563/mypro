package org.ys.core.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.ys.common.constant.CoreMenuContant;
import org.ys.common.http.HttpResult;
import org.ys.common.page.PageBean;
import org.ys.common.util.PasswordUtils;
import org.ys.core.vo.CoreUserCondition;
import org.ys.core.model.*;
import org.ys.core.service.CoreMenuService;
import org.ys.core.service.CoreUserRoleService;
import org.ys.core.service.CoreUserService;
import org.ys.core.service.CoreDeptService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/sys/coreUser")
public class CoreUserController {
    @Autowired
    private CoreUserService coreUserService;
    @Autowired
    private CoreDeptService coreDeptService;
    @Autowired
    private CoreUserRoleService coreUserRoleService;
    @Autowired
    private CoreMenuService coreMenuService;

    @PreAuthorize("hasAuthority('ROLE_CORE_USER_LIST')")
    @PostMapping("/findPage")
    public HttpResult findPage(@RequestBody CoreUserCondition coreUserCondition){
        if(null == coreUserCondition){
            return HttpResult.error("查询参数为空");
        }
        PageBean<CoreUser> pageBean = null;
        try {
            String userName = coreUserCondition.getUserName();
            String realName = coreUserCondition.getRealName();
            String sex = coreUserCondition.getSex();
            Long coreDeptId = coreUserCondition.getCoreDeptId();
            CoreUserExample example = new CoreUserExample();
            CoreUserExample.Criteria criteria = example.createCriteria();
            if(StringUtils.isNotEmpty(userName)){
                criteria.andUserNameLike("%"+userName.trim()+"%");
            }
            if(StringUtils.isNotEmpty(realName)){
                criteria.andRealNameLike("%"+realName.trim()+"%");
            }
            if(StringUtils.isNotEmpty(sex)){
                criteria.andSexEqualTo(sex.trim());
            }
            if(null != coreDeptId){
                Set<CoreDept> coreDepts = coreDeptService.queryAllSubCoreDeptsByDeptId(coreDeptId);
                if(null != coreDepts && coreDepts.size() > 0){
                    List<Long> deptIds = new ArrayList<>();
                    for (CoreDept coreDept : coreDepts) {
                        deptIds.add(coreDept.getCoreDeptId());
                    }
                    criteria.andCoreDeptIdIn(deptIds);
                }
            }
            pageBean = coreUserService.pageCoreUsersByExample(example, coreUserCondition.getPageNum(), coreUserCondition.getPageSize());
            if(null == pageBean) {
                pageBean = new PageBean<>(new ArrayList<>());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.error("程序出现异常");
        }
        return HttpResult.ok(pageBean);
    }

    @PreAuthorize("hasAuthority('ROLE_CORE_USER_ADD_EDIT')")
    @PostMapping("/saveOrEdit")
    public HttpResult saveOrEdit(@RequestBody CoreUser coreUser){
        if(null == coreUser){
            return HttpResult.error("参数为空");
        }
        try {
            CoreUserExample example = new CoreUserExample();
            example.createCriteria().andUserNameEqualTo(coreUser.getUserName());
            List<CoreUser> coreUsers = coreUserService.queryCoreUsersByExample(example);
            if(null != coreUsers && coreUsers.size() > 0){
                if(!(coreUsers.size() == 1 && coreUser.getCoreUserId() != null && coreUser.getCoreUserId() != 0l)){
                    return HttpResult.error("用户名已经存在！");
                }
            }
            coreUserService.updateCoreUserAndRoles(coreUser);
            return HttpResult.ok("保存成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.error("程序出现异常");
        }
    }

    @PreAuthorize("hasAuthority('ROLE_CORE_USER_DEL')")
    @DeleteMapping("/delete")
    public HttpResult delete(@RequestParam Long coreUserId){
        if(null == coreUserId || coreUserId == 0){
            return HttpResult.error("参数为为空！");
        }
        try {
            coreUserService.delCoreUserById(coreUserId);
            return HttpResult.ok("删除成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.error("程序出现异常");
        }
    }

    @PreAuthorize("hasAuthority('ROLE_CORE_USER_EDIT_VIEW')")
    @GetMapping("/find")
    public HttpResult find(@RequestParam Long coreUserId){
        if(null == coreUserId || coreUserId == 0){
            return HttpResult.ok();
        }
        try {
            CoreUser coreUser = coreUserService.queryCoreUserById(coreUserId);
            if(null != coreUser){
                List<CoreUserRole> coreUserRoles = coreUserRoleService.findCoreUserRoleByUserId(coreUserId);
                if(null != coreUserRoles && coreUserRoles.size() > 0){
                    coreUser.setUserRoles(coreUserRoles);
                }
                CoreDept coreDept = coreDeptService.queryCoreDeptById(coreUser.getCoreDeptId());
                if(null != coreDept){
                    coreUser.setDeptName(coreDept.getDeptName());
                }
            }
            return HttpResult.ok(coreUser);
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.error("程序出现异常");
        }
    }

    @PreAuthorize("hasAuthority('ROLE_CORE_USER_RESET')")
    @GetMapping("/resetPass")
    public HttpResult resetPass(@RequestParam Long coreUserId,@RequestParam String password){
        if(null == coreUserId || coreUserId == 0 || StringUtils.isEmpty(password)){
            return HttpResult.error("参数为空");
        }
        try {
            CoreUser coreUser = coreUserService.queryCoreUserById(coreUserId);
            if(null != coreUser){
                coreUser.setPassword("{bcrypt}"+PasswordUtils.encode(password));
                coreUserService.updateById(coreUser);
            }
            return HttpResult.ok("重置密码成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.error("程序出现异常");
        }
    }

    @GetMapping(value="/findPermissions")
    public HttpResult findPermissions(@RequestParam String userName) {
        Set<String> permiss = new HashSet<>();
        if(StringUtils.isEmpty(userName)){
            return HttpResult.ok(permiss);
        }
        try {
            CoreUser coreUser = coreUserService.queryCoreUserByUserName(userName);
            if(null != coreUser){
                List<CoreMenu> coreMenus = coreMenuService.listCoreMenusByUserId(coreUser.getCoreUserId());
                if(null != coreMenus && coreMenus.size() > 0){
                    for (CoreMenu coreMenu : coreMenus) {
                        if(StringUtils.equals(CoreMenuContant.MENU_TYPE_PERMISSION,coreMenu.getMenuType())){
                            permiss.add(coreMenu.getPermission());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResult.error("程序出现异常");
        }
        return HttpResult.ok(permiss);
    }
}
