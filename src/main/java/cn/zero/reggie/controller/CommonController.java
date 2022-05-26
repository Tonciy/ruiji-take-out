package cn.zero.reggie.controller;

import cn.zero.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Zero
 * @Description 文件上传、下载
 */
@Slf4j
@RequestMapping("/common")
@RestController
public class CommonController {


    @Value("${reggie.path}")
    private String bashPath;
    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        // file 是一个临时文件，需要指定存储位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());
        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        // 获取原始文件名的后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 使用 UUID 生成新的文件名，防止文件名称冲突产生覆盖
        String fileName = UUID.randomUUID().toString() + suffix;
        // 判断存放目录是否存在
        File dir = new File(bashPath);
        if(!dir.exists()){
            dir.mkdirs();
        }
        try {
            // 将临时文件转存到指定位置
            file.transferTo(new File(bashPath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 文件名得传给前端，前端后续用此图片作为新菜品的菜照
        return R.success(fileName);
    }


    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void  download(String name, HttpServletResponse response){
        FileInputStream fileInputStream = null;
        ServletOutputStream outputStream = null;
        // 设置响应 mine 类型
        response.setContentType("image/" + name.substring(name.lastIndexOf(".")));
        try {
            // 构造文件输入流读取图片
             fileInputStream = new FileInputStream(new File(bashPath + name));
            // 构造文件输出流用来将文件传回给客户端
             outputStream = response.getOutputStream();
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = (fileInputStream.read(bytes))) != -1){
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            // 关闭流
            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
