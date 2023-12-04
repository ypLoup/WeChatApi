package com.WeChatApi.service.wechatUserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.util.encoders.Hex;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.WeChatApi.bean.condition.operationOrderCondition;
import com.WeChatApi.bean.condition.wechatUserCondition;
import com.WeChatApi.bean.dto.rechargeDto;
import com.WeChatApi.bean.dto.sendTempDto;
import com.WeChatApi.bean.dto.storeChargeDiscountDto;
import com.WeChatApi.bean.dto.userChargeDiscountDto;
import com.WeChatApi.bean.dto.userChargeRecordDto;
import com.WeChatApi.bean.dto.userRechargeDto;
import com.WeChatApi.bean.models.wechatUser;
import com.WeChatApi.bean.models.noVehiclePlate;
import com.WeChatApi.bean.models.operationOrder;
import com.WeChatApi.bean.models.userStore;
import com.WeChatApi.bean.models.wechatUserVehicle;
import com.WeChatApi.bean.models.zfbUserVehicle;
import com.WeChatApi.controller.base.Base64Util;
import com.WeChatApi.controller.base.BaseException;
import com.WeChatApi.controller.base.BaseServiceException;
import com.WeChatApi.controller.base.CommonUtil;
import com.WeChatApi.controller.base.MD5Tools;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.dao.businessMapper;
import com.WeChatApi.dao.operationOrderMapper;
import com.WeChatApi.dao.parkinglotsMapper;
import com.WeChatApi.dao.wechatUserMapper;
import com.WeChatApi.dao.wechatUserVehicleMapper;
import com.WeChatApi.dao.zfbUserVehicleMapper;
import com.WeChatApi.service.blueCardService.blueCardService;
import com.WeChatApi.service.zfbApiService.zfbApiService;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONArray;


@Service
@Transactional
public class wechatUserService {
	
	@Autowired
	private wechatUserMapper wechatUsermapper;
	
	@Autowired
	private wechatUserVehicleMapper userVehiclemapper;
	
	@Autowired
	private zfbUserVehicleMapper zfbUserVehiclemapper;
	
	@Autowired
	private operationOrderMapper operationOrdermapper;
	
	@Autowired
	private blueCardService bluecardservice;
	
	@Autowired
	private businessMapper  businessmapper;
	
	@Autowired
	private parkinglotsMapper parkinglotsmapper;
	
	@Autowired
	private zfbApiService alipayservice;
	
	@Autowired
	private static ResourceBundle res = ResourceBundle.getBundle("rccApi");
	
	private static String mchId= res.getString("wechat_mch_id");
	private static String AppSecret= res.getString("wechat_AppSecret");
	private static String Appid= res.getString("wechat_appid");
	private static String mchName= res.getString("wechat_mch_name");

	public List<wechatUser> findWechatUserInfoByConditions(wechatUserCondition condition) {
		//根据openId查找名下所属车辆信息
		if(StringUtils.isBlank(condition.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"微信小程序openId不能为空！");
		}
		List<wechatUserVehicle> userVehicleList=userVehiclemapper.findUserVehicleByOpenId(condition.getUserOpenId());
		
		return wechatUsermapper.findWechatUserInfoByConditions(condition);
	}

	public long findWechatUserInfoCountByConditions(wechatUserCondition condition) {
		
		return wechatUsermapper.findWechatUserInfoCountByConditions(condition);
	}

	public void addWechatUserInfo(wechatUser userinfo) {
		if(StringUtils.isBlank(userinfo.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"微信小程序openId不能为空！");
		}
		wechatUsermapper.addWechatUserInfo(userinfo);
	}

	
	/**
	 * 更新用户信息
	 * @param userinfo
	 */
	public void updateWechatUserInfo(wechatUser userinfo) {
		if(StringUtils.isBlank(userinfo.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"微信小程序openId不能为空！");
		}
		wechatUsermapper.updateWechatUserInfo(userinfo);
	}

	public void deleteBatch(List<String> ids) {
		
		
	}

	public void changeStatusBatch(List<String> ids, String status) {
		
		
	}

	public wechatUser findWechatUserInfoByOpenId(wechatUserCondition condition) {
		//根据openId查找名下所属车辆信息
		if(StringUtils.isBlank(condition.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"微信小程序openId不能为空！");
		}
		wechatUser wechatUser =wechatUsermapper.findWechatUserInfoByOpenId(condition.getUserOpenId());
		//List<wechatUserVehicle> userVehicleList=userVehiclemapper.findUserVehicleByOpenId(condition.getUserOpenId());
		List<String> vehPlates = userVehiclemapper.findVehPlatesByUserId(wechatUser.getUserId().toString());
		List<Map<String, String>> vehPlatesMap = userVehiclemapper.findVehPlatesEndingTimeByUserId(wechatUser.getUserId().toString());
		List<wechatUserVehicle> userVehicleList=userVehiclemapper.findUserVehicleByUserId(wechatUser.getUserId().toString(),vehPlates);
		List<wechatUserVehicle> newList= new  ArrayList<wechatUserVehicle>();
		
		for (int i = 0; i < userVehicleList.size(); i++) { 
		    for (int j = 0; j < userVehicleList.size(); j++) { 
		        if(i!=j&&userVehicleList.get(i).getUvPlate().equals(userVehicleList.get(j).getUvPlate())&&userVehicleList.get(i).getPlateColor().equals(userVehicleList.get(j).getPlateColor())) { 
		        	userVehicleList.remove(userVehicleList.get(j)); 
		        }
		     } 
		}

		if(userVehicleList.size()!=0){
			for(wechatUserVehicle a :userVehicleList){
				for(Map<String, String> map :vehPlatesMap){
					if(a.getUvPlate().equals(map.get("uv_plate").toString())&&a.getPlateColor().equals(map.get("plate_color").toString())){
						a.setSub_expire_time(map.get("sub_expire_time")+"");
					}
				}
				newList.add(a);
			}
			wechatUser.setUserVehicleList(newList);
		}
		
		List<userStore> userStoreList = businessmapper.findUserStoreByPhone(wechatUser.getUserMobile());
		if(userStoreList.size()>1){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"该用户绑定多个商户，请管理员核实信息！");
		}
		if(userStoreList.size()==1){
			
			wechatUser.setUserStoreList(userStoreList);
		}
		List<Map<String, Object>> payList = new ArrayList<>();
		Map<String, Object> payMap = new HashMap<>();
		
