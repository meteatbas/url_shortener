package com.example.urlshorter.service;

import com.example.urlshorter.dto.InputURLDTO;
import com.example.urlshorter.dto.OutputDTO;
import com.example.urlshorter.dto.Status;
import com.example.urlshorter.repository.ShortenerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.urlshorter.constants.Constant.*;

@SpringBootTest
@AutoConfigureTestDatabase
class ShortenerServiceTest {
    private static final String LONG_LINK_GOOGlE = "https://www.google.com/";
    private static final String LONG_LINK_FB = "https://www.facebook.com/";
    private static final String SHORT_LINK_GOOGLE = "vat.sl/1";
    private static final String SHORT_LINK_FB = "vat.sl/2";

    @MockBean
    ShortenerRepository shortenerRepository;

    @Autowired
    ShortenerService shortenerService;

    @Test
    void testGetAllLinks() {
        List<OutputDTO> outputDTOList = new ArrayList<>();
        outputDTOList.add(new OutputDTO(LONG_LINK_GOOGlE, SHORT_LINK_GOOGLE));
        outputDTOList.add(new OutputDTO(LONG_LINK_FB, SHORT_LINK_FB));
        Mockito.when(shortenerRepository.getAllLinks()).thenReturn(outputDTOList);

        List<OutputDTO> outputDTOS = shortenerService.getAllLinks();
        Assertions.assertArrayEquals(outputDTOList.toArray(), outputDTOS.toArray());
    }

    @Test
    void testGetOriginalSuccess() {
        Optional<String> stringOptional = Optional.of(LONG_LINK_GOOGlE);
        Mockito.when(shortenerRepository.findByShortLink(Mockito.any())).thenReturn(stringOptional);

        InputURLDTO inputURLDTO = new InputURLDTO();
        inputURLDTO.setLink(SHORT_LINK_GOOGLE);

        OutputDTO result = shortenerService.getOriginal(inputURLDTO);
        Assertions.assertEquals(Status.SUCCESS, result.getStatus());
        Assertions.assertEquals(RETRIEVED_ORIGINAL_LINK, result.getMessage());
        Assertions.assertEquals(LONG_LINK_GOOGlE, result.getLongLink());
        Assertions.assertEquals(SHORT_LINK_GOOGLE, result.getShortLink());
    }

    @Test
    void testGetOriginalFailure() {
        Optional<String> stringOptional = Optional.empty();
        Mockito.when(shortenerRepository.findByShortLink(Mockito.any())).thenReturn(stringOptional);

        InputURLDTO inputURLDTO = new InputURLDTO();
        inputURLDTO.setLink(SHORT_LINK_GOOGLE);

        OutputDTO result = shortenerService.getOriginal(inputURLDTO);
        Assertions.assertEquals(Status.FAILED, result.getStatus());
        Assertions.assertEquals(SHORT_LINK_NOT_FOUND, result.getMessage());
        Assertions.assertNull(result.getLongLink());
        Assertions.assertEquals(SHORT_LINK_GOOGLE, result.getShortLink());
    }

    @Test
    void testShortenAlreadyPresent() {
        Optional<String> stringOptional = Optional.of(SHORT_LINK_GOOGLE);
        Mockito.when(shortenerRepository.findByLongLink(Mockito.any())).thenReturn(stringOptional);

        InputURLDTO inputURLDTO = new InputURLDTO();
        inputURLDTO.setLink(LONG_LINK_GOOGlE);

        OutputDTO result = shortenerService.shorten(inputURLDTO);
        Assertions.assertEquals(Status.SUCCESS, result.getStatus());
        Assertions.assertEquals(GENERATED_SHORT_LINK, result.getMessage());
        Assertions.assertEquals(LONG_LINK_GOOGlE, result.getLongLink());
        Assertions.assertEquals(SHORT_LINK_GOOGLE, result.getShortLink());
    }

    @Test
    void testShorten() {
        Optional<String> stringOptional = Optional.empty();
        Mockito.when(shortenerRepository.findByLongLink(Mockito.any())).thenReturn(stringOptional);
        Mockito.when(shortenerRepository.getMaxId()).thenReturn(1);

        InputURLDTO inputURLDTO = new InputURLDTO();
        inputURLDTO.setLink(LONG_LINK_GOOGlE);

        OutputDTO result = shortenerService.shorten(inputURLDTO);
        Assertions.assertEquals(Status.SUCCESS, result.getStatus());
        Assertions.assertEquals(GENERATED_SHORT_LINK, result.getMessage());
        Assertions.assertEquals(LONG_LINK_GOOGlE, result.getLongLink());
        Assertions.assertEquals(SHORT_LINK_FB, result.getShortLink());
    }

    @Test
    void testShortenIdNull() {
        Optional<String> stringOptional = Optional.empty();
        Mockito.when(shortenerRepository.findByLongLink(Mockito.any())).thenReturn(stringOptional);
        Mockito.when(shortenerRepository.getMaxId()).thenReturn(null);

        InputURLDTO inputURLDTO = new InputURLDTO();
        inputURLDTO.setLink(LONG_LINK_GOOGlE);

        OutputDTO result = shortenerService.shorten(inputURLDTO);
        Assertions.assertEquals(Status.SUCCESS, result.getStatus());
        Assertions.assertEquals(GENERATED_SHORT_LINK, result.getMessage());
        Assertions.assertEquals(LONG_LINK_GOOGlE, result.getLongLink());
        Assertions.assertEquals(SHORT_LINK_GOOGLE, result.getShortLink());
    }
}
