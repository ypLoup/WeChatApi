package com.WeChatApi.controller.zfbApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.WeChatApi.bean.condition.userInfoCondition;
import com.WeChatApi.bean.condition.wechatUserCondition;
import com.WeChatApi.bean.dto.prePayDto;
import com.WeChatApi.bean.models.noVehiclePlate;
import com.WeChatApi.bean.models.operationOrder;
import com.WeChatApi.bean.models.redPack;
import com.WeChatApi.bean.models.userInfo;
import com.WeChatApi.bean.models.wechatUser;
import com.WeChatApi.controller.base.BaseController;
import com.WeChatApi.controller.base.BaseException;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.service.blueCardService.blueCardService;
import com.WeChatApi.service.operationOrderService.operationOrderService;
import com.WeChatApi.service.userInfoService.userInfoService;

import java.io.PrintWriter;

import com.WeChatApi.service.wechatApiService.wechatApiService;
import com.WeChatApi.service.wechatUserService.wechatUserService;
import com.WeChatApi.service.zfbApiService.zfbApiService;

import com.alipay.api.internal.util.AlipaySignature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sun.misc.BASE64Encoder;

@RequestMapping("/zfbApi")
@Controller
public class zfbApiController extends BaseController {
	
	@Autowired
	private zfbApiService zfbapiservice;
	
	@Autowired
	private  blueCardService bluecardservice;
	
	@Autowired
	private wechatApiService wechatApiservice;
	
