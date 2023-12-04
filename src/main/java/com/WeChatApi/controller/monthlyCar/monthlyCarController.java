package com.WeChatApi.controller.monthlyCar;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.WeChatApi.bean.dto.doInvoiceDto;
import com.WeChatApi.bean.dto.refundDto;
import com.WeChatApi.bean.models.operationOrder;
import com.WeChatApi.bean.models.parkinglotsPayRefund;
import com.WeChatApi.controller.base.BaseController;
import com.WeChatApi.controller.base.BaseException;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.controller.base.WxDecodeUtil;
import com.WeChatApi.dao.monthlyCarMapper;
import com.WeChatApi.service.monthlyCarService.monthlyCarService;
import com.WeChatApi.service.wechatApiService.wechatApiService;

import com.alipay.api.internal.util.AlipaySignature;
import com.fasterxml.jackson.databind.ObjectMapper;

import sun.misc.BASE64Encoder;

@RequestMapping("/monthlyCar")
@Controller
public class monthlyCarController extends BaseController {
	
	@Autowired
	private monthlyCarService monthlycarservice;
	@Autowired
	private wechatApiService wechatApiservice;
	
	@Autowired
	private monthlyCarMapper monthlycarmapper;
	
	
	@RequestMapping(value = "/findSubscriptionTypeByPlate", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findSubscriptionTypeByPlate(@RequestBody Map<String, String> findTypeMap) throws  IOException {
		logger.info("apiName:"+"/monthlyCar/findSubscriptionTypeByPlate"+"; param:"+new ObjectMapper().writeValueAsString(findTypeMap));
		List<Map<String, String>> list = null;
		try {
			list = monthlycarservice.findSubscriptionTypeByPlate(findTypeMap);
			return backJsonResult(list, StatusCode.SUCESSLAYUI.getCode(), StatusCode.SUCESS.getErrorMsg());
		}catch (BaseException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	
	@RequestMapping(value = "/monthlyCarPayBywechat", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap monthlyCarPayBywechat(@RequestBody Map<String, String> wechatPayMap, HttpServletRequest request) throws  IOException {
		logger.info("apiName:"+"/monthlyCar/monthlyCarPayBywechat"+"; param:"+new ObjectMapper().writeValueAsString(wechatPayMap));
		Map<String, Object> returnMap= new  HashMap<String, Object>();
		try {
			returnMap = monthlycarservice.monthlyCarPayBywechat(wechatPayMap,request);
			return backJsonResult(returnMap, StatusCode.SUCESSLAYUI.getCode(), StatusCode.SUCESS.getErrorMsg());
		}catch (BaseException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	
	
	
	
	@RequestMapping(value = "/findSubscriptionRecordByUserOpenId", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findSubscriptionRecordByUserOpenId(@RequestBody Map<String, String> findRecordMap) throws  IOException {
		logger.info("apiName:"+"/monthlyCar/findSubscriptionRecordByUserOpenId"+"; param:"+new ObjectMapper().writeValueAsString(findRecordMap));
		List<Map<String, String>> list = null;
		try {
			list = monthlycarservice.findSubscriptionRecordByUserOpenId(findRecordMap);
			return backJsonResult(list, StatusCode.SUCESSLAYUI.getCode(), StatusCode.SUCESS.getErrorMsg());
		}catch (BaseException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	/**********************************************嘉善新包月逻辑************************************************/
	
	
	
	/**
	 * 查询用户的包期记录和停车场包期金额
	 * @param findRecordMap
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/findMonthlyPklRecord", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findMonthlyPklRecord(@RequestBody Map<String, String> findRecordMap) throws  IOException {
		logger.info("apiName:"+"/monthlyCar/findMonthlyPklRecord"+"; param:"+new ObjectMapper().writeValueAsString(findRecordMap));
		List<Map<String, String>> list = null;
		try {
			list = monthlycarservice.findMonthlyPklRecord(findRecordMap);
			return backJsonResult(list, StatusCode.SUCESSLAYUI.getCode(), StatusCode.SUCESS.getErrorMsg());
		}catch (BaseException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	@RequestMapping(value = "/monthlyCarPayBywechat_new", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap monthlyCarPayBywechat_new(@RequestBody Map<String, String> wechatPayMap, HttpServletRequest request) throws  IOException {
		logger.info("apiName:"+"/monthlyCar/monthlyCarPayBywechat_new"+"; param:"+new ObjectMapper().writeValueAsString(wechatPayMap));
		Map<String, Object> returnMap= new  HashMap<String, Object>();
		try {
			returnMap = monthlycarservice.monthlyCarPayBywechat_new(wechatPayMap,request);
			return backJsonResult(returnMap, StatusCode.SUCESSLAYUI.getCode(), StatusCode.SUCESS.getErrorMsg());
		}catch (BaseException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	@RequestMapping(value = "/monthlyCarPayByalipay_new", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap monthlyCarPayByalipay_new(@RequestBody Map<String, String> wechatPayMap, HttpServletRequest request) throws  IOException {
		logger.info("apiName:"+"/monthlyCar/monthlyCarPayByalipay_new"+"; param:"+new ObjectMapper().writeValueAsString(wechatPayMap));
		Map<String, String> returnMap= new  HashMap<String, String>();
		try {
			returnMap = monthlycarservice.monthlyCarPayByalipay_new(wechatPayMap,request);
			return backJsonResult(returnMap, StatusCode.SUCESSLAYUI.getCode(), StatusCode.SUCESS.getErrorMsg());
		}catch (BaseException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	@RequestMapping(value = "/payCallback", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
    public void payCallback(HttpServletRequest request,HttpServletResponse response) {
        String inputLine = "";
        String notityXml = "";
        try {
            while((inputLine = request.getReader().readLine()) != null){
                notityXml += inputLine;
            }
            //关闭流
            request.getReader().close();
            logger.info("+++++++++++++++++++++++++微信包月回调开始+++++++++++++++++++++++++++++"+notityXml);
            
            //解析成Map
            Map<String,String> map =wechatApiservice.doXMLParse(notityXml);
            //判断 支付是否成功
            if("SUCCESS".equals(map.get("result_code"))){
                String outTradeNo = map.get("out_trade_no");
                String transactionId = map.get("transaction_id");
                String timeEnd = map.get("time_end");
                String couponFee = map.get("total_fee");
                String rId=map.get("attach");
                //operationOrder order= operationOrderservice.findOderInfoByOutTradeNo(outTradeNo);
                
                monthlycarservice.callBackNotify(rId);
                monthlycarservice.updateOperationSubscriptionRecordByRid(rId);
                wechatApiservice.insertApiLogs("微信支付回调接口_包月", new ObjectMapper().writeValueAsString(map), "success", "");
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
            	
            	wechatApiservice.insertApiLogs("微信支付回调接口_包月", new ObjectMapper().writeValueAsString(map), "fail", map.get("err_code")+"/"+map.get("err_code_des"));
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
		//return "";
    }
	
	
	
	@RequestMapping(value = "/payCallback_alipay", method = RequestMethod.POST)
	@ResponseBody
	public String notifyUrl(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 //获取支付宝发送过来的信息
		logger.info("支付宝包期回调内容信息："+request.getParameterMap());
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
        logger.info("支付宝包期回调内容信息："+params);
        //调用SDK验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(params, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqliWU4ndPe3KkRlQe85h2SDNb2l9UbPeaNmZZaced7Oc6T896WyZzB6+42gMQOOrRPWZui7ZCGmT8wzjTwFGXb9Zb/qmzXmV6nnQpz6NXCOJBL9VyKLm/EYjA0A5mwDwBD75trMXydcGWOdcQCuPi9HrH2YSJ9QUK2KzdCKQ3XMahhdtyjQcFxMXooVhPvnGi+4LgmsOJZU45cfZLnItHCPWDqRVrsoOjiNHHGVdUOxdKTor0suIE1W/IMfP0ITmMe0H1L+lItd+FBjDGfP2sd8R8phgrNi6b11Jg7ZXXCzsC9tkXn84lyKbWknybISYU9Sy1Lr9e/XgF9uX1jU76wIDAQAB", "UTF-8", "RSA2");
        //boolean类型signVerified为true时 则验证成功
        if(signVerified) {
            //获取到支付的状态 TRADE_SUCCESS则支付成功
            String trade_status =request.getParameter("trade_status");
            String couponRecordId=request.getParameter("passback_params");
             if (trade_status.equals("TRADE_SUCCESS")){
            	 monthlycarservice.callBackNotify(couponRecordId);
                 monthlycarservice.updateOperationSubscriptionRecordByRid(couponRecordId);
                 wechatApiservice.insertApiLogs("支付宝包期支付回调接口", "用户id"+request.getParameter("buyer_id")+"订单号："+request.getParameter("out_trade_no")+";流水号："+request.getParameter("trade_no")+";金额："+request.getParameter("total_amount")+"优惠券id："+request.getParameter("passback_params"), "success", "");
            	 return "success";
             }else {
            	 wechatApiservice.insertApiLogs("支付宝包期支付回调接口", "用户id"+request.getParameter("buyer_id")+"订单号："+request.getParameter("out_trade_no")+";流水号："+request.getParameter("trade_no")+";金额："+request.getParameter("total_amount")+"优惠券id："+request.getParameter("passback_params"), "fail", params.toString());
            	 return "failure";
             }
        }
        //签名验证失败
        else {
        	logger.info("支付宝签名验证失败："+AlipaySignature.getSignCheckContentV1(params));
             
        	return "failure";
        }

	}
	
	
	
	@RequestMapping(value = "/doInvoice", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap doInvoice(@RequestBody doInvoiceDto invoiceDto ) throws  IOException {
		logger.info("apiName:"+"/monthlyCar/doInvoice"+"; param:"+new ObjectMapper().writeValueAsString(invoiceDto));
		try {
			monthlycarservice.doInvoice(invoiceDto);
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
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/payRefund", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	@Transactional
	public ModelMap payRefund(@RequestBody refundDto dto,HttpServletRequest request) throws  IOException {
		logger.info("apiName:"+"/monthlyCar/payRefund"+"; param:"+new ObjectMapper().writeValueAsString(dto));
		try {
			monthlycarservice.payRefund(dto,request);
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
	
	
	
	@RequestMapping(value = "/refundCallback_monthlyCar", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	@Transactional
    public void refundCallback(HttpServletRequest request,HttpServletResponse response) {
        String inputLine = "";
        String notityXml = "";
        try {
            while((inputLine = request.getReader().readLine()) != null){
                notityXml += inputLine;
            }
            //关闭流
            request.getReader().close();
            logger.info("+++++++++++++++++++++++++包期退款回调开始+++++++++++++++++++++++++++++");
            logger.info("包期退款回调内容信息："+notityXml);
            //解析成Map
            Map<String,String> map =wechatApiservice.doXMLParse(notityXml);
            //判断 支付是否成功
            if("SUCCESS".equals(map.get("return_code"))){
            	String req_info=map.get("req_info");
            	logger.info("微信退款回调内容待解密信息："+req_info);
            	Security.addProvider(new BouncyCastleProvider());
            	Map<String, String>reqmap=wechatApiservice.doXMLParse(WxDecodeUtil.decryptData(req_info));
            	logger.info("包期退款解密信息："+reqmap);
            	String outRefundNo=reqmap.get("out_refund_no");
            	String refundId = reqmap.get("refund_id");
            	String couponFee = reqmap.get("refund_fee");
            	
            	
                 //businessservice.intsertPayRefundLog(JSON.toJSONString(reqmap),dto.getR_id());
            	
            	
            	parkinglotsPayRefund dto=monthlycarservice.findMonthlyCarRefundByOutNo(outRefundNo);
            	monthlycarservice.updateMonthlyCarRefundByOutNo(outRefundNo,refundId);
            	if(dto.getOrder_type().equals("1")){
            		List<Map<String, Object>>recordList= monthlycarmapper.findSubscriptionRecordByRId(dto.getR_pay_id().toString());
            		System.out.println(recordList.get(0).get("refund_amount"));
            		int refund_fee =Integer.valueOf(recordList.get(0).get("refund_amount").toString());
        			int sum_refund_fee=refund_fee+Integer.valueOf(couponFee);
        			monthlycarmapper.updateMonthlyCarRefundByPayId(dto.getR_pay_id(),sum_refund_fee);
            	}else{
            		List<Map<String, Object>>recordList= monthlycarmapper.findSubscriptionRecordByRId_road(dto.getR_pay_id().toString());
            		System.out.println(recordList.get(0).get("refund_amount"));
            		int refund_fee =Integer.valueOf(recordList.get(0).get("refund_amount").toString());
        			int sum_refund_fee=refund_fee+Integer.valueOf(couponFee);
        			monthlycarmapper.updateMonthlyCarRefundByPayId_road(dto.getR_pay_id(),sum_refund_fee);
            	}
               
               
                //businessservice.updateParkingLotsPayRefundAmountByPayId(dto.getR_pay_id(),sumRefundAmount);
                wechatApiservice.insertApiLogs("包期退款回调接口",new ObjectMapper().writeValueAsString(reqmap), "success", "");
                StringBuffer buffer = new StringBuffer();
                buffer.append("<xml>");
                buffer.append("<return_code>"+map.get("return_code")+"</return_code>");
                buffer.append("<return_msg>OK</return_msg>");
                buffer.append("</xml>");

                //给微信服务器返回 成功标示 否则会一直询问 咱们服务器 是否回调成功
                PrintWriter writer = response.getWriter();
                //返回
                writer.print(buffer.toString());
            }else{
            	//logger.info("微信回调返回商户订单号："+map.get("out_trade_no")+"/"+map.get("err_code")+"/"+map.get("err_code_des"));
            	wechatApiservice.insertApiLogs("包期退款回调接口", new ObjectMapper().writeValueAsString(map), "fail", map.get("err_code")+"/"+map.get("err_code_des"));
            	StringBuffer buffer = new StringBuffer();
                buffer.append("<xml>");
                buffer.append("<return_code>"+map.get("return_code")+"</return_code>");
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
		//return "";
    }
	
	/////////////////////////////////////////////////////////////包月新20230717，含全包，半包内容////////////////////////////////////////////////////
	
	/**
	 * 获取包期信息，全包，半包等信息
	 * @param 
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/findMonthlyCarPackage", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findMonthlyCarPackage(@RequestBody Map<String,Object> map ) throws  IOException {
		logger.info("apiName:"+"/monthlyCar/findMonthlyCarPackage"+"; param:"+new ObjectMapper().writeValueAsString(map));
		try {
			List<Map<String, Object>> packageList = new ArrayList<Map<String,Object>>();
			packageList =monthlycarservice.findMonthlyCarPackage(map);
			return backJsonResult(packageList, StatusCode.SUCESSLAYUI.getCode(), StatusCode.SUCESS.getErrorMsg());
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
	 * 获取全包期信息包期停车场
	 * @param 
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/findMonthlyCarParkinglotAll", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findMonthlyCarParkinglotAll(@RequestBody Map<String,Object> map ) throws  IOException {
		logger.info("apiName:"+"/monthlyCar/findMonthlyCaParkinglotAll"+"; param:"+new ObjectMapper().writeValueAsString(map));
		try {
			List<Map<String, Object>> parkinglotList = new ArrayList<Map<String,Object>>();
			parkinglotList =monthlycarservice.findMonthlyCaParkinglotAll(map);
			return backJsonResult(parkinglotList, StatusCode.SUCESSLAYUI.getCode(), StatusCode.SUCESS.getErrorMsg());
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
	 * 提交包期审核
	 * @param 
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/monthlyCarAudit", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap monthlyCarAudit(@RequestBody Map<String,Object> map ) throws  IOException {
		logger.info("apiName:"+"/monthlyCar/monthlyCarAudit"+"; param:"+new ObjectMapper().writeValueAsString(map));
		try {
			monthlycarservice.monthlyCarAudit(map);
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
	 * 查询包期审核记录
	 * @param 
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/findMonthlyCarAudit", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findMonthlyCarAudit(@RequestBody Map<String,Object> map ) throws  IOException {
		logger.info("apiName:"+"/monthlyCar/findMonthlyCarAudit"+"; param:"+new ObjectMapper().writeValueAsString(map));
		try {
			List<Map<String, Object>> monthlyCarAuditList = new ArrayList<Map<String,Object>>();
			monthlyCarAuditList =monthlycarservice.findMonthlyCarAudit(map);
			return backJsonResult(monthlyCarAuditList, StatusCode.SUCESSLAYUI.getCode(), StatusCode.SUCESS.getErrorMsg());
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
	 * 查询包期审核记录详情
	 * @param 
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/findMonthlyCarAuditDet", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findMonthlyCarAuditDet(@RequestBody Map<String,Object> map ) throws  IOException {
		logger.info("apiName:"+"/monthlyCar/findMonthlyCarAuditDet"+"; param:"+new ObjectMapper().writeValueAsString(map));
		try {
			Map<String, Object> monthlyCarAuditList = new HashMap<>();
			monthlyCarAuditList =monthlycarservice.findMonthlyCarAuditDet(map);
			return backJsonResult(monthlyCarAuditList, StatusCode.SUCESSLAYUI.getCode(), StatusCode.SUCESS.getErrorMsg());
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
	 * 包期套餐缴费
	 * @param wechatPayMap
	 * @param request
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/monthlyCarPackagePayByWechat", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap monthlyCarPackagePayByWechat(@RequestBody Map<String, Object> wechatPayMap, HttpServletRequest request) throws  IOException {
		logger.info("apiName:"+"/monthlyCar/monthlyCarPackagePayByWechat"+"; param:"+new ObjectMapper().writeValueAsString(wechatPayMap));
		Map<String, Object> returnMap= new  HashMap<String, Object>();
		try {
			returnMap = monthlycarservice.monthlyCarPackagePayByWechat(wechatPayMap,request);
			return backJsonResult(returnMap, StatusCode.SUCESSLAYUI.getCode(), StatusCode.SUCESS.getErrorMsg());
		}catch (BaseException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	@RequestMapping(value = "/payCallback_package", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
    public void payCallback_package(HttpServletRequest request,HttpServletResponse response) {
        String inputLine = "";
        String notityXml = "";
        try {
            while((inputLine = request.getReader().readLine()) != null){
                notityXml += inputLine;
            }
            //关闭流
            request.getReader().close();
            logger.info("+++++++++++++++++++++++++微信包月回调开始_套餐+++++++++++++++++++++++++++++"+notityXml);
            
            //解析成Map
            Map<String,String> map =wechatApiservice.doXMLParse(notityXml);
            //判断 支付是否成功
            if("SUCCESS".equals(map.get("result_code"))){
                //String outTradeNo = map.get("out_trade_no");
                String transactionId = map.get("transaction_id");
                String timeEnd = map.get("time_end");
                String couponFee = map.get("total_fee");
                String attach=map.get("attach");
                String[] aa=attach.split("_");
                String au_id=aa[0];
                String days=aa[1];
                String outTradeNo=aa[2];
                
                monthlycarservice.callBackNotify_package(outTradeNo,au_id,days,couponFee);
                
                wechatApiservice.insertApiLogs("微信支付回调接口_包月套餐", new ObjectMapper().writeValueAsString(map), "success", "");
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
            	
            	wechatApiservice.insertApiLogs("微信支付回调接口_包月", new ObjectMapper().writeValueAsString(map), "fail", map.get("err_code")+"/"+map.get("err_code_des"));
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
		//return "";
    }
	
	
	
	/**
	 * 获取包期缴费记录，分全包单包
	 * @param findRecordMap
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/findSubscriptionRecordByUserOpenId_new", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findSubscriptionRecordByUserOpenId_new(@RequestBody Map<String, Object> findRecordMap) throws  IOException {
		logger.info("apiName:"+"/monthlyCar/findSubscriptionRecordByUserOpenId_new"+"; param:"+new ObjectMapper().writeValueAsString(findRecordMap));
		List<Map<String, Object>> list = null;
		try {
			list = monthlycarservice.findSubscriptionRecordByUserOpenId_new(findRecordMap);
			return backJsonResult(list, StatusCode.SUCESSLAYUI.getCode(), StatusCode.SUCESS.getErrorMsg());
		}catch (BaseException e) {
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
	 * 
	 * @param invoiceDto
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/doInvoice_new", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap doInvoice_new(@RequestBody doInvoiceDto invoiceDto ) throws  IOException {
		logger.info("apiName:"+"/monthlyCar/doInvoice_new"+"; param:"+new ObjectMapper().writeValueAsString(invoiceDto));
		try {
			monthlycarservice.doInvoice_new(invoiceDto);
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
