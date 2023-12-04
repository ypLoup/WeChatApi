package com.WeChatApi.controller.base;



import com.alipay.api.internal.util.AlipayEncrypt;
import com.alipay.api.internal.util.codec.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
 

public class aliPayAESCBCUtil {
    /**
     *
     * @param content 密文
     * @param key aes密钥
     * @return 原文
     */
    public static String RealDecrypt(String content, String key) throws Exception {
 
        //反序列化AES密钥
        SecretKeySpec keySpec = new SecretKeySpec(Base64.decodeBase64(key.getBytes()), "AES");
 
        //128bit全零的IV向量
        byte[] iv = new byte[16];
        for (int i = 0; i < iv.length; i++) {
            iv[i] = 0;
        }
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
 
        //初始化加密器并加密
        Cipher deCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        deCipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);
        byte[] encryptedBytes = Base64.decodeBase64(content.getBytes());
        byte[] bytes = deCipher.doFinal(encryptedBytes);
        return new String(bytes);
 
    }
    
    
    
    
    public static void main(String[] args) throws Exception {
		/*Security.addProvider(new BouncyCastleProvider());
		String aa=WxDecodeUtil.decryptData("aXiJfX3TyQfPuaeAaJMoBPEFi/WwiqYLmJfKC9Fh4WerKIydQ5WdKCb2qa4mOU1sNAksGfQvtMHezGd0L3FZEBr3xbJfwrtRUa72xk81120p5w6eslNxQ1pP9X0eeXtVXekDey6EORjOZNKGdONDKs6x6YwJQvlNiskNSQO9yNk1T2R+mKKS4B+FVxaG3V7wP21lqg9bxUU/JNxfWNDisCq586SVGkf/M1GCx3rEZxpqj7ju5AkgckVbzKKuwdM81IB1RbOHpLPTKS+RJUtmYUjI4i39nlq92yfUOLm7cNT3Y+sfOGndXvLuvFbsnx7UDd4AhP111IBEwrMhOUeAMQpORvGt/wJK1On9N5aV7epWwOcTHTDuY796KiXp8I0h0yogmqsVHMOJnX2UVquaJy/ActB0biSG9QuOkzNwa17KZVXZRxVPS8i4uaIZ1JTQQN1w6/Yv5K0wsC5bfdY/y8lL4xivk7FFb3TALYvNySpt8jnu2n0LnXTyFR2otX86qrGURzNborLfiCaltrw6/q3kPSUbFzsW4IJdzEuCTVaCuIfOl4EgnzvFtztHGTZLnDRcefV6KZfjbW8IWTIICmvUKeFmDhuG2W2++bhpQ3OfxxACWgsyWDyWyFP6UqPbRm1wqUL9jgsr+3wVFxwQ2M2CnZX87da6eeOlCWTrDq88Cs9nmSitCDHlsyGBvfoWaLPD7A22+k00jZOY4OY4uEFOoMq4mq4raf6/9hLUMosnA8LzHQ8PIF5vWQxsaCYdxKGvkmsp42hDN4Ph3T8tm29hoVqYAMWJeI35XWVVBeMf4cfbRhNcYQ6OZEKJYVhwdgEHnkbl0djqzkSBF9py3avpJh2xNLGnXwFGtMQOFCYUS3bBUKxO2EU4q6EjzhLiKQJgLRbQ/T4/boHEYJ/unosagIKKS4KIc07Z9yJHhCvRWnzbYPvf1DMYyU9rDIMHGVOLflQIpn27Qn2AsArmKqgwdhBxBo5N6i2AYPL0/fdz8WPa+ZO+izZa79UQo5rX44r+lhr6EFGf5e5EPhfjL8HGFaGoge1qDP7uExyBSYMdbijxmrL62SteNrfySQZz");
		 Map<String,String> map =doXMLParse(aa);
		 System.out.println(map);*/
		/*String encryptedData="PTWuRkgD4pWoLoWD3ZApzYMIw/IyTq3xG0Cd40nZKEgEm34yEeyK879w8vU0B1Q9q82TJ9Y36AgR1bAW/NsUII9ALa/5Ee3U/WCEgE1Ixif2SJTrfmz9WaRhmbke9BtSaoh67ocw4qeNw9Ed+RkIz8n4yRnOwsoqkyRKLlGEPlDK5mCRfI9KoH44G8Qlzb6CnIpBtRiqMJr1Q6PZ/uAooBoBP+G4dD05ZkU9qSnnkcXnWeLh7nG7OFmTOGqdquUngmpS/ZJXyXqZLmwvFnBgDK3lNLcGh0OpF/FE+FZBX/FziTGZLw5hu3DoMVgNyZpVswh6cTNLFR+JWUl1om+TeQkQ8SRSic4xg4VzJbws+iU=";
		String aesSecretKey="Dq2Z1qMFDhEQxrMk51uA8A==";
		String phoneResult = aliPayAESCBCUtil.RealDecrypt(encryptedData, aesSecretKey);
        JSONObject jsonObject1 = JSONObject.parseObject(phoneResult);
        
        String phone = jsonObject1.getString("mobile");
*/
    	/*String encryptedData="LxO+w5h75gNL0syAIGvEltUFwgqeR+9gzm1YtCliUCvGXqNh2EHcif0xfBFwF69iSWBEo7dSSm3/TDzRrZwXrQ==";
    	String plainData = AlipayEncrypt.decryptContent(encryptedData, "AES", "Dq2Z1qMFDhEQxrMk51uA8A==","UTF-8");
    	JSONObject jsonObject1 = JSONObject.parseObject(plainData);
    	String phone = jsonObject1.getString("mobile");
    	System.out.println(phone);*/
	}
}
