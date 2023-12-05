package com.WeChatApi.filter;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

public class BodyReaderRequestWrapper  extends HttpServletRequestWrapper {

    

	private final String body;

    public String getBody() {
        return body;
    }

    /**
     * ȡ��������body�еĲ�������������ʱִ�У�
     * @param request
     */
    public BodyReaderRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        StringBuilder sb = new StringBuilder();
        InputStream ins = request.getInputStream();
        BufferedReader isr = null;
        try{
            if (ins != null) {
                isr = new BufferedReader(new InputStreamReader(ins));
                char[] charBuffer = new char[128];
                int readCount;
                while ((readCount = isr.read(charBuffer)) != -1) {
                    sb.append(charBuffer, 0, readCount);
                }
            }
        } finally {
            if(isr != null) {
                isr.close();
            }
        }

        sb.toString();
        body = sb.toString();
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayIns = new ByteArrayInputStream(body.getBytes());
        return new ServletInputStream() {

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() {
                return byteArrayIns.read();
            }

        };
    }
}
