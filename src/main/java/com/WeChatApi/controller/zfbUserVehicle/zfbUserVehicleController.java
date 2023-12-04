package com.WeChatApi.controller.zfbUserVehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.WeChatApi.bean.condition.wechatUserVehicleCondition;
import com.WeChatApi.bean.condition.zfbUserVehicleCondition;
import com.WeChatApi.bean.models.wechatUserVehicle;
import com.WeChatApi.bean.models.zfbUserVehicle;
import com.WeChatApi.controller.base.BaseController;
import com.WeChatApi.controller.base.BaseException;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.service.wechatUserVehicleService.wechatUserVehicleService;
import com.WeChatApi.service.zfbUserVehicleService.zfbUserVehicleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@RequestMapping("/zfbUserVehicle")
@Controller
public class zfbUserVehicleController extends BaseController {
	
	
	@Autowired
	private zfbUserVehicleService userVehicleservice;
	
	
	@RequestMapping(value = "/findUserVehicleInfo", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findUserVehicleInfoByConditions(@RequestBody wechatUserVehicleCondition condition) throws JsonProcessingException {
		logger.info("apiName:"+"/zfbUserVehicle/findUserVehicleInfo"+"; param:"+new ObjectMapper().writeValueAsString(condition));
		List<wechatUserVehicle> list = null;
		try {
			list = userVehicleservice.findUserVehicleByConditions(condition);
			long total=userVehicleservice.findUserVehicleCountByConditions(condition);
			return backJsonPageResult(list,total, StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	

	

	@RequestMapping(value = "/addUserVehicleInfo", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap addUserVehicleInfo(@RequestBody wechatUserVehicle userVehicle) throws JsonProcessingException {
		logger.info("apiName:"+"/zfbUserVehicle/addUserVehicleInfo"+"; param:"+new ObjectMapper().writeValueAsString(userVehicle));
		try {
			if (true) {
				userVehicleservice.addUserVehicleInfo(userVehicle);
				return backJsonSuccessMsg(StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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

	@RequestMapping(value = "/updateUserVehicleInfo", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap updateWechatUserVehicleInfo(@RequestBody wechatUserVehicle userVehicle) throws JsonProcessingException {
		logger.info("apiName:"+"/zfbUserVehicle/updateUserVehicleInfo"+"; param:"+new ObjectMapper().writeValueAsString(userVehicle));
		try {
			if (true) {
				userVehicleservice.updateUserVehicleInfo(userVehicle);
				return backJsonSuccessMsg(StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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
			userVehicleservice.deleteBatch(ids);
			return this.backJsonSuccessMsg(StatusCode.SUCESSWECHAT.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	@RequestMapping(value = "/deleteVehicle", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ModelMap deleteVehicle(@RequestBody zfbUserVehicle userVehicle) throws JsonProcessingException {
		logger.info("apiName:"+"/zfbUserVehicle/deleteVehicle"+"param:"+new ObjectMapper().writeValueAsString(userVehicle));
		try {
			
			userVehicleservice.deleteVehicle(userVehicle.getUserOpenId(),userVehicle.getUvPlate());
			return this.backJsonSuccessMsg(StatusCode.SUCESSWECHAT.getErrorMsg());
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
			userVehicleservice.changeStatusBatch(ids,status);
			return this.backJsonSuccessMsg(StatusCode.SUCESSWECHAT.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	
	

}
