package com.honortone.api.utils;

import cn.hutool.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class GetSession {

    public static String getUsernameFromSession(HttpSession session){
        return session.getAttribute("username").toString();
    }

    public static String getPasswordFromSession(HttpSession session){
        return session.getAttribute("password").toString();
    }

    public String UploadFile(MultipartFile upload, String path) throws IOException {
        // MultipartFile
        // 判断该路径是否存在
        File file = new File(path);
        if (!file.exists()) {
            // 如果这个文件夹不存在的话,就创建这个文件
            file.mkdirs();
        }
        // 获取上传文件名称
        String filename = upload.getOriginalFilename();
        System.out.println("上传源文件名："+filename);
        // 把文件名称设置成唯一值 uuid 以防止文件名相同覆盖
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // 新文件名
        filename = uuid + "_" + filename;
        System.out.println("上传后文件名："+filename);
        // 完成文件上传
        upload.transferTo(new File(path, filename));
        File file1 = new File(path+filename);
        // 判断文件是否上传成功
        if(file1.exists()){
            System.out.println("success");
            return filename;
        }else {
            System.out.println("error");
            return filename = "上传失败";
        }
        //String filePath = "/" + filename;
        //String filePath = "upload/" + filename;
    }

}
