package com.github.ulwx.aka.webmvc.filter;

import com.ulwx.tool.RandomUtils;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;


/**
 * 处理Tomcat 服务器表单编码问题。
 * @author htqx
 * @version 1.0 2008-1-20
 */
@WebFilter(urlPatterns = {"/*"})
@Order(10)
public class F1LogFilter implements Filter {

    /** *//**
     * 客户端的编码类型。
     * 默认为 UTF-8
     */
    String encode = "UTF-8";

    public void destroy() {
        
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

    	MDC.put("logid", RandomUtils.getRandomNumberString(6));
    	try{
        chain.doFilter(request, response);
    	}finally{
    		MDC.remove("logid");
    	}
        
    }

	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}


}
