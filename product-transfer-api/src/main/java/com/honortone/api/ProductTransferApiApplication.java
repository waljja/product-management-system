package com.honortone.api;

import com.honortone.api.utils.SAPUtil;
import org.junit.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, SecurityAutoConfiguration.class })
@MapperScan("com.honortone.api.mapper")
@RestController
public class ProductTransferApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductTransferApiApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @GetMapping("/getStorageSync")
    public String getStorageSync(HttpServletRequest request) {

        HttpSession session = request.getSession();
        System.out.println("j"+session.getAttribute("username"));
        String username =  (String)session.getAttribute("username");
        System.out.println(username);
//        RestTemplate restTemplate = new RestTemplate();
//        String url = "http://localhost:8084/#/pages/login/login?username=jtm&password=jtm";
//        String result = restTemplate.getForObject(url, String.class);
//        System.out.println(result);
//        return result;
        return username;
    }

    @Test
    public void SapTest(){
        SAPUtil sapUtil = new SAPUtil();
        List<String[]> list = sapUtil.getInfo("5000","009-0001453-00DR3","0001439431","313");
        //String[] ss = list.get(0);
        //System.out.println(ss[3]);
        for (String[] s : list){
            System.out.println(s[3]);
        }
    }

    @Test
    public void SapTest1(){
        SAPUtil sapUtil = new SAPUtil();
        int n = sapUtil.getInfo1("5000","009-0001453-00DR3","0001439431","311");
        //String[] ss = list.get(0);
        //System.out.println(ss[3]);
        System.out.println("共有"+n+"条数据");
    }

    /**
     * @param multipartFile 上传文件
     * @param isCrypto 是否加密文件
     * @return
     */
    /*@Test
    public String upload(MultipartFile multipartFile, boolean isCrypto) {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        // 生成临时文件
        File tempFile = FileUtils.multipartFileToFile(multipartFile, request.getServletContext().getRealPath("/") + "\\static\\temp");
        // 上传文件并返回文件路径
        String uploadFilePath = FileUtils.uploadByJersey("http://192.168.50.126:8083", "/upload", tempFile, isCrypto);
        if (uploadFilePath != null) {
            return "上传成功";
        }
        else {
            return "上传失败";
        }
    }*/

}
