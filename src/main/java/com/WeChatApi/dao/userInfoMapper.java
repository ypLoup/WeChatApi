package com.WeChatApi.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;


import com.WeChatApi.bean.condition.userInfoCondition;

import com.WeChatApi.bean.models.userInfo;

public interface userInfoMapper {

	
	List<userInfo> findUserInfoByConditions(userInfoCondition condition);

	long findUserInfoCountByConditions(userInfoCondition condition);

	void addUserInfo(userInfo sysuser);

	void updateUserInfo(userInfo sysuser);

	List<userInfo> findUserInfoAll();

	void deleteBatch(@Param("ids")List<String> ids);

	void changeStatusBatch(@Param("ids")List<String> ids, @Param("status")String status);

	void updateUserSendInfoStatus(@Param("ids")List<String> ids, @Param("sendStatus")int sendStatus);

	

}
