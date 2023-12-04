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
     * @description ：上传文件方式:由Spring转到java
     * @author : P2M.WBA
     * @date : 2018/6/22 13:57
     */
    public static File MultipartFileToFile(MultipartFile multiFile) {
        // 获取文件名
        String fileName = multiFile.getOriginalFilename();
        // 获取文件后缀
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        // 用当前时间作为文件名，防止生成的临时文件重复
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
     * @description ：删除文件
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
     * 获取项目根路径(WebRoot)
     *
     * @return
     */
    /*public static String getWebRootPath() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        // 获取到webroot里面的数据
        URL url = classLoader.getResource("");
        File rootFile = new File(url.getPath() + "/");
        File webInfoDir = new File(rootFile.getParent() + "/");
        String path = webInfoDir.getParent() + Constant.TEMPLATE_DOWNLOAD;
        return path.replaceAll("\\\\", "/");
    }*/

    /**
     * 获取路径下的所有文件
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
                //如果这个文件是目录，则进行递归搜索
                if (fileIndex.isDirectory()) {
                    getFiles(fileIndex.getPath());
                } else {
                    //如果文件是普通文件，则将文件句柄放入集合中
                    fileList.add(fileIndex);
                }
            }
        }
        return fileList;
    }

    /**
     * 获取路径下的所有文件名称(全路径)
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
                //如果这个文件是目录，则进行递归搜索
                if (fileIndex.isDirectory()) {
                    getFiles(fileIndex.getPath());
                } else {
                    //如果文件是普通文件，则将文件句柄放入集合中
                    fileList.add(fileIndex.getName());
                }
            }
        }
        return fileList;
    }

    /**
     * 下载
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
            // 设置response参数，可以打开下载页面
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
