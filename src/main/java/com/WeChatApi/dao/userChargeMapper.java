package com.WeChatApi.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;


import com.WeChatApi.bean.condition.userInfoCondition;
import com.WeChatApi.bean.dto.operationSubscriptionRecordDto;
import com.WeChatApi.bean.models.userInfo;

public interface userChargeMapper {

	List<Map<String, String>> findUserChargeRecordInfoByUserId(@Param("userId")Integer userId);

	Map<String, Object> getSumUserChargeBalancesByRIdList(@Param("list")List<String> rIdList);

	void updateUserChargeRecordByList(@Param ("list")List<String> outTradeNoList,@Param ("fpurl") String fpurl, @Param ("companyName")String companyName, @Param ("taxNumber")String taxNumber, @Param ("addressMobile")String addressMobile, @Param ("bankNumber")String bankNumber, @Param ("email")String email);

	List<Map<String, String>> findUserChargeRecordInfoByUserId_fpUrlIsNotNull(@Param("userId")Integer userId);

	
	
}
