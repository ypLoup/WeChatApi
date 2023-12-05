package com.WeChatApi.service.userInfoService;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import javax.transaction.Transactional;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.WeChatApi.controller.base.BaseServiceException;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.dao.userInfoMapper;
import com.WeChatApi.bean.condition.userInfoCondition;
import com.WeChatApi.bean.models.userInfo;
import sun.misc.BASE64Decoder;

@Service
@Transactional
public class userInfoService {
	
	@Autowired
	private userInfoMapper userinfomapper;

	

	public void addUserInfo(userInfo userinfo) {
		
		if(StringUtils.isBlank(userinfo.getUserName())){
			throw new BaseServiceException(
					StatusCode.PARAMETER_FORMATE_RROR.getCode(),
					"姓名不能为空！");
		}
		userinfo.setFaceToken(UUID.randomUUID().toString());
		userinfo.setApiKey("system");
		userinfomapper.addUserInfo(userinfo);
		
	}

	public void updateUserInfo(userInfo userinfo) {
		
		if(StringUtils.isBlank(userinfo.getUserName())){
			throw new BaseServiceException(
					StatusCode.PARAMETER_FORMATE_RROR.getCode(),
					"姓名不能为空！");
		}
		
		
		userinfomapper.updateUserInfo(userinfo);
		List<String> idList= new ArrayList<>();
		idList.add(userinfo.getUserId().toString());
		userinfomapper.updateUserSendInfoStatus(idList,0);
		
	}

	public List<userInfo> findUserInfoByConditions(userInfoCondition condition) {
		// TODO Auto-generated method stub
		return userinfomapper.findUserInfoByConditions(condition);
	}

	public long findUserInfoCountByConditions(userInfoCondition condition) {
		// TODO Auto-generated method stub
		return userinfomapper.findUserInfoCountByConditions(condition);
	}

	public List<Map<String, String>> findUserInfoAll() {
        List<Map<String, String>> resultList=new ArrayList<>();
		
		List<userInfo> UserList=userinfomapper.findUserInfoAll();
		for(userInfo userInfo :UserList){
			Map<String, String> map= new HashMap<String, String>();
			map.put("value", userInfo.getUserId().toString());
			if(userInfo.getUserName()==null){
				map.put("title", "未命名("+userInfo.getUserMobile()+")");
				map.put("userName", "未命名");
				map.put("userIdCard", userInfo.getUserIdCard());
			}else{
				map.put("userName", userInfo.getUserName());
				map.put("userIdCard", userInfo.getUserIdCard());
				map.put("title",userInfo.getUserName()+"("+userInfo.getUserMobile()+")");
			}
			resultList.add(map);
		}
		return resultList;
	}

	public String uploadImg(String base64EncoderImg, String imageName) throws IOException {
		BASE64Decoder decoder = new BASE64Decoder();  
		byte[] imgByte = decoder.decodeBuffer(base64EncoderImg);  
		for ( int i = 0; i < imgByte. length; ++i) {  
		     if (imgByte[i] < 0) { // 调整异常数据  
		         imgByte[i] += 256;  
		    }  
		}
		String endFileDir =endFileDir();
		String path="/home/jgkj/Projects/platform_6900/app/image/register_face_image/";
		path=path+endFileDir+"/";
		boolean result=uploadImg(path,imageName,imgByte);
		if(result==true){
			return path+imageName;
		}else{
			return path+imageName;
		}
		
	}
	
	
	public static String endFileDir () {  
        Date date = new Date(System. currentTimeMillis());  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd" );  
        String str = sdf.format(date).toString();  
        return str;  
  } 
	
	
	private static boolean uploadImg (String path, String imgName, byte[] imgByte) {  
		
        // Linux服务器是反斜杠  
       path=path.replaceAll( "/", "\\\\");  
       File filePath = new File(path);  
       filePath.setWritable( true, false);  
        if (!filePath.exists()) {  
            filePath.mkdirs();  
       }  
       boolean isSuccess = false;  
       File file = new File(path);  
       FileOutputStream output = null;  
        try {  
            output = new FileOutputStream(file);  
            output.write(imgByte);  
            output.flush();  
            isSuccess = true;  
       } catch (IOException e) {  
             // TODO Auto-generated catch block  
            e.printStackTrace();  
            isSuccess = false;  
       } finally {  
             try {  
                  if (output != null) {  
                       output.close();  
                 }  
            } catch (IOException e) {  
                  // TODO Auto-generated catch block  
                 e.printStackTrace();  
            }  
       }  
        return isSuccess;  
 }

	public String uploadImg2(MultipartFile UploadFile) throws IllegalStateException, IOException {
		String PluploadFileName = UploadFile.getOriginalFilename();
        // 设置上传文件目录
		ResourceBundle res = ResourceBundle.getBundle("webConfig");
	    String UploadPath = res.getString("upload_url");
        //String UploadPath = "/home/jgkj/Projects/platform_6900/app/image/register_face_image/";
        String dataDir=endFileDir()+"/";
        File _UploadFile = new File(UploadPath+dataDir, PluploadFileName);
        if (!_UploadFile.isDirectory()) _UploadFile.mkdirs();
        UploadFile.transferTo(_UploadFile); 
        return UploadPath+dataDir+PluploadFileName;
	}

	public void deleteBatch(List<String> ids) {
		// TODO Auto-generated method stub
		userinfomapper.deleteBatch(ids);
		userinfomapper.updateUserSendInfoStatus(ids,2);
	}

	public void changeStatusBatch(List<String> ids, String status) {
		// TODO Auto-generated method stub
		userinfomapper.changeStatusBatch(ids,status);
	}

}
