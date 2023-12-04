package com.WeChatApi.controller.roadParkinglotsPay;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
import com.WeChatApi.bean.condition.roadParkinglotsPayCondition;
import com.WeChatApi.bean.dto.doInvoiceDto;
import com.WeChatApi.bean.dto.refundDto;
import com.WeChatApi.bean.models.invoice;
import com.WeChatApi.bean.models.parkinglots;
import com.WeChatApi.bean.models.parkinglotsPay;
import com.WeChatApi.bean.models.roadParkinglotsPay;
import com.WeChatApi.bean.models.storeInvoice;
import com.WeChatApi.controller.base.BaseController;
import com.WeChatApi.controller.base.BaseException;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.service.invoiceService.invoiceService;
import com.WeChatApi.service.roadParkinglotsPayService.roadParkinglotsPayService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;




@RequestMapping("/roadPay")
@Controller
public class roadParkinglotsPayController extends BaseController {
	
	@Autowired
	private roadParkinglotsPayService roadpayservice;
	

	
	@RequestMapping(value = "/findRoadParkingPayInfo", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findRoadParkingPayInfo(@RequestBody roadParkinglotsPayCondition condition) throws JsonProcessingException {
		logger.info("apiName:"+"/roadPay/findRoadParkingPayInfo"+"; param:"+new ObjectMapper().writeValueAsString(condition));
		List<roadParkinglotsPay> list = null;
		try {
			list = roadpayservice.findRoadParkingPayInfo(condition);
			long total=roadpayservice.findRoadParkingPayInfoCount(condition);
			return backJsonPageResult(list,total, StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	@RequestMapping(value = "/findParkingPayInfoByPayId", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findParkingPayInfoByPayId(@RequestBody Map<String, Object> map) throws JsonProcessingException {
		logger.info("apiName:"+"/roadPay/findParkingPayInfoByPayId"+"; param:"+new ObjectMapper().writeValueAsString(map));
		List<parkinglotsPay> list = null;
		try {
			list = roadpayservice.findParkingPayInfoByPayId(map);
			//long total=invoiceservice.findParkingPayInfoCount(condition);
			return backJsonPageResult(list,list.size(), StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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
	
	
	@RequestMapping(value = "/doInvoice", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap doInvoice(@RequestBody doInvoiceDto invoiceDto) throws JsonProcessingException {
		logger.info("apiName:"+"/roadPay/doInvoice"+"; param:"+new ObjectMapper().writeValueAsString(invoiceDto));
		try {
			roadpayservice.doInvoice(invoiceDto);
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
	
	
	/***
	 * Â·±ßÍË¿îÉêÇë
	 * @param refundDto
	 * @return
	 * @throws JsonProcessingException 
	 */
	@RequestMapping(value = "/roadPayRefund", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	@Transactional
	public ModelMap roadPayRefund(@RequestBody refundDto dto,HttpServletRequest request) throws JsonProcessingException {
		logger.info("apiName:"+"/roadPay/roadPayRefund"+"; param:"+new ObjectMapper().writeValueAsString(dto));
		try {
			roadpayservice.roadPayRefund(dto,request);
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
