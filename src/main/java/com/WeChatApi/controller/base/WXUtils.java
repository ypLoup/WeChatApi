package com.WeChatApi.controller.base;




import org.apache.commons.codec.binary.Base64;



import net.sf.json.JSONObject;



public class WXUtils {
	
	private static final String WATERMARK = "watermark";
	private static final String APPID = "appid";
	/**
	 * ½âÃÜÊý¾Ý
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String appId, String encryptedData, String sessionKey, String iv){
		String result = "";
		String phoneNumber="";
		try {
			AES aes = new AES();  
		    byte[] resultByte = aes.decrypt(Base64.decodeBase64(encryptedData), Base64.decodeBase64(sessionKey), Base64.decodeBase64(iv));  
		    if(null != resultByte && resultByte.length > 0){  
		        result = new String(WxPKCS7Encoder.decode(resultByte));  
		    	JSONObject jsonObject = JSONObject.fromObject(result);
		    	String decryptAppid = jsonObject.getJSONObject(WATERMARK).getString(APPID);
		    	phoneNumber =jsonObject.getString("phoneNumber");
		    	if(!appId.equals(decryptAppid)){
		    		result = "";
		    	}
	        }  
		} catch (Exception e) {
			result = "";
			e.printStackTrace();
		}
	    return phoneNumber;
	}
	
	
	public static void main(String[] args) throws Exception{
	  
		   String appId = "wx606d7fcc6d1402c9";
		   String encryptedData = "9LkHsWmXLaPj1OJA/d55NkrU0cNKO/Ld07epb25BU+2N0XtqPsFKVZ3LkRDzAUoqE6Lp3SEDTPY8K1uTR0n8hMJjmpSjPvZR4JPY7gt1e39RuHWoI6Wye3ZhpHzFEPiZrYMCXgOfWcYQ25VfprLWKXrVtU5oq8mkw7cVsCX/KrveUcOuqdrAL59397oxu2VL5+uTJKtSoMceWcCNAmOqBQ==";
		   String sessionKey = "vL5hM8o6u20BWndxa2MfzQ==";
		   String iv = "XuJs2tCsrM6klwQgln0TEg==";
		
		

		   
		   //String appId = "wx4f4bc4dec97d474b";
		   //String encryptedData = "CiyLU1Aw2KjvrjMdj8YKliAjtP4gsMZMQmRzooG2xrDcvSnxIMXFufNstNGTyaGS9uT5geRa0W4oTOb1WT7fJlAC+oNPdbB+3hVbJSRgv+4lGOETKUQz6OYStslQ142dNCuabNPGBzlooOmB231qMM85d2/fV6ChevvXvQP8Hkue1poOFtnEtpyxVLW1zAo6/1Xx1COxFvrc2d7UL/lmHInNlxuacJXwu0fjpXfz/YqYzBIBzD6WUfTIF9GRHpOn/Hz7saL8xz+W//FRAUid1OksQaQx4CMs8LOddcQhULW4ucetDf96JcR3g0gfRK4PC7E/r7Z6xNrXd2UIeorGj5Ef7b1pJAYB6Y5anaHqZ9J6nKEBvB4DnNLIVWSgARns/8wR2SiRS7MNACwTyrGvt9ts8p12PKFdlqYTopNHR1Vf7XjfhQlVsAJdNiKdYmYVoKlaRv85IfVunYzO0IKXsyl7JCUjCpoG20f0a04COwfneQAGGwd5oa+T8yO5hzuyDb/XcxxmK01EpqOyuxINew==";
		   //String sessionKey = "tiihtNczf5v6AKRyjwEUhQ==";
		   //String iv = "r7BXXKkLb8qrSNn05n0qiA==";


       System.out.println(decrypt(appId, encryptedData, sessionKey, iv));
    }

	
}