		payMap.put("name", "微信原生支付");
		payMap.put("type", "1");
		payMap.put("price", "0");
		payList.add(payMap);
		if(wechatUser.getBalances()>0){
			Map<String, Object> payMap2 = new HashMap<>();
			payMap2.put("name", "预充值支付");
			payMap2.put("type", "2");
			payMap2.put("price", wechatUser.getBalances());
			payList.add(payMap2);
		}
		if(wechatUser.getPoint()>0){
			Map<String, Object> payMap3 = new HashMap<>();
			payMap3.put("name", "平台余额支付");
			payMap3.put("type", "3");
			payMap3.put("price", wechatUser.getPoint());
			payList.add(payMap3);
		}
		wechatUser.setPayList(payList);
		return wechatUser;
	}
	
	public wechatUser findWechatUserInfoByOpenId_zfb(wechatUserCondition condition) {
		//根据openId查找名下所属车辆信息
		if(StringUtils.isBlank(condition.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"小程序openId不能为空！");
		}
		wechatUser wechatUser =wechatUsermapper.findWechatUserInfoByOpenId(condition.getUserOpenId());
		List<String> vehPlates = userVehiclemapper.findVehPlatesByUserId(wechatUser.getUserId().toString());
		List<wechatUserVehicle> userVehicleList=userVehiclemapper.findUserVehicleByUserId(wechatUser.getUserId().toString(),vehPlates);
		List<wechatUserVehicle> newList= new  ArrayList<wechatUserVehicle>();
		for (int i = 0; i < userVehicleList.size(); i++) { 
		    for (int j = 0; j < userVehicleList.size(); j++) { 
		        if(i!=j&&userVehicleList.get(i).getUvPlate().equals(userVehicleList.get(j).getUvPlate())) { 
		        	userVehicleList.remove(userVehicleList.get(j)); 
		        }
		     } 
		}
		if(userVehicleList.size()!=0){
			wechatUser.setUserVehicleList(userVehicleList);
		}
		
		
		List<userStore> userStoreList = businessmapper.findUserStoreByPhone(wechatUser.getUserMobile());
		if(userStoreList.size()>1){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"该用户绑定多个商户，请管理员核实信息！");
		}
		if(userStoreList.size()==1){
			
			wechatUser.setUserStoreList(userStoreList);
		}
		return wechatUser;
	}
	

	public Map<String, Object> VehiclePlateOut(noVehiclePlate noVehiclePlate, HttpServletRequest request) throws HttpException, IOException {
		Map<String, Object> vehcleMap = new HashMap<String, Object>();


		if(StringUtils.isBlank(noVehiclePlate.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"微信小程序openId不能为空！");
		}
		
		if(StringUtils.isBlank(noVehiclePlate.getVehiclePlate())){//车牌信息为空，无牌车获取扣款费用
			
			if(noVehiclePlate.getChannelCode()==null){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"停车场通道号不能为空！");
			}
			
			if(StringUtils.isBlank(noVehiclePlate.getParkingCode())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"停车场编号不能为空！");
			}
			
			/******************参数齐全，调用蓝卡开闸接口*******************/
			 operationOrderCondition condition = new operationOrderCondition();
			 condition.setVeh_auth_id(noVehiclePlate.getUserOpenId());
			 operationOrder order = operationOrdermapper.findOrderInfoLimitOne_(condition);
			 if(order==null){
				 throw new BaseServiceException(
							StatusCode.DATA_NOT_EXISTS.getCode(),
							"无牌车无订单信息，请核实！");
			 }
			 Map<String, Object>orderMap=new HashMap<String, Object>();
			 orderMap.put("veh_plate", order.getVeh_plate());
			 orderMap.put("order_entry_time", order.getOrder_entry_time()+"");
			 //orderMap.put("order_receivable", order.getOrder_receivable());
			 orderMap.put("out_trade_no", order.getOut_trade_no());
			 orderMap.put("veh_plate_color_txt", order.getVeh_plate_color_txt());
			 orderMap.put("car_type", order.getCar_type());
	         vehcleMap=bluecardservice.UnlicensedCarOut(noVehiclePlate,request);
	         System.out.println("无牌车费用金额++++++++++++++++++++++"+vehcleMap.get("price"));
	         orderMap.put("order_receivable", vehcleMap.get("price"));
	         vehcleMap.put("vehcleInfo", orderMap);
			 return vehcleMap;
			
		}else{//车牌信息不为空，根据牌车获取扣款费用
			if(StringUtils.isBlank(noVehiclePlate.getVehiclePlate())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"车牌号不能为空！");
			}
			/*if(StringUtils.isBlank(noVehiclePlate.getParkingCode())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"停车场编号不能为空！");
			}*/
			operationOrderCondition condition2 = new operationOrderCondition();
			condition2.setVeh_plate(noVehiclePlate.getVehiclePlate());
			condition2.setOrder_status(1);//1:嘉善；2秀洲
			condition2.setPkl_code(noVehiclePlate.getParkingCode());
			List<operationOrder> list =operationOrdermapper.findOrderInfoLimitOne(condition2);
			if(list.size()==0){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"车牌号【"+noVehiclePlate.getVehiclePlate()+"】不存在订单记录，请核实");
			}
			for(operationOrder a:list){
				noVehiclePlate.setParkingCode(a.getPkl_code());
			}
			
			 operationOrderCondition condition = new operationOrderCondition();
			 condition.setVeh_plate(noVehiclePlate.getVehiclePlate());
			 operationOrder order = operationOrdermapper.findOrderInfoLimitOne_(condition);
			 Map<String, Object>orderMap=new HashMap<String, Object>();
			 orderMap.put("veh_plate", order.getVeh_plate());
			 orderMap.put("order_entry_time", order.getOrder_entry_time()+"");
			 orderMap.put("out_trade_no", order.getOut_trade_no());
			 orderMap.put("veh_plate_color_txt", order.getVeh_plate_color_txt());
			 orderMap.put("car_type", order.getCar_type());
			 vehcleMap =bluecardservice.licensedCarOut(noVehiclePlate,request);
			 System.out.println("有牌车费用金额++++++++++++++++++++++"+vehcleMap.get("price"));
			 orderMap.put("order_receivable", vehcleMap.get("price"));
			 vehcleMap.put("vehcleInfo", orderMap);
			 return vehcleMap;
		}
		
		
		
	}
	
	
	
	public Map<String, Object> VehiclePlateOut_new(noVehiclePlate noVehiclePlate, HttpServletRequest request) throws HttpException, IOException {
		Map<String, Object> vehcleMap = new HashMap<String, Object>();


		if(StringUtils.isBlank(noVehiclePlate.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"微信小程序openId不能为空！");
		}
		
		if(StringUtils.isBlank(noVehiclePlate.getVehiclePlate())){//车牌信息为空，无牌车获取扣款费用
			
			if(noVehiclePlate.getChannelCode()==null){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"停车场通道号不能为空！");
			}
			
			if(StringUtils.isBlank(noVehiclePlate.getParkingCode())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"停车场编号不能为空！");
			}
			
			/*long parkingCount =parkinglotsmapper.findParkingLotsFreeCountByParkingCode(noVehiclePlate.getParkingCode());
			
			if(parkingCount>0){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"该停车场免费中，无需缴费！");
			}*/
			
			
			/******************参数齐全，调用蓝卡开闸接口*******************/
			
			vehcleMap=bluecardservice.UnlicensedCarOut_new(noVehiclePlate,request);
			
			
			 operationOrderCondition condition = new operationOrderCondition();
			 condition.setVeh_auth_id(noVehiclePlate.getUserOpenId());
			 operationOrder order = operationOrdermapper.findOrderInfoLimitOne_(condition);
			 if(order==null){
				 throw new BaseServiceException(
							StatusCode.DATA_NOT_EXISTS.getCode(),
							"无牌车无订单信息，请核实！");
			 }
			 String pkl_code = order.getPkl_code();
			 Map<String, Object>orderMap=new HashMap<String, Object>();
			 orderMap.put("order_id", order.getOrder_id());
			 orderMap.put("veh_plate", order.getVeh_plate());
			 orderMap.put("order_entry_time", order.getOrder_entry_time()+"");
			 //orderMap.put("order_receivable", order.getOrder_receivable());
			 orderMap.put("out_trade_no", order.getOut_trade_no());
			 orderMap.put("veh_plate_color_txt", order.getVeh_plate_color_txt());
			 orderMap.put("car_type", order.getCar_type());
	         List<Map<String, Object>>pkl_name = parkinglotsmapper.findParkinglotsInfoList(pkl_code);
	         System.out.println("无牌车费用金额++++++++++++++++++++++"+vehcleMap.get("price"));
	         orderMap.put("order_receivable", vehcleMap.get("price"));
	         orderMap.put("pkl_name", pkl_name.get(0).get("pkl_name").toString());
	         vehcleMap.put("vehcleInfo", orderMap);
	         System.out.println("vehcleMap++++++++++++++++++++++"+vehcleMap);
			 return vehcleMap;
			
		}else{//车牌信息不为空，根据牌车获取扣款费用
			if(StringUtils.isBlank(noVehiclePlate.getVehiclePlate())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"车牌号不能为空！");
			}
			/*if(StringUtils.isBlank(noVehiclePlate.getParkingCode())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"停车场编号不能为空！");
			}*/
			operationOrderCondition condition2 = new operationOrderCondition();
			condition2.setVeh_plate(noVehiclePlate.getVehiclePlate());
			condition2.setVeh_plate_color(noVehiclePlate.getVehPlateColour());
			condition2.setOrder_status(1);
			condition2.setPkl_code(noVehiclePlate.getParkingCode());
			List<operationOrder> list =operationOrdermapper.findOrderInfoLimitOne(condition2);
			if(list.size()==0){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"车牌号【"+noVehiclePlate.getVehiclePlate()+"】不存在订单记录，请核实");
			}
			for(operationOrder a:list){
				noVehiclePlate.setParkingCode(a.getPkl_code());
				long parkingCount =parkinglotsmapper.findParkingLotsFreeCountByParkingCode(a.getPkl_code());
				
				if(parkingCount>0){
					throw new BaseServiceException(
							StatusCode.MISSING_PARAMETER_ERROR.getCode(),
							"该停车场免费中，无需缴费！");
				}
			}
			
			
			vehcleMap =bluecardservice.licensedCarOut_new(noVehiclePlate,request);
			 System.out.println("有牌车费用金额++++++++++++++++++++++"+vehcleMap.get("price"));
			
			 operationOrderCondition condition = new operationOrderCondition();
			 condition.setVeh_plate(noVehiclePlate.getVehiclePlate());
			 operationOrder order = operationOrdermapper.findOrderInfoLimitOne_(condition);
			 Map<String, Object>orderMap=new HashMap<String, Object>();
			 orderMap.put("order_id", order.getOrder_id());
			 orderMap.put("veh_plate", order.getVeh_plate());
			 orderMap.put("order_entry_time", order.getOrder_entry_time()+"");
			 orderMap.put("out_trade_no", order.getOut_trade_no());
			 orderMap.put("veh_plate_color_txt", order.getVeh_plate_color_txt());
			 orderMap.put("car_type", order.getCar_type());
			 List<Map<String, Object>>pkl_name = parkinglotsmapper.findParkinglotsInfoList(order.getPkl_code());
			 orderMap.put("pkl_name", pkl_name.get(0).get("pkl_name").toString());
			 orderMap.put("order_receivable", vehcleMap.get("price"));
			 vehcleMap.put("vehcleInfo", orderMap);
			 System.out.println("vehcleMap++++++++++++++++++++++"+vehcleMap);
			 return vehcleMap;
		}
		
		
		
	}
	
	
	
	
	/*public Map<String, Object> VehiclePlateOut_new_xiuzhou(noVehiclePlate noVehiclePlate, HttpServletRequest request) throws HttpException, IOException {
		Map<String, Object> vehcleMap = new HashMap<String, Object>();


		if(StringUtils.isBlank(noVehiclePlate.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"微信小程序openId不能为空！");
		}
		
		if(StringUtils.isBlank(noVehiclePlate.getVehiclePlate())){//车牌信息为空，无牌车获取扣款费用
			
			if(noVehiclePlate.getChannelCode()==null){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"停车场通道号不能为空！");
			}
			
			if(StringUtils.isBlank(noVehiclePlate.getParkingCode())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"停车场编号不能为空！");
			}
			
			*//******************参数齐全，调用蓝卡开闸接口*******************//*
			
			vehcleMap=bluecardservice.UnlicensedCarOut_new(noVehiclePlate,request);
			
			
			 operationOrderCondition condition = new operationOrderCondition();
			 condition.setVeh_auth_id(noVehiclePlate.getUserOpenId());
			 operationOrder order = operationOrdermapper.findOrderInfoLimitOne_(condition);
			 if(order==null){
				 throw new BaseServiceException(
							StatusCode.DATA_NOT_EXISTS.getCode(),
							"无牌车无订单信息，请核实！");
			 }
			 Map<String, Object>orderMap=new HashMap<String, Object>();
			 orderMap.put("order_id", order.getOrder_id());
			 orderMap.put("veh_plate", order.getVeh_plate());
			 orderMap.put("order_entry_time", order.getOrder_entry_time()+"");
			 //orderMap.put("order_receivable", order.getOrder_receivable());
			 orderMap.put("out_trade_no", order.getOut_trade_no());
			 orderMap.put("veh_plate_color_txt", order.getVeh_plate_color_txt());
			 orderMap.put("car_type", order.getCar_type());
	         
	         System.out.println("无牌车费用金额++++++++++++++++++++++"+vehcleMap.get("price"));
	         orderMap.put("order_receivable", vehcleMap.get("price"));
	         vehcleMap.put("vehcleInfo", orderMap);
	         System.out.println("vehcleMap++++++++++++++++++++++"+vehcleMap);
			 return vehcleMap;
			
		}else{//车牌信息不为空，根据牌车获取扣款费用
			if(StringUtils.isBlank(noVehiclePlate.getVehiclePlate())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"车牌号不能为空！");
			}
			if(StringUtils.isBlank(noVehiclePlate.getParkingCode())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"停车场编号不能为空！");
			}
			operationOrderCondition condition2 = new operationOrderCondition();
			condition2.setVeh_plate(noVehiclePlate.getVehiclePlate());
			condition2.setOrder_status(2);
			condition2.setPkl_code(noVehiclePlate.getParkingCode());
			List<operationOrder> list =operationOrdermapper.findOrderInfoLimitOne(condition2);
			if(list.size()==0){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"车牌号【"+noVehiclePlate.getVehiclePlate()+"】不存在订单记录，请核实");
			}
			for(operationOrder a:list){
				noVehiclePlate.setParkingCode(a.getPkl_code());
			}
			
			
			vehcleMap =bluecardservice.licensedCarOut_new_xiuzhou(noVehiclePlate,request);
			if(vehcleMap==null){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"车牌号【"+noVehiclePlate.getVehiclePlate()+"】不存在订单记录，请核实");
			}
			 System.out.println("有牌车费用金额++++++++++++++++++++++"+vehcleMap.get("price"));
			
			 operationOrderCondition condition = new operationOrderCondition();
			 condition.setVeh_plate(noVehiclePlate.getVehiclePlate());
			 operationOrder order = operationOrdermapper.findOrderInfoLimitOne_(condition);
			 Map<String, Object>orderMap=new HashMap<String, Object>();
			 orderMap.put("order_id", vehcleMap.get("order_id"));
			 orderMap.put("veh_plate", vehcleMap.get("veh_plate"));
			 orderMap.put("order_entry_time", vehcleMap.get("order_entry_time"));
			 orderMap.put("out_trade_no", vehcleMap.get("out_trade_no"));
			 orderMap.put("veh_plate_color_txt", vehcleMap.get("veh_plate_color_txt"));
			 orderMap.put("car_type", vehcleMap.get("car_type"));
			 
			 orderMap.put("order_receivable", vehcleMap.get("price"));
			 vehcleMap.put("vehcleInfo", orderMap);
			 System.out.println("vehcleMap++++++++++++++++++++++"+vehcleMap);
			 return vehcleMap;
		}
		
		
		
	}*/
		
		
		public void VehiclePlateIn(noVehiclePlate noVehiclePlate) throws HttpException, IOException {
			
				if(StringUtils.isBlank(noVehiclePlate.getUserOpenId())){
					throw new BaseServiceException(
							StatusCode.MISSING_OPENID_ERROR.getCode(),
							"小程序openId不能为空！");
				}
			
				if(noVehiclePlate.getChannelCode()==null){
					throw new BaseServiceException(
							StatusCode.MISSING_PARAMETER_ERROR.getCode(),
							"停车场通道号不能为空！");
				}
				
				if(StringUtils.isBlank(noVehiclePlate.getParkingCode())){
					throw new BaseServiceException(
							StatusCode.MISSING_PARAMETER_ERROR.getCode(),
							"停车场编号不能为空！");
				}
				
				/******************参数齐全，调用蓝卡开闸接口*******************/
		         
		         
				bluecardservice.UnlicensedCarIn(noVehiclePlate);
				
		
		
		
		
	}

		public Map<String, Object> topUpByUser(userRechargeDto userRecharge, HttpServletRequest request) {
			
			
			if(StringUtils.isBlank(userRecharge.getUserOpenId())){
				throw new BaseServiceException(
						StatusCode.MISSING_OPENID_ERROR.getCode(),
						"小程序openId不能为空！");
			}
			
			wechatUser user2 = wechatUsermapper.findWechatUserInfoByOpenId(userRecharge.getUserOpenId());
			
			if(StringUtils.isBlank(user2.getUserMobile())){
				throw new BaseServiceException(
						StatusCode.MISSING_OPENID_ERROR.getCode(),
						"小程序用户未绑定手机号，无法充值！");
			}
			
			
			
			BigDecimal amount =new BigDecimal(userRecharge.getAmount());
			
			if(amount.compareTo(BigDecimal.ZERO)==0){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"预充值金额不能为0！");
			}

			if(userRecharge.getType()==null){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"充值渠道不能为空");
			}
			
			if(userRecharge.getType()==1){//for wechat
				
				userChargeRecordDto userchargerecord = new userChargeRecordDto();
				wechatUser user = wechatUsermapper.findWechatUserInfoByOpenId(userRecharge.getUserOpenId());
				userRecharge.setUserId(user.getUserId());
				if(user==null){
					throw new BaseException(
							StatusCode.DATA_NOT_MATCH.getCode(),
							"无该用户信息，请核实！");
				}
				
				Map<String, Object> map = new HashMap<String, Object>();

				if(userRecharge.getUcdId()!=null){//查看是否存在折扣
					userChargeDiscountDto discount=wechatUsermapper.findUserDiscountByScdId(userRecharge.getUcdId());
					if(discount!=null){
						if(discount.getUcd_discount()!=null&&discount.getUcd_discount()!=0){
							float amout= userRecharge.getAmount()*discount.getUcd_discount()/100;
							userRecharge.setRealAmout(amout*100);
							userRecharge.setDiscountAmount((userRecharge.getAmount()-amout)*100);
							userchargerecord.setR_user_id(user.getUserId());
							userchargerecord.setR_type(1);
							userchargerecord.setR_balances((int) (amout*100));
							DecimalFormat decimalFormat=new DecimalFormat(".00");
							float a=userRecharge.getAmount()-amout;
							String p=decimalFormat.format(a);
					        float parseFloat = Float.parseFloat(p);
							userchargerecord.setR_point((int)(parseFloat*100));
							userchargerecord.setR_discount_id(userRecharge.getUcdId());
						}else if(discount.getUcd_condition_amount()!=null&&discount.getUcd_condition_amount()!=0&&discount.getUcd_condition_amount()<=userRecharge.getAmount()){//满送折扣，符合满送
							int a =(int)userRecharge.getAmount()*100/discount.getUcd_condition_amount();
							int rechargePoint=discount.getUcd_present_amount()*a;
							userchargerecord.setR_user_id(user.getUserId());
							userchargerecord.setR_type(1);
							userchargerecord.setR_balances((int)userRecharge.getAmount()*100);
							userchargerecord.setR_point(rechargePoint);
							userchargerecord.setR_discount_id(userRecharge.getUcdId());
							float amout= userRecharge.getAmount();
							userRecharge.setRealAmout(amout*100);
							userRecharge.setDiscountAmount(0);
						}else{//不符合满送
							float amout= userRecharge.getAmount();
							userRecharge.setRealAmout(amout*100);
							userRecharge.setDiscountAmount(0);
							userchargerecord.setR_user_id(user.getUserId());
							userchargerecord.setR_type(1);
							userchargerecord.setR_balances((int)userRecharge.getAmount()*100);
							userchargerecord.setR_point(0);
							userchargerecord.setR_discount_id(userRecharge.getUcdId());
						}
					}else{
						float amout= userRecharge.getAmount();
						userRecharge.setRealAmout(amout*100);
						userRecharge.setDiscountAmount(0);
						userchargerecord.setR_user_id(user.getUserId());
						userchargerecord.setR_type(1);
						userchargerecord.setR_balances((int)userRecharge.getAmount()*100);
						userchargerecord.setR_point(0);
						userchargerecord.setR_discount_id(userRecharge.getUcdId());
					}
					
					
					
				}
				Map<String, Object>businessMap=new HashMap<String, Object>();
				int recordId=businessmapper.insertChargeRecord(userchargerecord);
				map =this.getChargePrepayId(userRecharge,userchargerecord.getR_id(),request);
				businessMap.put("rechargeAmout", map.get("price"));
				DecimalFormat decimalFormat=new DecimalFormat(".00");
		        String p=decimalFormat.format(userRecharge.getDiscountAmount()/100);
		        float parseFloat = Float.parseFloat(p);
				businessMap.put("disCountAmount",parseFloat);
				map.put("rechargeInfo", businessMap);
				return map;
			} else if(userRecharge.getType()==2) { // for alipay
				Map<String, Object> map = new HashMap<String, Object>();
				userChargeRecordDto userchargerecord = new userChargeRecordDto();
				
				
				if(StringUtils.isBlank(userRecharge.getUserOpenId())){
					throw new BaseServiceException(
							StatusCode.MISSING_OPENID_ERROR.getCode(),
							"小程序openId不能为空！");
				}
				
				wechatUser user = wechatUsermapper.findWechatUserInfoByOpenId(userRecharge.getUserOpenId());
				userRecharge.setUserId(user.getUserId());
				if(user==null){
					throw new BaseException(
							StatusCode.DATA_NOT_MATCH.getCode(),
							"无该用户信息，请核实！");
				}

				if(userRecharge.getUcdId()!=null){//查看是否存在折扣
					userChargeDiscountDto discount=wechatUsermapper.findUserDiscountByScdId(userRecharge.getUcdId());
					if(discount!=null){
						if(discount.getUcd_discount()!=null&&discount.getUcd_discount()!=0){
							float amout= userRecharge.getAmount()*discount.getUcd_discount()/100;
							userRecharge.setRealAmout(amout*100);
							userRecharge.setDiscountAmount((userRecharge.getAmount()-amout)*100);
							userchargerecord.setR_user_id(user.getUserId());
							userchargerecord.setR_type(1);
							userchargerecord.setR_balances((int)amout*100);
							DecimalFormat decimalFormat=new DecimalFormat(".00");
							float a=userRecharge.getAmount()-amout;
							String p=decimalFormat.format(a);
					        float parseFloat = Float.parseFloat(p);
							userchargerecord.setR_point((int)(parseFloat*100));
							//userchargerecord.setR_point((int)((userRecharge.getAmount()-amout)*100));
							userchargerecord.setR_discount_id(userRecharge.getUcdId());
						}else if(discount.getUcd_condition_amount()!=null&&discount.getUcd_condition_amount()!=0&&discount.getUcd_condition_amount()<=userRecharge.getAmount()){//满送折扣，符合满送
							int a =(int)userRecharge.getAmount()*100/discount.getUcd_condition_amount();
							int rechargePoint=discount.getUcd_present_amount()*a;
							userchargerecord.setR_user_id(user.getUserId());
							userchargerecord.setR_type(1);
							userchargerecord.setR_balances((int)userRecharge.getAmount()*100);
							userchargerecord.setR_point(rechargePoint);
							userchargerecord.setR_discount_id(userRecharge.getUcdId());
							float amout= userRecharge.getAmount();
							userRecharge.setRealAmout(amout*100);
							userRecharge.setDiscountAmount(0);
						}else{//不符合满送
							float amout= userRecharge.getAmount();
							userRecharge.setRealAmout(amout*100);
							userRecharge.setDiscountAmount(0);
							userchargerecord.setR_user_id(user.getUserId());
							userchargerecord.setR_type(1);
							userchargerecord.setR_balances((int)userRecharge.getAmount()*100);
							userchargerecord.setR_point(0);
							userchargerecord.setR_discount_id(userRecharge.getUcdId());
						}
					}else{
						float amout= userRecharge.getAmount();
						userRecharge.setRealAmout(amout*100);
						userRecharge.setDiscountAmount(0);
						userchargerecord.setR_user_id(user.getUserId());
						userchargerecord.setR_type(1);
						userchargerecord.setR_balances((int)userRecharge.getAmount()*100);
						userchargerecord.setR_point(0);
						userchargerecord.setR_discount_id(userRecharge.getUcdId());
					}
					
					
					
				}
				
				if(userRecharge.getRealAmout()==0){
					throw new BaseException(
							StatusCode.DATA_NOT_MATCH.getCode(),
							"充值实付金额不能为0或者不能为空！");
				}
				
				
				Map<String, Object>businessMap=new HashMap<String, Object>();
				int recordId=businessmapper.insertChargeRecord(userchargerecord);
				//businessmapper.inserStoreChargeRecord(recharge.getStoreId(),4,recharge.getScdId(),userchargerecord.getR_balances(),userchargerecord.getR_point());
				String aliPaykey =alipayservice.getChargePrepayIdByAliPay_userToUp(userRecharge,userchargerecord.getR_id(),request);
				DecimalFormat decimalFormat=new DecimalFormat(".00");
		        String rechargeAmout=decimalFormat.format(userRecharge.getRealAmout()/100);
		        String disCountAmount=decimalFormat.format(userRecharge.getDiscountAmount()/100);
		        //float parseFloat = Float.parseFloat(rechargeAmout);
		        //float parseFloat = Float.parseFloat(disCountAmount);
				businessMap.put("rechargeAmout", Float.parseFloat(rechargeAmout));
				businessMap.put("disCountAmount", Float.parseFloat(disCountAmount));
				businessMap.put("alipayReturn", aliPaykey);
				//map.put("rechargeInfo", businessMap);
				return businessMap;
				
				
			}
			return null;
			
			
			
		}

	

	

	
		@Transactional
		public Map<String, Object> getChargePrepayId(userRechargeDto recharge, Integer recordId, HttpServletRequest request)throws BaseException {


			try{
	            //公众号的appid
	            //String appid = "wx606d7fcc6d1402c9";
	           
	            String openid = recharge.getUserOpenId();

	            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
	            /** 当前时间 yyyyMMddHHmmss */
	            String currTime = CommonUtil.getCurrTime();
	            /** 8位日期 */
	            String strTime = currTime.substring(8, currTime.length());
	            /** 四位随机数 */
	            String strRandom = CommonUtil.buildRandom(4) + "";
	            
	            String outTradeNo=currTime+strRandom;

	            /** 公众号APPID */
	            parameters.put("appid", Appid);
	            parameters.put("body", "用户id"+recharge.getUserId()+"预充值");
	            /** 商户号 */
	            //String mch_id = "1604629952";
	            parameters.put("mch_id", mchId);
	            /** 随机字符串 */
	            parameters.put("nonce_str", CommonUtil.getNonceStr());
	            parameters.put("notify_url", "https://jiashan.iparking.tech/mp/WeChatApi/wechatUser/wechatPayCallback");
	            parameters.put("out_trade_no", outTradeNo);
	            parameters.put("openid", openid);
	            parameters.put("spbill_create_ip", request.getRemoteAddr());
	            parameters.put("total_fee", (int)recharge.getRealAmout());
	            parameters.put("trade_type", "JSAPI");
	            parameters.put("attach", recharge.getUcdId()+"_"+recharge.getUserId()+"_"+recordId);//折扣类型_商户id_充值记录表Id
	            
	            /** MD5进行签名，必须为UTF-8编码，注意上面几个参数名称的大小写 */
	            String api_key = "jgkj2020jgkj2020jgkj2020jgkj2020";
	            String sign = CommonUtil.createSign("UTF-8", parameters,api_key);
	            String requestJsonStr = new ObjectMapper().writeValueAsString(parameters);
	            //logger.info("发送的信息是"+requestJsonStr);
	            parameters.put("sign", sign);//
	            /** 生成xml结构的数据，用于统一下单接口的请求 */
	            String requestXML = CommonUtil.getRequestXml(parameters);
	            /**
	             * 读取证书
	             * 
	             */
	            CloseableHttpClient httpclient = null;
	            Map<String,String> result = new HashMap<String,String>();
	           try {
	                KeyStore keyStore = KeyStore.getInstance("PKCS12");
	                String pathname = "/tomcat/apiclient_cert.p12";//这里填你们的证书的地址，我这里放在linux服务器的/usr下面
	                //String pathname="D:\\home\\apiclient_cert.p12";
	                FileInputStream instream = new FileInputStream(new File(pathname)); //此处为证书所放的绝对路径
	                try {
	                    keyStore.load(instream, mchId.toCharArray());
	                } finally {
	                    instream.close();
	                }
	                // Trust own CA and all self-signed certs
	                SSLContext sslcontext = SSLContexts.custom()
	                        .loadKeyMaterial(keyStore, mchId.toCharArray())
	                        .build();
	                // Allow TLSv1 protocol only
	                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
	                        sslcontext,
	                        new String[]{"TLSv1"},
	                        null,
	                        SSLConnectionSocketFactory.getDefaultHostnameVerifier());//SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER
	                httpclient = HttpClients.custom()
	                        .setSSLSocketFactory(sslsf)
	                        .build();
	            }
	            catch (Exception e){
	            	//log.info("读取证书信息的时候发生异常异常信息是："+e.getMessage());
	                e.printStackTrace();
	            }
	            try {
	                String requestUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	                HttpPost httpPost = new HttpPost(requestUrl);
	                StringEntity reqEntity  = new StringEntity(requestXML, "utf-8");
	                // 设置类型
	                reqEntity.setContentType("application/x-www-form-urlencoded");
	                httpPost.setEntity(reqEntity);
	                //log.info("executing request" + httpPost.getRequestLine());
	                CloseableHttpResponse response = httpclient.execute(httpPost);
	                try {
	                    HttpEntity entity = response.getEntity();
	                    System.out.println(response.getStatusLine());
	                    if (entity != null) {
	                        // 从request中取得输入流
	                        InputStream inputStream = entity.getContent();
	                        // 读取输入流
	                        SAXReader reader = new SAXReader();
	                        Document document = reader.read(inputStream);
	                        // 得到xml根元素
	                        Element root = document.getRootElement();
	                        // 得到根元素的所有子节点
	                        List<Element> elementList = root.elements();
	                        // 遍历所有子节点
	                        for (Element e : elementList)
	                        {
	                            result.put(e.getName(), e.getText());
	                        }
	                        // 释放资源
	                        inputStream.close();
	                    }
	                    EntityUtils.consume(entity);
	                }
	                finally {
	                    if(response!=null) {
	                        response.close();
	                    }
	                }
	            }
	            catch (Exception e){
	                e.printStackTrace();
	            }finally {
	                try {
	                    httpclient.close();
	                }
	                catch (Exception e){
	                    e.printStackTrace();
	                }
	            }

	            if(result.get("return_code").equals("SUCCESS")) {
	            	Map<String, Object> map =CommonUtil.generateSignature(result);
	            	wechatUsermapper.insertApiLogs("统一下单接口——用户预充值","用户userOpneId："+recharge.getUserOpenId()+"折扣类型："+recharge.getUcdId()+"openid:"+openid+";订单号："+outTradeNo+";金额："+recharge.getAmount()+";实付："+recharge.getRealAmout(), "success", map.toString());
	            	map.put("price", recharge.getAmount());
	                return map;
	                }
	            else {
	            	//log.info("获取PrepayId="+openid+",发送时间是："+CommonUtil.getPreDay(new Date(),0));
	            	wechatUsermapper.insertApiLogs("统一下单接口——用户预充值", "用户userOpneId："+recharge.getUserOpenId()+"折扣类型："+recharge.getUcdId()+"openid:"+openid+";订单号："+outTradeNo+";金额："+recharge.getAmount()+";实付："+recharge.getRealAmout(), "fail", result.toString());
	            	throw new BaseException(
	    					StatusCode.SYSTEM_ERROR.getCode(),
	    					"用户预充值接口获取失败，请重新联系管理员！");
	            }
	        }
	        catch (Exception e){
	        	throw new BaseException(
						StatusCode.SYSTEM_ERROR.getCode(),
						"用户预充值接口获取失败，请重新联系管理员！");
	        }
			
		}

		public wechatUser findUserInfoByUserId(String userId) {
			// TODO Auto-generated method stub
			return wechatUsermapper.findUserInfoByUserId(userId);
		}

		public void updateWechatUserInfoMoney(int newBalances, int newPoint, String storeId) {
			// TODO Auto-generated method stub
			wechatUsermapper.updateWechatUserInfoMoney(newBalances,newPoint,storeId);
		}

		public List<Map<String, Object>> findUserDiscountByCondition(Map<String, Object> paramMap) {
			String userOpenId=paramMap.get("userOpenId").toString();
			if(StringUtils.isBlank(userOpenId)){
				throw new BaseServiceException(
						StatusCode.MISSING_OPENID_ERROR.getCode(),
						"小程序openId不能为空！");
			}
	
	     return wechatUsermapper.findUserDiscountByCondition();
		}

		public Map<String, Object> getCarOutByUserBalance(noVehiclePlate noVehiclePlate, HttpServletRequest request) throws HttpException, IOException {
			Map<String, Object> vehcleMap = new HashMap<String, Object>();


			if(StringUtils.isBlank(noVehiclePlate.getUserOpenId())){
				throw new BaseServiceException(
						StatusCode.MISSING_OPENID_ERROR.getCode(),
						"微信小程序openId不能为空！");
			}
			
			if(StringUtils.isBlank(noVehiclePlate.getVehiclePlate())){//车牌信息为空，无牌车获取扣款费用
				
				if(noVehiclePlate.getChannelCode()==null){
					throw new BaseServiceException(
							StatusCode.MISSING_PARAMETER_ERROR.getCode(),
							"停车场通道号不能为空！");
				}
				
				if(StringUtils.isBlank(noVehiclePlate.getParkingCode())){
					throw new BaseServiceException(
							StatusCode.MISSING_PARAMETER_ERROR.getCode(),
							"停车场编号不能为空！");
				}
				
				/******************参数齐全，调用蓝卡开闸接口*******************/
				 operationOrderCondition condition = new operationOrderCondition();
				 condition.setVeh_auth_id(noVehiclePlate.getUserOpenId());
				 operationOrder order = operationOrdermapper.findOrderInfoLimitOne_(condition);
				 if(order==null){
					 throw new BaseServiceException(
								StatusCode.DATA_NOT_EXISTS.getCode(),
								"无牌车无订单信息，请核实！");
				 }
				 Map<String, Object>orderMap=new HashMap<String, Object>();
				 orderMap.put("veh_plate", order.getVeh_plate());
				 orderMap.put("order_entry_time", order.getOrder_entry_time()+"");
				 //orderMap.put("order_receivable", order.getOrder_receivable());
				 orderMap.put("out_trade_no", order.getOut_trade_no());
				 orderMap.put("veh_plate_color_txt", order.getVeh_plate_color_txt());
				 orderMap.put("car_type", order.getCar_type());
		         //vehcleMap=bluecardservice.UnlicensedCarOut(noVehiclePlate,request);
		         int amount =bluecardservice.getUnlicensedCarOutMoney(noVehiclePlate,request);
		         System.out.println("无牌车费用金额++++++++++++++++++++++"+amount);
		         orderMap.put("order_receivable", amount);
		         vehcleMap.put("vehcleInfo", orderMap);
				 return vehcleMap;
				
			}else{//车牌信息不为空，根据牌车获取扣款费用
				if(StringUtils.isBlank(noVehiclePlate.getVehiclePlate())){
					throw new BaseServiceException(
							StatusCode.MISSING_PARAMETER_ERROR.getCode(),
							"车牌号不能为空！");
				}
				/*if(StringUtils.isBlank(noVehiclePlate.getParkingCode())){
					throw new BaseServiceException(
							StatusCode.MISSING_PARAMETER_ERROR.getCode(),
							"停车场编号不能为空！");
				}*/
				operationOrderCondition condition2 = new operationOrderCondition();
				condition2.setVeh_plate(noVehiclePlate.getVehiclePlate());
				condition2.setOrder_status(1);//1:嘉善；2秀洲
				condition2.setPkl_code(noVehiclePlate.getParkingCode());
				List<operationOrder> list =operationOrdermapper.findOrderInfoLimitOne(condition2);
				if(list.size()==0){
					throw new BaseServiceException(
							StatusCode.MISSING_PARAMETER_ERROR.getCode(),
							"车牌号【"+noVehiclePlate.getVehiclePlate()+"】不存在订单记录，请核实");
				}
				for(operationOrder a:list){
					noVehiclePlate.setParkingCode(a.getPkl_code());
				}
				
				 operationOrderCondition condition = new operationOrderCondition();
				 condition.setVeh_plate(noVehiclePlate.getVehiclePlate());
				 operationOrder order = operationOrdermapper.findOrderInfoLimitOne_(condition);
				 Map<String, Object>orderMap=new HashMap<String, Object>();
				 orderMap.put("veh_plate", order.getVeh_plate());
				 orderMap.put("order_entry_time", order.getOrder_entry_time()+"");
				 orderMap.put("out_trade_no", order.getOut_trade_no());
				 orderMap.put("veh_plate_color_txt", order.getVeh_plate_color_txt());
				 orderMap.put("car_type", order.getCar_type());
				 //vehcleMap =bluecardservice.licensedCarOut(noVehiclePlate,request);
				 int amount =bluecardservice.getlicensedCarOutMoney(noVehiclePlate,request);
				 System.out.println("有牌车费用金额++++++++++++++++++++++"+amount);
				 orderMap.put("order_receivable", amount);
				 vehcleMap.put("vehcleInfo", orderMap);
				 return vehcleMap;
			}
			
			
		}

		public Map<String, Object> findUserBalance(wechatUserCondition condition) {
			Map<String, Object> map =new HashMap<String, Object>();
			if(StringUtils.isBlank(condition.getUserOpenId())){
				throw new BaseServiceException(
						StatusCode.MISSING_OPENID_ERROR.getCode(),
						"微信小程序openId不能为空！");
			}
			//List<wechatUserVehicle> userVehicleList=userVehiclemapper.findUserVehicleByOpenId(condition.getUserOpenId());
			wechatUser wechatUser =wechatUsermapper.findWechatUserInfoByOpenId(condition.getUserOpenId());
			map.put("userOpenId", wechatUser.getUserOpenId());
			map.put("balance", wechatUser.getBalances());
			map.put("point", wechatUser.getPoint());
			return map;
		}

		public String getVerificationCode(String mobileNum,String userOpenId) throws Exception {
			
			if(StringUtils.isBlank(userOpenId)){
				throw new BaseServiceException(
						StatusCode.MISSING_OPENID_ERROR.getCode(),
						"小程序openId不能为空！");
			}
			
			sendTempDto sendTemp = new sendTempDto();
			int random=(int)((Math.random()*9+1)*100000);
			String [] paramss={String.valueOf(random)};
			sendTemp.setApId("jszhtc");
			sendTemp.setEcName("嘉善县城市管理服务有限公司");
			sendTemp.setSecretKey("12345Qwe@");
			sendTemp.setParams(new ObjectMapper().writeValueAsString(paramss));
			sendTemp.setMobiles(mobileNum);
			sendTemp.setAddSerial("");
			sendTemp.setSign("xd9RnzziE");
			sendTemp.setTemplateId("e48af5ec4b544a588411df38f3c74970");
			StringBuffer stringBuffer = new StringBuffer();
	        stringBuffer.append(sendTemp.getEcName())
	                .append(sendTemp.getApId())
	                .append(sendTemp.getSecretKey())
	                .append(sendTemp.getTemplateId())
	                .append(sendTemp.getMobiles())
	                .append(sendTemp.getParams())
	                .append(sendTemp.getSign())
	                .append(sendTemp.getAddSerial());
	        sendTemp.setMac(MD5Tools.MD5(stringBuffer.toString()).toLowerCase());
	        String jsonText = new ObjectMapper().writeValueAsString(sendTemp);

	        String body = Base64.getEncoder().encodeToString(jsonText.getBytes("UTF-8"));
	        
            String result=sendParamsToMas(body,String.valueOf(random),mobileNum);
			return result;
		}

		/*private String sendParamsToMas(String body,String code,String mobileNum) throws HttpException, IOException {
			PostMethod postMethod = null;
			try{
		    
		    postMethod = new PostMethod("http://112.35.1.155:1992/sms/tmpsubmit") ;

		    RequestEntity se = new StringRequestEntity (body ,"application/json" ,"UTF-8");

		    postMethod.setRequestEntity(se);
		    postMethod.setRequestHeader("Content-Type","application/json");

	   
	        HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true) );  
		    int response = client.executeMethod(postMethod); // POST
		    
		    if(response==200){
		       net.sf.json.JSONObject  dataJson = net.sf.json.JSONObject.fromObject(postMethod.getResponseBodyAsString());
	           String errorCode = dataJson.getString("rspcod");
	           if(errorCode.equals("success")){
	        	   wechatUsermapper.insertVerificationCodeTemp(mobileNum,code);
	        	   return "发送成功！";
	           }else{
	        	   throw new BaseServiceException(
							StatusCode.API_FREQUENTLY_ERROR.getCode(),
							"短信发送失败，原因："+dataJson.getString("rspcod"));
	           }
	          
		    }else{
		    	throw new BaseServiceException(
						StatusCode.API_FREQUENTLY_ERROR.getCode(),
						"请查看短信发送接口，联系管理员"+response);
		    }
			}finally {
				postMethod.releaseConnection();
			}
		}*/
		
		
		
		
		private String sendParamsToMas(String body,String code,String mobileNum) throws HttpException, IOException {
			PostMethod postMethod = null;
			try{
		    postMethod = new PostMethod("http://10.40.61.202:81/MiniProgram/sendSMS") ;

	        NameValuePair[] Senddata = {
		            new NameValuePair("app_id","a00a9b24866c5b32bb9ca4231fd0c47c54966fa7"),
		            new NameValuePair("app_key","164edf151624b0c7173f1747279ee13f812e4807"),
		            new NameValuePair("template_id","266641"),
		            new NameValuePair("mobile",mobileNum),
		            new NameValuePair("message",code)
		            
		    };
	        postMethod.setRequestBody(Senddata);
	        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;
	        //log.info("调用sendParamsToMas"+"开始++++++++++++++++++++++++++++++++++++");
	        HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true) );  
		    int response = client.executeMethod(postMethod); // POST
		    
		    if(response==200){
		    	net.sf.json.JSONObject  dataJson = net.sf.json.JSONObject.fromObject(postMethod.getResponseBodyAsString());
	            String errorCode = dataJson.getString("error_code");
	            if(errorCode.equals("0")){
	            	wechatUsermapper.insertVerificationCodeTemp(mobileNum,code);
		        	   return "发送成功！";
	            }else{
	            	throw new BaseServiceException(
							StatusCode.API_FREQUENTLY_ERROR.getCode(),
							"短信发送失败，原因："+dataJson.getString("error_msg"));
	            }
	           
	            
		    }else{
		    	throw new BaseServiceException(
						StatusCode.API_FREQUENTLY_ERROR.getCode(),
						"请查看短信发送接口，联系管理员"+response);
		    }
			}finally {
				postMethod.releaseConnection();
			}
		}

		public void bindMobileNum(Map<String, Object> bindMobileNumMap) {
			
			String userOpenId=bindMobileNumMap.get("userOpenId").toString();
			
			String code=bindMobileNumMap.get("code").toString();
			
			String mobileNum=bindMobileNumMap.get("mobileNum").toString();
			
			if(StringUtils.isBlank(userOpenId)){
				throw new BaseServiceException(
						StatusCode.MISSING_OPENID_ERROR.getCode(),
						"小程序openId不能为空！");
			}
			
			wechatUser wechatUser =wechatUsermapper.findWechatUserInfoByOpenId(userOpenId);//新插入用户
			if(StringUtils.isBlank(code)){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"验证码不能为空！");
					}
			
			if(StringUtils.isBlank(mobileNum)){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"手机号不能为空！");
			}
			
			//wechatUser haveUser= wechatUsermapper.findWechatUserInfoCountByPhone(mobileNum, userOpenId);
			
			wechatUser haveUser= wechatUsermapper.findUserInfoByPhoneNum(mobileNum);
			
			if(haveUser!=null&&StringUtils.isNotBlank(haveUser.getUserMobile())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"用户已绑定，请勿重复绑定！");
			}
			
			wechatUser userPhone=wechatUsermapper.findUserInfoByPhoneNum(mobileNum);//老用户
			long ishave =wechatUsermapper.findVerificationCode(mobileNum,code);
			if(userPhone==null){//没有老用户
				if(ishave!=0){
					wechatUser  userinfo = new wechatUser();
					userinfo.setUserMobile(mobileNum);
					if(StringUtils.isNotBlank(wechatUser.getUserOpenId())){
						userinfo.setUserOpenId(wechatUser.getUserOpenId());
					}
					if(StringUtils.isNotBlank(wechatUser.getUserOpenIdZfb())){
						userinfo.setUserOpenIdZfb(wechatUser.getUserOpenIdZfb());
					}
					userinfo.setUserId(wechatUser.getUserId());
					wechatUsermapper.updateWechatUserInfo(userinfo);
				}else{
					throw new BaseServiceException(
							StatusCode.MISSING_PARAMETER_ERROR.getCode(),
							"验证码已过期失效！");
				}
			}else{
				if(ishave!=0){
					wechatUser  userinfo = new wechatUser();
					//userinfo.setUserMobile(mobileNum);
					if(StringUtils.isBlank(userPhone.getUserOpenId())){
						userinfo.setUserOpenId(userOpenId);
					}
					if(StringUtils.isBlank(userPhone.getUserOpenIdZfb())){
						userinfo.setUserOpenIdZfb(userOpenId);
					}
					userinfo.setUserId(userPhone.getUserId());
					wechatUsermapper.updateWechatUserInfo(userinfo);
					List<String>ids=new ArrayList<>();
					ids.add(wechatUser.getUserId().toString());
					wechatUsermapper.deleteBatch(ids);
				}else{
					throw new BaseServiceException(
							StatusCode.MISSING_PARAMETER_ERROR.getCode(),
							"验证码已过期失效！");
				}
				
			}
			
			
		}

		public String uploadFile(MultipartFile UploadFile) throws IllegalStateException, IOException {
		String PluploadFileName = UploadFile.getOriginalFilename();
        // 设置上传文件目录
		ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
	    String UploadPath = res.getString("uploadFile_url");
        //String UploadPath = "/home/jgkj/Projects/platform_6900/app/image/register_face_image/";
        String dataDir=endFileDir()+"/";
        File _UploadFile = new File(UploadPath+dataDir, PluploadFileName);
        if (!_UploadFile.isDirectory()) _UploadFile.mkdirs();
        UploadFile.transferTo(_UploadFile); 
        return UploadPath+dataDir+PluploadFileName;
	}
		
		public static String endFileDir () {  
	        Date date = new Date(System. currentTimeMillis());  
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd" );  
	        String str = sdf.format(date).toString();  
	        return str;  
	  }

		public void vehPlateVerify(Map<String, Object> vehPlateMap) {
			
			String userOpenId=vehPlateMap.get("userOpenId").toString();
			String verifyContent=vehPlateMap.get("verifyContent").toString();
			String verifyImageUrl=vehPlateMap.get("verifyImageUrl").toString();
			String verifyImageUrl2=vehPlateMap.get("verifyImageUrl2").toString();
			String mobileNum=vehPlateMap.get("mobileNum").toString();
			String plateColor=vehPlateMap.get("plateColor").toString();
			String plate=vehPlateMap.get("plate").toString();
			if(StringUtils.isBlank(mobileNum)){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"手机号不能为空！");
			}
			if(StringUtils.isBlank(userOpenId)){
				throw new BaseServiceException(
						StatusCode.MISSING_OPENID_ERROR.getCode(),
						"小程序openid不能为空！");
			}
			if(StringUtils.isBlank(verifyImageUrl)){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"图片地址不能为空");
			}
			wechatUser wechatUser =wechatUsermapper.findWechatUserInfoByOpenId(userOpenId);
			wechatUsermapper.inserVehPlateVerify(wechatUser.getUserId().toString(),plate,plateColor,mobileNum,verifyImageUrl,verifyImageUrl2,verifyContent);
		}

		public void updateWechatUserInfoMoney_Pkl(int newBalances, int newPoint, String userId) {
			// TODO Auto-generated method stub
			wechatUsermapper.updateWechatUserInfoMoney_Pkl(newBalances,newPoint,userId);
		}

		public Map<String, Object> findNotifycation() {
			// TODO Auto-generated method stub
			return wechatUsermapper.findNotifycation();
		}

	


}
