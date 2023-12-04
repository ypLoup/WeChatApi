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
 * @Description: 获取参数时对参数进行XSS判断预防
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
                //解析json对象
                boolean b = resolveJSONObjectObj(requestPostStr);
                if (!b) return;
            } else if (requestPostStr.startsWith("[")) {
                //把数据转换成json数组
            	ObjectMapper objectMapper = new ObjectMapper();
            	JsonNode jsonNode = objectMapper.readTree(requestPostStr);
            	ArrayNode jsonArray = (ArrayNode) jsonNode;
                //JSONArray jsonArray = JSONArray.parseArray(requestPostStr);
                jsonArray.forEach(json -> {
                    //解析json对象
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
                //校验参数值是否合法
                String[] value = entry.getValue();
                for (String s : value) {
                    //校验参数值是否合法
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
     * 对JSONObject对象进行递归参数解析
     * @param requestPostStr
     * @return
     * @throws IOException 
     * @throws JsonProcessingException 
     */
    private boolean resolveJSONObjectObj(String requestPostStr) throws JsonProcessingException, IOException {
        boolean isover = true;
        // 创建需要处理的json对象
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(requestPostStr);
        String jsonString = jsonNode.toString();
        JSONObject jsonObject = new JSONObject(jsonString);
        //JSONObject jsonObject = JSONObject.parseObject(requestPostStr);
        // 获取所有的参数key
        Set<String> keys = jsonObject.keySet();
        if (keys.size() > 0) {
            for (String key : keys) {
                //获取参数名称
                String value;
                if (jsonObject.get(key) != null) {
                    value = String.valueOf(jsonObject.get(key));
                    //当value为数组时
                    if(value.startsWith("[")){
                        //把数据转换成json数组
                    	ObjectMapper objectMapper2 = new ObjectMapper();
                    	JsonNode jsonNode2 = objectMapper2.readTree(value);
                    	ArrayNode jsonArray = (ArrayNode) jsonNode2;
                        //JSONArray jsonArray = JSONArray.parseArray(value);
                        for (Object o : jsonArray) {
                            //解析json对象
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
                        //校验参数值是否合法
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
     * 校验参数非法字符
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

