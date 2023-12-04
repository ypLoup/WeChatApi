package com.WeChatApi.service.feedbackService;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.WeChatApi.bean.condition.feedbackCondition;
import com.WeChatApi.bean.models.feedback;
import com.WeChatApi.bean.models.feedbackOperation;
import com.WeChatApi.bean.models.wechatUser;
import com.WeChatApi.controller.base.BaseServiceException;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.dao.feedbackMapper;
import com.WeChatApi.dao.wechatUserMapper;

@Service
@Transactional
public class feedbackService {
	
	@Autowired
	private wechatUserMapper usermapper;
	
	@Autowired
	private feedbackMapper feedbackmapper;

	public List<feedback> findFeedbackInfo(feedbackCondition condition) {
		if(StringUtils.isBlank(condition.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"openId不能为空！");
		}
		wechatUser user =usermapper.findWechatUserInfoByOpenId(condition.getUserOpenId());
		if(user==null){
			throw new BaseServiceException(
					StatusCode.PARAMETER_FORMATE_RROR.getCode(),
					"用户信息为空,请核实信息！");
		}
		condition.setF_user_id(user.getUserId());
		List<feedback>feedbackList=feedbackmapper.findFeedbackInfo(condition);
		
		List<feedback>newFeedbackList= new ArrayList<feedback>();
		for(feedback feed:feedbackList){
			List<feedbackOperation> operationList=feedbackmapper.findFeedbackOperationByFid(feed.getF_id());
			feed.setDetailList(operationList);
			newFeedbackList.add(feed);
		}
		return newFeedbackList;
	}

	public long findFeedbackInfoCount(feedbackCondition condition) {
		if(StringUtils.isBlank(condition.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"openId不能为空！");
		}
		wechatUser user =usermapper.findWechatUserInfoByOpenId(condition.getUserOpenId());
		if(user==null){
			throw new BaseServiceException(
					StatusCode.PARAMETER_FORMATE_RROR.getCode(),
					"用户信息为空,请核实信息！");
		}
		condition.setF_user_id(user.getUserId());
		//List<feedback>feedbackList=feedbackmapper.findFeedbackInfo(condition);
		return feedbackmapper.findFeedbackInfoCount(condition);
	}

	public void addFeedbackInfo(feedback feedback) {
		if(StringUtils.isBlank(feedback.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"openId不能为空！");
		}
		if(feedback.getF_type()==null){
			throw new BaseServiceException(
					StatusCode.PARAMETER_FORMATE_RROR.getCode(),
					"反馈类型不能为空！");
		}
		wechatUser user =usermapper.findWechatUserInfoByOpenId(feedback.getUserOpenId());
		if(user==null){
			throw new BaseServiceException(
					StatusCode.PARAMETER_FORMATE_RROR.getCode(),
					"用户信息为空,请核实信息！");
		}
		feedback.setF_user_id(user.getUserId());
		
		feedbackmapper.addFeedbackInfo(feedback);
	}

	public void addFeedbackOperation(feedbackOperation feedbackoperation) {
		// TODO Auto-generated method stub
		if(feedbackoperation.getFo_f_id()==null){
			throw new BaseServiceException(
					StatusCode.PARAMETER_FORMATE_RROR.getCode(),
					"用户初次反馈ID不能为空！");
		}
		
		if(StringUtils.isBlank(feedbackoperation.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"openId不能为空！");
		}
		wechatUser user =usermapper.findWechatUserInfoByOpenId(feedbackoperation.getUserOpenId());
		feedbackoperation.setFo_operator_id(user.getUserId());
		feedbackoperation.setFo_operator_name("用户"+user.getUserId());
		
		feedbackmapper.addFeedbackOperation(feedbackoperation);
	}

}
