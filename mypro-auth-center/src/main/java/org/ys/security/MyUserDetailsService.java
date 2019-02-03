package org.ys.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.ys.common.constant.CoreMenuContant;
import org.ys.common.constant.CoreUserContant;
import org.ys.core.model.CoreMenu;
import org.ys.core.model.CoreUser;
import org.ys.core.service.CoreMenuService;
import org.ys.core.service.CoreUserService;

import java.util.ArrayList;
import java.util.List;

@Service("myUserDetailsService")
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private CoreUserService coreUserService;
    @Autowired
    private CoreMenuService coreMenuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CoreUser coreUser = null;
        try {
            if(StringUtils.isNotEmpty(username)){
                coreUser = coreUserService.queryCoreUserByUserName(username);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(null == coreUser){
            throw new UsernameNotFoundException("用户"+username+"未找到！");
        }
        List<CoreMenu> coreMenuList = coreMenuService.listCoreMenusByUserId(coreUser.getCoreUserId());
        List<GrantedAuthority> authList = null;
        if(null != coreMenuList && coreMenuList.size() > 0) {
            authList = new ArrayList<>();
            for (CoreMenu coreMenu : coreMenuList) {
                if(CoreMenuContant.MENU_TYPE_PERMISSION.equals(coreMenu.getMenuType()) && StringUtils.isNotEmpty(coreMenu.getPermission())) {
                    authList.add(new SimpleGrantedAuthority(coreMenu.getPermission()));
                }
            }
        }
        boolean enable = true;
        if(coreUser.getStatus() == CoreUserContant.STATUS_DISABLED) {
            enable = false;
        }
        return new User(coreUser.getUserName(),coreUser.getPassword(),enable,true,true,true, authList);
    }
}
