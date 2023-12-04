package com.WeChatApi.controller.feedbackController;

import java.io.IOException;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.WeChatApi.bean.condition.feedbackCondition;
import com.WeChatApi.bean.condition.invoiceCondition;
import com.WeChatApi.bean.models.feedback;
import com.WeChatApi.bean.models.feedbackOperation;
import com.WeChatApi.bean.models.invoice;
import com.WeChatApi.controller.base.BaseController;
import com.WeChatApi.controller.base.BaseException;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.service.feedbackService.feedbackService;
import com.fasterxml.jackson.databind.ObjectMapper;


@RequestMapping("/feedback")
@Controller
public class feedbackController extends BaseController  {
	
	@Autowired
	private feedbackService feedbackservice;
	
	@RequestMapping(value = "/findFeedbackInfo", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findFeedbackInfo(@RequestBody feedbackCondition condition) throws IOException {
		logger.info("apiName:"+"/feedback/findFeedbackInfo"+"; param:"+new ObjectMapper().writeValueAsString(condition));
		List<feedback> list = null;
		try {
			list = feedbackservice.findFeedbackInfo(condition);
			long total=feedbackservice.findFeedbackInfoCount(condition);
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
	
	
	/**
	 * 小程序用户初次反馈信息
	 * @param feedback
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/addFeedbackInfo", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap addFeedbackInfo(@RequestBody feedback feedback) throws  IOException {
		logger.info("apiName:"+"/feedback/addFeedbackInfo"+"; param:"+new ObjectMapper().writeValueAsString(feedback));
		
		try {
			feedbackservice.addFeedbackInfo(feedback);
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
	 * 小程序用户交流反馈信息
	 * @param feedback
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/addFeedbackOperation", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap addFeedbackOperation(@RequestBody feedbackOperation feedbackoperation) throws  IOException {
		logger.info("apiName:"+"/feedback/addFeedbackOperation"+"; param:"+new ObjectMapper().writeValueAsString(feedbackoperation));
		
		try {
			feedbackservice.addFeedbackOperation(feedbackoperation);
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
