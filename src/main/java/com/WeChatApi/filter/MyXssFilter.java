package com.WeChatApi.filter;

import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
 
/**
 * @Description: ��ȡ����ʱ�Բ�������XSS�ж�Ԥ��
 * @Author boss yan
 * @Date 2018/10/23 13:39
 */
@WebFilter(filterName="xssMyfilter",urlPatterns="/*")
public class MyXssFilter implements Filter {

	@Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String contentType = request.getContentType();
        BodyReaderRequestWrapper wrapper = null;
        if ("application/json".equals(contentType)) {
            wrapper = new BodyReaderRequestWrapper(request);
            String requestPostStr = wrapper.getBody();
            if (requestPostStr.startsWith("{")) {
                //����json����
                boolean b = resolveJSONObjectObj(requestPostStr);
                if (!b) return;
            } else if (requestPostStr.startsWith("[")) {
                //������ת����json����
            	ObjectMapper objectMapper = new ObjectMapper();
            	JsonNode jsonNode = objectMapper.readTree(requestPostStr);
            	ArrayNode jsonArray = (ArrayNode) jsonNode;
                //JSONArray jsonArray = JSONArray.parseArray(requestPostStr);
                jsonArray.forEach(json -> {
                    //����json����
                    boolean b= true;
					try {
						b = resolveJSONObjectObj(json.toString());
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    if (!b) return;
                });
            }
        } else {
            //application/x-www-form-urlencoded
            Map<String, String[]> parameterMap = request.getParameterMap();
            for (Map.Entry<String,String[]> entry : parameterMap.entrySet()) {
                //У�����ֵ�Ƿ�Ϸ�
                String[] value = entry.getValue();
                for (String s : value) {
                    //У�����ֵ�Ƿ�Ϸ�
                    boolean b = verifySql(s);
                    if (!b) {
                        return;
                    }
                }
            }
        }
        if (wrapper == null) {
            filterChain.doFilter(servletRequest,servletResponse);
        } else {
            filterChain.doFilter(wrapper,servletResponse);
        }
    }

    /**
     * ��JSONObject������еݹ��������
     * @param requestPostStr
     * @return
     * @throws IOException 
     * @throws JsonProcessingException 
     */
    private boolean resolveJSONObjectObj(String requestPostStr) throws JsonProcessingException, IOException {
        boolean isover = true;
        // ������Ҫ�����json����
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(requestPostStr);
        String jsonString = jsonNode.toString();
        JSONObject jsonObject = new JSONObject(jsonString);
        //JSONObject jsonObject = JSONObject.parseObject(requestPostStr);
        // ��ȡ���еĲ���key
        Set<String> keys = jsonObject.keySet();
        if (keys.size() > 0) {
            for (String key : keys) {
                //��ȡ��������
                String value;
                if (jsonObject.get(key) != null) {
                    value = String.valueOf(jsonObject.get(key));
                    //��valueΪ����ʱ
                    if(value.startsWith("[")){
                        //������ת����json����
                    	ObjectMapper objectMapper2 = new ObjectMapper();
                    	JsonNode jsonNode2 = objectMapper2.readTree(value);
                    	ArrayNode jsonArray = (ArrayNode) jsonNode2;
                        //JSONArray jsonArray = JSONArray.parseArray(value);
                        for (Object o : jsonArray) {
                            //����json����
                            boolean b = resolveJSONObjectObj(o.toString());
                            if (!b) {
                                isover = false;
                                break;
                            }
                        }
                    } else if (value.startsWith("{")) {
                        boolean b = resolveJSONObjectObj(value);
                        if (!b) {
                            isover = false;
                            break;
                        }
                    } else {
                        //У�����ֵ�Ƿ�Ϸ�
                        boolean b = verifySql(value);
                        if (!b) {
                            isover = false;
                            break;
                        }
                    }
                }
            }
        }
        return isover;
    }

    @Override
    public void destroy() {

    }

    /**
     * У������Ƿ��ַ�
     */
    public boolean verifySql(String parameter) {
        if (
                    parameter.toLowerCase().contains("select")
                    || parameter.toLowerCase().contains("update")
                    || parameter.toLowerCase().contains("delete")
                    || parameter.toLowerCase().contains("insert")
                    || parameter.toLowerCase().contains("truncate")
                    || parameter.toLowerCase().contains("substr")
                    || parameter.toLowerCase().contains("char")
                    || parameter.toLowerCase().contains("exec")
                    || parameter.toLowerCase().contains("master")
                    || parameter.toLowerCase().contains("drop")
                    || parameter.toLowerCase().contains("execute")
                    || parameter.toLowerCase().contains("xp_cmdshell")
                    //|| parameter.toLowerCase().contains("use")
                    || parameter.toLowerCase().contains("column_name")
                    || parameter.toLowerCase().contains("information_schema.columns")
                    || parameter.toLowerCase().contains("table_schema")
                    || parameter.toLowerCase().contains("union")
                    || parameter.toLowerCase().contains("#")
                    || parameter.toLowerCase().contains("$")
                    || parameter.toLowerCase().contains("%")
                    || parameter.toLowerCase().contains("http")
                    || parameter.toLowerCase().contains("https")
            ) {
                return false;
            } else {
                return true;
            }
    }

}

