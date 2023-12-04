package com.WeChatApi.dao;

import java.util.List;

import com.WeChatApi.bean.condition.feedbackCondition;
import com.WeChatApi.bean.models.feedback;
import com.WeChatApi.bean.models.feedbackOperation;

public interface feedbackMapper {

	List<feedback> findFeedbackInfo(feedbackCondition condition);

	List<feedbackOperation> findFeedbackOperationByFid(Integer f_id);

	long findFeedbackInfoCount(feedbackCondition condition);

	void addFeedbackInfo(feedback feedback);

	void addFeedbackOperation(feedbackOperation feedbackoperation);

}
