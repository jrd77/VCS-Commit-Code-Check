package com.atzuche.order.admin.filter;

import com.atzuche.order.admin.common.AdminUser;
import com.atzuche.order.admin.common.AdminUserUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 将管理后台用户的信息
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/2 3:29 下午
 **/
@WebFilter(filterName = "adminUserFilter",urlPatterns = "/*")
public class AdminUserFilter implements Filter {
    private final static Logger logger = LoggerFactory.getLogger(AdminUserFilter.class);

    private static final String ERROR_CODE="999999";
    private static final String ERROR_TXT="系统异常:filter";
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        AdminUserUtil.clear();
        if(request instanceof HttpServletRequest) {
            HttpServletRequest req = (HttpServletRequest) request;
            String authName = req.getHeader("Console-AUTH-Name");
            String authId = req.getHeader("Console-AUTH-ID");

            if(StringUtils.trimToNull(authId)!=null) {

                authName = URLDecoder.decode(authName, "utf-8");

                AdminUser adminUser = new AdminUser(authId, authName);

                AdminUserUtil.put(adminUser);
            }

            chain.doFilter(request,response);
        }
        else{
            logger.error("request not the instance of HttpServletRequest,adminUser cannot set----------");
        }

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //do nothing
    }

    @Override
    public void destroy() {
        //do nothing
    }
}
