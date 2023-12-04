package com.WeChatApi.controller.wechatApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.annotations.Param;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
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
import com.WeChatApi.bean.dto.getCashUrl;
import com.WeChatApi.bean.dto.prePayDto;
import com.WeChatApi.bean.dto.prePayDto2;
import com.WeChatApi.bean.dto.refundDto;
import com.WeChatApi.bean.models.operationOrder;
import com.WeChatApi.bean.models.parkinglotsPay;
import com.WeChatApi.bean.models.parkinglotsPayRefund;
import com.WeChatApi.bean.models.redPack;
import com.WeChatApi.bean.models.roadParkinglotsPay;
import com.WeChatApi.bean.models.userInfo;
import com.WeChatApi.bean.models.wechatUser;
import com.WeChatApi.controller.base.BaseController;
import com.WeChatApi.controller.base.BaseException;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.controller.base.WxDecodeUtil;
import com.WeChatApi.dao.roadParkinglotsPayMapper;
import com.WeChatApi.service.blueCardService.blueCardService;
import com.WeChatApi.service.businessService.businessService;
import com.WeChatApi.service.operationOrderService.operationOrderService;
import com.WeChatApi.service.userInfoService.userInfoService;

import java.io.PrintWriter;
import java.security.Security;

import com.WeChatApi.service.wechatApiService.wechatApiService;
import com.WeChatApi.service.wechatUserService.wechatUserService;
import com.WeChatApi.service.zfbApiService.zfbApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sun.misc.BASE64Encoder;

@RequestMapping("/wechatApi")
@Controller
public class wechatApiController extends BaseController {
	
	@Autowired
	private wechatApiService wechatApiservice;
	
	@Autowired
	private  blueCardService bluecardservice;
	
	@Autowired
	private businessService businessservice;
	@Autowired
	private operationOrderService operationOrderservice;
	
	@Autowired
	private roadParkinglotsPayMapper roadpaymapper;
	
	@Autowired
	private zfbApiService zfbapiservice;
	
	
	
