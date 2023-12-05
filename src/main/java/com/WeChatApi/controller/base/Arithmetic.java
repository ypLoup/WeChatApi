package com.WeChatApi.controller.base;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
public class Arithmetic {
	
	static Key key;

	/**
	 * ���ݲ�������KEY
	 * 
	 * @param strKey
	 */
	public void getKey(String strKey) {
		try {
			KeyGenerator _generator = KeyGenerator.getInstance("DES");
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );
            secureRandom.setSeed(strKey.getBytes());
            _generator.init(56,secureRandom);
			key = _generator.generateKey();
			//System.out.println(key);
			_generator = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����String��������,String�������
	 * 
	 * @param strMing
	 * @return
	 */
	public String getEncString(String strMing) {
		byte[] byteMi = null;
		byte[] byteMing = null;
		String strMi = "";
		try {
			return byte2hex(getEncCode(strMing.getBytes()));

			// byteMing = strMing.getBytes("UTF8");
			// byteMi = this.getEncCode(byteMing);
			// strMi = new String( byteMi,"UTF8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			byteMing = null;
			byteMi = null;
		}
		return strMi;
	}

	/**
	 * ���� ��String��������,String�������
	 * 
	 * @param strMi
	 * @return
	 */
	public String getDesString(String strMi) {
		byte[] byteMing = null;
		byte[] byteMi = null;
		String strMing = "";
		try {
			return new String(getDesCode(hex2byte(strMi.getBytes())));

			// byteMing = this.getDesCode(byteMi);
			// strMing = new String(byteMing,"UTF8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			byteMing = null;
			byteMi = null;
		}
		return strMing;
	}

	/**
	 * ������byte[]��������,byte[]�������
	 * 
	 * @param byteS
	 * @return
	 */
	private static byte[] getEncCode(byte[] byteS) {
		byte[] byteFina = null;
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byteFina = cipher.doFinal(byteS);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cipher = null;
		}
		return byteFina;
	}

	/**
	 * ������byte[]��������,��byte[]�������
	 * 
	 * @param byteD
	 * @return
	 */
	private static byte[] getDesCode(byte[] byteD) {
		Cipher cipher;
		byte[] byteFina = null;
		try {
			cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byteFina = cipher.doFinal(byteD);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cipher = null;
		}
		return byteFina;
	}

	/**
	 * ������ת�ַ���
	 * 
	 * @param b
	 * @return
	 */
	public static String byte2hex(byte[] b) { // һ���ֽڵ�����
		// ת��16�����ַ���
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			// ����ת��ʮ�����Ʊ�ʾ
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase(); // ת�ɴ�д
	}

	public static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0)
			throw new IllegalArgumentException("���Ȳ���ż��");
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			// ��λһ�飬��ʾһ���ֽ�,��������ʾ��16�����ַ�������ԭ��һ�������ֽ�
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}

		return b2;
	}
	
	
	
    public static String Decrypt(String src, String key) throws Exception {
        try {
            // �ж�Key�Ƿ���ȷ
            if (key == null) {
                System.out.print("KeyΪ��null");
                return null;
            }
 
            // ��Կ��λ
            int plus= 32-key.length();            
            byte[] data = key.getBytes("utf-8");
            byte[] raw = new byte[32];
            
            byte[] plusbyte={ 0x08, 0x08, 0x04, 0x0b, 0x02, 0x0f, 0x0b, 0x0c,0x01, 0x03, 0x09, 0x07, 0x0c, 0x03, 0x07, 0x0a, 0x04, 0x0f,0x06, 0x0f, 0x0e, 0x09, 0x05, 0x01, 0x0a, 0x0a, 0x01, 0x09,0x06, 0x07, 0x09, 0x0d };    
            for(int i=0;i<32;i++)
            {
            	if (data.length > i)
            		raw[i] = data[i];
            	else
            		raw[i] = plusbyte[plus];
            }
            
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            
            //byte[] encrypted1 = new Base64().decode(src);//base64
            byte[] encrypted1 = toByteArray(src);//ʮ������
            
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original,"utf-8");
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }

    public static byte[] toByteArray(String hexString) {
        if (hexString.isEmpty())
            throw new IllegalArgumentException("this hexString must not be empty");
 
        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {//��Ϊ��16���ƣ����ֻ��ռ��4λ��ת�����ֽ���Ҫ����16���Ƶ��ַ�����λ����
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;

    }

	public static void main(String[] args) {
		Arithmetic des = new Arithmetic();// ʵ����һ������
		des.getKey("jsy,2016.");// �����ܳ�
		String strEnc = des.getEncString("1@5@235456@10@5");// �����ַ���,����String������
		//String strEnc = des.getEncString("o3J0E5VxSlIKw1sdwafQg_YlHbz4@5@��A88888@p201210135204_2_25151_151823@25151@25151@5@10");// �����ַ���,����String������
		String url ="https://jiashan.iparking.tech/change?sign="+strEnc;
		System.out.println(strEnc+"--------length:"+url.length());

		//String strDes = des.getDesString("759CD7D5FBF56D81DABBB0762F835C1C5CE58E8C8E8E759B2271174BF05781D159BB697326795E0E9C54235599A711F7DB48A688118CB1ADBD6D9CDBC7514C6116650F68F7408AEA09D91F8D24325629C34FE489740A59D2");// ��String ���͵����Ľ���
		//String strDes=des.getDesString("56ED238B4521AD2F27E2EB920D3A5B590D2BA618BED5F859262D05BD2333216FA42975459A87DB85F91BCB08C10C3C06217FA4FF677A542828EA3F48FB9AF61BB7880ECF526EBD7936A1F92A0997B1D4DDCAA835118CF23E");
		//System.out.println(strDes);
	}

}
