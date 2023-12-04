package com.WeChatApi.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.WeChatApi.bean.condition.couponCondition;
import com.WeChatApi.bean.condition.invoiceCondition;
import com.WeChatApi.bean.condition.operationOrderCondition;
import com.WeChatApi.bean.condition.parkinglotsCondition;
import com.WeChatApi.bean.condition.parkinglotsPayCondition;
import com.WeChatApi.bean.models.invoice;
import com.WeChatApi.bean.models.operationOrder;
import com.WeChatApi.bean.models.parkinglots;
import com.WeChatApi.bean.models.parkinglotsPay;

public interface invoiceMapper {

	List<invoice> findInvoiceInfo(invoiceCondition condition);

	long findInvoiceInfoCount(invoiceCondition condition);

	void addInvoiceInfo(invoice invoice);

	void updateInvoiceInfo(invoice invoice);

	void delInvoiceInfo(invoice invoice);

	List<parkinglotsPay> findParkingPayInfo(parkinglotsPayCondition condition);

	long findParkingPayInfoCount(parkinglotsPayCondition condition);

	invoice findInvoiceInfoByTaxNumber(@Param("taxNumber")String taxNumber,@Param("userOpenId") String userOpenId);

	parkinglotsPay findParkingPayNoInvoiceInfoByOutTradeNo(@Param("outTradeNo") String outTradeNo);

	void updateParkingPayInfo(parkinglotsPay pay);

	Map<String, Object> getSumPriceByOutTradeNoList(@Param ("list")List<String> list);

	List<Map<String, Object>> findCouponRecordListByCondition(couponCondition condition);

	long findCouponRecordCountByCondition(couponCondition condition);

	void updateStoreChargeRecordBy(@Param("recordList")List<String> recordList, @Param("fpurl")String fpurl);

	String getStoreSumPriceByRecordId(@Param("recordList")List<String> recordList);

	List<parkinglotsPay> findParkingPayInfoByPayId( @Param("payId")String payId);

	void updateParkingPayInfo_parking_charge_record(parkinglotsPay pay);



	

	

}
