package com.glface.security;

import com.alibaba.fastjson.JSON;
import com.glface.base.bean.R;
import com.glface.common.web.ApiCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import javax.annotation.Resource;
import java.io.PrintWriter;

/**
 * spring security配置
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	/**
	 * 不进行权限验证的url
	 */
	@Value("${urlFilter.permit}")
	private String[] permitUrls;

	@Resource
	private DifferentDomainFilter differentDomainFilter;
	@Resource
	private TokenFilter tokenFilter;
	//未登录处理
	@Resource
	private AuthenticationEntryPoint authenticationEntryPoint;

	@Bean
	public HttpFirewall httpFirewall() {
		return new DefaultHttpFirewall();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				.csrf().disable()
				// 基于token，所以不需要session
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.authorizeRequests()
				// 允许匿名访问 还是会访问tokenFilter及之后的Security过滤器
				.antMatchers(permitUrls).permitAll()
				// 除上面外的所有请求全部需要鉴权认证
				.anyRequest().authenticated()
				.and()
				.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
		//tokenFilter在UsernamePasswordAuthenticationFilter之前执行
		httpSecurity.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
		httpSecurity.addFilterBefore(differentDomainFilter, TokenFilter.class);
		// 解决不允许显示在iframe的问题
		httpSecurity.headers().frameOptions().disable();
		httpSecurity.headers().cacheControl();// 禁用缓存
	}

	/**
	 * 密码生成策略.
	 */
	@Bean
	public CustomPasswordEncoder passwordEncoder() {
		return new CustomPasswordEncoder();
	}

	/**
	 * 未登录，返回401
	 */
	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return (request, response, authException) -> {
			response.setStatus(401);
			response.setContentType("application/json;charset=utf-8");
			response.setCharacterEncoding("utf-8");
			PrintWriter writer = response.getWriter();
			writer.write(JSON.toJSONString(R.fail(ApiCode.ACCOUNT_NOT_LOGIN.getMsg())));
			writer.flush();
			writer.close();
		};
	}

}
