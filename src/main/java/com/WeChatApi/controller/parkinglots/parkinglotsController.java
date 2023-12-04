package com.WeChatApi.controller.parkinglots;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.annotations.Param;

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
import com.WeChatApi.bean.condition.userInfoCondition;
import com.WeChatApi.bean.condition.wechatUserCondition;
import com.WeChatApi.bean.dto.doInvoiceDto;
import com.WeChatApi.bean.dto.userChargeRecordDto;
import com.WeChatApi.bean.dto.userRechargeDto;
import com.WeChatApi.bean.dto.userRechargeRecordDto;
import com.WeChatApi.bean.models.parkinglots;
import com.WeChatApi.bean.models.userInfo;
import com.WeChatApi.bean.models.wechatUser;
import com.WeChatApi.controller.base.BaseController;
import com.WeChatApi.controller.base.BaseException;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.dao.parkinglotsMapper;
import com.WeChatApi.service.parkinglotsService.parkinglotsService;
import com.WeChatApi.service.userInfoService.userInfoService;
import com.WeChatApi.service.wechatApiService.wechatApiService;
import com.WeChatApi.service.wechatUserService.wechatUserService;
import com.alipay.api.internal.util.AlipaySignature;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONObject;
import sun.misc.BASE64Encoder;

@RequestMapping("/parklots")
@Controller
public class parkinglotsController extends BaseController {
	
	@Autowired
	private parkinglotsService parkinglotservice;
	
	@Autowired
	private wechatApiService wechatApiservice;
	
	@Autowired
	private parkinglotsMapper parkinglotsmapper;
	
	@Autowired
	private wechatUserService wechatUserservice;
	
	
	
