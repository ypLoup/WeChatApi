package com.WeChatApi.controller.sendMessage;

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

import com.WeChatApi.bean.condition.invoiceCondition;
import com.WeChatApi.bean.condition.parkinglotsCondition;
import com.WeChatApi.bean.condition.parkinglotsPayCondition;
import com.WeChatApi.bean.dto.doInvoiceDto;
import com.WeChatApi.bean.models.invoice;
import com.WeChatApi.bean.models.parkinglots;
import com.WeChatApi.bean.models.parkinglotsPay;
import com.WeChatApi.bean.models.storeInvoice;
import com.WeChatApi.controller.base.BaseController;
import com.WeChatApi.controller.base.BaseException;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.service.invoiceService.invoiceService;
import com.WeChatApi.service.sendMessageService.sendMessageService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;




@RequestMapping("/sendMessage")
@Controller
public class sendMessageController extends BaseController {
	
	@Autowired
	private sendMessageService sendMessageservice;
	
	
	
	
	
	/**
	 * 用户授权消息模板
	 * @param map
	 * @return
	 * @throws JsonProcessingException 
	 */
	@RequestMapping(value = "/setMessagePower",  method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap setMessagePower(@RequestBody Map<String, String>map) throws JsonProcessingException {
		logger.info("apiName:"+"/sendMessage/setMessagePower"+"; param:"+new ObjectMapper().writeValueAsString(map));
		
		try {
			sendMessageservice.setMessagePower(map);
			return backJsonSuccessMsg(StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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
	 * 发送消息模板
	 * @param map
	 * @return
	 * @throws JsonProcessingException 
	 */
	@RequestMapping(value = "/doSendMessage", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap doInvoice(@RequestBody Map<String, String>map) throws JsonProcessingException {
		logger.info("apiName:"+"/sendMessage/doSendMessage"+"; param:"+new ObjectMapper().writeValueAsString(map));
		try {
			sendMessageservice.doSendMessage(map);
			return backJsonSuccessMsg(StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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
