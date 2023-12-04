package com.WeChatApi.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.WeChatApi.bean.condition.wechatUserCondition;
import com.WeChatApi.bean.condition.wechatUserVehicleCondition;
import com.WeChatApi.bean.condition.zfbUserVehicleCondition;
import com.WeChatApi.bean.models.wechatUser;
import com.WeChatApi.bean.models.wechatUserVehicle;
import com.WeChatApi.bean.models.zfbUserVehicle;

public interface zfbUserVehicleMapper {

	void deleteBatch(@Param("ids")List<String> ids);

	void changeStatusBatch(@Param("ids")List<String> ids, @Param("status")String status);

	void updateUserSendInfoStatus(@Param("ids")List<String> ids, @Param("sendStatus")int sendStatus);

	List<zfbUserVehicle> findUserVehicleByConditions(zfbUserVehicleCondition condition);

	long findUserVehicleCountByConditions(zfbUserVehicleCondition condition);
  
	zfbUserVehicle  findUserVehicleBeanByCondition(zfbUserVehicleCondition condition);

	void addUserVehicleInfo(zfbUserVehicle userVehicle);

	void updateUserVehicleInfo(zfbUserVehicle userVehicle);

	List<zfbUserVehicle> findUserVehicleByOpenId(@Param("userOpenId")String userOpenId);

	void deleteVehicle(@Param("userOpenId")String userOpenId,@Param("vehiclePlate") String vehiclePlate);

	

}
