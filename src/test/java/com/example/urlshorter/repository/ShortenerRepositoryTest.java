package com.example.urlshorter.repository;

import com.example.urlshorter.dto.OutputDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class ShortenerRepositoryTest {
    private static final String LONG_LINK_GOOGlE = "https://www.google.com/";
    private static final String LONG_LINK_FB = "https://www.facebook.com/";
    private static final String SHORT_LINK_GOOGLE = "vat.sl/1";
    private static final String SHORT_LINK_FB = "vat.sl/2";

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ShortenerRepository shortenerRepository;

    @BeforeEach
    private void initialise() {
        ReflectionTestUtils.setField(shortenerRepository, "jdbcTemplate", jdbcTemplate);
    }

    @Test
    void testGetAllLinks() {
        List<OutputDTO> outputDTOList = new ArrayList<>();
        outputDTOList.add(new OutputDTO(LONG_LINK_GOOGlE, SHORT_LINK_GOOGLE));
        outputDTOList.add(new OutputDTO(LONG_LINK_FB, SHORT_LINK_FB));
        Mockito.when(jdbcTemplate.query(Mockito.anyString(), (RowMapper<Object>) Mockito.any())).thenReturn(Collections.unmodifiableList(outputDTOList));
        List<OutputDTO> outputDTOS = shortenerRepository.getAllLinks();
        Assertions.assertArrayEquals(outputDTOList.toArray(), outputDTOS.toArray());
    }

    @Test
    void testFindByLongLink() {
        OutputDTO outputDTO = new OutputDTO(LONG_LINK_GOOGlE, SHORT_LINK_GOOGLE);
        Mockito.when(jdbcTemplate.queryForObject(Mockito.anyString(), (RowMapper<Object>) Mockito.any(), Mockito.any())).thenReturn(outputDTO);
        Optional<String> resultOptional = shortenerRepository.findByLongLink(LONG_LINK_GOOGlE);
        Assertions.assertEquals(SHORT_LINK_GOOGLE, resultOptional.get());
    }

    @Test
    void testFindByLongLinkEmptyResultDataAccessException() {
        OutputDTO outputDTO = new OutputDTO(LONG_LINK_GOOGlE, SHORT_LINK_GOOGLE);
        Mockito.when(jdbcTemplate.queryForObject(Mockito.anyString(), (RowMapper<Object>) Mockito.any(), Mockito.any())).thenReturn(outputDTO);
        Mockito.when(jdbcTemplate.queryForObject(Mockito.anyString(), (RowMapper<Object>) Mockito.any(), Mockito.any())).thenThrow(new EmptyResultDataAccessException(1));
        Optional<String> resultOptional = shortenerRepository.findByLongLink(LONG_LINK_GOOGlE);
        Assertions.assertTrue(resultOptional.isEmpty());
    }

    @Test
    void testFindByShortLink() {
        OutputDTO outputDTO = new OutputDTO(LONG_LINK_GOOGlE, SHORT_LINK_GOOGLE);
        Mockito.when(jdbcTemplate.queryForObject(Mockito.anyString(), (RowMapper<Object>) Mockito.any(), Mockito.any())).thenReturn(outputDTO);
        Optional<String> resultOptional = shortenerRepository.findByShortLink(SHORT_LINK_GOOGLE);
        Assertions.assertEquals(LONG_LINK_GOOGlE, resultOptional.get());
    }

    @Test
    void testFindByShortLinkEmptyResultDataAccessException() {
        OutputDTO outputDTO = new OutputDTO(LONG_LINK_GOOGlE, SHORT_LINK_GOOGLE);
        Mockito.when(jdbcTemplate.queryForObject(Mockito.anyString(), (RowMapper<Object>) Mockito.any(), Mockito.any())).thenReturn(outputDTO);
        Mockito.when(jdbcTemplate.queryForObject(Mockito.anyString(), (RowMapper<Object>) Mockito.any(), Mockito.any())).thenThrow(new EmptyResultDataAccessException(1));
        Optional<String> resultOptional = shortenerRepository.findByShortLink(SHORT_LINK_GOOGLE);
        Assertions.assertTrue(resultOptional.isEmpty());
    }

    @Test
    void testGetMaxId() {;
        Mockito.when(jdbcTemplate.queryForObject(Mockito.anyString(), (Class<Object>) Mockito.any())).thenReturn(1);
        Integer id = shortenerRepository.getMaxId();
        Assertions.assertEquals(1, id);
    }

    @Test
    void testGetMaxIdNull() {;
        Mockito.when(jdbcTemplate.queryForObject(Mockito.anyString(), (Class<Object>) Mockito.any())).thenReturn(null);
        Integer id = shortenerRepository.getMaxId();
        Assertions.assertNull(id);
    }
}
