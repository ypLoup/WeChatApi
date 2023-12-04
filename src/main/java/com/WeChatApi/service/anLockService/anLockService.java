package com.WeChatApi.service.anLockService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;


import com.WeChatApi.controller.base.BaseServiceException;
import com.WeChatApi.controller.base.StatusCode;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Transactional
@Service
public class anLockService {
	
	  //锟斤拷录锟接匡拷
	 private String loginGetPath="https://yun.an-lock.com/api/userlogin";
	 //锟斤拷取锟剿猴拷锟斤拷锟斤拷锟斤拷锟脚碉拷锟叫憋拷
	 private String getStoreList ="https://yun.an-lock.com/api/netlock/getStoreList";
	 //锟斤拷取锟脚碉拷锟斤拷锟斤拷锟斤拷锟斤拷锟借备
	 private String getDeviceList = "https://yun.an-lock.com/api/netlock/getDeviceList";
	 //锟斤拷取锟脚店单锟斤拷锟斤拷锟斤拷锟借备
	 private String getDevice = "";
	 //锟斤拷取锟斤拷锟斤拷锟借备钥锟斤拷锟叫憋拷
	 private String getDeviceKeys = "";
	 //锟斤拷取锟斤拷锟斤拷指锟斤拷keyid钥锟阶讹拷锟斤拷
	 private String getDeviceKey = "";
	 //锟斤拷指锟斤拷锟斤拷锟斤拷锟斤拷锟皆匡拷锟�
	 private String addNetLockKey = "";
	 //锟斤拷锟斤拷钥锟斤拷
	 private String freezeNetLockKey = "";
	 //锟睫革拷钥锟阶匡拷锟斤拷时锟戒范围
	 private String reletNetLockKey = "";
	 //删锟斤拷指锟斤拷锟斤拷锟斤拷钥锟斤拷
	 private String deleteNetLockKey = "";
	 //删锟斤拷指锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷钥锟斤拷
	 private String deleteNetLockKeyType = "";
	 //锟睫革拷钥锟斤拷锟斤拷锟斤拷
	 private String setKeyPwd ="";
	 //删锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷钥锟斤拷(锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷员锟斤拷锟斤拷)
	 private String cleanNetLockKey = "";
	 //锟斤拷锟矫管硷拷锟斤拷锟斤拷锟斤拷锟斤拷
	 private String setManagerPwd = "";
	 //锟斤拷取指锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷志
	 private String getNetLockLogList = "";
	 //锟斤拷锟斤拷锟斤拷锟斤拷璞�
	 private String addDevice="";
	 //删锟斤拷锟斤拷锟斤拷锟借备
	 private String  deleteDevice  ="";
	 
	// 
	 private String getInfo="http://open.sennor.net:8088/device/getDeviceInfo?secretKey=D515A9CD19D846F2&deviceNumber=143B93E7B7E9";
	 
	 /**
	  * 锟斤拷取锟斤拷录token
	  * @param username
	  * @param password
	  * @return
	 * @throws IOException 
	 * @throws HttpException 
	  */

