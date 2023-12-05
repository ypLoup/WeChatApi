package com.WeChatApi.controller.base;
import java.io.IOException;
import java.security.MessageDigest;

import sun.misc.BASE64Decoder;
 
/**
 * MD5�������ܽ���
 */
public class MD5Tools {
	
	protected static  String appKey = "1516738984515167389845";
	
	
    /***
     * MD5���� ����32λmd5��
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
     * ���ܽ����㷨 ִ��һ�μ��ܣ����ν���
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
            // ����ʵ��ָ��ժҪ�㷨�� MessageDigest ����
            MessageDigest md = MessageDigest.getInstance("MD5");
            // ʹ��ָ�����ֽ��������ժҪ��
            md.update(plainText.getBytes());
            // ͨ��ִ���������֮������ղ�����ɹ�ϣ���㡣
            byte b[] = md.digest();
            // ���ɾ����md5���뵽buf����(32λСд)
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
            // ����ʵ��ָ��ժҪ�㷨�� MessageDigest ����
            MessageDigest md = MessageDigest.getInstance("MD5");
            // ʹ��ָ�����ֽ��������ժҪ��
            md.update(plainText);
            // ͨ��ִ���������֮������ղ�����ɹ�ϣ���㡣
            byte b[] = md.digest();
            // ���ɾ����md5���뵽buf����
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
     * ���php��javaMD5���ܲ�ͬ ��ȡMD5���ܺ���ַ���
     *
     * @param str
     *            ����
     * @return ���ܺ���ַ���
     * @throws Exception
     */
    public static String getMD5(String str) throws Exception {
        /** ����MD5���ܶ��� */
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        /** ���м��� */
        md5.update(str.getBytes("GBK"));
        /** ��ȡ���ܺ���ֽ����� */
        byte[] md5Bytes = md5.digest();
        String res = "";
        for (int i = 0; i < md5Bytes.length; i++) {
            int temp = md5Bytes[i] & 0xFF;
            if (temp <= 0XF) { // ת����ʮ�����Ʋ�����λ��ǰ�����
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
 
    // ����������
    public static void main(String args[]) throws IOException {
        /*String s = new String("e94287d7937aa5fac3113fcd8750fd50");
        long t = System.currentTimeMillis();
        BASE64Decoder baseDecoder = new BASE64Decoder();
        
        System.out.println(new String(baseDecoder.decodeBuffer("AFA51873F8ED370A39329DBA79724B1F")));
        System.out.println(t);
        System.out.println("ԭʼ��" + s);
        System.out.println("MD5��" + string2MD5(s));
        System.out.println("���ܵģ�" + convertMD5(s));*/
        System.out.println("���ܵģ�" + convertMD5(convertMD5("e94287d7937aa5fac3113fcd8750fd50")));
 
    }
}

