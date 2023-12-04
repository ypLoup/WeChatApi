package com.WeChatApi.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.WeChatApi.bean.dto.parkinglotsChargeRecordDto;
import com.WeChatApi.bean.dto.roadPklPayCode;
import com.WeChatApi.bean.models.parkinglots;

public interface parkinglotsMapper {

	List<parkinglots> findParkinglotInfo();

	long findParkinglotInfoCount();

	roadPklPayCode findPayCodeInfoByPklCode(@Param("pklCode")String pklCode);

	long findParkingLotsFreeCountByParkingCode(@Param("pklCode")String pklCode);

	List<Map<String, String>> findParkinglotsCharge();

	List<Map<String, String>> findParkinglotsChargeByChargeId(@Param("chargeId")String chargeId);

	void insertParkinglotsChargeRecord(parkinglotsChargeRecordDto parkinglotsChargeRecord);

	List<Map<String, Object>> findParkingChargeRecordByCrId(@Param("cr_id")String cr_id);

	List<Map<String, String>> findParkinglotsChargeRecord(@Param("userId")String userId, @Param("fpUrlStatus")String fpUrlStatus);

	Map<String, Object> getSumPriceByOutTradeNoList(@Param("list")List<String> outTradeNoList);

	void updateParkinglotsChargeRecordStatus(@Param("cr_id") String cr_id);

	void insertParkinglotsChargeLogInfo(@Param("i_type")String i_type, @Param("userId")String userId, @Param("cr_id")String cr_id, @Param("i_balances")String i_balances, @Param("i_point")String i_point,
			@Param("newBalances")int newBalances, @Param("newPoint")int newPoint, @Param("i_text")String i_text);

	String getParkinglotsMchSeqByPklCode(@Param("pklCode")String pklCode);

	List<Map<String, Object>> findParkinglotsInfoList(@Param("pklCode")String pkl_code);

	

	

}
