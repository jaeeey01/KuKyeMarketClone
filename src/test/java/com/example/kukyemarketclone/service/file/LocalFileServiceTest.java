package com.example.kukyemarketclone.service.file;


import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class LocalFileServiceTest {
    LocalFileService localFileService = new LocalFileService();

    //테스트 단계에서 업로드성공여부를 확인할 수 있도록 테스트 전용 location 지정
    String testLocation = new File("src/test/resources/static").getAbsolutePath() + "/";

    @BeforeEach
    void beforeEach() throws IOException { //ReflectionTestUtils 을 이용하여 localFileService에 location을 주입해주고
        ReflectionTestUtils.setField(localFileService,"location",testLocation);
        FileUtils.cleanDirectory(new File(testLocation));//테스트 시작전에 testLocation의 모든 파일 제거
    }

    @Test
    void uploadTest(){
        //given
        MultipartFile file = new MockMultipartFile("myfile","myFile.txt", MediaType.TEXT_PLAIN_VALUE,"test".getBytes());
        String filename = "testFile.txt";

        //when
        localFileService.upload(file,filename);

        //then
        assertThat(isExists(testLocation + filename)).isTrue();
    }

    @Test
    void deleteTest(){
        //given
        MultipartFile file = new MockMultipartFile("myFile","myFile.txt",MediaType.TEXT_PLAIN_VALUE,"test".getBytes());
        String filename = "testFile.txt";
        localFileService.upload(file, filename);
        boolean before = isExists(testLocation + filename);

        //when
        localFileService.delete(filename);

        //then
        boolean after = isExists(testLocation + filename);
        assertThat(before).isTrue();
        assertThat(after).isFalse();
    }

    boolean isExists(String filePath) {
        return new File(filePath).exists();
    }

}