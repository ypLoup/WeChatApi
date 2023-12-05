package com.WeChatApi.controller.base;


import java.nio.charset.Charset;
import java.util.Arrays;
 
 
/**
 * ΢��С����ӽ���
 *
 *
 */
public class WxPKCS7Encoder {
	private static final Charset CHARSET = Charset.forName("utf-8");
	private static final int BLOCK_SIZE = 32;
 
	/**
	 * ��ö����Ľ��в�λ�����ֽ�.
	 *
	 * @param count
	 *            ��Ҫ������䲹λ�����������ֽڸ���
	 * @return �����õ��ֽ�����
	 */
	public static byte[] encode(int count) {
		// ������Ҫ����λ��
		int amountToPad = BLOCK_SIZE - (count % BLOCK_SIZE);
		if (amountToPad == 0) {
			amountToPad = BLOCK_SIZE;
		}
		// ��ò�λ���õ��ַ�
		char padChr = chr(amountToPad);
		String tmp = new String();
		for (int index = 0; index < amountToPad; index++) {
			tmp += padChr;
		}
		return tmp.getBytes(CHARSET);
	}
 
	/**
	 * ɾ�����ܺ����ĵĲ�λ�ַ�
	 *
	 * @param decrypted
	 *            ���ܺ������
	 * @return ɾ����λ�ַ��������
	 */
	public static byte[] decode(byte[] decrypted) {
		int pad = decrypted[decrypted.length - 1];
		if (pad < 1 || pad > 32) {
			pad = 0;
		}
		return Arrays.copyOfRange(decrypted, 0, decrypted.length - pad);
	}
 
	/**
	 * ������ת����ASCII���Ӧ���ַ������ڶ����Ľ��в���
	 *
	 * @param a
	 *            ��Ҫת��������
	 * @return ת���õ����ַ�
	 */
	public static char chr(int a) {
		byte target = (byte) (a & 0xFF);
		return (char) target;
	}
}