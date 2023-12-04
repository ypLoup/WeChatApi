package com.WeChatApi.controller.business;

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

import com.WeChatApi.bean.condition.businessCouponCondition;
import com.WeChatApi.bean.condition.couponCondition;
import com.WeChatApi.bean.condition.invoiceCondition;
import com.WeChatApi.bean.condition.parkinglotsCondition;
import com.WeChatApi.bean.condition.parkinglotsPayCondition;
import com.WeChatApi.bean.condition.storeChargeRecordCondition;
import com.WeChatApi.bean.dto.businessCouponDto;
import com.WeChatApi.bean.dto.couponDto;
import com.WeChatApi.bean.dto.doInvoiceDto;
import com.WeChatApi.bean.dto.meetingCodeDto;
import com.WeChatApi.bean.dto.rechargeDto;
import com.WeChatApi.bean.dto.refundDto;
import com.WeChatApi.bean.dto.storeChargeRecordDto;
import com.WeChatApi.bean.dto.userChargeRecordDto;
import com.WeChatApi.bean.dto.userRechargeRecordDto;
import com.WeChatApi.bean.models.invoice;
import com.WeChatApi.bean.models.parkinglots;
import com.WeChatApi.bean.models.parkinglotsPay;
import com.WeChatApi.bean.models.userInfo;
import com.WeChatApi.bean.models.userStore;
import com.WeChatApi.controller.base.BaseController;
import com.WeChatApi.controller.base.BaseException;
import com.WeChatApi.controller.base.BaseServiceException;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.service.blueCardService.blueCardService;
import com.WeChatApi.service.businessService.businessService;
import com.WeChatApi.service.invoiceService.invoiceService;
import com.WeChatApi.service.wechatApiService.wechatApiService;
import com.alipay.api.internal.util.AlipaySignature;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONObject;




@RequestMapping("/business")
@Controller
public class businessController extends BaseController {
	
	@Autowired
	private businessService businessservice;

