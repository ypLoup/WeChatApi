package com.WeChatApi.service.zfbUserVehicleService;

import java.util.List;
import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.WeChatApi.bean.condition.wechatUserCondition;
import com.WeChatApi.bean.condition.wechatUserVehicleCondition;
import com.WeChatApi.bean.condition.zfbUserVehicleCondition;
import com.WeChatApi.bean.models.wechatUser;
import com.WeChatApi.bean.models.wechatUserVehicle;
import com.WeChatApi.bean.models.zfbUserVehicle;
import com.WeChatApi.controller.base.BaseServiceException;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.dao.wechatUserMapper;
import com.WeChatApi.dao.wechatUserVehicleMapper;
import com.WeChatApi.dao.zfbUserVehicleMapper;


@Service
@Transactional
public class zfbUserVehicleService {
	
	@Autowired
	private wechatUserVehicleMapper userVehiclemapper;
	
	@Autowired
	private wechatUserMapper wechatUsermapper;

	public List<wechatUserVehicle> findUserVehicleByConditions(wechatUserVehicleCondition condition) {
		if(StringUtils.isBlank(condition.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"小程序openId不能为空！");
		}
		wechatUser wechatUser =wechatUsermapper.findWechatUserInfoByOpenId(condition.getUserOpenId());
		condition.setUserOpenId(null);
		condition.setUserId(wechatUser.getUserId());
		return userVehiclemapper.findUserVehicleByConditions(condition);
	}

	public long findUserVehicleCountByConditions(wechatUserVehicleCondition condition) {
		wechatUser wechatUser =wechatUsermapper.findWechatUserInfoByOpenId(condition.getUserOpenId());
		condition.setUserOpenId(null);
		condition.setUserId(wechatUser.getUserId());
		return userVehiclemapper.findUserVehicleCountByConditions(condition);
	}

	
	
	public void addUserVehicleInfo(wechatUserVehicle userVehicle) {
		if(StringUtils.isBlank(userVehicle.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"小程序openId不能为空！");
		}
		wechatUser wechatUser =wechatUsermapper.findWechatUserInfoByOpenId(userVehicle.getUserOpenId());
		if(StringUtils.isBlank(userVehicle.getUvPlate())){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"用户车牌号不能为空！");
		}
		/*zfbUserVehicleCondition condition = new zfbUserVehicleCondition();
		condition.setUvPlate(userVehicle.getUvPlate());
		condition.setUvStatus(1);
		List<zfbUserVehicle> vehicleList=userVehiclemapper.findUserVehicleByConditions(condition);*/
		wechatUserVehicleCondition condition = new wechatUserVehicleCondition();
		//condition.setUserOpenId(userVehicle.getUserOpenId());
		condition.setUvPlate(userVehicle.getUvPlate());
		condition.setUvStatus(1);
		List<wechatUserVehicle> vehicleList=userVehiclemapper.findUserVehicleByConditions(condition);
		if(vehicleList.size()!=0){
			throw new BaseServiceException(
					StatusCode.DATA_IS_EXISTS.getCode(),
					"车牌号:"+userVehicle.getUvPlate()+"已存在，且正常使用中！");
		}else{
			//zfbUserVehicleCondition condition2 = new zfbUserVehicleCondition();
			wechatUserVehicleCondition condition2 = new wechatUserVehicleCondition();
			condition2.setUserOpenId(userVehicle.getUserOpenId());
			condition2.setUvPlate(userVehicle.getUvPlate());
			//zfbUserVehicle a =userVehiclemapper.findUserVehicleBeanByCondition(condition2);
			wechatUserVehicle a =userVehiclemapper.findUserVehicleBeanByCondition(condition2);
			if(a!=null&&a.getUvStatus()==0){
				userVehicle.setUvId(a.getUvId());
				userVehicle.setUvStatus(1);
				userVehiclemapper.updateUserVehicleInfo(userVehicle);
			}else{
				userVehicle.setUserId(wechatUser.getUserId());
				userVehiclemapper.addUserVehicleInfo(userVehicle);
			}
		}
		
		/*wechatUserVehicleCondition condition2 = new wechatUserVehicleCondition();
		condition2.setUserOpenId(userVehicle.getUserOpenId());
		condition2.setUvPlate(userVehicle.getUvPlate());
		wechatUserVehicle a =userVehiclemapper.findUserVehicleBeanByCondition(condition2);
		if(a!=null&&a.getUvStatus()==0){
			userVehicle.setUvId(a.getUvId());
			userVehicle.setUvStatus(1);
			userVehiclemapper.updateUserVehicleInfo(userVehicle);
		}else{
			userVehiclemapper.addUserVehicleInfo(userVehicle);
		}*/
		
		
		
	}

	public void deleteBatch(List<String> ids) {
		// TODO Auto-generated method stub
		userVehiclemapper.deleteBatch(ids);
	}

	public void changeStatusBatch(List<String> ids, String status) {
		// TODO Auto-generated method stub
		userVehiclemapper.changeStatusBatch(ids, status);
	}

	public void updateUserVehicleInfo(wechatUserVehicle userVehicle) {
		if(StringUtils.isBlank(userVehicle.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"小程序openId不能为空！");
		}
		
		if(userVehicle.getUvId()==null){
			throw new BaseServiceException(StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"主键id不能为空！");
		}
		wechatUserVehicleCondition condition = new wechatUserVehicleCondition();
		//condition.setUserOpenId(userVehicle.getUserOpenId());
		condition.setUvPlate(userVehicle.getUvPlate());
		condition.setUvStatus(1);
		List<wechatUserVehicle> vehicleList=userVehiclemapper.findUserVehicleByConditions(condition);
		if(vehicleList.size()!=0){
			throw new BaseServiceException(
					StatusCode.DATA_IS_EXISTS.getCode(),
					"车牌号:"+userVehicle.getUvPlate()+"已存在，且正常使用中！");
		}else{
			userVehiclemapper.updateUserVehicleInfo(userVehicle);
		}
		
		
	}

	public void deleteVehicle(String userOpenId, String vehiclePlate) {
		// TODO Auto-generated method stub
		wechatUser wechatUser =wechatUsermapper.findWechatUserInfoByOpenId(userOpenId);
		userVehiclemapper.deleteVehicle(wechatUser.getUserId().toString(),vehiclePlate);
	}

}
