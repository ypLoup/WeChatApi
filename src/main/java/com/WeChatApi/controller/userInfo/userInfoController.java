package com.WeChatApi.controller.userInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.WeChatApi.bean.condition.userInfoCondition;
import com.WeChatApi.bean.models.userInfo;
import com.WeChatApi.controller.base.BaseController;
import com.WeChatApi.controller.base.BaseException;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.service.userInfoService.userInfoService;
import sun.misc.BASE64Encoder;

@RequestMapping("/userInfo")
@Controller
public class userInfoController extends BaseController {
	
	@Autowired
	private userInfoService userinfoservice;
	
	
	
	@RequestMapping(value = "/getUserInfo", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findUserInfoByConditions(userInfoCondition condition) {
		List<userInfo> list = null;
		try {
			list = userinfoservice.findUserInfoByConditions(condition);
			long total=userinfoservice.findUserInfoCountByConditions(condition);
			return backJsonPageResult(list,total, StatusCode.SUCESSLAYUI.getCode(), StatusCode.SUCESS.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	@RequestMapping(value = "/getUserInfoAll", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap getUserInfoAll() {
		List<Map<String, String>> userInfoList=null;
		try {
			userInfoList = userinfoservice.findUserInfoAll();
			
			return backJsonResult(userInfoList, StatusCode.SUCESSLAYUI.getCode(), StatusCode.SUCESS.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	

	@RequestMapping(value = "/addUserInfo", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap addUserInfo(userInfo userinfo) {
		try {
			if (true) {
				userinfoservice.addUserInfo(userinfo);
				return backJsonSuccessMsg(StatusCode.SUCESS.getCode(), StatusCode.SUCESS.getErrorMsg());
			}
			return backJsonFailureMsg(StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					StatusCode.PARAMETER_FORMATE_RROR.getErrorMsg());
		}catch(BaseException e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}

	@RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap updateUserInfo(userInfo userinfo) {
		try {
			if (true) {
				userinfoservice.updateUserInfo(userinfo);
				return backJsonSuccessMsg(StatusCode.SUCESS.getCode(), StatusCode.SUCESS.getErrorMsg());
			}
			return backJsonFailureMsg(StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					StatusCode.PARAMETER_FORMATE_RROR.getErrorMsg());
		}catch(BaseException e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		} 
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}

	@RequestMapping(value = "/deleteBatch", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ModelMap deleteBatch(@RequestParam Map<String, Object> map) {
		try {
			List<String> ids = new ArrayList<String>();
			for (String s : map.get("ids").toString().split(",")) {
				ids.add(s);
			}
			userinfoservice.deleteBatch(ids);
			return this.backJsonSuccessMsg(StatusCode.SUCESS.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	@RequestMapping(value = "/changeStatusBatch", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ModelMap changeStatusBatch(@RequestParam Map<String, Object> map,String status) {
		try {
			List<String> ids = new ArrayList<String>();
			for (String s : map.get("ids").toString().split(",")) {
				ids.add(s);
			}
			userinfoservice.changeStatusBatch(ids,status);
			return this.backJsonSuccessMsg(StatusCode.SUCESS.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	@RequestMapping(value = "/uploadImg", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ModelMap deleteBatch(@RequestParam("file") MultipartFile file) {
		try {
			
			BASE64Encoder base64Encoder =new BASE64Encoder();
	        String base64EncoderImg = base64Encoder.encode(file.getBytes());
	        //String imageUrl=userinfoservice.uploadImg(base64EncoderImg,file.getOriginalFilename());
	        String imageUrl=userinfoservice.uploadImg2(file);
			
			return this.backJsonResult(imageUrl,StatusCode.SUCESS.getCode(),StatusCode.SUCESS.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	

}
