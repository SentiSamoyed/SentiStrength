package web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import web.entity.vo.TextRequestVO;
import web.util.TextRequestVOGenerator;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author tanziyue
 * @date 2023/4/6
 * @description 分析 Controller 的测试
 */
@SpringBootTest
public class AnalysisControllerTest {

  MockMvc mockMvc;
  @Autowired
  WebApplicationContext webApplicationContext;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Test
  void controllerTest() throws Exception {
    TextRequestVO requestVO = TextRequestVOGenerator.generate(false);
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    mockMvc
        .perform(
            post("/sentiment/analysis/text")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ow.writeValueAsString(requestVO))
        )
        .andExpect(status().isOk())
        .andDo(print());
  }
}
