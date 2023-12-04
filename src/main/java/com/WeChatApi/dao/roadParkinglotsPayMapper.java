package com.WeChatApi.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.WeChatApi.bean.condition.couponCondition;
import com.WeChatApi.bean.condition.invoiceCondition;
import com.WeChatApi.bean.condition.operationOrderCondition;
import com.WeChatApi.bean.condition.parkinglotsCondition;
import com.WeChatApi.bean.condition.parkinglotsPayCondition;
import com.WeChatApi.bean.condition.roadParkinglotsPayCondition;
import com.WeChatApi.bean.dto.refundDto;
import com.WeChatApi.bean.models.invoice;
import com.WeChatApi.bean.models.operationOrder;
import com.WeChatApi.bean.models.parkinglots;
import com.WeChatApi.bean.models.parkinglotsPay;
import com.WeChatApi.bean.models.roadParkinglotsPay;

public interface roadParkinglotsPayMapper {

	



	List<roadParkinglotsPay> findRoadParkingPayInfo(roadParkinglotsPayCondition condition);

	long findRoadParkingPayInfoCount(roadParkinglotsPayCondition condition);

	Map<String, Object> getSumPriceByOutTradeNoList(@Param ("list")List<String> list);

	List<parkinglotsPay> findParkingPayInfoByPayId( @Param("payId")String payId);

	void updateRoadParkingPayInfo(roadParkinglotsPay pay);

	roadParkinglotsPay findRoadParkingLotsPayInfoByPayId(@Param("payId")Integer payId);

	void insertRoadPayRefund_success(refundDto dto);

	void intsertRoadPayRefundLog(@Param("logTxt")String string,@Param("rId") Integer rId);

	void updateRoadParkingLotsPayRefundAmountByPayId(@Param("payId")Integer payId, @Param("sumRefundFee")int sumRefundFee);

	void insertRoadPayRefund(refundDto dto);

	parkinglotsPay findParkinglotsPayInfoByPayId(@Param("payId") Integer payId);

	List<roadParkinglotsPay> findRoadParkingPayInfo_old(roadParkinglotsPayCondition condition);



	

	

}
