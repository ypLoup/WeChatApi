package com.WeChatApi.controller.base;

import java.io.ByteArrayInputStream;

/*import org.apache.soap.util.xml.*;  
import org.apache.soap.*;  
import org.apache.soap.rpc.*;  */
  
import java.io.*;  
import java.net.*;  
import java.util.Vector; 
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.WeChatApi.bean.dto.TemplateData;
import com.WeChatApi.bean.dto.refundDto;
import com.WeChatApi.bean.dto.userRechargeRecordDto;
import com.WeChatApi.bean.dto.wxMsgDto;
import com.WeChatApi.bean.models.UnifiedOrderRequestBean;
import com.WeChatApi.bean.models.wechatUser;
import com.WeChatApi.service.wechatApiService.wechatApiService;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;

import cn.com.eagle.sdk.base.INetTools;
import cn.com.eagle.sdk.bean.NetReqConfBean;
import cn.com.eagle.sdk.bean.OipReqBean;
import cn.com.eagle.sdk.bean.OipRspBean;
import cn.com.eagle.sdk.net.factory.NetToolsHttpFactory;

public class test {
	
	@Autowired
	private  wechatApiService wechatApiservice;
	
	@Autowired
	private static ResourceBundle res = ResourceBundle.getBundle("rccApi");
	
	private static String aa= res.getString("wechat_mch_name");
	
	private static Logger logger = LoggerFactory.getLogger(test.class);
	  public static HashMap<String, Socket> socketList = new HashMap<>();
	    public static String channelToken;  //socket ����
	    private static BufferedReader bufferedReader;


	
	
	@SuppressWarnings("rawtypes")
	public static Map<String,String> doXMLParse(String strxml) throws Exception {
        if(null == strxml || "".equals(strxml)) {
            return null;
        }

        Map<String,String> m = new HashMap<String,String>();
        InputStream in = String2Inputstream(strxml);
        SAXReader builder = new SAXReader();
        Document document = builder.read(in);
        Element root = document.getRootElement();
        List list = root.elements();
        Iterator it = list.iterator();
        while(it.hasNext()) {
            Element e = (Element) it.next();
            String k = e.getName();
            String v = "";
            List children = e.elements();
            if(children.isEmpty()) {
                v = e.getTextTrim();
            } else {
                v = getChildrenText(children);
            }

            m.put(k, v);
        }

        //�ر���
        in.close();

        return m;
    }

    private static  InputStream String2Inputstream(String str) {
        return new ByteArrayInputStream(str.getBytes());
    }

