package com.WeChatApi.service.operationOrderService;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.WeChatApi.bean.condition.operationOrderCondition;
import com.WeChatApi.bean.models.operationOrder;
import com.WeChatApi.bean.models.parkinglots;
import com.WeChatApi.controller.base.BaseServiceException;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.dao.operationOrderMapper;
import com.WeChatApi.dao.parkinglotsMapper;



@Service
@Transactional
public class operationOrderService {
	
	@Autowired
	private operationOrderMapper operationOrdermapper;

	public List<operationOrder> findOrderInfo(operationOrderCondition condition) {
		if(StringUtils.isBlank(condition.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"openId不能为空！");
		}
		if(condition.getVeh_plate_color_txt()!=null){
			List<Map<String,String>> ids = new ArrayList<>();
			int veh_plates_count = condition.getVeh_plates().toString().split(",").length;
			if(condition.getVeh_plates().toString().split(",").length!=condition.getVeh_plate_color_txt().toString().split(",").length){
				throw new BaseServiceException(
						StatusCode.MISSING_OPENID_ERROR.getCode(),
						"车牌数和车牌颜色数不匹配，请核实！");
			}
			/*if(StringUtils.isNotBlank(condition.getVeh_plates())){
				for (String s : condition.getVeh_plates().toString().split(",")) {
					ids.add(s);
				}
				condition.setVehPlateLits(ids);
			}*/
			for(int i =0 ;i<veh_plates_count;i++){
				Map<String, String> map = new HashMap<String, String>();
				map.put("plate", condition.getVeh_plates().toString().split(",")[i].toString());
				map.put("colour", condition.getVeh_plate_color_txt().toString().split(",")[i].toString());
				ids.add(map);
			}
			if(ids.size()!=0){
				condition.setVehPlateMapLits(ids);
			}
			
			return operationOrdermapper.findOrderInfo(condition);
		}else{
			List<String> ids = new ArrayList<>();
			if(StringUtils.isNotBlank(condition.getVeh_plates())){
			for (String s : condition.getVeh_plates().toString().split(",")) {
				ids.add(s);
			}
			condition.setVehPlateLits(ids);
		}
			return operationOrdermapper.findOrderInfo(condition);
		}
	}

	public long findOrderInfoCount(operationOrderCondition condition) {
		List<String> ids = new ArrayList<String>();
		for (String s : condition.getVeh_plates().toString().split(",")) {
			ids.add(s);
		}
		condition.setVehPlateLits(ids);
		return operationOrdermapper.findOrderInfoCount(condition);
	}

	public operationOrder findOderInfoByOutTradeNo(String outTradeNo) {
		// TODO Auto-generated method stub
		String pklCode= outTradeNo.split("_")[0];
		String orderNum=outTradeNo.split("_")[2];
		return operationOrdermapper.findOderInfoByOutTradeNo(pklCode,orderNum);
	}

	public void updateOrderInfoByOutTradeNo(String couponFee,String payMethod, String outTradeNo) {
		// TODO Auto-generated method stub
		operationOrdermapper.updateOrderInfoByOutTradeNo(couponFee,payMethod,outTradeNo);
	}

	public void updateRoadOrderInfoByOutTradeNo(String couponFee, String payMethod, String outTradeNo) {
		// TODO Auto-generated method stub
		operationOrdermapper.updateRoadOrderInfoByOutTradeNo(couponFee,payMethod,outTradeNo);
	}

	public List<Map<String, Object>> findRoadOrderInfoByOrderId(String orderId) {
		// TODO Auto-generated method stub
		return operationOrdermapper.findRoadOrderInfoByOrderId(orderId);
	}

	public void deleteBatch(List<String> ids) {
      
		if(ids.size()==0){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"订单为空！");
		}

		operationOrdermapper.deleteBatch(ids);
	}

	

	
}
