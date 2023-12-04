package com.WeChatApi.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import com.WeChatApi.bean.condition.wechatUserCondition;
import com.WeChatApi.bean.dto.userChargeDiscountDto;
import com.WeChatApi.bean.models.wechatUser;

public interface wechatUserMapper {

	void deleteBatch(@Param("ids")List<String> ids);

	void changeStatusBatch(@Param("ids")List<String> ids, @Param("status")String status);

	void updateUserSendInfoStatus(@Param("ids")List<String> ids, @Param("sendStatus")int sendStatus);

	List<wechatUser> findWechatUserInfoByConditions(wechatUserCondition condition);

	long findWechatUserInfoCountByConditions(wechatUserCondition condition);

	void addWechatUserInfo(wechatUser userinfo);

	void updateWechatUserInfo(wechatUser userinfo);

	wechatUser findWechatUserInfoByOpenId(@Param("userOpenId")String userOpenId);

	long findWechatUserInfoCountByOpenId(@Param("userOpenId")String userOpenId);

	void updateWechatUserInfoByOpenId(@Param("userOpenId")String userOpenId);

	void insertApiLogs(@Param("apiName")String apiName, @Param("apiKey")String apiKey, @Param("apiResult")String apiResult, @Param("apiRemarks")String apiRemarks);

	void changeUserRoleTypeByUserOpenId(@Param("userOpenId")String userOpenId, @Param("suId")Integer suId);

	userChargeDiscountDto findUserDiscountByScdId(@Param("ucdId")Integer ucdId);

	wechatUser findUserInfoByUserId(@Param("userId")String userId);

	void updateWechatUserInfoMoney(@Param("newBalances")int newBalances, @Param("newPoint")int newPoint, @Param("userId")String userId);

	List<Map<String, Object>> findUserDiscountByCondition();

	void insertVerificationCodeTemp(@Param("mobileNum")String mobileNum, @Param("code")String code);

	long findVerificationCode(@Param("mobileNum")String mobileNum, @Param("code")String code);

	void inserVehPlateVerify(@Param("userOpenId")String userOpenId,@Param("plate")String plate,@Param("plateColor")String plateColor, @Param("mobileNum")String mobileNum, @Param("verifyImageUrl")String verifyImageUrl, @Param("verifyImageUrl2")String verifyImageUrl2,
			@Param("verifyContent")String verifyContent);

	wechatUser findWechatUserInfoCountByPhone(@Param("phone")String phone, @Param("openId")String openId);

	wechatUser findUserInfoByVehplate(@Param("vehplate")String vehplate);

	void insertRedpackRecord(@Param("userId")String userId,@Param("userOpenId")String userOpenId ,@Param("amount") String amount , @Param("orderNumber")String orderNumber, @Param("dataJson")String dataJson, @Param("wechatJson")String wechatJson, @Param("redpackJson")String redpackJson, @Param("type")String type, @Param("resultType")String resultType ,@Param("channelType") String channelType);

	void updateWechatUserBalances(@Param("userId")String userId);

    wechatUser findUserInfoByPhoneNum(@Param("mobileNum")String mobileNum);

	List<wechatUser> findUserInfoByOrderNum(@Param("orderNum")String orderNum);

	void updateWechatUserInfoMoney_Pkl(@Param("newBalances")int newBalances, @Param("newPoint")int newPoint, @Param("userId")String userId);

	long findIsZengsong();

	void addWechatUserPointRecord(@Param("userId")Integer userId);

	long findWechatUserPointRecordByUserId(@Param("userId")Integer userId);

	Map<String, Object> findNotifycation();

	List<String> findTempNameByTempId(@Param("templateId")String templateId);

	void insertWechatUserTemplate(@Param("map")Map<String, Object> tempMap);

	long findWechatUsertTempInfoByTempId(@Param("userOpenId")String userOpenId, @Param("templateId")String templateId);

	void updateWechatUserTemplateByTempId(@Param("userOpenId")String userOpenId, @Param("templateId")String templateId);

	long findBlackPlateCountByPlate(@Param("plate")String plate, @Param("plateColor")String plateColor);


	

}
