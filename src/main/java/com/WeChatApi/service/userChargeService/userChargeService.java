package com.WeChatApi.service.userChargeService;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
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
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.WeChatApi.controller.base.BaseException;
import com.WeChatApi.controller.base.BaseServiceException;
import com.WeChatApi.controller.base.CommonUtil;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.dao.invoiceMapper;
import com.WeChatApi.dao.monthlyCarMapper;
import com.WeChatApi.dao.userChargeMapper;
import com.WeChatApi.dao.userInfoMapper;
import com.WeChatApi.dao.wechatUserMapper;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.response.AlipayTradeCreateResponse;

import net.sf.json.JSONObject;

import com.WeChatApi.bean.condition.userInfoCondition;
import com.WeChatApi.bean.dto.doInvoiceDto;
import com.WeChatApi.bean.dto.operationSubscriptionRecordDto;
import com.WeChatApi.bean.dto.prePayDto2;
import com.WeChatApi.bean.models.invoice;
import com.WeChatApi.bean.models.redPack;
import com.WeChatApi.bean.models.userInfo;
import com.WeChatApi.bean.models.wechatUser;

import sun.misc.BASE64Decoder;

@Service
@Transactional
public class userChargeService {
	
	private static Logger log = Logger.getLogger(redPack.class.getName());

	
	@Autowired
	private wechatUserMapper usermapper;
	
	@Autowired
	private userChargeMapper  userchargemapper;
	
	@Autowired
	private invoiceMapper invoicemapper;

	public List<Map<String, String>> findUserChargeRecordInfo(Map<String, String> findTypeMap) {
		
		if(StringUtils.isBlank(findTypeMap.get("userOpenId").toString())){
			throw new BaseException(StatusCode.MISSING_OPENID_ERROR.getCode(),"小程序userOpenId不能为空！");
		}
		wechatUser user =usermapper.findWechatUserInfoByOpenId(findTypeMap.get("userOpenId").toString());
		if(findTypeMap.containsKey("fpUrl")){
			if(StringUtils.isNotBlank(findTypeMap.get("fpUrl").toString())){
			return userchargemapper.findUserChargeRecordInfoByUserId_fpUrlIsNotNull(user.getUserId());
			}else{
				return userchargemapper.findUserChargeRecordInfoByUserId(user.getUserId());
			}
		}else{
			return userchargemapper.findUserChargeRecordInfoByUserId(user.getUserId());
		}
		
	}

	
	
	
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
				List<String> rIdList = new ArrayList<>();
				for(String outTradeNo : invoiceDto.getrIds().toString().split(",")){
				
					rIdList.add(outTradeNo);
				}
				Map<String, Object> sumPriceMap = new HashMap<>();
				if(rIdList.size()>0){
					sumPriceMap = userchargemapper.getSumUserChargeBalancesByRIdList(rIdList);
					//sumPriceMap.put("vehPlate", invoiceDto.getVehPlate());
					this.doInvoiceUserCharge(rIdList,invoice2,sumPriceMap);
				}
				
				
				invoicemapper.addInvoiceInfo(invoice2);
			}else{
				List<String> rIdList = new ArrayList<>();
				for(String outTradeNo : invoiceDto.getrIds().toString().split(",")){
					
					rIdList.add(outTradeNo);
				}
				Map<String, Object> sumPriceMap = new HashMap<>();
				if(rIdList.size()>0){
					
					sumPriceMap = userchargemapper.getSumUserChargeBalancesByRIdList(rIdList);
					//sumPriceMap.put("vehPlate", invoiceDto.getVehPlate());
					this.doInvoiceUserCharge(rIdList,invoice,sumPriceMap);
				}
				
			}
			
			
			
			
		
	}
	
	
	
	
	private void doInvoiceUserCharge(List<String> outTradeNoList, invoice invoice2, Map<String, Object> sumPriceMap) throws IOException {
		
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
	            new NameValuePair("remark","预充值订单号："+sumPriceMap.get("recordId").toString())
	            
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
            	usermapper.insertApiLogs("预充值开票接口", "订单号:"+outTradeNoList.toString()+";税号:"+invoice2.getTaxNumber()+";开票金额:"+sumPriceMap, "success", "发票地址:"+dataJsonR.getString("pdfUrl"));
            	userchargemapper.updateUserChargeRecordByList(outTradeNoList,dataJsonR.getString("pdfUrl"),invoice2.getCompanyName(),invoice2.getTaxNumber(),invoice2.getAddressMobile(),invoice2.getBankNumber(),invoice2.getEmail());
            	

            }else{
            	log.info("开票号："+outTradeNoList.toString()+"开票失败");

            	usermapper.insertApiLogs("预充值开票接口", "订单号:"+outTradeNoList.toString()+";税号:"+invoice2.getTaxNumber()+";开票金额:"+sumPriceMap, "fail", dataJson.getString("error_msg"));
            	throw new BaseServiceException(
            			Integer.parseInt(errorCode),
            			dataJson.getString("error_msg"));
            }

	    }else{
	    	throw new BaseServiceException(
					StatusCode.API_FREQUENTLY_ERROR.getCode(),
					"请查看开票接口");
	    }
		}finally {
			postMethod.releaseConnection();
		}
		
	}
	

}
