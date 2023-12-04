package com.WeChatApi.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;


import com.WeChatApi.bean.condition.userInfoCondition;
import com.WeChatApi.bean.dto.operationSubscriptionRecordDto;
import com.WeChatApi.bean.dto.refundDto;
import com.WeChatApi.bean.models.parkinglotsPayRefund;
import com.WeChatApi.bean.models.userInfo;

public interface monthlyCarMapper {

	
	List<userInfo> findUserInfoByConditions(userInfoCondition condition);

	long findUserInfoCountByConditions(userInfoCondition condition);

	void addUserInfo(userInfo sysuser);

	void updateUserInfo(userInfo sysuser);

	List<userInfo> findUserInfoAll();

	void deleteBatch(@Param("ids")List<String> ids);

	void changeStatusBatch(@Param("ids")List<String> ids, @Param("status")String status);

	void updateUserSendInfoStatus(@Param("ids")List<String> ids, @Param("sendStatus")int sendStatus);

	List<Map<String, String>> findSubscriptionTypeByPlkCodeList(@Param("plkCodeList") List<String> plkCodeList);

	List<String> findSubscriptionByPlate(@Param("plate")String plate);

	Map<String, Object> findMonthlyCarByPlate(@Param("pklCode")String pklCode, @Param("plate")String plate);

	void insertSubscriptionRecord(operationSubscriptionRecordDto dto);

	void updateOperationSubscriptionRecordByRid(@Param("rId")String rId);

	List<Map<String, String>> findSubscriptionRecordByUserOpenId(@Param("userOpenId")String userOpenId,@Param("userOpenIdZfb")String userOpenIdZfb);

	void insertSubscriptionRecord_new(operationSubscriptionRecordDto dto);

	List<Map<String, String>> findMonthlyPklRecord(@Param("rSubId")String rSubId);

	Map<String, Object> getSumPriceByRIdList( @Param ("list")List<String> list);

	Map<String, Object> getRoadSumPriceByRIdList(@Param ("list")List<String> list);

	void updateSubscriptionRecordByList(@Param ("list")List<String> outTradeNoList, @Param ("fpurl")String fpurl, @Param ("companyName")String companyName, @Param ("taxNumber")String taxNumber, @Param ("addressMobile")String addressMobile, @Param ("bankNumber")String bankNumber, @Param ("email")String email);

	void updateRoadSubscriptionRecordByList(@Param ("list")List<String> outTradeNoList,@Param ("fpurl") String fpurl, @Param ("companyName")String companyName, @Param ("taxNumber")String taxNumber, @Param ("addressMobile")String addressMobile, @Param ("bankNumber")String bankNumber, @Param ("email")String email);

	List<Map<String, Object>> findSubscriptionRecordByRId(@Param("rId")String rId);

	List<Map<String, Object>> findSubscriptionRecordByRId_road(@Param("rId")String rId);
	
	void insertMonthlyCarRefund(refundDto dto);
	
	parkinglotsPayRefund findMonthlyCarRefundByOutNo(@Param("outRefundNo")String outRefundNo);

	void updateMonthlyCarRefundByOutNo(@Param("outRefundNo")String outRefundNo, @Param("refundId")String refundId);
	
	void updateMonthlyCarRefundByPayId(@Param("r_pay_id")Integer r_pay_id, @Param("sum_refund_fee")int sum_refund_fee);

	void updateMonthlyCarRefundByPayId_road(@Param("r_pay_id")Integer r_pay_id, @Param("sum_refund_fee")int sum_refund_fee);

	List<Map<String, String>> findMonthlyPklRecord_origin(@Param("rSubId")String rSubId,@Param("origin") String origin);

	List<Map<String, Object>> findMonthlyCarPackage(@Param("map")Map<String, Object> map);

	List<Map<String, Object>> findMonthlyCarPackageParkingLotInfo(@Param("map")Map<String, Object> map);

	List<Map<String, Object>> findMonthlyCarPackage_single(@Param("map")Map<String, Object> map);

	void monthlyCarAudit(@Param("map")Map<String, Object> userMap);

	List<Map<String, Object>> findMonthlyCarAudit(@Param("map")Map<String, Object> map);

	List<Map<String, Object>> findOperationSubscriptionParkinglotById(@Param("map")Map<String, Object> map);

	List<Map<String, Object>> findOperationSubscriptionAllById(@Param("map")Map<String, Object> map);

	List<Map<String, Object>> findOperationSubscriptionParkinglotDetById(@Param("map")Map<String, Object> map);

	List<Map<String, Object>> findMonthlyCarPackageAllParkingLotInfo(@Param("map")Map<String, Object> map);

	List<Map<String, String>> findSubscriptionRecordByUserOpenId_new(@Param("userOpenId")String userOpenId,@Param("userOpenIdZfb")String userOpenIdZfb);

	List<Map<String, Object>> findSubscriptionRecordByAudit(@Param("map")Map<String, Object> findRecordMap);

	void updateSubscriptionRecordByList_new(@Param ("list")List<String> outTradeNoList, @Param ("fpurl")String fpurl, @Param ("companyName")String companyName, @Param ("taxNumber")String taxNumber, @Param ("addressMobile")String addressMobile, @Param ("bankNumber")String bankNumber, @Param ("email")String email);


	void updateRoadSubscriptionRecordByList_new(@Param ("list")List<String> outTradeNoList, @Param ("fpurl")String fpurl, @Param ("companyName")String companyName, @Param ("taxNumber")String taxNumber, @Param ("addressMobile")String addressMobile, @Param ("bankNumber")String bankNumber, @Param ("email")String email);

	String findMonthlyCarSingleEndingTime(@Param ("ospId")String ospId, @Param ("plate")String plate);

	String findMonthlyCarParkingAllEndingTime(@Param ("plate")String plate);

	String findMonthlyCarRoadAllEndingTime(@Param ("plate")String plate);

	String findMonthlyCarAllEndingTime(@Param ("plate")String plate);

	long findUseMonthlyCount(@Param ("au_id")String au_id);

	long findMonthlyCarAuditCount(@Param ("map")Map<String, Object> userMap);


}
