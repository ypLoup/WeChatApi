package com.WeChatApi.bean.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class wxMsgDto {
	

	 
	    private String touser;//用户openId
	    private String template_id;//模版id
	    private Map<String , TemplateData> data;//推送文字
	    private String page="pages/index/index";//跳转路径 ，默认跳转到小程序首页
		public String getTouser() {
			return touser;
		}
		public void setTouser(String touser) {
			this.touser = touser;
		}
		public String getTemplate_id() {
			return template_id;
		}
		public void setTemplate_id(String template_id) {
			this.template_id = template_id;
		}
		public Map<String, TemplateData> getData() {
			return data;
		}
		public void setData(Map<String, TemplateData> data) {
			this.data = data;
		}
		public String getPage() {
			return page;
		}
		public void setPage(String page) {
			this.page = page;
		}
	 
	

}
