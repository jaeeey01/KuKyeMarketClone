package com.example.kukyemarketclone.service.file;

import com.example.kukyemarketclone.exception.FileUploadFailureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class LocalFileService implements FileService{

    @Value("${upload.image.location}")
    private String location;    // 파일 업로드 할 위치 주입받음

    @PostConstruct
    void postConstruct(){   //파일 업로드할 디렉토리 생성
        File dir = new File(location);
        if(!dir.exists()){
            dir.mkdir();
        }
    }

    @Override
    public void upload(MultipartFile file, String filename) {//실제 파일을 지정된 위치에 저장 해줌
        try{
            file.transferTo(new File(location + filename));
        }catch (IOException e){
        throw new FileUploadFailureException(e);
        }
    }

    @Override
    public void delete(String filename) {
        new File(location + filename).delete();
    }
}
