package com.WeChatApi.service.roadParkinglotsPayService;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.jar.Pack200.Packer;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.WeChatApi.bean.condition.couponCondition;
import com.WeChatApi.bean.condition.invoiceCondition;
import com.WeChatApi.bean.condition.operationOrderCondition;
import com.WeChatApi.bean.condition.parkinglotsCondition;
import com.WeChatApi.bean.condition.parkinglotsPayCondition;
import com.WeChatApi.bean.condition.roadParkinglotsPayCondition;
import com.WeChatApi.bean.dto.doInvoiceDto;
import com.WeChatApi.bean.dto.refundDto;
import com.WeChatApi.bean.models.invoice;
import com.WeChatApi.bean.models.noVehiclePlate;
import com.WeChatApi.bean.models.operationOrder;
import com.WeChatApi.bean.models.parkinglots;
import com.WeChatApi.bean.models.parkinglotsPay;
import com.WeChatApi.bean.models.roadParkinglotsPay;
import com.WeChatApi.bean.models.storeInvoice;
import com.WeChatApi.bean.models.wechatUser;
import com.WeChatApi.controller.base.BaseException;
import com.WeChatApi.controller.base.BaseServiceException;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.dao.invoiceMapper;
import com.WeChatApi.dao.operationOrderMapper;
import com.WeChatApi.dao.parkinglotsMapper;
import com.WeChatApi.dao.roadParkinglotsPayMapper;
import com.WeChatApi.dao.wechatUserMapper;
import com.WeChatApi.service.blueCardService.blueCardService;
import com.WeChatApi.service.wechatUserService.wechatUserService;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONObject;



@Service
@Transactional
public class roadParkinglotsPayService {
	
	@Autowired
	private roadParkinglotsPayMapper roadpaymapper;
	
	@Autowired
	private blueCardService bluecardservice;
	
	@Autowired
	private invoiceMapper invoicemapper;
	
	@Autowired
	private wechatUserMapper wechatUsermapper;
	
	

	
	private static Logger log = Logger.getLogger(noVehiclePlate.class.getName());
	

	

	public List<roadParkinglotsPay> findRoadParkingPayInfo(roadParkinglotsPayCondition condition) {
		
		if(condition.getVeh_plates_colours()==null){
			List<String> ids = new ArrayList<String>();
			for (String s : condition.getVeh_plates().toString().split(",")) {
				ids.add(s);
			}
			condition.setVehPlateLits(ids);
			return roadpaymapper.findRoadParkingPayInfo_old(condition);
		}else{
			List<Map<String,String>> ids = new ArrayList<>();
			int veh_plates_count = condition.getVeh_plates().toString().split(",").length;
			if(condition.getVeh_plates().toString().split(",").length!=condition.getVeh_plates_colours().toString().split(",").length){
				throw new BaseServiceException(
						StatusCode.MISSING_OPENID_ERROR.getCode(),
						"车牌数和车牌颜色数不匹配，请核实！");
			}
			for(int i =0 ;i<veh_plates_count;i++){
				Map<String, String> map = new HashMap<String, String>();
				map.put("plate", condition.getVeh_plates().toString().split(",")[i].toString());
				map.put("colour", condition.getVeh_plates_colours().toString().split(",")[i].toString());
				ids.add(map);
			}
			if(ids.size()!=0){
				condition.setVehPlateMapLits(ids);
			}
			
			return roadpaymapper.findRoadParkingPayInfo(condition);
		}
	}

	public long findRoadParkingPayInfoCount(roadParkinglotsPayCondition condition) {
		
		List<String> ids = new ArrayList<String>();
		for (String s : condition.getVeh_plates().toString().split(",")) {
			ids.add(s);
		}
		condition.setVehPlateLits(ids);
		
		return roadpaymapper.findRoadParkingPayInfoCount(condition);
	}
	
	
	
