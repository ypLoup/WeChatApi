package com.WeChatApi.controller.userCharge;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.WeChatApi.bean.dto.doInvoiceDto;
import com.WeChatApi.bean.models.operationOrder;
import com.WeChatApi.controller.base.BaseController;
import com.WeChatApi.controller.base.BaseException;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.service.monthlyCarService.monthlyCarService;
import com.WeChatApi.service.userChargeService.userChargeService;
import com.WeChatApi.service.wechatApiService.wechatApiService;

import com.alipay.api.internal.util.AlipaySignature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sun.misc.BASE64Encoder;

@RequestMapping("/userCharge")
@Controller
public class userChargeController extends BaseController {
	
	@Autowired
	private userChargeService userchargeservice;

	/**
	 * 查询个人预充值记录
	 * @param findTypeMap
	 * @return
	 * @throws JsonProcessingException 
	 */
	@RequestMapping(value = "/findUserChargeRecordInfo", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findUserChargeRecordInfo(@RequestBody Map<String, String> findTypeMap) throws JsonProcessingException {
		logger.info("apiName:"+"/userCharge/findUserChargeRecordInfo"+"; param:"+new ObjectMapper().writeValueAsString(findTypeMap));
		List<Map<String, String>> list = null;
		try {
			list = userchargeservice.findUserChargeRecordInfo(findTypeMap);
			return backJsonResult(list, StatusCode.SUCESSLAYUI.getCode(), StatusCode.SUCESS.getErrorMsg());
		}catch (BaseException e) {
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
    * 预充值开票
    * @param invoiceDto
    * @return
 * @throws JsonProcessingException 
    */
	@RequestMapping(value = "/doInvoice", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap doInvoice(@RequestBody doInvoiceDto invoiceDto ) throws JsonProcessingException {
		logger.info("apiName:"+"/userCharge/doInvoice"+"; param:"+new ObjectMapper().writeValueAsString(invoiceDto));
		try {
			userchargeservice.doInvoice(invoiceDto);
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