	@RequestMapping(value = "/findParkinglotInfo", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findParkinglotInfo() {
		List<parkinglots> list = null;
		try {
			list = parkinglotservice.findParkinglotInfo();
			long total=parkinglotservice.findParkinglotInfoCount();
			return backJsonPageResult(list,total, StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	//discountParking
	/**
	 * 
	 * @param userRecharge
	 * @param request
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/topUpParkingByUser", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ModelMap topUpParkingByUser(@RequestBody Map<String,String>map,HttpServletRequest request) throws  IOException {
		logger.info("apiName:"+"/parklots/topUpParkingByUser"+"; param:"+new ObjectMapper().writeValueAsString(map));
		Map<String, Object> returnmap = new HashMap<String,Object>(); 
		try {
			returnmap=parkinglotservice.topUpParkingByUser(map,request);
			return backJsonResult(returnmap,StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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
	 * 查询停车场优惠列表
	 * @param userRecharge
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/findParkinglotsCharge", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ModelMap findParkinglotsCharge() {
		
		Map<String, Object> returnmap = new HashMap<>(); 
		try {
			returnmap=parkinglotservice.findParkinglotsCharge();
			return backJsonResult(returnmap,StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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
	 * 用户停车场优惠预充值回调接口
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
            logger.info("+++++++++++++++++++++++++用户停车场优惠预充值微信回调开始+++++++++++++++++++++++++++++");
            logger.info("微信回调内容信息："+notityXml);
            //解析成Map
            Map<String,String> map =wechatApiservice.doXMLParse(notityXml);
            JSONObject jsonObject = JSONObject.fromObject(map);
            String cr_id=map.get("attach");

            List<Map<String, Object>> parkingChargeRecordList = new ArrayList<>();
            parkingChargeRecordList=parkinglotsmapper.findParkingChargeRecordByCrId(cr_id);
            if(parkingChargeRecordList.size()!=0){
            	String userId= parkingChargeRecordList.get(0).get("cr_user_id").toString();
            	String cr_status = parkingChargeRecordList.get(0).get("cr_status").toString();
            	wechatUser userInfo= wechatUserservice.findUserInfoByUserId(userId);
                if("SUCCESS".equals(map.get("result_code"))){

                    
                    if(cr_status.equals("1")){
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
                    	parkinglotsmapper.updateParkinglotsChargeRecordStatus(cr_id);
                        int newBalances=userInfo.getPkl_balances()+Integer.valueOf(parkingChargeRecordList.get(0).get("cr_c_balances").toString());
                        int newPoint=userInfo.getPkl_point()+Integer.valueOf(parkingChargeRecordList.get(0).get("cr_c_point").toString());
                        parkinglotsmapper.insertParkinglotsChargeLogInfo("1",userId,cr_id,parkingChargeRecordList.get(0).get("cr_c_balances").toString(),parkingChargeRecordList.get(0).get("cr_c_point").toString(),newBalances,newPoint,"停车场充值");
                        wechatUserservice.updateWechatUserInfoMoney_Pkl(newBalances,newPoint,userId);
                        wechatApiservice.insertApiLogs("微信停车场优惠充值接口", new ObjectMapper().writeValueAsString(map), "success", "");
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
            String cr_id=request.getParameter("passback_params");
            List<Map<String, Object>> parkingChargeRecordList = new ArrayList<>();
            parkingChargeRecordList=parkinglotsmapper.findParkingChargeRecordByCrId(cr_id);

            if(parkingChargeRecordList.size()!=0){
            	String userId= parkingChargeRecordList.get(0).get("cr_user_id").toString();
            	String cr_status = parkingChargeRecordList.get(0).get("cr_status").toString();
            	wechatUser userInfo= wechatUserservice.findUserInfoByUserId(userId);
            	if (trade_status.equals("TRADE_SUCCESS")){                  
                    if(cr_status.equals("1")){
                    	return "success";
                    }else{
                    	parkinglotsmapper.updateParkinglotsChargeRecordStatus(cr_id);
                        int newBalances=userInfo.getPkl_balances()+Integer.valueOf(parkingChargeRecordList.get(0).get("cr_c_balances").toString());
                        int newPoint=userInfo.getPkl_point()+Integer.valueOf(parkingChargeRecordList.get(0).get("cr_c_point").toString());
                        wechatUserservice.updateWechatUserInfoMoney_Pkl(newBalances,newPoint,userId);
                        wechatApiservice.insertApiLogs("支付宝停车场优惠充值接口", new ObjectMapper().writeValueAsString(jsonObject), "success", "");
                        return "success";
                    }
                }else{
                	//logger.info("微信回调返回商户订单号："+map.get("out_trade_no")+"/"+map.get("err_code")+"/"+map.get("err_code_des"));
                	 wechatApiservice.insertApiLogs("支付宝停车场优惠充值接口", new ObjectMapper().writeValueAsString(jsonObject), "fail",params.toString()); 
                	 return "failure";
                }
            	
            } 
        }
        //签名验证失败
        else {
        	logger.info("支付宝签名验证失败："+AlipaySignature.getSignCheckContentV1(params));
             
        	return "failure";
        }
		return null;

	}
	
	
	
	/**
	 * 查询停车场优惠充值记录
	 * @param userRecharge
	 * @param request
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/findParkinglotsChargeRecord", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ModelMap findParkinglotsChargeRecord(@RequestBody Map<String,String>map) throws  IOException {
		logger.info("apiName:"+"/parklots/findParkinglotsChargeRecord"+"; param:"+new ObjectMapper().writeValueAsString(map));
		List<Map<String, String>> returnmap = new ArrayList(); 
		try {
			returnmap=parkinglotservice.findParkinglotsChargeRecord(map);
			return backJsonResult(returnmap,StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
			
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
	
	
	
	@RequestMapping(value = "/doInvoice", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap doInvoice(@RequestBody doInvoiceDto invoiceDto) throws  IOException {
		logger.info("apiName:"+"/parklots/doInvoice"+"; param:"+new ObjectMapper().writeValueAsString(invoiceDto));
		try {
			parkinglotservice.doInvoice(invoiceDto);
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
	
	
	
	

	

	
	

}
