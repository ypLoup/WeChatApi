package com.WeChatApi.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import com.WeChatApi.bean.condition.wechatUserCondition;
import com.WeChatApi.bean.condition.wechatUserVehicleCondition;
import com.WeChatApi.bean.models.wechatUser;
import com.WeChatApi.bean.models.wechatUserVehicle;

public interface wechatUserVehicleMapper {

	void deleteBatch(@Param("ids")List<String> ids);

	void changeStatusBatch(@Param("ids")List<String> ids, @Param("status")String status);

	void updateUserSendInfoStatus(@Param("ids")List<String> ids, @Param("sendStatus")int sendStatus);

	List<wechatUserVehicle> findUserVehicleByConditions(wechatUserVehicleCondition condition);

	long findUserVehicleCountByConditions(wechatUserVehicleCondition condition);
  
	wechatUserVehicle  findUserVehicleBeanByCondition(wechatUserVehicleCondition condition);

	void addUserVehicleInfo(wechatUserVehicle userVehicle);

	void updateUserVehicleInfo(wechatUserVehicle userVehicle);

	List<wechatUserVehicle> findUserVehicleByOpenId(@Param("userOpenId")String userOpenId);

	void deleteVehicle(@Param("userId")String userId,@Param("vehiclePlate") String vehiclePlate);

	List<wechatUserVehicle> findUserVehicleByUserId(@Param("userId")String userId, @Param("list")List<String> vehPlates);

	List<String> findVehPlatesByUserId(@Param("userId")String userId);

	List<Map<String, String>> findVehPlatesEndingTimeByUserId(@Param("userId")String userId);

	

}
