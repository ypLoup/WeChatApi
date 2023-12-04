package com.WeChatApi.service.wechatUserVehicleService;

import java.util.List;
import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.WeChatApi.bean.condition.wechatUserCondition;
import com.WeChatApi.bean.condition.wechatUserVehicleCondition;
import com.WeChatApi.bean.models.wechatUser;
import com.WeChatApi.bean.models.wechatUserVehicle;
import com.WeChatApi.controller.base.BaseServiceException;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.dao.wechatUserMapper;
import com.WeChatApi.dao.wechatUserVehicleMapper;


@Service
@Transactional
public class wechatUserVehicleService {
	
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
		return userVehiclemapper.findUserVehicleByConditions(condition);
	}

	public long findUserVehicleCountByConditions(wechatUserVehicleCondition condition) {
		
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
		wechatUserVehicleCondition condition = new wechatUserVehicleCondition();
		//condition.setUserOpenId(userVehicle.getUserOpenId());
		condition.setUvPlate(userVehicle.getUvPlate());
		condition.setUvStatus(1);
		condition.setPlateColor(userVehicle.getPlateColor());
		List<wechatUserVehicle> vehicleList=userVehiclemapper.findUserVehicleByConditions(condition);
		if(vehicleList.size()!=0){
			throw new BaseServiceException(
					StatusCode.DATA_IS_EXISTS.getCode(),
					"车牌号:"+userVehicle.getUvPlate()+"已存在，且正常使用中！");
		}else{
			wechatUserVehicleCondition condition2 = new wechatUserVehicleCondition();
			condition2.setUserOpenId(userVehicle.getUserOpenId());
			condition2.setUvPlate(userVehicle.getUvPlate());
			condition2.setPlateColor(userVehicle.getPlateColor());
			wechatUserVehicle a =userVehiclemapper.findUserVehicleBeanByCondition(condition2);
			if(a!=null&&a.getUvStatus()==0){
				userVehicle.setUvId(a.getUvId());
				userVehicle.setUvStatus(1);
				userVehiclemapper.updateUserVehicleInfo(userVehicle);
			}else{
				wechatUserVehicleCondition condition3 = new wechatUserVehicleCondition();
				condition3.setUvStatus(1);
				condition3.setUserId(wechatUser.getUserId());
				List<wechatUserVehicle> vehicleList2=userVehiclemapper.findUserVehicleByConditions(condition3);
                if(vehicleList2.size()>10){
                	throw new BaseServiceException(
        					StatusCode.DATA_IS_EXISTS.getCode(),
        					"车牌绑定已到上限，请勿绑定！");
                }
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
		userVehiclemapper.updateUserVehicleInfo(userVehicle);
		
	}

	public void deleteVehicle(String userOpenId, String vehiclePlate) {
		// TODO Auto-generated method stub
		wechatUser wechatUser =wechatUsermapper.findWechatUserInfoByOpenId(userOpenId);
		userVehiclemapper.deleteVehicle(wechatUser.getUserId().toString(),vehiclePlate);
	}

}
