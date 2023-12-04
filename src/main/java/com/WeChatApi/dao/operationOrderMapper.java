package com.WeChatApi.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.WeChatApi.bean.condition.operationOrderCondition;
import com.WeChatApi.bean.models.operationOrder;

public interface operationOrderMapper {

	List<operationOrder> findOrderInfo(operationOrderCondition condition);

	long findOrderInfoCount(operationOrderCondition condition);
	
	List<operationOrder> findOrderInfoLimitOne(operationOrderCondition condition);

	operationOrder findOrderInfoLimitOne_(operationOrderCondition condition);

	operationOrder findOderInfoByOutTradeNo(@Param("pklCode")String pklCode, @Param("orderNum")String orderNum);

	void insertOperationPay(@Param("outTradeNo")String outTradeNo, @Param("orderNumber")String orderNumber, @Param("pakCode")String pakCode, @Param("vehPlate")String vehPlate, @Param("orderNoAtm")String orderNoAtm,@Param("mch_billno")String mch_billno,
			@Param("totalMent")String totalMent, @Param("redValueDouble")String redValueDouble, @Param("payMethod")String payMethod, @Param("payKind")String payKind, @Param("payChannel")String payChannel, @Param("payRemark")String payRemark,
			@Param("inCash")String inCash, @Param("outCash")String outCash);

	long findRedPackCountByOrderNumber(@Param("orderNumber")String orderNumber);

	String findRoadOperationOrderAmount(@Param("list")List<String> list);

	String findRedPackByOrderNumber(@Param("orderNum")String orderNum);

	void updateOrderInfoByOutTradeNo(@Param("couponFee")String couponFee,@Param("payMethod")String payMethod, @Param("outTradeNo")String outTradeNo);

	void updateRoadOrderInfoByOutTradeNo(@Param("couponFee")String couponFee,@Param("payMethod")String payMethod, @Param("outTradeNo")String outTradeNo);

	List<Map<String, Object>> findRoadOrderInfoByOrderId(@Param("orderId")String orderId);

	void updateOperationPayInfoByOutTradeNo(@Param("mchBillno")String mchBillno, @Param("redValueDouble")String redValueDouble, @Param("outTradeNo")String outTradeNo);

	void deleteBatch(@Param("list")List<String> list);

	String findRedPackByOrderNumber_web(@Param("orderNum")String orderNum);

	List<String> findOrderPayNumberList(operationOrderCondition orderCondition);

	void insertSelectRoadPayBy(@Param("order_number")String order_number, @Param("pkl_code")String pkl_code, @Param("pkll_code")String pkll_code, @Param("veh_plate")String veh_plate, @Param("order_receivable")String order_receivable);


	

	

}
