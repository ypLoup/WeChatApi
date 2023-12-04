package com.WeChatApi.controller.coupon;

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

import com.WeChatApi.bean.condition.couponCondition;
import com.WeChatApi.bean.condition.invoiceCondition;
import com.WeChatApi.bean.condition.parkinglotsCondition;
import com.WeChatApi.bean.condition.parkinglotsPayCondition;
import com.WeChatApi.bean.dto.couponDto;
import com.WeChatApi.bean.dto.doInvoiceDto;
import com.WeChatApi.bean.dto.meetingCodeDto;
import com.WeChatApi.bean.models.invoice;
import com.WeChatApi.bean.models.parkinglots;
import com.WeChatApi.bean.models.parkinglotsPay;
import com.WeChatApi.controller.base.BaseController;
import com.WeChatApi.controller.base.BaseException;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.service.blueCardService.blueCardService;
import com.WeChatApi.service.invoiceService.invoiceService;
import com.fasterxml.jackson.databind.ObjectMapper;





@RequestMapping("/coupon")
@Controller
public class couponController extends BaseController {
	
	@Autowired
	private blueCardService bluecardservice;
	
	@Autowired
	private invoiceService invoiceservice;//·¢Æ±service
	
	
	
	
	
	
	@RequestMapping(value = "/getCoupon", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap getCoupon(@RequestBody couponDto coupon) throws  IOException {
		logger.info("apiName:"+"/coupon/getCoupon"+"; param:"+new ObjectMapper().writeValueAsString(coupon));
		try {
			bluecardservice.getCoupon(coupon);
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
	
	
	@RequestMapping(value = "/findCouponRecord", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap getCoupon(@RequestBody couponCondition condition) throws  IOException {
		logger.info("apiName:"+"/coupon/findCouponRecord"+"; param:"+new ObjectMapper().writeValueAsString(condition));
		List<Map<String, Object>> couponRecordList=null;
		try {
			couponRecordList=invoiceservice.findCouponRecordListByCondition(condition);
			long count =invoiceservice.findCouponRecordCountByCondition(condition);
			return backJsonPageResult(couponRecordList,count,StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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
	
	
	
	@RequestMapping(value = "/getMeetingCode", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap getMeetingCode(@RequestBody meetingCodeDto meetingcode) throws  IOException {
		logger.info("apiName:"+"/coupon/getMeetingCode"+"; param:"+new ObjectMapper().writeValueAsString(meetingcode));
		try {
			bluecardservice.getMeetingCode(meetingcode);
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