	/**
	 * @Param("jsCode")String  jsCode,
	 * @param loginMap
	 * @return
	 */
	@RequestMapping(value = "/wechatLogin", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap wechatLogin(@RequestBody Map<String,Object> loginMap) {
		logger.info("apiName:"+"/wechatApi/wechatLogin"+"; param:"+loginMap);
		try {
			String openId=wechatApiservice.wechatLogin(loginMap.get("jsCode").toString(),loginMap.get("ivData").toString(),loginMap.get("encryptedData").toString());
			logger.info("apiName:"+"/wechatApi/wechatLogin"+"; getParam:"+openId);
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
	 * @Param("jsCode")String  jsCode,
	 * @param loginMap
	 * @return
	 */
	/*@RequestMapping(value = "/wechatLogin", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap wechatLogin(String jsCode) {
		
		try {
			String openId=wechatApiservice.wechatLogin(jsCode,null,null);
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
	
	
	
	@RequestMapping(value = "/wechatSendredpack", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap wechatSendredpack(@RequestBody redPack redpack,HttpServletRequest request) {
		
		try {
			Map<String, Object> openId=wechatApiservice.wechatSendredpack(redpack,request);
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
	
	/*@RequestMapping(value = "/getPrepayId", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap getPrepayId(String uvPlate, HttpServletRequest request) {
		
		try {
			String prepayId=wechatApiservice.getPrepayId(uvPlate,request);
			return backJsonResult(prepayId);
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
	
	
	/**
	 * 嘉善回调
	 */
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
            logger.info("+++++++++++++++++++++++++微信回调开始+++++++++++++++++++++++++++++");
            logger.info("微信回调内容信息："+notityXml);
            //解析成Map
            Map<String,String> map =wechatApiservice.doXMLParse(notityXml);
            //判断 支付是否成功
            if("SUCCESS".equals(map.get("result_code"))){
            	StringBuffer buffer = new StringBuffer();
                buffer.append("<xml>");
                buffer.append("<return_code>"+map.get("result_code")+"</return_code>");
                buffer.append("<return_msg>OK</return_msg>");
                buffer.append("</xml>");

                //给微信服务器返回 成功标示 否则会一直询问 咱们服务器 是否回调成功
                PrintWriter writer = response.getWriter();
                //返回
                writer.print(buffer.toString());
                String outTradeNo = map.get("out_trade_no");
                String transactionId = map.get("transaction_id");
                String timeEnd = map.get("time_end");
                String couponFee = map.get("total_fee");
                String couponRecordId=map.get("attach");
                //operationOrder order= operationOrderservice.findOderInfoByOutTradeNo(outTradeNo);
                String orderIds[]=couponRecordId.split("_");
                bluecardservice.callBackNotify(Integer.valueOf(orderIds[1].toString()),outTradeNo,transactionId,timeEnd,couponFee,"2",orderIds[0].toString(),orderIds[2].toString());
                wechatApiservice.insertApiLogs("微信支付回调接口", "订单号："+outTradeNo+";流水号："+transactionId+";金额："+couponFee+";优惠券记录id："+couponRecordId, "success", "");
                
            }else{
            	//logger.info("微信回调返回商户订单号："+map.get("out_trade_no")+"/"+map.get("err_code")+"/"+map.get("err_code_des"));
            	wechatApiservice.insertApiLogs("微信支付回调接口", "订单号："+map.get("out_trade_no")+";流水号："+map.get("transaction_id")+";金额："+map.get("coupon_fee")+";优惠券记录id："+map.get("attach"), "fail", map.get("err_code")+"/"+map.get("err_code_des"));
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
	
	
	
	
	@RequestMapping(value = "/roadPayCallback", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
    public void roadPayCallback(HttpServletRequest request,HttpServletResponse response) {
        String inputLine = "";
        String notityXml = "";
        try {
            while((inputLine = request.getReader().readLine()) != null){
                notityXml += inputLine;
            }
            //关闭流
            request.getReader().close();
            logger.info("+++++++++++++++++++++++++微信路边支付回调开始+++++++++++++++++++++++++++++");
            logger.info("微信路边支付回调内容信息："+notityXml);
            //解析成Map
            Map<String,String> map =wechatApiservice.doXMLParse(notityXml);
            //判断 支付是否成功
            if("SUCCESS".equals(map.get("result_code"))){
                String outTradeNo = map.get("out_trade_no");
                String transactionId = map.get("transaction_id");
                String timeEnd = map.get("time_end");
                String couponFee = map.get("total_fee");
                String couponRecordId=map.get("attach");
                if(couponRecordId.equals("0")){
                	int length =outTradeNo.length();
                	 operationOrderservice.updateRoadOrderInfoByOutTradeNo(couponFee,"32",outTradeNo.substring(0,length-3));
                }else{
                	String orderIds[]=couponRecordId.split(",");
                	for(String orderId:orderIds){
                		List<Map<String, Object>> roadOrderInfoList = operationOrderservice.findRoadOrderInfoByOrderId(orderId);
                		 operationOrderservice.updateRoadOrderInfoByOutTradeNo(roadOrderInfoList.get(0).get("order_arrears").toString(),"32",roadOrderInfoList.get(0).get("order_number").toString());
                	}
                }
               
                wechatApiservice.insertApiLogs("微信路边支付回调接口", "订单号："+outTradeNo+";流水号："+transactionId+";金额："+couponFee+";欠费id："+couponRecordId, "success", "");
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
            	wechatApiservice.insertApiLogs("微信路边支付回调接口", "订单号："+map.get("out_trade_no")+";流水号："+map.get("transaction_id")+";金额："+map.get("coupon_fee")+";欠费id："+map.get("attach"), "fail", map.get("err_code")+"/"+map.get("err_code_des"));
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
	
	
	
	@RequestMapping(value = "/cashOutCallback", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	@Transactional 
    public void cashOutCallback(HttpServletRequest request,HttpServletResponse response) {
        String inputLine = "";
        String notityXml = "";
        try {
            while((inputLine = request.getReader().readLine()) != null){
                notityXml += inputLine;
            }
            //关闭流
            request.getReader().close();
            logger.info("+++++++++++++++++++++++++微信提现回调开始+++++++++++++++++++++++++++++");
            logger.info("微信提现回调内容信息："+notityXml);
            //解析成Map
            Map<String,String> map =wechatApiservice.doXMLParse(notityXml);
            //判断 支付是否成功
            if("SUCCESS".equals(map.get("return_code"))){
             
            	String req_info=map.get("req_info");
            	logger.info("微信提现回调内容待解密信息："+req_info);
            	Security.addProvider(new BouncyCastleProvider());
            	Map<String, String>reqmap=wechatApiservice.doXMLParse(WxDecodeUtil.decryptData(req_info));
            	logger.info("微信提现解密信息："+reqmap);
            	String outTradeNo=reqmap.get("out_trade_no");
            	String refundId = reqmap.get("refund_id");
            	String refundFee = reqmap.get("refund_fee");
            	String refundStatus = reqmap.get("refund_status");
            	//String recordId = reqmap.get("refund_desc");
            	//String orderIds[]=recordId.split("_");
            	//logger.info("recordId:"+orderIds[1]);
            	if(refundStatus.equals("SUCCESS")){
            		businessservice.updateUserReChargeRecord(outTradeNo,refundFee);
            		//businessservice.updateChargeRecordStatus(orderIds[1],1);//用户充值记录表
            	}
            
                wechatApiservice.insertApiLogs("微信提现回调接口",new ObjectMapper().writeValueAsString(reqmap), "success", reqmap.get("refund_status"));
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
            	wechatApiservice.insertApiLogs("微信提现回调接口", new ObjectMapper().writeValueAsString(map), "fail", map.get("err_code")+"/"+map.get("err_code_des"));
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
	
	
	
	
	
	
	@RequestMapping(value = "/getPrepayId", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap getPrepayId(@RequestBody prePayDto2 dto, HttpServletRequest request) throws JsonProcessingException {
		Map<String, Object> prepayId = new HashMap<String, Object>();
		//logger.info("微信唤起支付信息："+JSON.toJSONString(dto));
		logger.info("apiName:"+"/wechatApi/getPrepayId"+"; param:"+new ObjectMapper().writeValueAsString(dto));
		try {
			if(dto.getOrderReceivable()!=0){
				prepayId=wechatApiservice.getPrepayId_new(dto,request);
				return backJsonResult(prepayId);
			}else{
				prepayId=bluecardservice.payByDiscountNotify(dto);
				return backJsonSuccessMsg(100,"success");
			}
			
			
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
	
	
	@RequestMapping(value = "/getPrepayId_zfb", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap getPrepayId_zfb(@RequestBody prePayDto2 dto, HttpServletRequest request) throws JsonProcessingException {
		String prepayId="";
		//logger.info("支付宝唤起支付信息："+JSON.toJSONString(dto));
		logger.info("apiName:"+"/wechatApi/getPrepayId_zfb"+"; param:"+new ObjectMapper().writeValueAsString(dto));
		try {
			if(dto.getOrderReceivable()!=0){
				prepayId=zfbapiservice.getPrepayId_zfb_new(dto,request);
				Map<String, Object> map =new HashMap<String, Object>();
				map.put("alipayReturn", prepayId);
				
				return backJsonResult(map);
			}else{
				prepayId=bluecardservice.payByDiscountNotify_zfb(dto);
				return backJsonSuccessMsg(100,"success");
			}
			
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
	 * 路边泊位支付宝支付
	 * @throws JsonProcessingException 
	 */
	@RequestMapping(value = "/roadsidePay_zfb", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap roadsidePay_zfb(@RequestBody prePayDto2 dto, HttpServletRequest request) throws JsonProcessingException {
		String prepayId="";
		logger.info("apiName:"+"/wechatApi/roadsidePay_zfb"+"; param:"+new ObjectMapper().writeValueAsString(dto));
		try {
			if(dto.getOrderReceivable()!=0){
				prepayId=zfbapiservice.roadsidePay_zfb(dto,request);
			}
			Map<String, Object> map =new HashMap<String, Object>();
			map.put("alipayReturn", prepayId);
			return backJsonResult(map);
			//return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), "暂停服务！");
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
	 * 获取现金红包地址
	 * @throws JsonProcessingException 
	 */
	@RequestMapping(value = "/getWechatCashUrl", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Map<String, Object> getWechatCashUrl(@RequestBody(required=false) getCashUrl dto) throws JsonProcessingException{
		logger.info("apiName:"+"/wechatApi/getWechatCashUrl"+"; param:"+new ObjectMapper().writeValueAsString(dto));
		String cashUrl="";
		Map<String, Object>result= new HashMap<>();
		Map<String, Object>data= new HashMap<>();
		try {
			cashUrl=wechatApiservice.getWechatCashUrl(dto);
			if(dto.getPayment()-dto.getPaymenttotal()>0){
				if(cashUrl!=""){
					result.put("result", 0);
					data.put("backpayment", dto.getPayment()-dto.getPaymenttotal());
					data.put("paymentnow", 0);
					data.put("back_url", cashUrl);
					data.put("lsh", System.currentTimeMillis()+"");
					result.put("data", data);
				}
			}else if(dto.getPayment()-dto.getPaymenttotal()<0){
				result.put("result", 0);
				data.put("backpayment", 0);
				data.put("paymentnow", dto.getPaymenttotal()-dto.getPayment());
				data.put("back_url", cashUrl);
				data.put("lsh", System.currentTimeMillis()+"");
				result.put("data", data);
			}else if(dto.getPayment()-dto.getPaymenttotal()==0){
				result.put("result", 0);
				data.put("backpayment", 0);
				data.put("paymentnow", dto.getPaymenttotal()-dto.getPayment());
				data.put("back_url", cashUrl);
				data.put("lsh", System.currentTimeMillis()+"");
				result.put("data", data);
			}
			logger.info("apiName:"+"/wechatApi/getWechatCashUrl"+"; 传参:"+new ObjectMapper().writeValueAsString(dto)+"; 回参:"+new ObjectMapper().writeValueAsString(result));
			return result;
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
	
	
	
	@RequestMapping(value = "/redpack", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap redpack(@RequestBody Map<String,Object> map ,HttpServletRequest request) throws JsonProcessingException {
		logger.info("apiName:"+"/wechatApi/redpack"+"; param:"+new ObjectMapper().writeValueAsString(map));
		try {
			Map<String, Object> openId=wechatApiservice.redpack(map,request);
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
	
	@RequestMapping(value = "/cashOut_wechat", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
    public ModelMap cashOut_wechat(@RequestBody Map<String,Object> map ,HttpServletRequest request) throws JsonProcessingException {
		logger.info("apiName:"+"/wechatApi/cashOut_wechat"+"; param:"+new ObjectMapper().writeValueAsString(map));
		try {
			wechatApiservice.cashOut_wechat(map,request);
			return backJsonSuccessMsg(StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
	        //return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), "系统维护中，请联系客服！");
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
	
	@RequestMapping(value = "/refundCallback", method = { RequestMethod.POST, RequestMethod.GET })
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
            logger.info("+++++++++++++++++++++++++微信退款回调开始+++++++++++++++++++++++++++++");
            logger.info("微信退款回调内容信息："+notityXml);
            //解析成Map
            Map<String,String> map =wechatApiservice.doXMLParse(notityXml);
            //判断 支付是否成功
            if("SUCCESS".equals(map.get("return_code"))){
            	String req_info=map.get("req_info");
            	logger.info("微信退款回调内容待解密信息："+req_info);
            	Security.addProvider(new BouncyCastleProvider());
            	Map<String, String>reqmap=wechatApiservice.doXMLParse(WxDecodeUtil.decryptData(req_info));
            	logger.info("微信退款解密信息："+reqmap);
            	String outRefundNo=reqmap.get("out_refund_no");
            	String refundId = reqmap.get("refund_id");
            	String couponFee = reqmap.get("refund_fee");
            	parkinglotsPayRefund dto=businessservice.findRefundDtoByOutNo(outRefundNo);
                parkinglotsPay pay =businessservice.findParkingLotsPayInfoByPayId(dto.getR_pay_id());
                int sumRefundAmount =pay.getRefund_amount()+Integer.valueOf(couponFee);
                businessservice.updatePayRefundByOutNo(outRefundNo,refundId);
                businessservice.intsertPayRefundLog(new ObjectMapper().writeValueAsString(reqmap),dto.getR_id());
                businessservice.updateParkingLotsPayRefundAmountByPayId(dto.getR_pay_id(),sumRefundAmount);
                wechatApiservice.insertApiLogs("微信退款回调接口",new ObjectMapper().writeValueAsString(reqmap), "success", "");
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
            	wechatApiservice.insertApiLogs("微信退款回调接口", new ObjectMapper().writeValueAsString(map), "fail", map.get("err_code")+"/"+map.get("err_code_des"));
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
	
	
	
	/*@RequestMapping(value = "/refundCallback_xz", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	@Transactional
    public void refundCallback_xz(HttpServletRequest request,HttpServletResponse response) {
        String inputLine = "";
        String notityXml = "";
        try {
            while((inputLine = request.getReader().readLine()) != null){
                notityXml += inputLine;
            }
            //关闭流
            request.getReader().close();
            logger.info("+++++++++++++++++++++++++微信退款回调开始+++++++++++++++++++++++++++++");
            logger.info("微信退款回调内容信息："+notityXml);
            //解析成Map
            Map<String,String> map =wechatApiservice.doXMLParse(notityXml);
            //判断 支付是否成功
            if("SUCCESS".equals(map.get("return_code"))){
            	String req_info=map.get("req_info");
            	logger.info("微信退款回调内容待解密信息："+req_info);
            	Security.addProvider(new BouncyCastleProvider());
            	Map<String, String>reqmap=wechatApiservice.doXMLParse(WxDecodeUtil.decryptData(req_info));
            	logger.info("微信退款解密信息："+reqmap);
            	String outRefundNo=reqmap.get("out_refund_no");
            	String refundId = reqmap.get("refund_id");
            	String couponFee = reqmap.get("refund_fee");
            	String attach = reqmap.get("attach");
            	//String[] aa=attach.split("_");
            	if(attach.equals("1")){
            		parkinglotsPayRefund dto=businessservice.findRefundDtoByOutNo(outRefundNo);
                    parkinglotsPay pay =businessservice.findParkingLotsPayInfoByPayId(dto.getR_pay_id());
                    int sumRefundAmount =pay.getRefund_amount()+Integer.valueOf(couponFee);
                    businessservice.updatePayRefundByOutNo(outRefundNo,refundId);
                    businessservice.intsertPayRefundLog(JSON.toJSONString(reqmap),dto.getR_id());
                    businessservice.updateParkingLotsPayRefundAmountByPayId(dto.getR_pay_id(),sumRefundAmount);
                    wechatApiservice.insertApiLogs("微信退款回调接口",JSON.toJSONString(reqmap), "success", "");
            	}else{
            		parkinglotsPayRefund dto=businessservice.findRoadRefundDtoByOutNo(outRefundNo);
            		roadParkinglotsPay pay=roadpaymapper.findRoadParkingLotsPayInfoByPayId(Integer.valueOf(dto.getR_pay_id()));
            		int sumRefundAmount =pay.getRefund_amount()+Integer.valueOf(couponFee);
            		businessservice.updateRoadPayRefundByOutNo(outRefundNo,refundId);
            		roadpaymapper.intsertRoadPayRefundLog(JSON.toJSONString(reqmap),dto.getR_id());
            		roadpaymapper.updateRoadParkingLotsPayRefundAmountByPayId(dto.getR_pay_id(),sumRefundAmount);
            		 wechatApiservice.insertApiLogs("微信退款回调接口",JSON.toJSONString(reqmap), "success", "");
            	}
            	
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
            	wechatApiservice.insertApiLogs("微信退款回调接口", JSON.toJSONString(map), "fail", map.get("err_code")+"/"+map.get("err_code_des"));
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
    }*/
	
	
	@RequestMapping(value = "/findRedpackInfo", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findRedpackInfo(@RequestBody Map<String,Object> map ,HttpServletRequest request) throws JsonProcessingException {
		logger.info("apiName:"+"/wechatApi/findRedpackInfo"+"; param:"+new ObjectMapper().writeValueAsString(map));
		try {
			Map<String, Object> openId=wechatApiservice.findRedpackStatus_web(map,request);
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

	
	

}