	@Autowired
	private operationOrderService operationOrderservice;
	
	
	
	
/*	@RequestMapping(value = "/zfbUserLogin", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap zfbUserLogin(@Param("authCode")String  authCode) {
		
		try {
			Map<String, Object> openId=zfbapiservice.zfbUserLogin(authCode);
			return backJsonResult(openId);
		}catch(BaseException e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}*/
	
	
	@RequestMapping(value = "/zfbUserLogin", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap zfbUserLogin(@RequestBody Map<String, String> map) throws JsonProcessingException {
		logger.info("apiName:"+"/zfbApi/zfbUserLogin"+"param:"+new ObjectMapper().writeValueAsString(map));
		try {
			Map<String, Object> openId=zfbapiservice.zfbUserLogin_new(map);
			return backJsonResult(openId);
		}catch(BaseException e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	/**
	 * 有/无牌车出场
	 * @param userinfo
	 * @return
	 * @throws JsonProcessingException 
	 */
	@RequestMapping(value = "/VehiclePlateOut", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap VehiclePlateOut(@RequestBody noVehiclePlate noVehiclePlate,HttpServletRequest request) throws JsonProcessingException {
		logger.info("apiName:"+"/zfbApi/VehiclePlateOut"+"param:"+new ObjectMapper().writeValueAsString(noVehiclePlate));
		try {
			if (true) {
				Map<String, Object>map =new HashMap<String, Object>();
				map =zfbapiservice.VehiclePlateOut(noVehiclePlate,request);
				/*map.put("paysin", "sign");
	            //返回随机串(这个随机串是新创建的)
				map.put("nonceStr", "nonceStr");
	            //返回时间戳
				map.put("timeStamp", "timeStamp");
	            //返回数据包
				map.put("package", "prepay_id=prepayId");*/
				return backJsonResult(map,StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
			}
			return backJsonFailureMsg(StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					StatusCode.PARAMETER_FORMATE_RROR.getErrorMsg());
		}catch(BaseException e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	
	/**
	 * 有/无牌车出场
	 * @param userinfo
	 * @return
	 * @throws JsonProcessingException 
	 */
	@RequestMapping(value = "/VehiclePlateOut_new", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap VehiclePlateOut_new(@RequestBody noVehiclePlate noVehiclePlate,HttpServletRequest request) throws JsonProcessingException {
		logger.info("apiName:"+"/zfbApi/VehiclePlateOut_new"+"param:"+new ObjectMapper().writeValueAsString(noVehiclePlate));
		try {
			if (true) {
				Map<String, Object>map =new HashMap<String, Object>();
				map =zfbapiservice.VehiclePlateOut_new(noVehiclePlate,request);
				/*map.put("paysin", "sign");
	            //返回随机串(这个随机串是新创建的)
				map.put("nonceStr", "nonceStr");
	            //返回时间戳
				map.put("timeStamp", "timeStamp");
	            //返回数据包
				map.put("package", "prepay_id=prepayId");*/
				return backJsonResult(map,StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
			}
			return backJsonFailureMsg(StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					StatusCode.PARAMETER_FORMATE_RROR.getErrorMsg());
		}catch(BaseException e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	/**
	 * 无牌车进入
	 * @param userinfo
	 * @return
	 * @throws JsonProcessingException 
	 */
	@RequestMapping(value = "/VehiclePlateIn", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap VehiclePlateIn(@RequestBody noVehiclePlate noVehiclePlate) throws JsonProcessingException {
		logger.info("apiName:"+"/zfbApi/VehiclePlateIn"+"param:"+new ObjectMapper().writeValueAsString(noVehiclePlate));
		try {
			if (true) {
				zfbapiservice.VehiclePlateIn(noVehiclePlate);
				return backJsonSuccessMsg(StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
			}
			return backJsonFailureMsg(StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					StatusCode.PARAMETER_FORMATE_RROR.getErrorMsg());
		}catch(BaseException e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	
	@RequestMapping(value = "/notifyUrl", method = RequestMethod.POST)
	@ResponseBody
	public String notifyUrl(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 //获取支付宝发送过来的信息
		logger.info("支付宝回调内容信息："+request.getParameterMap());
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        //循环获取到所有的值
        for(String str:requestParams.keySet()) {
            String name =str;
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                      : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        logger.info("支付宝内容信息："+params);
        //调用SDK验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(params, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqliWU4ndPe3KkRlQe85h2SDNb2l9UbPeaNmZZaced7Oc6T896WyZzB6+42gMQOOrRPWZui7ZCGmT8wzjTwFGXb9Zb/qmzXmV6nnQpz6NXCOJBL9VyKLm/EYjA0A5mwDwBD75trMXydcGWOdcQCuPi9HrH2YSJ9QUK2KzdCKQ3XMahhdtyjQcFxMXooVhPvnGi+4LgmsOJZU45cfZLnItHCPWDqRVrsoOjiNHHGVdUOxdKTor0suIE1W/IMfP0ITmMe0H1L+lItd+FBjDGfP2sd8R8phgrNi6b11Jg7ZXXCzsC9tkXn84lyKbWknybISYU9Sy1Lr9e/XgF9uX1jU76wIDAQAB", "UTF-8", "RSA2");
        //boolean类型signVerified为true时 则验证成功
        if(signVerified) {
            //获取到支付的状态 TRADE_SUCCESS则支付成功
            String trade_status =request.getParameter("trade_status");
            String couponRecordId=request.getParameter("passback_params");
            String orderIds[]=couponRecordId.split("_");
             if (trade_status.equals("TRADE_SUCCESS")){
            	 operationOrder order= operationOrderservice.findOderInfoByOutTradeNo(request.getParameter("out_trade_no"));
            	 bluecardservice.callBackNotify_zfb(order.getOrder_id(),request.getParameter("out_trade_no"),request.getParameter("trade_no"),request.getParameter("gmt_payment"),request.getParameter("total_amount"),"1",orderIds[0].toString(),orderIds[1].toString());
                 wechatApiservice.insertApiLogs("支付宝支付回调接口", "用户id"+request.getParameter("buyer_id")+"订单号："+request.getParameter("out_trade_no")+";流水号："+request.getParameter("trade_no")+";金额："+request.getParameter("total_amount")+"优惠券id："+request.getParameter("passback_params"), "success", "");
            	 return "success";
             }else {
            	 wechatApiservice.insertApiLogs("支付宝支付回调接口", "用户id"+request.getParameter("buyer_id")+"订单号："+request.getParameter("out_trade_no")+";流水号："+request.getParameter("trade_no")+";金额："+request.getParameter("total_amount")+"优惠券id："+request.getParameter("passback_params"), "fail", params.toString());
            	 return "failure";
             }
        }
        //签名验证失败
        else {
        	logger.info("支付宝签名验证失败："+AlipaySignature.getSignCheckContentV1(params));
             
        	return "failure";
        }

	}
	
	
	
	
	/**
	 * 有/无牌车出场
	 * @param userinfo
	 * @return
	 */
	@RequestMapping(value = "/VehiclePlateOut_test", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap VehiclePlateOut_test(@RequestBody noVehiclePlate noVehiclePlate,HttpServletRequest request) {
		try {
			if (true) {
				Map<String, Object>map =new HashMap<String, Object>();
				prePayDto prePaydto=new prePayDto("2088302409978811", "20201229001", 1);
				map.put("rende", zfbapiservice.getPrepayId_zfb(prePaydto, request));
				//zfbapiservice.getPrepayId_zfb(prePaydto, request);
				return backJsonResult(map,StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
			}
			return backJsonFailureMsg(StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					StatusCode.PARAMETER_FORMATE_RROR.getErrorMsg());
		}catch(BaseException e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	
	/*
	@RequestMapping(value = "/cashOut_zfb", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
    public ModelMap cashOut_zfb(@RequestBody Map<String,Object> map ,HttpServletRequest request) {
		
		try {
			zfbapiservice.cashOut_zfb(map);
			return backJsonSuccessMsg(StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
		}catch(BaseException e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}*/
	
	
	
	
	
	
	
	
	

	

	
	

}
