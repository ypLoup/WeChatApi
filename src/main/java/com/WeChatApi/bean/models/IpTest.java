package com.WeChatApi.bean.models;
import java.net.InetAddress;

import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class IpTest {

	private InetAddress myIpAddress=null;

	private InetAddress[] myServer = null;

	public static void main(String args[]) {
		String regex = "\\$\\{checktool\\.DeviceIDType:\\d{20}}(01|02|03|99)[1-2]\\d{3}[0-1][0-9][0-3][0-9][0-2][0-9][0-5][0-9][0-5][0-9]\\d{5}";
        String input = "200020022";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            String matchedString = matcher.group();
            System.out.println("Matched: " + matchedString);
        } else {
            System.out.println("No match found.");
        }

	}

	/**

	* 获得 localhost本地IP地址

	* @author www.yoodb.com

	* @return

	*/

	public InetAddress getLocalhostIP() {
	try {
	myIpAddress = InetAddress.getLocalHost();

	} catch (UnknownHostException e) {
	e.printStackTrace();

	}

	return myIpAddress;

	}

	/**

	* 获得某域名的IP地址

	* @author www.yoodb.com

	* @param domain 域名

	* @return

	*/

	public InetAddress[] getServerIP(String domain) {
	try {
	myServer = InetAddress.getAllByName(domain);

	} catch (UnknownHostException e) {
	e.printStackTrace();

	}

	return myServer;

	}

	}

