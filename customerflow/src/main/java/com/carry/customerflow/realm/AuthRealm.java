package com.carry.customerflow.realm;

import com.carry.customerflow.bean.Permission;
import com.carry.customerflow.bean.Role;
import com.carry.customerflow.bean.User;
import com.carry.customerflow.service.UserService;
import com.carry.customerflow.utils.ShiroUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AuthRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;
    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // Subject subject = SecurityUtils.getSubject();
        //String id = (String)subject.getSession().getAttribute("id"); 获取当前的用户id
        //获取用户
        User user = (User)principalCollection.fromRealm(this.getClass().getName()).iterator().next();
        System.out.println(user);

        //添加权限认证
        List<String> permissionList = new ArrayList<>();
        //添加角色认证
//        List<String> roleNameList = new ArrayList<>();
        //获取用户角色
//        Set<Role> roleSet = user.getRoles();
//        if (CollectionUtils.isNotEmpty(roleSet)){
//            for (Role role : roleSet)
//            {
//                roleNameList.add(role.getRname());
//                Set<Permission> permissionSet = role.getPermissions();
//                if (CollectionUtils.isNotEmpty(roleSet)) {
//                    for (Permission permission:permissionSet) {
//                        //添加权限 判断这个角色有没有相应的权限
//                        permissionList.add(permission.getName());
//                    }
//                }
//            }
//        }

        Set<Permission> permissionSet = user.getPermissions();
                if (CollectionUtils.isNotEmpty(permissionSet))
                    for (Permission permission:permissionSet)
                        //添加权限 判断这个角色有没有相应的权限
                        permissionList.add(permission.getName());


        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //添加权限认证
        info.addStringPermissions(permissionList);
        //添加用户认证
//        info.addRoles(roleNameList);
        return info;
    }

    //认证登录
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //加这一步的目的是在Post请求的时候会先进认证，然后在到请求
        if (authenticationToken.getPrincipal() == null) {
            return null;
        }
        //获取用户信息
        UsernamePasswordToken token = (UsernamePasswordToken)authenticationToken;
        String username = token.getUsername();
        User user = userService.findByUsername(username);
//        System.out.println(user);
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(user,user.getPassword(),this.getClass().getName());
        //认证成功开始踢人
        ShiroUtil.deleteCache(username,true);
        return simpleAuthenticationInfo;
    }
    /**
     * 清空缓存(一般在修改权限的service中调用)
     */
    public void clearCached(){
        PrincipalCollection principals=SecurityUtils.getSubject().getPrincipals();
        super.clearCache(principals);
    }

}
