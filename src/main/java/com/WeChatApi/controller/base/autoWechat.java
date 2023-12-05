package com.WeChatApi.controller.base;

import java.io.ByteArrayInputStream;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;

/*import org.apache.soap.util.xml.*;  
import org.apache.soap.*;  
import org.apache.soap.rpc.*;  */
  
import java.io.*;  
import java.net.*;  
import java.util.Vector; 
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.WeChatApi.bean.models.UnifiedOrderRequestBean;
import com.WeChatApi.service.wechatApiService.wechatApiService;


import cn.com.eagle.sdk.base.INetTools;
import cn.com.eagle.sdk.bean.NetReqConfBean;
import cn.com.eagle.sdk.bean.OipReqBean;
import cn.com.eagle.sdk.bean.OipRspBean;
import cn.com.eagle.sdk.net.factory.NetToolsHttpFactory;

public class autoWechat {
	
	public static void main(String[] args) throws InterruptedException {
        // �����ǳ�
       // String friendNickName = "��˧";
        System.out.println("�������������");
        Scanner scan = new Scanner(System.in);
//        String friendNickName = "�ļ���������";
        String friendNickName= scan.nextLine();
        if(StringUtils.isNotBlank(friendNickName)){
        	searchMyFriendAndSend(friendNickName);
        }
        
    }

	private static void searchMyFriendAndSend(String friendNickName) throws InterruptedException {
        // ����Robot����
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        //��΢�� Ctrl+Alt+W
        assert robot != null;
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_W);
        //�ͷ�Ctrl��������Ctrl���˸����ɾ���������Ĺ����԰������ڰ��º�һ��Ҫ�ͷ�
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_ALT);

        // ���ӳٲ����٣������޷�����
        robot.delay(1000);

        // Ctrl + F ����ָ������
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_F);
        robot.keyRelease(KeyEvent.VK_CONTROL);

        // �������ǳƷ��͵����а�
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(friendNickName);
        clip.setContents(tText, null);
        // �������а�����ctrl+v�����ճ������
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.delay(1000);
        robot.keyPress(KeyEvent.VK_ENTER);

        // ������Ϣ
        sendMsg(friendNickName);
    }

    private static void sendMsg(String friendNickName) throws InterruptedException {
        /*String[] mottoes = {
                "��ֻ�������죬�����������춬�죡",
                "��ֻ�������죬����,����,���죡",
                "��ֻ�������죬���죬���죡",
                "��ֻ����һ�죬ÿһ�죡",
                "����ôô�գ�",
                "[��Ц]",
                ""
        };*/
    	String[] mottoes = {
                "���,"+friendNickName,
                "����΢�Ż�����",
                "����ʲô�����˵����",
                "�Ҷ����Ծ���������˵��",
                "���ĵĲ����ĵĶ�����...",
                "[��Ц]",
                ""
        };
        for (String motto : mottoes) {
            sendOneMsg(motto);
        }
        Thread.sleep(2000);

        sendOneMsg("����˵��������¡�");
    }

    private static void sendOneMsg(String msg) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText;
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        //�ӳ�ʮ�룬��Ҫ��Ϊ��Ԥ�����򿪴��ڵ�ʱ�䣬�����ڵĵ�λΪ����
        assert robot != null;
        robot.delay(500);
        tText = new StringSelection(msg);
        clip.setContents(tText, null);
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.delay(500);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.delay(500);
    }

	
    } 
	
	 

