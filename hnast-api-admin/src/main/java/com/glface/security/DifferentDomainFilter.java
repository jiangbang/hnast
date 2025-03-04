package com.glface.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 跨域访问过滤器
 */
@Component
public class DifferentDomainFilter implements Filter {

	/**
	 * 是否允许跨域访问
	 */
	@Value("${myself.differentDomain}")
	private boolean differentDomain;

	public DifferentDomainFilter(){
		this.differentDomain = true;
	}

	public DifferentDomainFilter(boolean differentDomain){
		this.differentDomain = differentDomain;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String method = req.getMethod();
		if(differentDomain){
			 res.setHeader("Access-Control-Allow-Origin","*");
			 res.setHeader("Access-Control-Allow-Methods","*");
		}
		res.setHeader("Access-Control-Allow-Headers","x_requested_with,x-requested-with,content-type,token");
		if ("OPTIONS".equals(method)) {
			if (differentDomain) {//允许访问
				return;
			} else {
				res.sendError(400, "禁止跨越访问");
			}
		}else{
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
	}

}
