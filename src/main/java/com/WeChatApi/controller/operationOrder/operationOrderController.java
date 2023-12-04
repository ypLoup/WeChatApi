package com.WeChatApi.controller.operationOrder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.annotations.Param;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.WeChatApi.bean.condition.operationOrderCondition;
import com.WeChatApi.bean.condition.userInfoCondition;
import com.WeChatApi.bean.condition.wechatUserCondition;
import com.WeChatApi.bean.models.operationOrder;
import com.WeChatApi.bean.models.parkinglots;
import com.WeChatApi.bean.models.userInfo;
import com.WeChatApi.bean.models.wechatUser;
import com.WeChatApi.controller.base.BaseController;
import com.WeChatApi.controller.base.BaseException;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.service.operationOrderService.operationOrderService;
import com.WeChatApi.service.parkinglotsService.parkinglotsService;
import com.WeChatApi.service.userInfoService.userInfoService;
import com.WeChatApi.service.wechatApiService.wechatApiService;
import com.WeChatApi.service.wechatUserService.wechatUserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import sun.misc.BASE64Encoder;

@RequestMapping("/order")
@Controller
public class operationOrderController extends BaseController {
	
	@Autowired
	private operationOrderService operationOrderservice;
	
	
	
	@RequestMapping(value = "/findOrderInfo", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findOrderInfo(@RequestBody operationOrderCondition condition) throws  IOException {
		logger.info("apiName:"+"/order/findOrderInfo"+"; param:"+new ObjectMapper().writeValueAsString(condition));
		List<operationOrder> list = null;
		try {
			list = operationOrderservice.findOrderInfo(condition);
			long total=operationOrderservice.findOrderInfoCount(condition);
			return backJsonPageResult(list,total, StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
		}catch(BaseException e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	
	
	/**
	 * Âß¼­É¾³ýÒÑ½É·Ñ¶©µ¥
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/deleteBatch", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ModelMap deleteBatch(@RequestBody Map<String, Object> map) {
		try {
			List<String> ids = new ArrayList<String>();
			for (String s : map.get("orderIds").toString().split(",")) {
				ids.add(s);
			}
			operationOrderservice.deleteBatch(ids);
			return this.backJsonSuccessMsg(StatusCode.SUCESSWECHAT.getErrorMsg());
		}catch(BaseException e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	

	

	
	

}
