package WXUtils.java;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.*;


import org.apache.tomcat.util.codec.binary.Base64;


public class WxDecodeUtil {
	
	/*private static String ALGORITHM = "AES";

    private static String ALGORITHM_MODE_PADDING = "AES/ECB/PKCS7Padding";

    private static String key="jgkj2020jgkj2020jgkj2020jgkj2020";
    
    private static SecretKeySpec secretKey = new SecretKeySpec(MD5Util.MD5Encode(key, "UTF-8").toLowerCase().getBytes(), ALGORITHM);



    public static String decryptData(String base64Data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64Util.decode(base64Data)));
    }
    
    public static void main(String[] args) throws Exception {

        String A = "aXiJfX3TyQfPuaeAaJMoBPEFi/WwiqYLmJfKC9Fh4WeN/mo7GE/zySobRNfn96ZdNAksGfQvtMHezGd0L3FZEBr3xbJfwrtRUa72xk81122PGDqLtk36T7PZh0MUOrTI+qXaiwtQ+gIsEIibk1RRvBY9kJ3NoFt0s1+/SHjLa8fbfL/LPaRl5PgAGBx6OwJ2so+/KLFfZGjDPXw/e98XOvnJL0pDjTE37skYqPlzhl6G98fnT0ldGb1086bkHwqYMqwumqCTbUwq9oZ/mv4ygvzppqUIpTOygdoiFVcIC1UZ3NcynIXAm2Y/e+/f+YUoMzwfiJ5ghcarxOb8yTKW6oATPow39b02AvhOBrvPfh22Tk+dsIhH9zSoJJuU9ccv6uDHGWA3QAAQ6eJBSoTrg5Y97kCQVfxO0Pi2q7n8xkp12RCybNqOKw/esjcGqEtnuGlygXz0D/Gbjx4sXzKr649gRl4dWAq4I689hthWjJDTY7OL6rJjUPJZMke2hZBCyJZyVFrhc/iNlrhpzGvgmzbIZQhnK8rH5dk3pM/cxEf7gZbOMTbm8GW2KvzO9bku5QMSj2gAt23jKWj/n9KpPEd6PMfpeKJ3b6z9cpKcYrWCxAhXvQ1mSSVdyIsptA/DNEFyoovhJp9O5AWHvXMjRTL0jYQwxPjGIe4DYGZn/RrJjYRwXOmsXPAPcV16Gpe5FQvBuj4X9CBPhbz0iHAmYyHgikj+z6ZJbI/CAYxLcWOHSFBofaoraqIBSKShXrrleTgzBWATouQUh8ireAbux5NRCBuCc1mbPrztlWTkEeyaeGJrQl8Nohlkog4XG8CcxBZbsSkcy/e7P08OYEEUKN+dqKsL7rtXN1yqVmerF4tHGXLxTJvg4IsSFYCEKzNEJuHV+MDTNXR0D7DsgYThqgx3KNcZT4FR+vtExEFnwfuQjKZZl5jVr/R0IVrOZC2NaC80E2Ml3ddCOk7FYiXfFwm4/DPEr5maGz7Ccso/O7TjXwqAnofQ3FVKF+USYU7GOZhQq9DHUWW+yjuDWD6EkQ6RGfwj7/3MaUe4XIWPOKg=";
        String B = WxDecodeUtil.decryptData(A);
        System.out.println(B);
    }
*/
	
	
	/**
	 * 密钥算法
	 */
	private static final String ALGORITHM = "AES";
	/**
	 * 加解密算法/工作模式/填充方式
	 */
	private static final String ALGORITHM_MODE_PADDING = "AES/ECB/PKCS7Padding";
	/**
	 * 生成key
	 */
	private static SecretKeySpec key = new SecretKeySpec(MD5Util.MD5Encode("jgkj2020jgkj2020jgkj2020jgkj2020", "UTF-8").toLowerCase().getBytes(), ALGORITHM);
	
	static {
		
	}
	
	/**
	 * AES加密
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String encryptData(String data) throws Exception {
		// 创建密码器
		Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING, "BC");
		// 初始化
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return Base64Util.encode(cipher.doFinal(data.getBytes()));
	}
 
	/**
	 * AES解密
	 * 
	 * @param base64Data
	 * @return
	 * @throws Exception
	 */
	public static String decryptData(String base64Data) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING, "BC");
		cipher.init(Cipher.DECRYPT_MODE, key);
		return new String(cipher.doFinal(Base64Util.decode(base64Data)));
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
    
    
    public static JSONObject getUserInfo(String encryptedData, String sessionKey, String iv){
        // 被加密的数据
        byte[] dataByte = Base64.decodeBase64(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decodeBase64(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.decodeBase64(iv);
 
        try {
            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, "UTF-8");
                return new JSONObject(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
 
	public static void main(String[] args) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		String aa=WxDecodeUtil.decryptData("aXiJfX3TyQfPuaeAaJMoBPEFi/WwiqYLmJfKC9Fh4WerKIydQ5WdKCb2qa4mOU1sNAksGfQvtMHezGd0L3FZEBr3xbJfwrtRUa72xk81120p5w6eslNxQ1pP9X0eeXtVXekDey6EORjOZNKGdONDKs6x6YwJQvlNiskNSQO9yNk1T2R+mKKS4B+FVxaG3V7wP21lqg9bxUU/JNxfWNDisCq586SVGkf/M1GCx3rEZxpqj7ju5AkgckVbzKKuwdM81IB1RbOHpLPTKS+RJUtmYUjI4i39nlq92yfUOLm7cNT3Y+sfOGndXvLuvFbsnx7UDd4AhP111IBEwrMhOUeAMQpORvGt/wJK1On9N5aV7epWwOcTHTDuY796KiXp8I0h0yogmqsVHMOJnX2UVquaJy/ActB0biSG9QuOkzNwa17KZVXZRxVPS8i4uaIZ1JTQQN1w6/Yv5K0wsC5bfdY/y8lL4xivk7FFb3TALYvNySpt8jnu2n0LnXTyFR2otX86qrGURzNborLfiCaltrw6/q3kPSUbFzsW4IJdzEuCTVaCuIfOl4EgnzvFtztHGTZLnDRcefV6KZfjbW8IWTIICmvUKeFmDhuG2W2++bhpQ3OfxxACWgsyWDyWyFP6UqPbRm1wqUL9jgsr+3wVFxwQ2M2CnZX87da6eeOlCWTrDq88Cs9nmSitCDHlsyGBvfoWaLPD7A22+k00jZOY4OY4uEFOoMq4mq4raf6/9hLUMosnA8LzHQ8PIF5vWQxsaCYdxKGvkmsp42hDN4Ph3T8tm29hoVqYAMWJeI35XWVVBeMf4cfbRhNcYQ6OZEKJYVhwdgEHnkbl0djqzkSBF9py3avpJh2xNLGnXwFGtMQOFCYUS3bBUKxO2EU4q6EjzhLiKQJgLRbQ/T4/boHEYJ/unosagIKKS4KIc07Z9yJHhCvRWnzbYPvf1DMYyU9rDIMHGVOLflQIpn27Qn2AsArmKqgwdhBxBo5N6i2AYPL0/fdz8WPa+ZO+izZa79UQo5rX44r+lhr6EFGf5e5EPhfjL8HGFaGoge1qDP7uExyBSYMdbijxmrL62SteNrfySQZz");
		 Map<String,String> map =doXMLParse(aa);
		 System.out.println(map);
	}
}