	 public String loginGetToken(String username, String password) throws HttpException, IOException {
		
		/*String path=loginGetPath;
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(path); 
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30000).setConnectionRequestTimeout(30000).setSocketTimeout(30000).build();
		httppost.setConfig(requestConfig);
		httppost.setHeader("Content-type","application/x-www-form-urlencoded; charset=utf-8") ;
		//httppost.setHeader("Accept", "application/json");
		//httppost.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)");
		//锟斤拷锟秸诧拷锟斤拷
		Map<String,Object> param=new HashMap<String, Object>();
		param.put("grant_type","password");
		param.put("username",username);
		param.put("password",password);
		String paramJsonStr = JSON.toJSONString(param);
		if(param != null){
			httppost.setEntity(new StringEntity(paramJsonStr, Charset.forName("UTF-8")));
		}
		HttpResponse response = null;
		try {
			response= httpclient.execute(httppost);
			HttpEntity httpEntity=response.getEntity();
			String result = EntityUtils.toString(httpEntity);
			return result;
		} catch (Exception e) {
			// TODO: handle exception
			return "锟斤拷锟斤拷失锟杰ｏ拷";
		}*/
		 if(StringUtils.isBlank(username)){
			 throw new BaseServiceException(
						StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"锟皆诧拷锟斤拷锟矫伙拷锟剿号诧拷锟斤拷为锟秸ｏ拷");
		 }
         if(StringUtils.isBlank(username)){
        	 throw new BaseServiceException(
						StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"锟皆诧拷锟斤拷锟矫伙拷锟斤拷锟诫不锟斤拷为锟秸ｏ拷");
		 }
		    PostMethod postMethod = null;
		    postMethod = new PostMethod(loginGetPath) ;
		    postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;
		//锟斤拷锟斤拷锟斤拷锟矫ｏ拷锟斤拷要注锟斤拷木锟斤拷锟斤拷锟竭诧拷锟杰达拷NULL锟斤拷要锟斤拷锟斤拷锟街凤拷锟斤拷
		    NameValuePair[] data = {
		            new NameValuePair("grant_type","password"),
		            new NameValuePair("username",username),
		            new NameValuePair("password",password)
		            
		    };

		    postMethod.setRequestBody(data);

		    HttpClient httpClient = new HttpClient();
		    int response = httpClient.executeMethod(postMethod); // 执锟斤拷POST锟斤拷锟斤拷
		    if(response==200){
		    	return postMethod.getResponseBodyAsString() ;
		    }else{
		    	return "锟接口碉拷锟斤拷失锟杰ｏ拷";
		    }
		    
		 
	 }

	 
	 /**
	  * 锟斤拷取锟剿猴拷锟斤拷锟斤拷锟斤拷锟脚碉拷锟叫憋拷
	  * @param tokenKey
	  * @return
	  * @throws HttpException
	  * @throws IOException
	  */
	public String getAnLockStoreLits(String tokenKey) throws HttpException, IOException {
		
		if(StringUtils.isBlank(tokenKey)){
			throw new BaseServiceException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"锟皆诧拷锟斤拷tokenKey锟斤拷锟斤拷为锟秸ｏ拷");
		}
		GetMethod getMethod=null;
		getMethod =  new GetMethod(getStoreList);
		getMethod.addRequestHeader("Authorization", "Bearer "+tokenKey);
		getMethod.addRequestHeader("Content-Type","Content-Type:application/json");
		HttpClient httpClient = new HttpClient();
		int response =httpClient.executeMethod(getMethod);
		if(response==200){
	    	return getMethod.getResponseBodyAsString() ;
	    }else{
	    	return "锟接口碉拷锟斤拷失锟杰ｏ拷";
	    }
		
	}


	public String getAnLockDeviceList(String tokenKey, String warning, String storeid) {
		
		if(StringUtils.isBlank(tokenKey)){
			throw new BaseServiceException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"锟皆诧拷锟斤拷tokenKey锟斤拷锟斤拷为锟秸ｏ拷");
		}
		GetMethod getMethod=null;
		//getMethod
		
		return null;
	}


	public Map<String, String> getInfo() throws HttpException, IOException {
		GetMethod getMethod=null;
		getMethod =  new GetMethod(getInfo);
		//getMethod.addRequestHeader("Authorization", "Bearer "+tokenKey);
		getMethod.addRequestHeader("Content-Type","Content-Type:application/json");
		HttpClient httpClient = new HttpClient();
		int response =httpClient.executeMethod(getMethod);
		if(response==200){
			Map<String, String> returnMap=new HashMap<>();
			String data =getMethod.getResponseBodyAsString();
			JSONObject  dataJson = JSONObject.fromObject(data);
            String array = dataJson.getString("data");
            JSONObject  dataJson2 = JSONObject.fromObject(array);
            System.out.println(dataJson2.getString("time"));
            returnMap.put("time", dataJson2.getString("time"));
            returnMap.put("onLineState", dataJson2.getString("onLineState"));
            String type[]=dataJson2.getString("type").split("/");
            String data2[]=dataJson2.getString("data").split("\\|");
            returnMap.put("type1", type[0]);
            returnMap.put("type2", type[1]);
            returnMap.put("type3", type[2]);
            returnMap.put("data21", data2[0]);
            returnMap.put("data22", data2[1]);
            returnMap.put("data23", data2[2]);
	    	return returnMap;
	    }else{
	    	return null;
	    }
	}
	
	

}
