package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * 通用controller
 *
 * @author: Lin
 * @Date: 2023-04-21 23:29
 **/
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    //从配置文件中传入参数的值,传入配置文件的值的时候, 最好写完前缀和后缀
    @Value("${reggie.path}")
    private String basePath;


    @PostMapping("/upload")
    public R<String> upload(@RequestPart("file") MultipartFile file) {//这里的file名字不能随便写,因为web中的表单数据就是用file这个名字
        //file是一个临时文件文, 必须存储到指定的位置, 因为请求完成之后临时文件就会自动删除
        log.info(file.toString());
        //获取原始文件名字(上传之前的文件名字)
        String originalFilename = file.getOriginalFilename();
        //获取文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));//lastIndexOf()是寻找某字符串最后一次出现的位置(下标),substring是从某一位置(包括这个位置)截取之后的子字符串
        //随机生成文件名字,为了保证文件名字不重复(因为上传的文件一般都会重复,所以一般不用原始文件名字,可以用uuid随机生成字符)
        String fileName = UUID.randomUUID().toString() + suffix;
        //保存上传的文件, 并且确保保存的指定位置是存在的(保证文件夹存在)
        File dir = new File(basePath);
        if (!dir.exists()){//if the file no exist, then the file is false, "!dir.exists()" is true, it will get into if()
            //create the dir
            dir.mkdirs();//the difference between dir.mkdirs() and dir.mkdir is the difference between creating a first level directory and multi-level directory.
        }

        //Perform file transfer
        try {
            file.transferTo(new File(basePath+fileName));//the complete path is written in new File()
        } catch (IOException e) {
            e.printStackTrace();//perform track() method.
        }
        return R.success(fileName);//return result set.
    }

    /**
     * file download
     * url:http://localhost:8080/backend/page/demo/upload.html
     * @param name
     * @param response
     * @return
     */
    //file download
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        //read the file
        try {
            //Use InputSteam to read the file
            //Create inputStream Object
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(new File(basePath + name)));
            //OutputStream is inside response, get OutputStream object
            ServletOutputStream outputStream = response.getOutputStream();
            //Set the type of outputStream
            response.setContentType("image/jpeg");
            //Read the byteStream
            //Set initial length
            int len = 0;
            //Set length of byteArray
            byte[] bytes = new byte[1024];
            while ((len = bufferedInputStream.read(bytes)) != -1) {
                //ByteStream corresponding request字节流相应请求
                outputStream.write(bytes, 0, len);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
