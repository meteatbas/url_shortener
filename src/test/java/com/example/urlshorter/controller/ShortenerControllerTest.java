package com.example.urlshorter.controller;

import com.example.urlshorter.dto.OutputDTO;
import com.example.urlshorter.dto.Status;
import com.example.urlshorter.service.ShortenerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.example.urlshorter.constants.Constant.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShortenerController.class)
class ShortenerControllerTest {
    private static final String LONG_LINK_GOOGlE = "https://www.google.com/";
    private static final String LONG_LINK_FB = "https://www.facebook.com/";
    private static final String SHORT_LINK_GOOGLE = "vat.sl/1";
    private static final String SHORT_LINK_FB = "vat.sl/2";

    private static final String BASE_URL = "/v1";
    private static final String GET_ALL_LINKS_URL = "/";
    private static final String SHORTEN_URL = "/shorten";
    private static final String GET_ORIGINAL_URL = "/original";

    private static final String SHORTEN_INPUT_DTO = "{\n" +
            "    \"link\": \"https://www.google.com/\"\n" +
            "}";

    private static final String SHORTEN_INPUT_DTO_INVALID = "{\n" +
            "    \"link\": \"\"\n" +
            "}";

    private static final String GET_ORIGINAL_INPUT_DTO = "{\n" +
            "    \"link\": \"vat.sl/1\"\n" +
            "}";


    @Autowired
    MockMvc mockMvc;

    @MockBean
    ShortenerService shortenerService;

    @Test
    void testGetAllLinks() throws Exception {
        List<OutputDTO> outputDTOList = new ArrayList<>();
        outputDTOList.add(new OutputDTO(LONG_LINK_GOOGlE, SHORT_LINK_GOOGLE));
        outputDTOList.add(new OutputDTO(LONG_LINK_FB, SHORT_LINK_FB));
        Mockito.when(shortenerService.getAllLinks()).thenReturn(outputDTOList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + GET_ALL_LINKS_URL)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONAssert.assertEquals(
                "[{\"longLink\":\"https://www.google.com/\",\"shortLink\":\"vat.sl/1\"},{\"longLink\":\"https://www.facebook.com/\",\"shortLink\":\"vat.sl/2\"}]", response, JSONCompareMode.LENIENT);
    }

    @Test
    void testGetAllLinksEmptyResult() throws Exception {
        List<OutputDTO> outputDTOList = new ArrayList<>();
        Mockito.when(shortenerService.getAllLinks()).thenReturn(outputDTOList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + GET_ALL_LINKS_URL)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONAssert.assertEquals(
                "[]", response, JSONCompareMode.LENIENT);
    }

    @Test
    void testShorten() throws Exception {
        OutputDTO outputDTO = new OutputDTO(Status.SUCCESS, GENERATED_SHORT_LINK, LONG_LINK_GOOGlE, SHORT_LINK_GOOGLE);
        Mockito.when(shortenerService.shorten(Mockito.any())).thenReturn(outputDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + SHORTEN_URL)
                        .content(SHORTEN_INPUT_DTO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONAssert.assertEquals(
                "{\n" +
                        "    \"status\": \"SUCCESS\",\n" +
                        "    \"message\": \"Generated short link\",\n" +
                        "    \"longLink\": \"https://www.google.com/\",\n" +
                        "    \"shortLink\": \"vat.sl/1\"\n" +
                        "}", response, JSONCompareMode.LENIENT);
    }

    @Test
    void testShortenMethodArgumentNotValidException() throws Exception {

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + SHORTEN_URL)
                        .content(SHORTEN_INPUT_DTO_INVALID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONAssert.assertEquals(
                "{\"requestViolations\":[{\"fieldName\":\"link\",\"errorMessage\":\"must match \\\"[(http(s)?):\\\\/\\\\/(www\\\\.)?a-zA-Z0-9@:%._\\\\+~#=]{2,256}\\\\.[a-z]{2,6}\\\\b([-a-zA-Z0-9@:%_\\\\+.~#?&//=]*)\\\"\"}]}", response, JSONCompareMode.LENIENT);
    }

    @Test
    void testShortenInternalServerError() throws Exception {
        OutputDTO outputDTO = new OutputDTO(Status.SUCCESS, GENERATED_SHORT_LINK, LONG_LINK_GOOGlE, SHORT_LINK_GOOGLE);
        Mockito.when(shortenerService.shorten(Mockito.any())).thenReturn(outputDTO);
        Mockito.when(shortenerService.shorten(Mockito.any())).thenThrow();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + SHORTEN_URL)
                        .content(SHORTEN_INPUT_DTO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONAssert.assertEquals(
                "{\"errorMessage\":\"Please check logs for more details\"}", response, JSONCompareMode.LENIENT);
    }

    @Test
    void testGetOriginalSuccess() throws Exception {
        OutputDTO outputDTO = new OutputDTO(Status.SUCCESS, RETRIEVED_ORIGINAL_LINK, LONG_LINK_GOOGlE, SHORT_LINK_GOOGLE);
        Mockito.when(shortenerService.getOriginal(Mockito.any())).thenReturn(outputDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + GET_ORIGINAL_URL)
                        .content(GET_ORIGINAL_INPUT_DTO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONAssert.assertEquals(
                "{\n" +
                        "    \"status\": \"SUCCESS\",\n" +
                        "    \"message\": \"Retrieved original link\",\n" +
                        "    \"longLink\": \"https://www.google.com/\",\n" +
                        "    \"shortLink\": \"vat.sl/1\"\n" +
                        "}", response, JSONCompareMode.LENIENT);
    }

    @Test
    void testGetOriginalFailure() throws Exception {
        OutputDTO outputDTO = new OutputDTO(Status.FAILED, SHORT_LINK_NOT_FOUND, null, SHORT_LINK_GOOGLE);
        Mockito.when(shortenerService.getOriginal(Mockito.any())).thenReturn(outputDTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + GET_ORIGINAL_URL)
                        .content(GET_ORIGINAL_INPUT_DTO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONAssert.assertEquals(
                "{\"status\":\"FAILED\",\"message\":\"Short link not found\",\"shortLink\":\"vat.sl/1\"}", response, JSONCompareMode.LENIENT);
    }

    @Test
    void testGetOriginalMethodArgumentNotValidException() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + GET_ORIGINAL_URL)
                        .content(SHORTEN_INPUT_DTO_INVALID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONAssert.assertEquals(
                "{\"requestViolations\":[{\"fieldName\":\"link\",\"errorMessage\":\"must match \\\"[(http(s)?):\\\\/\\\\/(www\\\\.)?a-zA-Z0-9@:%._\\\\+~#=]{2,256}\\\\.[a-z]{2,6}\\\\b([-a-zA-Z0-9@:%_\\\\+.~#?&//=]*)\\\"\"}]}", response, JSONCompareMode.LENIENT);
    }

    @Test
    void testGetOriginalInternalServerError() throws Exception {
        OutputDTO outputDTO = new OutputDTO(Status.SUCCESS, RETRIEVED_ORIGINAL_LINK, LONG_LINK_GOOGlE, SHORT_LINK_GOOGLE);
        Mockito.when(shortenerService.getOriginal(Mockito.any())).thenReturn(outputDTO);
        Mockito.when(shortenerService.getOriginal(Mockito.any())).thenThrow();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + GET_ORIGINAL_URL)
                        .content(GET_ORIGINAL_INPUT_DTO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONAssert.assertEquals(
                "{\"errorMessage\":\"Please check logs for more details\"}", response, JSONCompareMode.LENIENT);
    }
}
