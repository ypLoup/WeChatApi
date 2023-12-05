package WXUtils.java;

import java.io.ByteArrayInputStream;

/*import org.apache.soap.util.xml.*;  
import org.apache.soap.*;  
import org.apache.soap.rpc.*;  */
  
import java.io.*;  
import java.net.*;  
import java.util.Vector; 
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;

import com.WeChatApi.service.wechatApiService.wechatApiService;

public class test {
	
	@Autowired
	private  wechatApiService wechatApiservice;

	public static void main(String[] args) throws Exception {
		String a = "2021-02-25 20:28:53".replaceAll("-","").replaceAll(":", "").replaceAll(" ", "");
		
		System.out.println(a);
		
	}
	
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
    } 
	
	 

