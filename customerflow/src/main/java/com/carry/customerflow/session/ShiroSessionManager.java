package com.carry.customerflow.session;

import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

/**
 * shiro 的 session 管理
 * 自定义session规则，实现前后分离，在跨域等情况下使用token 方式进行登录验证才需要，否则没必须使用本类。
 * shiro默认使用 ServletContainerSessionManager 来做 session 管理，它是依赖于浏览器的 cookie 来维护 session 的,
 * 调用 storeSessionId  方法保存sesionId 到 cookie中
 * 为了支持无状态会话，我们就需要继承 DefaultWebSessionManager
 * 自定义生成sessionId 则要实现 SessionIdGenerator
 */
public class ShiroSessionManager extends DefaultWebSessionManager {

    private static final String AUTHORIZATION = "Authorization";
    private static final String REFERENCED_SESSION_ID_SOURCE = "Stateless request";

    public ShiroSessionManager() {
        super();
        this.setDeleteInvalidSessions(true);
    }

    /**
     * 重写方法实现从请求头获取Token便于接口统一
     * 每次请求进来,Shiro会去从请求头找Authorization这个key对应的Value(Token)
     * @param request
     * @param response
     * @return
     */
    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        String token = WebUtils.toHttp(request).getHeader(AUTHORIZATION);
//        System.out.println("token:"+token);
        if(!(token==null)){
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE,REFERENCED_SESSION_ID_SOURCE);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID,token);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID,Boolean.TRUE);
            return token;
        }else{
            //禁用Cookie获取方式  默认从Cookie中获取Token
//        return super.getSessionId(request, response);
            return null;
        }


    }
}

