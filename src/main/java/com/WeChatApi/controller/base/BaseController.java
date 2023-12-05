package com.WeChatApi.controller.base;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;



public class BaseController {

/*	protected static String admin_token="";
	
	protected static String adminCounty="";
	
	protected static String adminArea="";
	
	protected static String adminCommunity="";*/
	
	protected Logger logger = Logger.getLogger(this.getClass());
	/*@Autowired
	private SysLogSettingService sysLogSettingService;
	
	public SysLogSettingService getSysLogSettingService() {
		return sysLogSettingService;
	}

	public void setSysLogSettingService(SysLogSettingService sysLogSettingService) {
		this.sysLogSettingService = sysLogSettingService;
	}*/

	//����ת����yyyy-MM-dd HH:mm:ss��
	@InitBinder
	public void initBinder(ServletRequestDataBinder binder){
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
	            true));
	}
	
	/**
	 * ���ط�ҳjson����
	 * @param list ÿҳ����
	 * @param total �ܼ�¼��
	 * @param code ���ؽ����?
	 * @param msg ������Ϣ
	 * @return ��ҳjson����
	 */
	protected ModelMap backJsonPageResult(List<?> list, long total, int code, String msg) {
		ModelMap modelMap = new ModelMap();
		if (null == list){
			msg = "�޼�¼";
			modelMap.addAttribute("count", 0);
			modelMap.addAttribute("data", new ArrayList<Object>());
		}
		else{
			modelMap.addAttribute("count", total);
			modelMap.addAttribute("data", list);
		}
		modelMap.addAttribute("code", code);
		modelMap.addAttribute("msg", msg);
		/*modelMap.addAttribute("requestId", UUID.randomUUID().toString());
		modelMap.addAttribute("version", "1.0");*/
		//System.out.println(JSONObject.toJSON(modelMap));
		return modelMap;
	}
	//Ĭ�Ϸ�ҳ�ɹ�
	protected ModelMap backJsonPageResult(List<?> list, long total) {
		return backJsonPageResult(list, total,0,"Success");
	}
	/**
	 * ���ض������list��װ���json���ݣ�˽�У�
	 * @param data �������list����
	 * @param code ���ؽ����?
	 * @param msg ������Ϣ
	 * @return �������list��װ���json����
	 */
	protected ModelMap backJsonResult(Object data, int code ,String msg) {
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("data", data);
		modelMap.addAttribute("code", code);
		modelMap.addAttribute("msg", msg);
		modelMap.addAttribute("requestId", UUID.randomUUID().toString());
		modelMap.addAttribute("version", "1.0");
		//System.out.println(JSONObject.toJSON(modelMap));
		return modelMap;
	}
	//Ĭ�ϳɹ�����
	protected ModelMap backJsonResult(Object data) {
		return backJsonResult(data,0,"Success");
	}
	protected ModelMap backJsonReport(List<?> rows, long total, Object footer, int code, String message) {
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("rows", rows);
		modelMap.addAttribute("total", total);
		modelMap.addAttribute("code", code);
		modelMap.addAttribute("msg", message);
		modelMap.addAttribute("requestId", UUID.randomUUID().toString());
		modelMap.addAttribute("version", "1.0");
		modelMap.addAttribute("footer", footer);
		return modelMap;
	}
	
	/**
	 * ���سɹ���json��Ϣ
	 * @param msg json��Ϣ
	 * @return �ɹ���json��Ϣ
	 */
	protected ModelMap backJsonSuccessMsg(int code,String msg) {
		return backJsonResult(null,code,msg);
	}
	//Ĭ�ϳɹ���Ϣ
	protected ModelMap backJsonSuccessMsg() {
		return backJsonSuccessMsg(0,"Success");
	}
	/**
	 * ���سɹ���json��Ϣ
	 * @param msg json��Ϣ
	 * @return �ɹ���json��Ϣ
	 */
	protected ModelMap backJsonSuccessMsg(String msg) {
		return backJsonResult(null, 0, msg);
	}
	
	/**
	 * ����ʧ�ܵ�json��Ϣ
	 * @param code �쳣����
	 * @param msg json��Ϣ
	 * @return ʧ�ܵ�json��Ϣ
	 */
	protected ModelMap backJsonFailureMsg(int code ,String msg) {
		return backJsonResult(null,code,msg);
	}
	//Ĭ��ϵͳ�쳣
	protected ModelMap backJsonFailureMsg() {
		return backJsonFailureMsg(900,"ϵͳ�쳣");
	}

	public void payCallback(HttpServletRequest request, HttpServletResponse response) {
		
		// TODO Auto-generated method stub
		
	}
}
