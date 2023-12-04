package com.WeChatApi.controller.wechatUser;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.WeChatApi.bean.condition.wechatUserCondition;
import com.WeChatApi.bean.dto.userChargeRecordDto;
import com.WeChatApi.bean.dto.userRechargeDto;
import com.WeChatApi.bean.dto.userRechargeRecordDto;
import com.WeChatApi.bean.models.wechatUser;
import com.WeChatApi.bean.models.noVehiclePlate;
import com.WeChatApi.controller.base.BaseController;
import com.WeChatApi.controller.base.BaseException;
import com.WeChatApi.controller.base.BaseServiceException;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.service.businessService.businessService;
import com.WeChatApi.service.wechatApiService.wechatApiService;
import com.WeChatApi.service.wechatUserService.wechatUserService;
import com.alipay.api.internal.util.AlipaySignature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONObject;
import sun.misc.BASE64Encoder;

@RequestMapping("/wechatUser")
@Controller
public class wechatUserController extends BaseController {
	
	@Autowired
	private wechatUserService wechatUserservice;
	
	@Autowired
	private businessService businessservice;
	
	@Autowired
	private wechatApiService wechatApiservice;
	
	
	
	@RequestMapping(value = "/findWechatUserInfo", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findWechatUserInfoByConditions(@RequestBody wechatUserCondition condition) throws JsonProcessingException {
		logger.info("apiName:"+"/wechatUser/findWechatUserInfo"+"; param:"+new ObjectMapper().writeValueAsString(condition));
		List<wechatUser> list = null;
		try {
			list = wechatUserservice.findWechatUserInfoByConditions(condition);
			long total=wechatUserservice.findWechatUserInfoCountByConditions(condition);
			return backJsonPageResult(list,total, StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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
	
	
	
	@RequestMapping(value = "/findWechatUserInfoByOpenId", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findWechatUserInfoByOpenId(@RequestBody wechatUserCondition condition) throws JsonProcessingException {
		logger.info("apiName:"+"/wechatUser/findWechatUserInfoByOpenId"+"; param:"+new ObjectMapper().writeValueAsString(condition));
		wechatUser list = null;
		try {
			list = wechatUserservice.findWechatUserInfoByOpenId(condition);
			//long total=wechatUserservice.findWechatUserInfoCountByConditions(condition);
			return backJsonResult(list, StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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
	
	@RequestMapping(value = "/findWechatUserInfoByOpenId_zfb", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findWechatUserInfoByOpenId_zfb(@RequestBody wechatUserCondition condition) throws JsonProcessingException {
		logger.info("apiName:"+"/wechatUser/findWechatUserInfoByOpenId_zfb"+"; param:"+new ObjectMapper().writeValueAsString(condition));
		wechatUser list = null;
		try {
			list = wechatUserservice.findWechatUserInfoByOpenId_zfb(condition);
			//long total=wechatUserservice.findWechatUserInfoCountByConditions(condition);
			return backJsonResult(list, StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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
	
	

	

	@RequestMapping(value = "/addWechatUserInfo", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap addWechatUserInfo(@RequestBody wechatUser userinfo) throws JsonProcessingException {
		logger.info("apiName:"+"/wechatUser/addWechatUserInfo"+"; param:"+new ObjectMapper().writeValueAsString(userinfo));
		try {
			if (true) {
				wechatUserservice.addWechatUserInfo(userinfo);
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
	
	/**
	 * 无牌车进入
	 * @param userinfo
	 * @return
	 * @throws JsonProcessingException 
	 */
	@RequestMapping(value = "/VehiclePlateIn", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap VehiclePlateIn(@RequestBody noVehiclePlate noVehiclePlate) throws JsonProcessingException {
		logger.info("apiName:"+"/wechatUser/VehiclePlateIn"+"; param:"+new ObjectMapper().writeValueAsString(noVehiclePlate));
		try {
			if (true) {
				wechatUserservice.VehiclePlateIn(noVehiclePlate);
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
	
	
	
	/**
	 * 有/无牌车出场
	 * @param userinfo
	 * @return
	 * @throws JsonProcessingException 
	 */
	@RequestMapping(value = "/VehiclePlateOut", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap VehiclePlateOut(@RequestBody noVehiclePlate noVehiclePlate,HttpServletRequest request) throws JsonProcessingException {
		logger.info("apiName:"+"/wechatUser/VehiclePlateOut"+"; param:"+new ObjectMapper().writeValueAsString(noVehiclePlate));
		try {
			if (true) {
				Map<String, Object>map =new HashMap<String, Object>();
				map =wechatUserservice.VehiclePlateOut(noVehiclePlate,request);
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
	 * 有/无牌车出场(嘉善)
	 * @param userinfo
	 * @return
	 * @throws JsonProcessingException 
	 */
	@RequestMapping(value = "/VehiclePlateOut_new", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap VehiclePlateOut_new(@RequestBody noVehiclePlate noVehiclePlate,HttpServletRequest request) throws JsonProcessingException {
		logger.info("apiName:"+"/wechatUser/VehiclePlateOut_new"+"; param:"+new ObjectMapper().writeValueAsString(noVehiclePlate));
		try {
			if (true) {
				Map<String, Object>map2 =new HashMap<String, Object>();
				List<Map<String, Object>>aa=new ArrayList<>();
				Map<String, Object>map =new HashMap<String, Object>();
				map =wechatUserservice.VehiclePlateOut_new(noVehiclePlate,request);
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
	
	
	

	@RequestMapping(value = "/updateWechatUserInfo", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap updateWechatUserInfo(@RequestBody wechatUser userinfo) throws JsonProcessingException {
		logger.info("apiName:"+"/wechatUser/updateWechatUserInfo"+"; param:"+new ObjectMapper().writeValueAsString(userinfo));
		try {
			if (true) {
				wechatUserservice.updateWechatUserInfo(userinfo);
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

	@RequestMapping(value = "/deleteBatch", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ModelMap deleteBatch(@RequestParam Map<String, Object> map) {
		try {
			List<String> ids = new ArrayList<String>();
			for (String s : map.get("ids").toString().split(",")) {
				ids.add(s);
			}
			wechatUserservice.deleteBatch(ids);
			return this.backJsonSuccessMsg(StatusCode.SUCESSWECHAT.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	@RequestMapping(value = "/changeStatusBatch", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ModelMap changeStatusBatch(@RequestParam Map<String, Object> map,String status) {
		try {
			List<String> ids = new ArrayList<String>();
			for (String s : map.get("ids").toString().split(",")) {
				ids.add(s);
			}
			wechatUserservice.changeStatusBatch(ids,status);
			return this.backJsonSuccessMsg(StatusCode.SUCESSWECHAT.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	@RequestMapping(value = "/topUpByUser", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ModelMap topUpByUser(@RequestBody userRechargeDto userRecharge,HttpServletRequest request) throws JsonProcessingException {
		logger.info("apiName:"+"/wechatUser/topUpByUser"+"; param:"+new ObjectMapper().writeValueAsString(userRecharge));
		Map<String, Object> map = new HashMap<String,Object>(); 
		try {
			map=wechatUserservice.topUpByUser(userRecharge,request);
			return backJsonResult(map,StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
			//return backJsonResult(map,901, "预充值功能暂未开通！");
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
	 * 用户预支付微信支付回调接口
	 */
	@RequestMapping(value = "/wechatPayCallback", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	@Transactional
    public void payCallback(HttpServletRequest request,HttpServletResponse response) {
        String inputLine = "";
        String notityXml = "";
        try {
            while((inputLine = request.getReader().readLine()) != null){
                notityXml += inputLine;
            }
            //关闭流
            request.getReader().close();
            logger.info("+++++++++++++++++++++++++用户预充值微信回调开始+++++++++++++++++++++++++++++");
            logger.info("微信回调内容信息："+notityXml);
            //解析成Map
            Map<String,String> map =wechatApiservice.doXMLParse(notityXml);
            JSONObject jsonObject = JSONObject.fromObject(map);
            String scdId_storeId_recordId=map.get("attach");
            String[] aa=scdId_storeId_recordId.split("_");
            String scdId=aa[0];
            String userId=aa[1];
            String recordId=aa[2];
            //判断 支付是否成功
            //String scdId_storeId="";
            wechatUser userInfo= wechatUserservice.findUserInfoByUserId(userId);
            if("SUCCESS".equals(map.get("result_code"))){
                String outTradeNo = map.get("out_trade_no");
                long num =businessservice.findUserRechargeRecordCountByOutTradeNo(outTradeNo);
                if(num!=0){
                	StringBuffer buffer = new StringBuffer();
                    buffer.append("<xml>");
                    buffer.append("<return_code>"+map.get("result_code")+"</return_code>");
                    buffer.append("<return_msg>OK</return_msg>");
                    buffer.append("</xml>");

                    //给微信服务器返回 成功标示 否则会一直询问 咱们服务器 是否回调成功
                    PrintWriter writer = response.getWriter();
                    //返回
                    writer.print(buffer.toString());
                }else{
                	String transactionId = map.get("transaction_id");
                    String timeEnd = map.get("time_end");
                    String couponFee = map.get("total_fee");
                   
                    userChargeRecordDto userChargeRecord=businessservice.findChargeRecordByRid(recordId);
                    int newBalances=userInfo.getBalances()+userChargeRecord.getR_balances();
                    int newPoint=userInfo.getPoint()+userChargeRecord.getR_point();
                    //businessservice.addUserStoreBalances(scdId_storeId_recordId);
                    businessservice.updateChargeRecordStatus(recordId,1);//用户充值记录表
                    businessservice.updateChargeRecordRLeft(newBalances,newPoint,recordId);
                    wechatUserservice.updateWechatUserInfoMoney(newBalances,newPoint,userId);;
                    //businessservice.inserStoreChargeRecord(Integer.valueOf(storeId),4,Integer.valueOf(scdId),userChargeRecord.getR_balances(),userChargeRecord.getR_point());//商户充值记录表
                    businessservice.insertUserChargePayLog(jsonObject.toString(),recordId,"1");
                    wechatApiservice.insertApiLogs("用户预支付微信支付回调接口", "订单号："+outTradeNo+";流水号："+transactionId+";金额："+couponFee+"折扣_商户："+scdId_storeId_recordId, "success", "");
                    userRechargeRecordDto record = new userRechargeRecordDto();
                    record.setUserChargeRId(Integer.valueOf(recordId));
                    record.setUserId(Integer.valueOf(userId));
                    record.setOutTradeNo(outTradeNo);
                    record.setBalances(couponFee);
                    record.setStatus(2);
                    businessservice.insertUserRechargeRecordByDto(record);
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("<xml>");
                    buffer.append("<return_code>"+map.get("result_code")+"</return_code>");
                    buffer.append("<return_msg>OK</return_msg>");
                    buffer.append("</xml>");

                    //给微信服务器返回 成功标示 否则会一直询问 咱们服务器 是否回调成功
                    PrintWriter writer = response.getWriter();
                    //返回
                    writer.print(buffer.toString());
                }
                
            }else{
            	//logger.info("微信回调返回商户订单号："+map.get("out_trade_no")+"/"+map.get("err_code")+"/"+map.get("err_code_des"));
            	 
            	 businessservice.updateChargeRecordStatus(recordId,3);
            	 businessservice.insertUserChargePayLog(jsonObject.toString(),recordId,"2");
            	 wechatApiservice.insertApiLogs("用户预支付微信支付回调接口", "订单号："+map.get("out_trade_no")+";流水号："+map.get("transaction_id")+";金额："+map.get("coupon_fee")+"折扣_商户："+map.get("attach"), "fail", map.get("err_code")+"/"+map.get("err_code_des"));
            	 
            	StringBuffer buffer = new StringBuffer();
                buffer.append("<xml>");
                buffer.append("<return_code>"+map.get("result_code")+"</return_code>");
                buffer.append("<return_msg>OK</return_msg>");
                buffer.append("</xml>");

                //给微信服务器返回 成功标示 否则会一直询问 咱们服务器 是否回调成功
                PrintWriter writer = response.getWriter();
                //返回
                writer.print(buffer.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	
	@RequestMapping(value = "/aliPayCallback", method = RequestMethod.POST)
	@ResponseBody
	@Transactional
	public String notifyUrl(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 //获取支付宝发送过来的信息
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
        logger.info("支付宝商户内容信息："+params);
        JSONObject jsonObject = JSONObject.fromObject(params);
        //调用SDK验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(params, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqliWU4ndPe3KkRlQe85h2SDNb2l9UbPeaNmZZaced7Oc6T896WyZzB6+42gMQOOrRPWZui7ZCGmT8wzjTwFGXb9Zb/qmzXmV6nnQpz6NXCOJBL9VyKLm/EYjA0A5mwDwBD75trMXydcGWOdcQCuPi9HrH2YSJ9QUK2KzdCKQ3XMahhdtyjQcFxMXooVhPvnGi+4LgmsOJZU45cfZLnItHCPWDqRVrsoOjiNHHGVdUOxdKTor0suIE1W/IMfP0ITmMe0H1L+lItd+FBjDGfP2sd8R8phgrNi6b11Jg7ZXXCzsC9tkXn84lyKbWknybISYU9Sy1Lr9e/XgF9uX1jU76wIDAQAB", "UTF-8", "RSA2");
        //boolean类型signVerified为true时 则验证成功
        if(signVerified) {
            //获取到支付的状态 TRADE_SUCCESS则支付成功
            String trade_status =request.getParameter("trade_status");
            int couponFee=0;
            String scdId_storeId=request.getParameter("passback_params");
          String[] aa=scdId_storeId.split("_");
          String scdId=aa[0];
          String storeId=aa[1];
          String recordId=aa[2];
          wechatUser userInfo= wechatUserservice.findUserInfoByUserId(storeId);
             if (trade_status.equals("TRADE_SUCCESS")){
            	 couponFee=Integer.valueOf(request.getParameter("total_amount"))*100;
            	 userChargeRecordDto userChargeRecord=businessservice.findChargeRecordByRid(recordId);
            	 
                 int newBalances=userInfo.getBalances()+userChargeRecord.getR_balances();
                 int newPoint=userInfo.getPoint()+userChargeRecord.getR_point();
            	 businessservice.updateChargeRecordStatus(recordId,1);
            	 businessservice.updateChargeRecordRLeft(newBalances,newPoint,recordId);
            	 wechatUserservice.updateWechatUserInfoMoney(newBalances,newPoint,storeId);;
                 
                 businessservice.insertUserChargePayLog(jsonObject.toString(),recordId,"1");
            	 businessservice.insertChargePayLog(jsonObject.toString(),recordId,"1");
            	 wechatApiservice.insertApiLogs("支付宝商户支付回调接口_用户预充值", "用户id"+request.getParameter("buyer_id")+"订单号："+request.getParameter("out_trade_no")+";流水号："+request.getParameter("trade_no")+";金额："+request.getParameter("total_amount"), "success", "");
            	 return "success";
             }else {
            	 businessservice.updateChargeRecordStatus(recordId,3);
            	 businessservice.insertUserChargePayLog(jsonObject.toString(),recordId,"2");
            	 wechatApiservice.insertApiLogs("支付宝商户支付回调接口_用户预充值", "用户id"+request.getParameter("buyer_id")+"订单号："+request.getParameter("out_trade_no")+";流水号："+request.getParameter("trade_no")+";金额："+request.getParameter("total_amount"), "fail", params.toString());
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
	 * 查询折扣列表
	 * @param dto
	 * @return
	 * @throws JsonProcessingException 
	 */
	@RequestMapping(value = "/findUserDiscountByCondition", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findUserDiscountByCondition(@RequestBody Map<String, Object> paramMap) throws JsonProcessingException {
		logger.info("apiName:"+"/wechatUser/findUserDiscountByCondition"+"; param:"+new ObjectMapper().writeValueAsString(paramMap));
		List<Map<String, Object>> list = null;
		try {
			list = wechatUserservice.findUserDiscountByCondition(paramMap);
			return backJsonResult(list, StatusCode.SUCESSLAYUI.getCode(), StatusCode.SUCESS.getErrorMsg());
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
	@RequestMapping(value = "/getCarOutByUserBalance", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap carOutByUserBalance(@RequestBody noVehiclePlate noVehiclePlate,HttpServletRequest request) throws JsonProcessingException {
		logger.info("apiName:"+"/wechatUser/getCarOutByUserBalance"+"; param:"+new ObjectMapper().writeValueAsString(noVehiclePlate));
		try {
			if (true) {
				Map<String, Object>map =new HashMap<String, Object>();
				map =wechatUserservice.getCarOutByUserBalance(noVehiclePlate,request);
				
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
	
	@RequestMapping(value = "/findUserBalance", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findUserBalance(@RequestBody wechatUserCondition condition) throws JsonProcessingException {
		logger.info("apiName:"+"/wechatUser/findUserBalance"+"; param:"+new ObjectMapper().writeValueAsString(condition));
		Map<String,Object> map =new HashMap<>() ;
		try {
			map=wechatUserservice.findUserBalance(condition);
			return backJsonResult(map,StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	/**
	 * 获取验证码
	 * @param mobileNum
	 * @param userOpenId
	 * @return
	 */
	@RequestMapping(value = "/getVerificationCode", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap getVerificationCode(String mobileNum,String userOpenId) {
		logger.info("apiName:"+"/wechatUser/getVerificationCode"+"; param:"+mobileNum+";"+userOpenId);
		String result="";
		try {
			result=wechatUserservice.getVerificationCode(mobileNum,userOpenId);
			return backJsonResult(result,StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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
	 * 用户绑定成商户
	 * @param loginMap
	 * @return
	 * @throws JsonProcessingException 
	 */
	@RequestMapping(value = "/bindMobileNum", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap userBind(@RequestBody Map<String, Object> bindMobileNumMap) throws JsonProcessingException {
		logger.info("apiName:"+"/wechatUser/bindMobileNum"+"; param:"+new ObjectMapper().writeValueAsString(bindMobileNumMap));
		try {
			wechatUserservice.bindMobileNum(bindMobileNumMap);
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
	}
	
	/**
	 * 文件上传
	 * @param file
	 * @return
	 */
	@RequestMapping(value = "/uploadFile", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ModelMap uploadFile(@RequestParam("file") MultipartFile[] file) {
		List<String>urlList= new ArrayList<>();
		try {
			for(int i=0;i<file.length;i++){
				Boolean a=verificationPicFile(file[i]);
				String b=verificationPicFile2(file[i]);
				if(a==true&&b.equals("000000")==false){
					String imageUrl=wechatUserservice.uploadFile(file[i]);
			        urlList.add(imageUrl);
				}else{
					throw new BaseServiceException(
							StatusCode.MISSING_PARAMETER_ERROR.getCode(),
							"请上传图片类型文件！");
				}
				
			}
			return this.backJsonResult(urlList,StatusCode.SUCESS.getCode(),StatusCode.SUCESS.getErrorMsg());
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
	
	
	
	
	private String verificationPicFile2(MultipartFile file) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		byte [] src=file.getBytes();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        String xxx= stringBuilder.toString().toUpperCase().substring(0,6);
        String type=checkType(xxx);
		return type;
	}
	
	
	 public static String checkType(String xxxx) {
	        
	        switch (xxxx) {
	        case "FFD8FF": return "jpg";
	        case "89504E": return "png";
	        //case "474946": return "jif";

	        default: return "000000";
	        }
	    }



	private Boolean verificationPicFile(MultipartFile file) {
		Boolean A =false;
		String name= file.getOriginalFilename();
		String type =name.substring(name.indexOf(".")).toUpperCase();
		if(type.equals(".PNG")||type.equals(".JPG")||type.equals(".JPEG")){
			A=true;
		}
		//System.out.println(name);
		return A;
	}
	
	public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }



	/**
	 * 车辆信息验证
	 * @param loginMap
	 * @return
	 * @throws JsonProcessingException 
	 */
	@RequestMapping(value = "/vehPlateVerify", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap vehPlateVerify(@RequestBody Map<String, Object> vehPlateMap) throws JsonProcessingException {
		logger.info("apiName:"+"/wechatUser/vehPlateVerify"+"; param:"+new ObjectMapper().writeValueAsString(vehPlateMap));
		try {
			wechatUserservice.vehPlateVerify(vehPlateMap);
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
	}
	
	
	@RequestMapping(value = "/findNotifycation", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ModelMap findNotifycation() {
		//logger.info("apiName:"+"/wechatUser/findNotifycation"+"; param:"+JSON.toJSONString(userRecharge));
		Map<String, Object> map = new HashMap<String,Object>(); 
		try {
			map=wechatUserservice.findNotifycation();
			return backJsonResult(map,StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
			//return backJsonResult(map,901, "预充值功能暂未开通！");
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
	
	
	
	
	

}
