package com.WeChatApi.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionFilter implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}
 
	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) arg0;
		HttpServletResponse httpResponse = (HttpServletResponse) arg1;
		String loginUrl = httpRequest.getContextPath() + "/login.jsp";
		
		String url = httpRequest.getRequestURI();
		if (!url.contains("login.jsp") && httpRequest.getSession(false) == null) {//indexï¿½ï¿½ï¿½ÒµÄµï¿½Â¼Ò³ï¿½ï¿½Â·ï¿½ï¿½
			String str = "<script language='javascript'>alert('系统超时，请重新登录！');"
					+ "window.top.location.href='"
					+ loginUrl
					+ "';</script>";
			httpResponse.setContentType("text/html;charset=UTF-8");// ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
			PrintWriter writer = httpResponse.getWriter();
			writer.write(str);
			writer.flush();
			return;
		}
		chain.doFilter(httpRequest, httpResponse);
	}
 
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
	}


}
