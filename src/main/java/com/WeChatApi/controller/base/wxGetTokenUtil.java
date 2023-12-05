package com.WeChatApi.controller.base;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
public class wxGetTokenUtil {

	    public static JsonNode doGetJson(String URL) throws IOException {
	    	JsonNode  jsonObject = null;
	        HttpURLConnection conn = null;
	        InputStream is = null;
	        BufferedReader br = null;
	        StringBuilder result = new StringBuilder();
	        try {
	            //����Զ��url���Ӷ���
	            URL url = new URL(URL);
	            //ͨ��Զ��url���Ӷ����һ�����ӣ�ǿת��HTTPURLConnection��
	            conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("GET");
	            //�������ӳ�ʱʱ��Ͷ�ȡ��ʱʱ��
	            conn.setConnectTimeout(15000);
	            conn.setReadTimeout(60000);
	            conn.setRequestProperty("Accept", "application/json");
	            //��������
	            conn.connect();
	            //ͨ��connȡ������������ʹ��Reader��ȡ
	            if (200 == conn.getResponseCode()) {
	                is = conn.getInputStream();
	                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	                String line;
	                while ((line = br.readLine()) != null) {
	                    result.append(line);
	                    System.out.println(line);
	                }
	            } else {
	                System.out.println("ResponseCode is an error code:" + conn.getResponseCode());
	            }
	        } catch (MalformedURLException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                if (br != null) {
	                    br.close();
	                }
	                if (is != null) {
	                    is.close();
	                }
	            } catch (IOException ioe) {
	                ioe.printStackTrace();
	            }
	            conn.disconnect();
	        }
	        ObjectMapper objectMapper = new ObjectMapper();
	        jsonObject = objectMapper.readTree(result.toString());
	        return jsonObject;
	    }
	

}