    /**
     * ��ȡ�ӽ���xml
     * @param children
     * @return String
     */
    @SuppressWarnings("rawtypes")
    private static String getChildrenText(List children) {
        StringBuffer sb = new StringBuffer();
        if(!children.isEmpty()) {
            Iterator it = children.iterator();
            while(it.hasNext()) {
                Element e = (Element) it.next();
                String name = e.getName();
                String value = e.getTextTrim();
                List list = e.elements();
                sb.append("<" + name + ">");
                if(!list.isEmpty()) {
                    sb.append(getChildrenText(list));
                }
                sb.append(value);
                sb.append("</" + name + ">");
            }
        }

        return sb.toString();
    }
	
    
    /*public static String getService(String user) {  
    	try {  
            String endpoint = "http://localhost:8080/ca3/services/caSynrochnized?wsdl";  
            // ֱ������Զ�̵�wsdl�ļ�  
            // ���¶�����·  
            Service service = new Service();  
            Call call = (Call) service.createCall();  
            call.setTargetEndpointAddress(endpoint);  
            call.setOperationName("addUser");// WSDL���������Ľӿ�����  
            call.addParameter("userName",  
                    org.apache.axis.encoding.XMLType.XSD_DATE,  
                    javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���  
            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);// ���÷�������  
            String temp = "������Ա";  
            String result = (String) call.invoke(new Object[] { temp });  
            // ���������ݲ��������ҵ��÷���  
            System.out.println("result is " + result);  
        } catch (Exception e) {  
            System.err.println(e.toString());  
        }  
    }  */
    
    
    public static void main(String[] args) throws Exception {
    	/*String  APP_ACCESS_TOKEN_URL= "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx606d7fcc6d1402c9"+"&secret=42da2ad4c7bfad7524559abb47267973";
    
    	RestTemplate restTemplate = new RestTemplate();
    	JsonNode accessTokenJson = wxGetTokenUtil.doGetJson(APP_ACCESS_TOKEN_URL);
        String accessToken  = accessTokenJson.get("access_token").asText();
        //������������ÿ�ζ���ȡ���µ�access_token��ʱ�俪���У�Ӧ����access_token�����ʱ�����»�ȡ��
        String url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + accessToken;
        //ƴ�����͵�ģ��
        wxMsgDto wxMsgVo = new wxMsgDto();
        wxMsgVo.setTouser("o3J0E5VxSlIKw1sdwafQg_YlHbz4");//�û���openid��Ҫ���͸��Ǹ��û���
        wxMsgVo.setTemplate_id("bBrNZPigCPV36FPPVTsUJu4_FfE0zO2lDAhonkXnJSg");//������Ϣģ��id
        wxMsgVo.setPage("pages/index/index");*/
    	/*refundDto a= new refundDto();
        Map<String, String> m = new HashMap<>(3);
        m.put("car_number1", "˫ɫ��");
        m.put("thing4", "");
        a.setOrderType("1");
        System.out.println(JSON.toJSONString(a)); 
        System.out.println(new ObjectMapper().writeValueAsString(a)); */
        /*wxMsgVo.setData(m);
        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity(url, wxMsgVo, String.class);
        logger.info(responseEntity.getBody());*/
    	String jsonString = "{\"wcPayData\":{\"timeStamp\":\"1695177357\",\"codeUrl\":null,\"package\":\"prepay_id=wx20103557040626313e417169c5f2700000\",\"paySign\":\"bOCzAy3Dpz3z1MPJzrqDmwUEjcU96Ilc8G3jG+Q+fjdLDq3V9tLO3t94PoFWdcMOTe7kxIrVVRERhFR6i2LUsSgCAJ3+SfJ0MdU3sc6CH/7lQpAzaOHjTfvclBhppegGgioLeaMq14gjkkXs6JXJFvQrz7YMKH/MYI8obqw++dOKPID8/5ul/dzMiVvOg637HfRIFpWzX6ORSoZY2M6n/k1jRcLjU5d9tBOEfSk/2w8hPWt9b5gw534M5ojxiKZNd3sJCLT2cRfbdWRRAvXrVbwGUqE+1ez8jrvjT2Jr5Zsr87mDw6QWfGPPgwhqR5EzZRyKOB227JayaPD9nBKCLg==\",\"appId\":\"wx606d7fcc6d1402c9\",\"signType\":\"RSA\",\"partnerId\":null,\"mwebUrl\":null,\"nonceStr\":\"2e13af24f00940d7ae961575fadbc664\"},\"orderId\":\"S1202309200086588267\",\"bizId\":\"2023092009374510600171895\",\"prepayId\":\"wx20103557040626313e417169c5f2700000\"}";

        try {
            // ����һ��ObjectMapper�������ڴ���JSON
            ObjectMapper objectMapper = new ObjectMapper();

            // ��JSON�ַ�������ΪMap
            Map<String, Object> jsonMap = objectMapper.readValue(jsonString, Map.class);

            // ��Map�л�ȡ "wcPayData" ��ֵ
            Map<String, Object> wcPayDataMap = (Map<String, Object>) jsonMap.get("wcPayData");

            // ��ӡ "wcPayData" ��Map
            System.out.println(wcPayDataMap);
            
            
            Map bizData = new ObjectMapper().readValue(jsonString, Map.class);
			//bizData.get("wcPayData").toString();
			Map wxData = new ObjectMapper().readValue(bizData.get("wcPayData").toString(), Map.class);
			System.out.println(wxData);
            // ����������ʹ�� wcPayDataMap ������ "wcPayData" �ڵ�����
        } catch (IOException e) {
            e.printStackTrace();
        }
    	
    	
    }

	private static String getToken() {
		
		return null;
	}
    } 
	
	 

