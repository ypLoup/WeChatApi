package com.WeChatApi.controller.base;
import java.io.IOException;
import java.security.MessageDigest;

import sun.misc.BASE64Decoder;
 
/**
 * MD5技术加密解密
 */
public class MD5Tools {
	
	protected static  String appKey = "1516738984515167389845";
	
	
    /***
     * MD5加码 生成32位md5码
     */
    public static String string2MD5(String inStr){
        MessageDigest md5 = null;
        try{
            md5 = MessageDigest.getInstance("MD5");
        }catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];
 
        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++){
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
 
    }
 
    /**
     * 加密解密算法 执行一次加密，两次解密
     */
    public static String convertMD5(String inStr){
 
        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++){
            a[i] = (char) (a[i] ^ 't');
        }
        String s = new String(a);
        return s;
 
    }
    
    public static String getMd5(String s) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };

        try {
            byte[] strTemp = s.getBytes();
            MessageDigest mdTemp = MessageDigest.getInstance("md5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }
    
    
    
    public static String toMD5(String plainText) {
        StringBuffer buf = new StringBuffer("");
        try {
            // 生成实现指定摘要算法的 MessageDigest 对象。
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 使用指定的字节数组更新摘要。
            md.update(plainText.getBytes());
            // 通过执行诸如填充之类的最终操作完成哈希计算。
            byte b[] = md.digest();
            // 生成具体的md5密码到buf数组(32位小写)
            int i;

            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0){
                    i += 256;
                }
                if (i < 16){
                    buf.append("0");
                }else{
                    buf.append(Integer.toHexString(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buf.toString();
    }

    public static byte[] toMD5byte(byte[] plainText) {
        StringBuffer buf = new StringBuffer("");
        try {
            // 生成实现指定摘要算法的 MessageDigest 对象。
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 使用指定的字节数组更新摘要。
            md.update(plainText);
            // 通过执行诸如填充之类的最终操作完成哈希计算。
            byte b[] = md.digest();
            // 生成具体的md5密码到buf数组
            int i;

            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buf.toString().getBytes();
    }

    /**
     * 解决php与javaMD5加密不同 获取MD5加密后的字符串
     *
     * @param str
     *            明文
     * @return 加密后的字符串
     * @throws Exception
     */
    public static String getMD5(String str) throws Exception {
        /** 创建MD5加密对象 */
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        /** 进行加密 */
        md5.update(str.getBytes("GBK"));
        /** 获取加密后的字节数组 */
        byte[] md5Bytes = md5.digest();
        String res = "";
        for (int i = 0; i < md5Bytes.length; i++) {
            int temp = md5Bytes[i] & 0xFF;
            if (temp <= 0XF) { // 转化成十六进制不够两位，前面加零
                res += "0";
            }
            res += Integer.toHexString(temp);
        }
        return res;
    }
    
    public static String MD5(String s) {
	    try {
	        MessageDigest md = MessageDigest.getInstance("MD5");
	        byte[] bytes = md.digest(s.getBytes("utf-8"));
	        return toHex(bytes);
	    }
	    catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}

	private static String toHex(byte[] bytes) {

	    final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
	    StringBuilder ret = new StringBuilder(bytes.length * 2);
	    for (int i=0; i<bytes.length; i++) {
	        ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
	        ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
	    }
	    return ret.toString();
	}
 
    // 测试主函数
    public static void main(String args[]) throws IOException {
        /*String s = new String("e94287d7937aa5fac3113fcd8750fd50");
        long t = System.currentTimeMillis();
        BASE64Decoder baseDecoder = new BASE64Decoder();
        
        System.out.println(new String(baseDecoder.decodeBuffer("AFA51873F8ED370A39329DBA79724B1F")));
        System.out.println(t);
        System.out.println("原始：" + s);
        System.out.println("MD5后：" + string2MD5(s));
        System.out.println("加密的：" + convertMD5(s));*/
        System.out.println("解密的：" + convertMD5(convertMD5("e94287d7937aa5fac3113fcd8750fd50")));
 
    }
}