	@Autowired
	private wechatApiService wechatApiservice;
	
	
	/**
	 * 用户绑定成商户
	 * @param loginMap
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/userBind", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap userBind(@RequestBody Map<String, Object> loginMap) throws  IOException {
		logger.info("apiName:"+"/business/userBind"+"; param:"+new ObjectMapper().writeValueAsString(loginMap));
		try {
			businessservice.loginByWechat(loginMap);
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
	 * 根据storeId获取商户信息
	 * @param storeMap
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/findStoreInfo", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findStoreInfo(@RequestBody Map<String, Object> storeMap) throws  IOException {
		logger.info("apiName:"+"/business/findStoreInfo"+"; param:"+new ObjectMapper().writeValueAsString(storeMap));
		try {
			userStore store=businessservice.findUserStoreBySuId(storeMap);
			return backJsonResult(store,StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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
	
	
	/***
	 * 添加优惠券
	 * @param businessCouponCondition
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/addCoupon", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	@Transactional
	public ModelMap addCoupon(@RequestBody businessCouponDto dto) throws  IOException {
		logger.info("apiName:"+"/business/addCoupon"+"; param:"+new ObjectMapper().writeValueAsString(dto));
		try {
			businessservice.addCoupon(dto);
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
	 * 查询商户优惠券
	 * @param dto
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/findStoreCouponByCondition", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findStoreCouponByCondition(@RequestBody businessCouponCondition condition) throws  IOException {
		logger.info("apiName:"+"/business/findStoreCouponByCondition"+"; param:"+new ObjectMapper().writeValueAsString(condition));
		List<businessCouponDto> list = null;
		try {
			businessservice.setStoreCouponUnused();//将过期优惠券c_status =2
			list = businessservice.findStoreCouponByCondition(condition);
			long total=businessservice.findStoreCouponCountByCondition(condition);
			return backJsonPageResult(list,total, StatusCode.SUCESSLAYUI.getCode(), StatusCode.SUCESS.getErrorMsg());
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
	 * 查询用户充值——退款信息
	 * @param dto
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/findUserChargeInfoByCondition", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findUserChargeInfoByCondition(@RequestBody storeChargeRecordCondition condition) throws  IOException {
		logger.info("apiName:"+"/business/findUserChargeInfoByCondition"+"; param:"+new ObjectMapper().writeValueAsString(condition));
		List<storeChargeRecordDto> list = null;
		try {
			list = businessservice.findUserChargeInfoByCondition(condition);
			long total=businessservice.findUserChargeCountByCondition(condition);
			return backJsonPageResult(list,total, StatusCode.SUCESSLAYUI.getCode(), StatusCode.SUCESS.getErrorMsg());
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
	
	
	
	
	
	/***
	 * 结算优惠券
	 * @param businessCouponCondition
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/settleUpCoupon", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap settleUpCoupon(@RequestBody businessCouponDto dto) throws  IOException {
		logger.info("apiName:"+"/business/settleUpCoupon"+"; param:"+new ObjectMapper().writeValueAsString(dto));
		try {
			businessservice.settleUpCoupon(dto);
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
	 * 查询折扣列表
	 * @param dto
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/findStoreDiscountByCondition", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findStoreDiscountByCondition(@RequestBody Map<String, Object> paramMap) throws  IOException {
		logger.info("apiName:"+"/business/findStoreDiscountByCondition"+"; param:"+new ObjectMapper().writeValueAsString(paramMap));
		List<Map<String, Object>> list = null;
		try {
			list = businessservice.findStoreDiscountByCondition(paramMap);
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
	 * 微信充值
	 * @param paramMap
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/rechargeByWechat", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap rechargeByWechat(@RequestBody rechargeDto recharge,HttpServletRequest request) throws  IOException {
		logger.info("apiName:"+"/business/rechargeByWechat"+"; param:"+new ObjectMapper().writeValueAsString(recharge));
		Map<String, Object>businessMap=new HashMap<String, Object>();
		try {
			businessMap=businessservice.rechargeByWechat(recharge,request);
			return backJsonResult(businessMap,StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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
	 * 商户微信支付回调接口
	 */
	/*@RequestMapping(value = "/wechatPayCallback", method = { RequestMethod.POST, RequestMethod.GET })
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
            logger.info("+++++++++++++++++++++++++商户微信回调开始+++++++++++++++++++++++++++++");
            logger.info("微信回调内容信息："+notityXml);
            //解析成Map
            Map<String,String> map =wechatApiservice.doXMLParse(notityXml);
            JSONObject jsonObject = JSONObject.fromObject(map);
            String scdId_storeId_recordId=map.get("attach");
            String[] aa=scdId_storeId_recordId.split("_");
            String scdId=aa[0];
            String storeId=aa[1];
            String balances=aa[2];
            String point=aa[3];
            //判断 支付是否成功
            //String scdId_storeId="";
            if("SUCCESS".equals(map.get("result_code"))){
                String outTradeNo = map.get("out_trade_no");
                String transactionId = map.get("transaction_id");
                String timeEnd = map.get("time_end");
                String couponFee = map.get("total_fee");
               
                //userChargeRecordDto userChargeRecord=businessservice.findChargeRecordByRid(recordId);
                
                businessservice.addUserStoreBalances(scdId_storeId_recordId);
                //businessservice.updateChargeRecordStatus(recordId,1);//用户充值记录表
                
                businessservice.inserStoreChargeRecord(Integer.valueOf(storeId),4,Integer.valueOf(scdId),Integer.valueOf(balances),Integer.valueOf(point));//商户充值记录表
                businessservice.insertChargePayLog(jsonObject.toString(),recordId,"1");
                wechatApiservice.insertApiLogs("商户微信支付回调接口", "订单号："+outTradeNo+";流水号："+transactionId+";金额："+couponFee+"折扣_商户："+scdId_storeId_recordId, "success", "");
                
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
            	//logger.info("微信回调返回商户订单号："+map.get("out_trade_no")+"/"+map.get("err_code")+"/"+map.get("err_code_des"));
            	 
            	 //businessservice.updateChargeRecordStatus(recordId,3);
            	 //businessservice.insertChargePayLog(jsonObject.toString(),recordId,"2");
            	 wechatApiservice.insertApiLogs("商户微信支付回调接口", "订单号："+map.get("out_trade_no")+";流水号："+map.get("transaction_id")+";金额："+map.get("coupon_fee")+"折扣_商户："+map.get("attach"), "fail", map.get("err_code")+"/"+map.get("err_code_des"));
            	 
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
    }*/
	
	
	@RequestMapping(value = "/wechatPayCallback_new", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	@Transactional
    public void wechatPayCallback_new(HttpServletRequest request,HttpServletResponse response) {
        String inputLine = "";
        String notityXml = "";
        try {
            while((inputLine = request.getReader().readLine()) != null){
                notityXml += inputLine;
            }
            //关闭流
            request.getReader().close();
            logger.info("+++++++++++++++++++++++++商户微信回调开始+++++++++++++++++++++++++++++");
            logger.info("微信回调内容信息："+notityXml);
            //解析成Map
            Map<String,String> map =wechatApiservice.doXMLParse(notityXml);
            JSONObject jsonObject = JSONObject.fromObject(map);
            String scdId_storeId_recordId=map.get("attach");
            String[] aa=scdId_storeId_recordId.split("_");
            String scdId=aa[0];
            String storeId=aa[1];
            String recordId=aa[2];
            
            //判断 支付是否成功
            //String scdId_storeId="";
            if("SUCCESS".equals(map.get("result_code"))){
                String outTradeNo = map.get("out_trade_no");
                String transactionId = map.get("transaction_id");
                String timeEnd = map.get("time_end");
                String couponFee = map.get("total_fee");
               
                //storeChargeRecordDto storeChargeRecord=businessservice.findStoreChargeRecordByRid(recordId);
                long num =businessservice.findStoreChargePayLogCount(recordId);
                logger.info("store_charge_pay_log：记录数"+num);
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
                	businessservice.addUserStoreBalances(scdId_storeId_recordId);
                    businessservice.updateStoreChargeRecordStatus(recordId,1);//更新商户充值表
                    
                    //businessservice.inserStoreChargeRecord(Integer.valueOf(storeId),4,Integer.valueOf(scdId),storeChargeRecord.getR_balances(),storeChargeRecord.getR_point());//商户充值记录表
                    businessservice.insertChargePayLog(jsonObject.toString(),recordId,"1");
                    wechatApiservice.insertApiLogs("商户微信支付回调接口", "订单号："+outTradeNo+";流水号："+transactionId+";金额："+couponFee+"折扣_商户："+scdId_storeId_recordId, "success", "");
                    
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
            	 
            	 //businessservice.updateChargeRecordStatus(recordId,3);
            	 //businessservice.insertChargePayLog(jsonObject.toString(),recordId,"2");
            	 wechatApiservice.insertApiLogs("商户微信支付回调接口", "订单号："+map.get("out_trade_no")+";流水号："+map.get("transaction_id")+";金额："+map.get("coupon_fee")+"折扣_商户："+map.get("attach"), "fail", map.get("err_code")+"/"+map.get("err_code_des"));
            	 
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
	
	/**
	 * 支付宝充值
	 * @param paramMap
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/rechargeByAlipay", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap rechargeByAlipay(@RequestBody rechargeDto recharge,HttpServletRequest request) throws  IOException {
		logger.info("apiName:"+"/business/rechargeByAlipay"+"; param:"+new ObjectMapper().writeValueAsString(recharge));
		Map<String, Object>businessMap=new HashMap<String, Object>();
		try {
			businessMap=businessservice.rechargeByAlipay(recharge,request);
			return backJsonResult(businessMap,StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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

        if(signVerified) {
            //获取到支付的状态 TRADE_SUCCESS则支付成功
            //String trade_status =request.getParameter("trade_status");
        	String trade_status=jsonObject.getString("trade_status");
            int couponFee=0;
            //String scdId_storeId=request.getParameter("passback_params");
            String scdId_storeId=jsonObject.getString("passback_params");
          String[] aa=scdId_storeId.split("_");
          String scdId=aa[0];
          String storeId=aa[1];
          String recordId=aa[2];
          logger.info("trade_status："+trade_status);
             if (trade_status.equals("TRADE_SUCCESS")){
            	 //bluecardservice.callBackNotify(request.getParameter("out_trade_no"),request.getParameter("trade_no"),request.getParameter("gmt_payment"),request.getParameter("total_amount"),"1");
            	 //couponFee=Integer.valueOf(request.getParameter("total_amount"))*100;
            	 //userChargeRecordDto userChargeRecord=businessservice.findChargeRecordByRid(recordId);
            	 logger.info("支付宝商户信息更新开始："+scdId_storeId);
            	 businessservice.addUserStoreBalances(scdId_storeId);
            	
            	 
            	 //businessservice.insertChargePayLog("订单号："+request.getParameter("out_trade_no")+";流水号："+request.getParameter("trade_no")+";金额："+couponFee+"折扣_商户："+scdId_storeId,"订单号："+request.getParameter("trade_no")+";流水号："+request.getParameter("trade_no"),"1");
            	 businessservice.updateStoreChargeRecordStatus(recordId,1);
            	 //businessservice.inserStoreChargeRecord(Integer.valueOf(storeId),4,Integer.valueOf(scdId),userChargeRecord.getR_balances(),userChargeRecord.getR_point());//商户充值记录表
            	 businessservice.insertChargePayLog(jsonObject.toString(),recordId,"1");
            	 wechatApiservice.insertApiLogs("支付宝商户支付回调接口", "用户id"+request.getParameter("buyer_id")+"订单号："+request.getParameter("out_trade_no")+";流水号："+request.getParameter("trade_no")+";金额："+request.getParameter("total_amount"), "success", "");
            	 return "success";
             }else {
            	 
            	 //businessservice.insertChargePayLog("订单号："+request.getParameter("out_trade_no")+";流水号："+request.getParameter("trade_no")+";金额："+couponFee+"折扣_商户："+scdId_storeId,"订单号："+request.getParameter("trade_no")+";流水号："+request.getParameter("trade_no"),"2");
            	 businessservice.updateChargeRecordStatus(recordId,3);
            	 businessservice.insertChargePayLog(jsonObject.toString(),recordId,"2");
            	 wechatApiservice.insertApiLogs("支付宝商户支付回调接口", "用户id"+request.getParameter("buyer_id")+"订单号："+request.getParameter("out_trade_no")+";流水号："+request.getParameter("trade_no")+";金额："+request.getParameter("total_amount"), "fail", params.toString());
            	 return "failure";
             }
        }
        //签名验证失败
        else {
        	logger.info("支付宝签名验证失败："+AlipaySignature.getSignCheckContentV1(params));
             
        	return "failure";
        }

	}
	
	
	/***
	 * 退款申请
	 * @param refundDto
	 * @return
	 */
	/*@RequestMapping(value = "/payRefund_xz", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	@Transactional
	public ModelMap payRefund_xz(@RequestBody refundDto dto,HttpServletRequest request) {
		logger.info("apiName:"+"/business/payRefund_xz"+"; param:"+JSON.toJSONString(dto));
		try {
			businessservice.payRefund_xz(dto,request);
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
	
	
	
	/***
	 * 退款申请
	 * @param refundDto
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/payRefund", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	@Transactional
	public ModelMap payRefund(@RequestBody refundDto dto,HttpServletRequest request) throws  IOException {
		logger.info("apiName:"+"/business/payRefund"+"; param:"+new ObjectMapper().writeValueAsString(dto));
		try {
			businessservice.payRefund(dto,request);
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
	
	
	/***
	 * 获取优惠券二维码字符串
	 * @param map
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/getERCodeString", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	@Transactional
	public ModelMap getERCodeString(@RequestBody Map<String, Object> map) throws  IOException {
		logger.info("apiName:"+"/business/getERCodeString"+"; param:"+new ObjectMapper().writeValueAsString(map));
		try {
			String eRCode= businessservice.getERCodeString(map);
			return backJsonResult(eRCode,StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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
	
	
	
	/***
	 * 扫码领取优惠券
	 * @param map
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/scanERCodeGetCoupon", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	@Transactional
	public ModelMap scanERCodeGetCoupon(@RequestBody Map<String, Object> map) throws  IOException {
		logger.info("apiName:"+"/business/scanERCodeGetCoupon"+"; param:"+new ObjectMapper().writeValueAsString(map));
		try {
			businessservice.scanERCodeGetCoupon(map);
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
	
	
	
	/***
	 * 退款申请
	 * @param refundDto
	 * @return
	 *//*
	@RequestMapping(value = "/insertUserRechargeRecordByDto", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	@Transactional
	public ModelMap insertUserRechargeRecordByDto(@RequestBody userRechargeRecordDto dto,HttpServletRequest request) {
		logger.info("apiName:"+"/business/payRefund"+"; param:"+JSON.toJSONString(dto));
		try {
			businessservice.insertUserRechargeRecordByDto(dto);
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
