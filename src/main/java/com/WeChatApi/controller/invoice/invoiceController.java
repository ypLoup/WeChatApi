package com.WeChatApi.controller.invoice;

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
import com.fasterxml.jackson.databind.ObjectMapper;





@RequestMapping("/invoice")
@Controller
public class invoiceController extends BaseController {
	
	@Autowired
	private invoiceService invoiceservice;
	
	
	
	@RequestMapping(value = "/findInvoiceInfo", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findInvoiceInfo(@RequestBody invoiceCondition condition) throws  IOException {
		logger.info("apiName:"+"/invoice/findInvoiceInfo"+"; param:"+new ObjectMapper().writeValueAsString(condition));
		List<invoice> list = null;
		try {
			list = invoiceservice.findInvoiceInfo(condition);
			long total=invoiceservice.findInvoiceInfoCount(condition);
			return backJsonPageResult(list,total, StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
		} catch(BaseException e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	@RequestMapping(value = "/addInvoiceInfo", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap addInvoiceInfo(@RequestBody invoice invoice) throws  IOException {
		logger.info("apiName:"+"/invoice/addInvoiceInfo"+"; param:"+new ObjectMapper().writeValueAsString(invoice));
		
		try {
			invoiceservice.addInvoiceInfo(invoice);
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
	
	@RequestMapping(value = "/updateInvoiceInfo", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap updateInvoiceInfo(@RequestBody invoice invoice) throws  IOException {
		logger.info("apiName:"+"/invoice/updateInvoiceInfo"+"; param:"+new ObjectMapper().writeValueAsString(invoice));
		try {
			invoiceservice.updateInvoiceInfo(invoice);
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
	
	
	@RequestMapping(value = "/delInvoiceInfo", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap delInvoiceInfo(@RequestBody invoice invoice) throws  IOException {
		logger.info("apiName:"+"/invoice/delInvoiceInfo"+"; param:"+new ObjectMapper().writeValueAsString(invoice));
		try {
			invoiceservice.delInvoiceInfo(invoice);
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
	
	
	
	
	@RequestMapping(value = "/findParkingPayInfo", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findParkingPayInfo(@RequestBody parkinglotsPayCondition condition) throws  IOException {
		logger.info("apiName:"+"/invoice/findParkingPayInfo"+"; param:"+new ObjectMapper().writeValueAsString(condition));
		List<parkinglotsPay> list = null;
		try {
			list = invoiceservice.findParkingPayInfo(condition);
			long total=invoiceservice.findParkingPayInfoCount(condition);
			return backJsonPageResult(list,total, StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	@RequestMapping(value = "/findParkingPayInfoByPayId", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findParkingPayInfoByPayId(@RequestBody Map<String, Object> map) throws  IOException {
		logger.info("apiName:"+"/invoice/findParkingPayInfoByPayId"+"; param:"+new ObjectMapper().writeValueAsString(map));
		List<parkinglotsPay> list = null;
		try {
			list = invoiceservice.findParkingPayInfoByPayId(map);
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
	public ModelMap doInvoice(@RequestBody doInvoiceDto invoiceDto) throws  IOException {
		logger.info("apiName:"+"/invoice/doInvoice"+"; param:"+new ObjectMapper().writeValueAsString(invoiceDto));
		try {
			invoiceservice.doInvoice(invoiceDto);
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
	
	
	
	@RequestMapping(value = "/doInvoiceByStore", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap doInvoiceByStore(@RequestBody storeInvoice invoice ) throws  IOException {
		logger.info("apiName:"+"/invoice/doInvoiceByStore"+"; param:"+new ObjectMapper().writeValueAsString(invoice));
		try {
			invoiceservice.doInvoiceByStore(invoice);
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
