package learning;

import com.example.kukyemarketclone.controller.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
public class WebMvcTest {

    @InjectMocks
    TestController testController;
    MockMvc mockMvc;//컨트롤러로 요청을 보내기 위해

    @Controller //  테스트 용도 컨트롤러 :result필드가 포함되어 있지 않다는걸 검증해야함
    public static class TestController {
        @GetMapping("/test/ignore-null-value")
        public Response ignoreNullValueTest(){
            return Response.success();
        }
    }

    @BeforeEach
    void beforeEach(){  //TestController띄우기 = MockMvc로 컨트롤러에 요청을 보내서 테스트 가능
        mockMvc = MockMvcBuilders.standaloneSetup(testController).build();
    }

    @Test
    void ignoreNullValueInJsonResponseTest()throws Exception {
        mockMvc.perform( //mockMvc.perform로 요청을 보내고 검증,
                get("/test/ignore-null-value"))
                .andExpect(status().isOk())//응답코드 200확인
                .andExpect(jsonPath("$.result").doesNotExist()); //result필드 없음 확인
    }

}
