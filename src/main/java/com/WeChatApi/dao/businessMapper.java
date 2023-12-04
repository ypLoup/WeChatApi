package com.WeChatApi.dao;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.annotations.Param;

import com.WeChatApi.bean.condition.businessCouponCondition;
import com.WeChatApi.bean.condition.couponCondition;
import com.WeChatApi.bean.condition.invoiceCondition;
import com.WeChatApi.bean.condition.operationOrderCondition;
import com.WeChatApi.bean.condition.parkinglotsCondition;
import com.WeChatApi.bean.condition.parkinglotsPayCondition;
import com.WeChatApi.bean.condition.storeChargeRecordCondition;
import com.WeChatApi.bean.dto.businessCouponDto;
import com.WeChatApi.bean.dto.rechargeDto;
import com.WeChatApi.bean.dto.refundDto;
import com.WeChatApi.bean.dto.storeChargeDiscountDto;
import com.WeChatApi.bean.dto.storeChargeRecordDto;
import com.WeChatApi.bean.dto.storeCouponRecordDto;
import com.WeChatApi.bean.dto.userChargeRecordDto;
import com.WeChatApi.bean.dto.userRechargeRecordDto;
import com.WeChatApi.bean.dto.userStoreDto;
import com.WeChatApi.bean.models.invoice;
import com.WeChatApi.bean.models.operationOrder;
import com.WeChatApi.bean.models.parkinglots;
import com.WeChatApi.bean.models.parkinglotsPay;
import com.WeChatApi.bean.models.parkinglotsPayRefund;
import com.WeChatApi.bean.models.userStore;

public interface businessMapper {

	userStore findUserStoreByUserName(@Param("userName")String userName, @Param("mdPassword")String mdPassword);

	void changeUserStoreIsBindBy(@Param("suId")Integer suId);

	userStore findUserStoreBySuId(@Param("storeId")Integer storeId);

	void updatePriceByUserStoreDto(userStoreDto userstoredto);

	void insertStoreCouponInfoByDto(businessCouponDto businesscoupondto);

	List<businessCouponDto> findStoreCouponByCondition(businessCouponCondition condition);

	long findStoreCouponCountByCondition(businessCouponCondition condition);

	void updateStoreCouponStatusBycId(@Param("getcId")Integer getcId);

	List<Map<String, Object>> findStoreDiscountByCondition();

	storeChargeDiscountDto findStoreDiscountByScdId(Integer scdId);

	void insertChargePayLog(@Param("logTxt")String logTxt, @Param("chargeRecordId")String chargeRecordId, @Param("logType")String logType);

	businessCouponDto findBusinessCouponByCid(@Param("c_id")Integer c_id);

	int insertChargeRecord(userChargeRecordDto userchargerecord);

	userChargeRecordDto findChargeRecordByRid(@Param("recordId")String recordId);

	void updateChargeRecordStatus(@Param("recordId")String recordId,@Param("status")Integer status);

	void inserStoreChargeRecord(@Param("storeId")Integer storeId, @Param("type")int type, @Param("scdId")Integer scdId, @Param("r_balances")Integer r_balances, @Param("r_point")Integer r_point);

	void insertPayRefund(refundDto dto);

	void updatePayrefundStatus(@Param("outRefundNo")String outRefundNo,@Param("tradeNo") String tradeNo);

	void insertPayRefund_success(refundDto dto);

	void intsertPayRefundLog(@Param("logTxt")String logTxt,@Param("rId") Integer rId);

	parkinglotsPayRefund findRefundDtoByOutNo(@Param("outTradeNo")String outTradeNo);

	void updatePayRefundByOutNo(@Param("outTradeNo")String outTradeNo, @Param("refundId")String refundId);

	parkinglotsPay findParkingLotsPayInfoByPayId(@Param("payId")Integer payId);

	void updateParkingLotsPayRefundAmountByPayId(@Param("payId")Integer payId, @Param("sumRefundFee")Integer sumRefundFee);

	void setStoreCouponUnused();

	List<storeChargeRecordDto> findUserChargeInfoByCondition(storeChargeRecordCondition condition);

	long findUserChargeCountByCondition(storeChargeRecordCondition condition);

	void insertUserChargePayLog(@Param("logTxt")String logTxt, @Param("chargeRecordId")String chargeRecordId, @Param("logType")String logType);

	void insertStoreCouponRecordDtoByList(@Param("list")List<storeCouponRecordDto> list);

	List<userStore> findUserStoreByPhone(@Param("userMobile")String userMobile);

	List<Map<String, Object>> findStoreDiscountByStoreId(@Param("storeId")String storeId);

	int insertStoreChargeRecord(storeChargeRecordDto dto);

	storeChargeRecordDto findStoreChargeRecordByRid(@Param("recordId")String recordId);

	void updateStoreChargeRecordStatus(@Param("recordId")String recordId, @Param("status")Integer status);

	void insertUserRechargeRecordByDto(userRechargeRecordDto record);

	List<userRechargeRecordDto> findUseRechargeRecordByUserId(@Param("userId")String userId);

	void updateUserReChargeRecord(@Param("outTradeNo")String outTradeNo, @Param("refundFee")String refundFee);

	parkinglotsPayRefund findRoadRefundDtoByOutNo(@Param("outRefundNo")String outRefundNo);

	void updateRoadPayRefundByOutNo(@Param("outRefundNo")String outRefundNo, @Param("refundId")String refundId);

	long findUserRechargeRecordCountByOutTradeNo(@Param("outTradeNo")String outTradeNo);

	int insertChargeRecord_cashOut(userChargeRecordDto userchargerecord);

	long findStoreChargePayLogCount(@Param("recordId")String recordId);

	void insertChargeRecord_addWechatUserPoint(userChargeRecordDto dto);

	void updateChargeRecordRLeft(@Param("newBalances")int newBalances, @Param("newPoint")int newPoint, @Param("recordId")String recordId);

	//Map<String, Object> getChargePrepayId(rechargeDto recharge, HttpServletRequest request);

	


}
