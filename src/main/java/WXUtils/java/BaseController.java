package WXUtils.java;

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

	//锟斤拷锟斤拷转锟斤拷锟斤拷yyyy-MM-dd HH:mm:ss锟斤�?
	@InitBinder
	public void initBinder(ServletRequestDataBinder binder){
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
	            true));
	}
	
	/**
	 * 锟斤拷锟截凤拷页json锟斤拷锟斤拷
	 * @param list 每页锟斤拷锟斤拷
	 * @param total 锟杰硷拷录锟斤拷
	 * @param code 锟斤拷锟截斤拷锟斤拷锟�
	 * @param msg 锟斤拷锟斤拷锟斤拷息
	 * @return 锟斤拷页json锟斤拷锟斤拷
	 */
	protected ModelMap backJsonPageResult(List<?> list, long total, int code, String msg) {
		ModelMap modelMap = new ModelMap();
		if (null == list){
			msg = "无记�?";
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
	//默锟较凤拷页锟缴癸拷
	protected ModelMap backJsonPageResult(List<?> list, long total) {
		return backJsonPageResult(list, total,0,"Success");
	}
	/**
	 * 锟斤拷锟截讹拷锟斤拷锟斤拷锟絣ist锟斤拷装锟斤拷锟絡son锟斤拷锟捷ｏ拷私锟叫ｏ拷
	 * @param data 锟斤拷锟斤拷锟斤拷锟絣ist锟斤拷锟斤拷
	 * @param code 锟斤拷锟截斤拷锟斤拷锟�
	 * @param msg 锟斤拷锟斤拷锟斤拷息
	 * @return 锟斤拷锟斤拷锟斤拷锟絣ist锟斤拷装锟斤拷锟絡son锟斤拷锟斤拷
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
	//默锟较成癸拷锟斤拷锟斤拷
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
	 * 锟斤拷锟截成癸拷锟斤拷json锟斤拷息
	 * @param msg json锟斤拷息
	 * @return 锟缴癸拷锟斤拷json锟斤拷息
	 */
	protected ModelMap backJsonSuccessMsg(int code,String msg) {
		return backJsonResult(null,code,msg);
	}
	//默锟较成癸拷锟斤拷息
	protected ModelMap backJsonSuccessMsg() {
		return backJsonSuccessMsg(0,"Success");
	}
	/**
	 * 锟斤拷锟截成癸拷锟斤拷json锟斤拷息
	 * @param msg json锟斤拷息
	 * @return 锟缴癸拷锟斤拷json锟斤拷息
	 */
	protected ModelMap backJsonSuccessMsg(String msg) {
		return backJsonResult(null, 0, msg);
	}
	
	/**
	 * 锟斤拷锟斤拷失锟杰碉拷json锟斤拷息
	 * @param code 锟届常锟斤拷锟斤�?
	 * @param msg json锟斤拷息
	 * @return 失锟杰碉拷json锟斤拷息
	 */
	protected ModelMap backJsonFailureMsg(int code ,String msg) {
		return backJsonResult(null,code,msg);
	}
	//默锟斤拷系统锟届�?
	protected ModelMap backJsonFailureMsg() {
		return backJsonFailureMsg(900,"系统异常");
	}

	public void payCallback(HttpServletRequest request, HttpServletResponse response) {
		
		// TODO Auto-generated method stub
		
	}
}
