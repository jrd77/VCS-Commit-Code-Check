package com.atzuche.violation.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.Calendar;
import java.util.UUID;

/**
 * 文件上传
 * 
 * @author shl
 * @date 2014年5月29日上午10:17:40
 */
@Slf4j
public class FileUtil {

	private final static String UPLOAD_PATH = "upload/";

    /**
     * 上传文件到当前服务器
     * 表单中的文件上传
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static String uploadFile(MultipartFile file, ServletContext context) {
        String result = "";
        try {
            String path = context.getRealPath("/");
            String oldFileName = file.getOriginalFilename();
            String fileType = oldFileName.substring(oldFileName.lastIndexOf("."));
            String newFileName = UPLOAD_PATH
                    + Calendar.getInstance().get(Calendar.YEAR) + "/"
                    + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "/"
                    + Calendar.getInstance().get(Calendar.DATE) + "/" + UUID.randomUUID() + fileType;

            File file2 = new File(path, newFileName);
            File dir = file2.getParentFile();
            if (!dir.exists())
                dir.mkdirs();
            file.transferTo(file2);
            result = newFileName;
            System.out.println("file:" + file2.getAbsolutePath());
        } catch (Exception e) {
            log.error("上传文件失败,e--->>>", e);
            return result;
        }
        return result;
    }
}