    /**
     * 发起开票
     * @param invoiceDto
     * @throws IOException 
     */
	public void doInvoice(doInvoiceDto invoiceDto) throws IOException {
		String userOpenId = invoiceDto.getUserOpenId();
		
		if(StringUtils.isBlank(invoiceDto.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"用户userOpenId不能为空！");
		}
		
		invoice invoice = invoicemapper.findInvoiceInfoByTaxNumber(invoiceDto.getTaxNumber(),invoiceDto.getUserOpenId());
		if(invoice==null){
			/*if(StringUtils.isBlank(invoiceDto.getBankNumber())){
				throw new BaseServiceException(
						StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"开户行账号不能为空！");
			}*/
			
			if(StringUtils.isBlank(invoiceDto.getCompanyName())){
				throw new BaseServiceException(
						StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"发票抬头不能为空！");
			}
			/*if(StringUtils.isBlank(invoiceDto.getTaxNumber())){
				throw new BaseServiceException(
						StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"发票税号不能为空！");
			}*/
			if(StringUtils.isBlank(invoiceDto.getEmail())){
				throw new BaseServiceException(
						StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"邮箱地址不能为空！");
			}
			/*if(StringUtils.isBlank(invoiceDto.getAddressMobile())){
				throw new BaseServiceException(
						StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"联系地址电话不能为空！");
			}*/
			invoice invoice2 = new  invoice();
			invoice2.setUserOpenId(userOpenId);
			invoice2.setCompanyName(invoiceDto.getCompanyName());
			invoice2.setTaxNumber(invoiceDto.getTaxNumber());
			invoice2.setBankNumber(invoiceDto.getBankNumber());
			invoice2.setEmail(invoiceDto.getEmail());
			invoice2.setAddressMobile(invoiceDto.getAddressMobile());
			List<String> outTradeNoList = new ArrayList<>();
			for(String outTradeNo : invoiceDto.getOutTradeNos().toString().split(",")){
				/*parkinglotsPay parkinglots=invoicemapper.findParkingPayNoInvoiceInfoByOutTradeNo(outTradeNo);
				if(parkinglots!=null){
					bluecardservice.doInvoice(parkinglots,invoice2);
				}*/
				outTradeNoList.add(outTradeNo);
			}
			if(outTradeNoList.size()>0){
				Map<String, Object> sumPriceMap = roadpaymapper.getSumPriceByOutTradeNoList(outTradeNoList);
				//String vehiclePates=invoicemapper.getVehiclesByOutTradeNoList(outTradeNoList);
				doInvoiceSumPrice(outTradeNoList,invoice2,sumPriceMap);
			}
			
			
			invoicemapper.addInvoiceInfo(invoice2);
		}else{
			List<String> outTradeNoList = new ArrayList<>();
			for(String outTradeNo : invoiceDto.getOutTradeNos().toString().split(",")){
				/*parkinglotsPay parkinglots=invoicemapper.findParkingPayNoInvoiceInfoByOutTradeNo(outTradeNo);
				if(parkinglots!=null){
					bluecardservice.doInvoice(parkinglots,invoice);
				}*/
				outTradeNoList.add(outTradeNo);
			}
			if(outTradeNoList.size()>0){
				Map<String, Object> sumPriceMap = roadpaymapper.getSumPriceByOutTradeNoList(outTradeNoList);
				doInvoiceSumPrice(outTradeNoList,invoice,sumPriceMap);
			}
			
		}
		
		
		
	}

	
	

