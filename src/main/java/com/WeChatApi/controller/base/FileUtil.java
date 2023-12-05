package com.WeChatApi.controller.base;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	
	/**
     * @description ���ϴ��ļ���ʽ:��Springת��java
     * @author : P2M.WBA
     * @date : 2018/6/22 13:57
     */
    public static File MultipartFileToFile(MultipartFile multiFile) {
        // ��ȡ�ļ���
        String fileName = multiFile.getOriginalFilename();
        // ��ȡ�ļ���׺
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        // �õ�ǰʱ����Ϊ�ļ�������ֹ���ɵ���ʱ�ļ��ظ�
        try {
            File file = File.createTempFile(System.currentTimeMillis() + "", prefix);

            multiFile.transferTo(file);

            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @description ��ɾ���ļ�
     * @author : P2M.WBA
     * @date : 2018/6/22 13:59
     */
    public static void deleteFile(File... files) {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * ��ȡ��Ŀ��·��(WebRoot)
     *
     * @return
     */
    /*public static String getWebRootPath() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        // ��ȡ��webroot���������
        URL url = classLoader.getResource("");
        File rootFile = new File(url.getPath() + "/");
        File webInfoDir = new File(rootFile.getParent() + "/");
        String path = webInfoDir.getParent() + Constant.TEMPLATE_DOWNLOAD;
        return path.replaceAll("\\\\", "/");
    }*/

    /**
     * ��ȡ·���µ������ļ�
     *
     * @param path
     * @return
     */
    public static List<File> getFiles(String path) {

        List<File> fileList = new ArrayList<File>();

        File file = new File(path);

        if (file.isDirectory()) {

            File[] files = file.listFiles();

            for (File fileIndex : files) {
                //�������ļ���Ŀ¼������еݹ�����
                if (fileIndex.isDirectory()) {
                    getFiles(fileIndex.getPath());
                } else {
                    //����ļ�����ͨ�ļ������ļ�������뼯����
                    fileList.add(fileIndex);
                }
            }
        }
        return fileList;
    }

    /**
     * ��ȡ·���µ������ļ�����(ȫ·��)
     *
     * @param path
     * @return
     */
    public static List<String> getFileName(String path) {

        List<String> fileList = new ArrayList<String>();

        File file = new File(path);

        if (file.isDirectory()) {

            File[] files = file.listFiles();

            for (File fileIndex : files) {
                //�������ļ���Ŀ¼������еݹ�����
                if (fileIndex.isDirectory()) {
                    getFiles(fileIndex.getPath());
                } else {
                    //����ļ�����ͨ�ļ������ļ�������뼯����
                    fileList.add(fileIndex.getName());
                }
            }
        }
        return fileList;
    }

    /**
     * ����
     *
     * @param file
     * @param response
     * @throws IOException
     */
    public static void download(File file, HttpServletResponse response) throws IOException {
        ServletOutputStream out = response.getOutputStream();
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            // ����response���������Դ�����ҳ��
            response.reset();
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(file.getName().getBytes("GB2312"), "ISO_8859_1"));
            bis = new BufferedInputStream(new FileInputStream(file));
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (final IOException e) {
            throw e;
        } finally {
            if (bis != null) bis.close();
            if (bos != null) bos.close();
        }
    }

}
