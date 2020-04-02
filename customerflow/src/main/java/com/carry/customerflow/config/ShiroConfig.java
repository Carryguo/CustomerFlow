package com.carry.customerflow.config;

import com.carry.customerflow.matcher.CredentialMatcher;
import com.carry.customerflow.realm.AuthRealm;
import com.carry.customerflow.session.ShiroSessionManager;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.LinkedHashMap;

@Configuration
public class ShiroConfig {

    //Redis配置
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.timeout}")
    private int timeout;

    /**
     　　* 开启shiro aop注解支持.
     　　* 使用代理方式;所以需要开启代码支持;
         * @Bean 开启代理
         * 这里的@Qualifier的作用是使用指定的service/bean,而这里的@Qualifier是指下文的 @Bean("credentialMatcher")
     　　*/
    @Bean("authRealm")
    public AuthRealm authRealm(@Qualifier("credentialMatcher")CredentialMatcher matcher,@Qualifier("myCacheManager")RedisCacheManager cacheManager){
        AuthRealm authRealm = new AuthRealm();
        //开启Cache管理
        authRealm.setCacheManager(cacheManager);
        authRealm.setCredentialsMatcher(matcher);
        return authRealm;
    }

    @Bean("credentialMatcher")
    public CredentialMatcher credentialMatcher(){
        return new CredentialMatcher();
    }

    /**
     * 权限管理，配置主要是Realm的管理认证
     * @return manager
     */
    @Bean("securityManager")
    public SecurityManager securityManager(@Qualifier("authRealm") AuthRealm authRealm,@Qualifier("sessionManager") SessionManager sessionManager)
    {
        DefaultSecurityManager  manager = new DefaultWebSecurityManager();
        manager.setRealm(authRealm);
        manager.setSessionManager(sessionManager);
        return manager;
    }

//         Filter工厂，设置对应的过滤条件和跳转条件
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier("securityManager")SecurityManager manager)
    {
        //System.out.print("这里有问题");
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(manager);
//        登录的url
        bean.setLoginUrl("/login");
//        登录成功后的url
//        bean.setSuccessUrl("/index");
//        显示没有授权的url
        bean.setUnauthorizedUrl("/unauthorized");
//        拦截
        LinkedHashMap<String,String> filterChainDefinitionMap = new LinkedHashMap<>();
      /*
        anon: 无需认证即可访问
        authc: 需要认证才可访问
        user: 点击“记住我”功能可访问
        perms: 拥有权限才可以访问
        role: 拥有某个角色权限才能访问
        */

//        需要认证才可访问
//        filterChainDefinitionMap.put("/index","authc");
//        无需认证即可访问
//        filterChainDefinitionMap.put("/login","anon");
//        filterChainDefinitionMap.put("/loginUser","anon");
//        filterChainDefinitionMap.put("/admin","roles[admin]");
//        filterChainDefinitionMap.put("/edit","perms[edit]");
//        filterChainDefinitionMap.put("/admin","authc");
        filterChainDefinitionMap.put("/test","roles[boss,admin]");
        filterChainDefinitionMap.put("/test","roles[boss,staff]");
//        filterChainDefinitionMap.put("/findMachineByAddress","roles[boss,admin]");
//        filterChainDefinitionMap.put("/findMachineByAddress","roles[boss,staff]");
        filterChainDefinitionMap.put("/test","perms[edit,add,query,delete]");

        //开发完再把限权打开
        filterChainDefinitionMap.put("/**","anon");
        bean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return bean;
    }

    /**
     *  开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator和AuthorizationAttributeSourceAdvisor)即可实现此功能
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("securityManager")SecurityManager manager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(manager);
        return advisor;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }

    @Bean("sessionManager")
    public SessionManager sessionManager() {
        ShiroSessionManager shiroSessionManager = new ShiroSessionManager();
        shiroSessionManager.setSessionDAO(redisSessionDAO());
        shiroSessionManager.setGlobalSessionTimeout(7200000);
        return shiroSessionManager;
    }


    /**
     * 配置Cache管理器
     * 用于往Redis存储权限和角色标识
     * @Attention 使用的是shiro-redis开源插件
     */
    @Bean("myCacheManager")
    public RedisCacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
//        redisCacheManager.setKeyPrefix(CACHE_KEY);
        // 配置缓存的话要求放在session里面的实体类必须有个id标识
        redisCacheManager.setPrincipalIdFieldName("username");
        return redisCacheManager;
    }
    /**
     * 配置RedisSessionDAO
     * @Attention 使用的是shiro-redis开源插件
     * @Author Sans
     * @CreateTime 2019/6/12 13:44
     */
    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
//        redisSessionDAO.setSessionIdGenerator(sessionIdGenerator());
//        redisSessionDAO.setKeyPrefix(SESSION_KEY);
        redisSessionDAO.setExpire(7200);
        return redisSessionDAO;
    }

    /**
     * 配置Redis管理器
     * @Attention 使用的是shiro-redis开源插件
     * @Author Sans
     * @CreateTime 2019/6/12 11:06
     */
    @Bean
    public RedisManager redisManager(){
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host);
        redisManager.setPort(port);
        redisManager.setTimeout(timeout);
        return  redisManager;
    }

}