	public List<parkinglotsPay> findParkingPayInfoByPayId(Map<String, Object> map) {
		if(StringUtils.isBlank(map.get("payId").toString())){
			throw new BaseServiceException(
					StatusCode.PARAMETER_FORMATE_RROR.getCode(),
					"查询主键不能为空！");
		}
		
		return roadpaymapper.findParkingPayInfoByPayId(map.get("payId").toString());
	}
	
	
	public void doInvoiceSumPrice(List<String> outTradeNoList, invoice invoice2, Map<String, Object> sumPriceMap) throws IOException {
		
		PostMethod postMethod = null;
		try{
		// 设置上传文件目录
	    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
	    String doInvoiceUrl = res.getString("doInvoice_url");
    	
	    postMethod = new PostMethod(doInvoiceUrl) ;

        NameValuePair[] Senddata = {
	            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
	            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
	            new NameValuePair("invoiceAmount",sumPriceMap.get("price")+""),
	            new NameValuePair("companyName",invoice2.getCompanyName()),
	            new NameValuePair("taxNumber",invoice2.getTaxNumber()),
	            new NameValuePair("addressAndMobile",invoice2.getAddressMobile()),
	            new NameValuePair("bankAndNumber",invoice2.getBankNumber()),
	            new NameValuePair("email",invoice2.getEmail()),
	            new NameValuePair("remark",sumPriceMap.get("vehPlates")+"")
	            
	    };
        
        
        postMethod.setRequestBody(Senddata);
        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;

       
        HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true) );  
	    int response = client.executeMethod(postMethod); // POST
	    
