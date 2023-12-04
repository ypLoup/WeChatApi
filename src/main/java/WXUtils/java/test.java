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

        //关闭流
        in.close();

        return m;
    }

    private static  InputStream String2Inputstream(String str) {
        return new ByteArrayInputStream(str.getBytes());
    }

    /**
     * 获取子结点的xml
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
            // 直接引用远程的wsdl文件  
            // 以下都是套路  
            Service service = new Service();  
            Call call = (Call) service.createCall();  
            call.setTargetEndpointAddress(endpoint);  
            call.setOperationName("addUser");// WSDL里面描述的接口名称  
            call.addParameter("userName",  
                    org.apache.axis.encoding.XMLType.XSD_DATE,  
                    javax.xml.rpc.ParameterMode.IN);// 接口的参数  
            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);// 设置返回类型  
            String temp = "测试人员";  
            String result = (String) call.invoke(new Object[] { temp });  
            // 给方法传递参数，并且调用方法  
            System.out.println("result is " + result);  
        } catch (Exception e) {  
            System.err.println(e.toString());  
        }  
    }  */
    } 
	
	 

