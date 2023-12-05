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
	
	  //��¼�ӿ�
	 private String loginGetPath="https://yun.an-lock.com/api/userlogin";
	 //��ȡ�˺��������ŵ��б�
	 private String getStoreList ="https://yun.an-lock.com/api/netlock/getStoreList";
	 //��ȡ�ŵ����������豸
	 private String getDeviceList = "https://yun.an-lock.com/api/netlock/getDeviceList";
	 //��ȡ�ŵ굥�������豸
	 private String getDevice = "";
	 //��ȡ�����豸Կ���б�
	 private String getDeviceKeys = "";
	 //��ȡ����ָ��keyidԿ�׶���
	 private String getDeviceKey = "";
	 //��ָ���������Կ��?
	 private String addNetLockKey = "";
	 //����Կ��
	 private String freezeNetLockKey = "";
	 //�޸�Կ�׿���ʱ�䷶Χ
	 private String reletNetLockKey = "";
	 //ɾ��ָ������Կ��
	 private String deleteNetLockKey = "";
	 //ɾ��ָ����������Կ��
	 private String deleteNetLockKeyType = "";
	 //�޸�Կ������
	 private String setKeyPwd ="";
	 //ɾ����������Կ��(����������Ա����)
	 private String cleanNetLockKey = "";
	 //���ùܼ���������
	 private String setManagerPwd = "";
	 //��ȡָ������������־
	 private String getNetLockLogList = "";
	 //���������?
	 private String addDevice="";
	 //ɾ�������豸
	 private String  deleteDevice  ="";
	 
	// 
	 private String getInfo="http://open.sennor.net:8088/device/getDeviceInfo?secretKey=D515A9CD19D846F2&deviceNumber=143B93E7B7E9";
	 
	 /**
	  * ��ȡ��¼token
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
		//���ղ���
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
			return "����ʧ�ܣ�";
		}*/
		 if(StringUtils.isBlank(username)){
			 throw new BaseServiceException(
						StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"�Բ����û��˺Ų���Ϊ�գ�");
		 }
         if(StringUtils.isBlank(username)){
        	 throw new BaseServiceException(
						StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"�Բ����û����벻��Ϊ�գ�");
		 }
		    PostMethod postMethod = null;
		    postMethod = new PostMethod(loginGetPath) ;
		    postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;
		//�������ã���Ҫע��ľ�����߲��ܴ�NULL��Ҫ�����ַ���
		    NameValuePair[] data = {
		            new NameValuePair("grant_type","password"),
		            new NameValuePair("username",username),
		            new NameValuePair("password",password)
		            
		    };

		    postMethod.setRequestBody(data);

		    HttpClient httpClient = new HttpClient();
		    int response = httpClient.executeMethod(postMethod); // ִ��POST����
		    if(response==200){
		    	return postMethod.getResponseBodyAsString() ;
		    }else{
		    	return "�ӿڵ���ʧ�ܣ�";
		    }
		    
		 
	 }

	 
	 /**
	  * ��ȡ�˺��������ŵ��б�
	  * @param tokenKey
	  * @return
	  * @throws HttpException
	  * @throws IOException
	  */
	public String getAnLockStoreLits(String tokenKey) throws HttpException, IOException {
		
		if(StringUtils.isBlank(tokenKey)){
			throw new BaseServiceException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"�Բ���tokenKey����Ϊ�գ�");
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
	    	return "�ӿڵ���ʧ�ܣ�";
	    }
		
	}


	public String getAnLockDeviceList(String tokenKey, String warning, String storeid) {
		
		if(StringUtils.isBlank(tokenKey)){
			throw new BaseServiceException(StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"�Բ���tokenKey����Ϊ�գ�");
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
