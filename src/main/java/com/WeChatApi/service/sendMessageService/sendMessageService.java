package com.WeChatApi.service.sendMessageService;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Pack200.Packer;

import javax.transaction.Transactional;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.WeChatApi.bean.condition.couponCondition;
import com.WeChatApi.bean.condition.invoiceCondition;
import com.WeChatApi.bean.condition.operationOrderCondition;
import com.WeChatApi.bean.condition.parkinglotsCondition;
import com.WeChatApi.bean.condition.parkinglotsPayCondition;
import com.WeChatApi.bean.dto.doInvoiceDto;
import com.WeChatApi.bean.models.invoice;
import com.WeChatApi.bean.models.operationOrder;
import com.WeChatApi.bean.models.parkinglots;
import com.WeChatApi.bean.models.parkinglotsPay;
import com.WeChatApi.bean.models.storeInvoice;
import com.WeChatApi.bean.models.wechatUser;
import com.WeChatApi.controller.base.BaseException;
import com.WeChatApi.controller.base.BaseServiceException;
import com.WeChatApi.controller.base.SqlUtil;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.dao.invoiceMapper;
import com.WeChatApi.dao.operationOrderMapper;
import com.WeChatApi.dao.parkinglotsMapper;
import com.WeChatApi.dao.wechatUserMapper;
import com.WeChatApi.service.blueCardService.blueCardService;
import com.WeChatApi.service.wechatUserService.wechatUserService;



@Service
@Transactional
public class sendMessageService {
	
	@Autowired
	private wechatUserMapper wechatUsermapper;

	public void setMessagePower(Map<String, String> map) {
		
		Map<String, Object> tempMap = new HashMap<>();
		
		String userOpenId=map.get("userOpenId").toString();
		
		String templateId=map.get("templateId").toString();
		
		if(StringUtils.isBlank(userOpenId)){
			throw new BaseException(StatusCode.MISSING_OPENID_ERROR.getCode(),"小程序userOpenId不能为空！");
		}
		
		
		if(StringUtils.isBlank(templateId)){
			throw new BaseException(StatusCode.MISSING_OPENID_ERROR.getCode(),"小程序推送模板信息不能为空！");
		}
		
		wechatUser user = wechatUsermapper.findWechatUserInfoByOpenId(map.get("userOpenId").toString());
		if(user==null){
			throw new BaseServiceException(
					StatusCode.PARAMETER_FORMATE_RROR.getCode(),
					"请核实用户信息");
		}
		for (String s : templateId.split(",")) {
			
			List<String>  templateName=wechatUsermapper.findTempNameByTempId(s);
			
			if(templateName.size()!=0){
				tempMap.put("template_name", templateName.get(0).toString());
			}else{
				tempMap.put("template_name", "未知");
			}
			
			tempMap.put("user_open_id", userOpenId);
			tempMap.put("template_id", s);
			tempMap.put("user_id", user.getUserId());
			long num =wechatUsermapper.findWechatUsertTempInfoByTempId(userOpenId,s);
			if(num==0){
				wechatUsermapper.insertWechatUserTemplate(tempMap);
			}else {
				wechatUsermapper.updateWechatUserTemplateByTempId(userOpenId,s);
			}
			
			
		}
		
		
		
	}

	public void doSendMessage(Map<String, String> map) {
		
		
	}

	
	
}