	    if(response==200){
	    	JSONObject  dataJson = JSONObject.fromObject(postMethod.getResponseBodyAsString());
            String errorCode = dataJson.getString("error_code");
            if(errorCode.equals("0")){
            String data = dataJson.getString("data");
            JSONObject  dataJsonR = JSONObject.fromObject(data);
                
            	log.info("订单号："+outTradeNoList.toString()+"开票成功");
            	wechatUsermapper.insertApiLogs("路边泊位开票接口", "订单号:"+outTradeNoList.toString()+";税号:"+invoice2.getTaxNumber()+";开票金额:"+sumPriceMap, "success", "发票地址:"+dataJsonR.getString("pdfUrl"));
            	roadParkinglotsPay pay=new roadParkinglotsPay();
            	pay.setOrder_numberList(outTradeNoList);
            	pay.setInvoice_pdf(dataJsonR.getString("pdfUrl"));
            	pay.setInvoice_status(1);
            	pay.setInvoice_company_name(invoice2.getCompanyName());
            	pay.setInvoice_tax_number(invoice2.getTaxNumber());
            	pay.setInvoice_bank_number(invoice2.getBankNumber());
            	pay.setInvoice_address_mobile(invoice2.getAddressMobile());
            	pay.setInvoice_email(invoice2.getEmail());
            	roadpaymapper.updateRoadParkingPayInfo(pay);

            }else{
            	log.info("开票号："+outTradeNoList.toString()+"开票失败");
            	roadParkinglotsPay pay=new roadParkinglotsPay();
            	pay.setOrder_numberList(outTradeNoList);
            	//pay.setInvoice_pdf(dataJsonR.getString("pdfUrl"));
            	//pay.setInvoice_status(1);
            	pay.setInvoice_company_name(invoice2.getCompanyName());
            	pay.setInvoice_tax_number(invoice2.getTaxNumber());
            	pay.setInvoice_bank_number(invoice2.getBankNumber());
            	pay.setInvoice_address_mobile(invoice2.getAddressMobile());
            	pay.setInvoice_email(invoice2.getEmail());
            	roadpaymapper.updateRoadParkingPayInfo(pay);
            	wechatUsermapper.insertApiLogs("路边泊位开票接口", "订单号:"+outTradeNoList.toString()+";税号:"+invoice2.getTaxNumber()+";开票金额:"+sumPriceMap, "fail", dataJson.getString("error_msg"));
            	throw new BaseServiceException(
            			Integer.parseInt(errorCode),
            			dataJson.getString("error_msg"));
            }
           
            
	    }else{
	    	throw new BaseServiceException(
					StatusCode.API_FREQUENTLY_ERROR.getCode(),
					"请查看路边泊位开票接口");
	    }
		}finally {
			postMethod.releaseConnection();
		}
		
	}

	public void roadPayRefund(refundDto dto, HttpServletRequest request) throws JsonProcessingException {
		roadParkinglotsPay pay=roadpaymapper.findRoadParkingLotsPayInfoByPayId(dto.getPayId());
		int sumRefundFee=dto.getRefundFee()+pay.getRefund_amount();
		if(sumRefundFee>dto.getTotalFee()){
			throw new BaseException(
					StatusCode.SYSTEM_ERROR.getCode(),
					"退款金额不能大于支付金额！");
		}
		log.info("支付宝路边泊位退款开始========================"+new ObjectMapper().writeValueAsString(dto)+"ip地址："+request.getRemoteAddr());
		Map<String, Object> resultMap= roadPayRefundByAlipay(dto);
		log.info("支付宝路边泊位退款结束========================"+new ObjectMapper().writeValueAsString(dto)+"结果："+resultMap.get("resultCode"));
		if(resultMap.get("resultCode").equals("SUCCESS")){
			dto.setRefundId(resultMap.get("refundId").toString());
			roadpaymapper.insertRoadPayRefund_success(dto);
			roadpaymapper.intsertRoadPayRefundLog(resultMap.get("logTxt").toString(),dto.getrId());
			roadpaymapper.updateRoadParkingLotsPayRefundAmountByPayId(dto.getPayId(),sumRefundFee);
		}
		
	}

	private Map<String, Object> roadPayRefundByAlipay(refundDto dto) throws JsonProcessingException {
		//获得初始化的AlipayClient
				AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","2021002116635517","MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDgrTZKCortL5ye3LVe3XhT/2FNVkeQZuMSKUbjJa8sFVyrASg0qTKpHNfNQ+WNSArFxLXUSJ6jnJnwKy8OBVuz7zgpTYfUbInNuxAussbOGvMwSzSF/ATWXJwPTjNj4atOcIvMwUG+33d9qBKa1V2mZz2y6XnkMKzLAFYPfFVAGM9mc37dN3gWd/wzV3n8D2SUQtOhPDqtcpiK4rHtUNp09b+ZQmCrTIXClhOaqAuAxzLJQ1+O0SOg48/Ta00RSoBRrDjn5ufrwDKpSArVF68WSmgQhlHzh/UO1ODY0a2XIDZD4BwNVlDF5sHpbJmvo53eJkFO9OZzsCdE7ixzQ1FhAgMBAAECggEBAIWEKbkCXuDo5Gg7vMStVC6Gmyo8VbbojZRjVy7xXBIUd8dfnqb8zZBWBaCD8sxsvYodhfirVyrfi1OANixc3swlIvjYjBmejp3lxo4Zy677sP1IE+RzieRhkJQ/4qY+m3C3zTxWtF1bq0TPNo8wsMCMpcvGl5sVmfnhL0NwnbDCcCPfLsRJmmeAVWTZZr6wIcXSMJ2UD5Rxjt3hZDs0H/e1MtPOY+AfUsZpCQg6IMlDovU7ceB8FxjWUTNM5lTyUzwiXY/stSZcQhPdJpDyxz1Tw4ifDku4rC5HGOL8NuisI5u/nDfoRWzEKPEAQh4hmBsGEAApQ4n2bMS5d7qrV6kCgYEA/mOxJA6RAkL9rmuqrngaYMpcG74+WFxCz10IVzV4JVcHqGSv3NhvMgE3m+usXHHRa0h/4usAlVrVJD/s2SampRrfVwf4mjxqL+9OSMW+CaSNWwaT5yBLMR/SPm+wATl1cfqE99Bg17BrHdCcw7sOUdS3E3OKtpMRYl55jG3amCMCgYEA4hlcwUzTuALQKDnfYpEtNnEtJ2Vi8i55ALM4t/LVBM/A+1dtuGExXcLypPOSzuqKr+z+zy/I4YlzG8iDRE0g7hbbb/jroK8unwKZ+HItEMi1YYPgQY2vvpcGsGJ/nkRhLzhc4+ccD+SHSmeNC26zurm9WqU66h1RQMwsoeMTpqsCgYA6ZxzWEFoOUN742lMXNw+nRQ4ceHLLynK0NMGjc+0fdXmWmXcelroAmvLvYw3xM1hG7yAdMqKxp2bjqaFwwUTlBuRbkMLsdg5S7vfkETHt+M9TJJF0MJ/SqWGVYALiZRSnlEJOupJhMmmFMM2syhchhlp5pr8j52PIV7REHzxeTQKBgHWQVH+aUBhFNjFEblgVPtopHR54BGwDKB3qEtrrJEJ9OE44lq+hsgab4IlOsY5vvC9f3tqicSR80OKob0Drjeze67kcSIDjti0uMBtUWwR4KIAND2Vmo/RJGUxL2ynTSmlAOGS0fg3xeTuB/n/sTv4PqFriwXCMT/yObcK7jTNDAoGBAOlrRAljuL67pdrsNDrELhDaULXP2kmuMup5iSmU5QN2i+6M77Hq4gmUuNPrHMNAwFhhCq/FTViWZYh3LgM2/l323ppbXIqsiqLXtbq5Ff+nee50RiwN1sT7KMDv00BBqLOjuDeQeOeD57RXVflgbMdnKaEZKupuFVyOQmKIREZF","json","UTF-8","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqliWU4ndPe3KkRlQe85h2SDNb2l9UbPeaNmZZaced7Oc6T896WyZzB6+42gMQOOrRPWZui7ZCGmT8wzjTwFGXb9Zb/qmzXmV6nnQpz6NXCOJBL9VyKLm/EYjA0A5mwDwBD75trMXydcGWOdcQCuPi9HrH2YSJ9QUK2KzdCKQ3XMahhdtyjQcFxMXooVhPvnGi+4LgmsOJZU45cfZLnItHCPWDqRVrsoOjiNHHGVdUOxdKTor0suIE1W/IMfP0ITmMe0H1L+lItd+FBjDGfP2sd8R8phgrNi6b11Jg7ZXXCzsC9tkXn84lyKbWknybISYU9Sy1Lr9e/XgF9uX1jU76wIDAQAB","RSA2");
		        //设置请求参数
				AlipayTradeRefundRequest  request2 = new AlipayTradeRefundRequest ();

		        JSONObject json=new JSONObject();
		        //订单号
		        json.put("out_trade_no",dto.getOutRefundNo());
		        
		        json.put("trade_no",dto.getTransactionId());
		        //金额 这里的金额是以元为单位的可以不转换但必须是字符串
		        json.put("refund_amount",dto.getRefundFee()/100.0f);
		        //描述
		        json.put("refund_reason",dto.getRefundReason());
		        //部分退款
		        int i=(int)(Math.random()*900)+100;
		        String random= String.valueOf(i);
		        json.put("out_request_no",dto.getOutRefundNo()+random);
		        //对象转化为json字符串
		        String jsonStr=json.toString();
		        //商户通过该接口进行交易的创建下单
		        request2.setBizContent(jsonStr);
		        try {
		        	String refundjson=new ObjectMapper().writeValueAsString(dto);//关键
		        	Map<String, Object>returnMap=new HashMap<String, Object>();
		        	AlipayTradeRefundResponse  response = alipayClient.execute(request2);
		            if(response.isSuccess()){
		            	Map<String, Object> alipayMap=new HashMap<String, Object>();
		            	alipayMap.put("resultCode", "SUCCESS");
		            	alipayMap.put("logTxt", response.getBody());
		            	alipayMap.put("refundId", response.getTradeNo());
		            	log.info(response.getBody());
		            	//businessservice.updatePayrefundStatus(dto.getOutRefundNo(),response.getTradeNo());
		            	wechatUsermapper.insertApiLogs("支付宝路边泊位退款接口",refundjson, "success", response.getTradeNo());
		            	return alipayMap;
		            }else{
		            	wechatUsermapper.insertApiLogs("支付宝退款接口",refundjson,"fail", response.getSubCode()+"_"+response.getSubMsg());
		            	throw new BaseServiceException(
								StatusCode.API_FREQUENTLY_ERROR.getCode(),response.getSubCode()+"_"
										+ response.getSubMsg()
								);
		            }
		        } catch (AlipayApiException e) {
		            e.printStackTrace();
		        }
				return null;
				
	}

	

	

	
}
