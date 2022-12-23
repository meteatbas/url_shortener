package com.example.urlshorter.repository;

import com.example.urlshorter.dto.OutputDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * A repository class to interact with the db.
 */

@Slf4j
@Repository
public class ShortenerRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * Returns all long links and short links in the db.
     * @return List<ShortenOutputDTO>
     */
    public List<OutputDTO> getAllLinks() {
        return jdbcTemplate.query("select * from shortener", new BeanPropertyRowMapper<>(OutputDTO.class));
    }

    /**
     * Returns short link for the given long link.
     * @param longLink String
     * @return List<String>
     */
    public Optional<String> findByLongLink(String longLink) {
        try {
            OutputDTO result = jdbcTemplate.queryForObject("select * from shortener where long_link = ?",
                    new BeanPropertyRowMapper<>(OutputDTO.class),
                    longLink);
            return Optional.of(result.getShortLink());
        }
        catch (EmptyResultDataAccessException e) {
            log.debug("No record found in database for:"+ longLink, e);
            return Optional.empty();
        }
    }

    /**
     * Returns long link for the given short link.
     * @param shortLink String
     * @return List<String>
     */
    public Optional<String> findByShortLink(String shortLink) {
        try {
            OutputDTO result = jdbcTemplate.queryForObject("select * from shortener where short_link = ?",
                    new BeanPropertyRowMapper<>(OutputDTO.class),
                    shortLink);
            return Optional.of(result.getLongLink());
        }
        catch (EmptyResultDataAccessException e) {
            log.debug("No record found in database for:"+ shortLink, e);
            return Optional.empty();
        }
    }

    /**
     * Returns the value of maximum Id in the db.
     * @return Integer
     */
    public Integer getMaxId() {
        return jdbcTemplate.queryForObject("select max(id) from shortener", Integer.class);
    }

    /**
     * Returns long link for the given short link.
     * @param id Integer
     * @param longLink String
     * @param shortLink String
     */
    public void insert(Integer id, String longLink, String shortLink) {
        jdbcTemplate.update(
                "insert into shortener(id, long_link, short_link) values(?, ?, ?)",
                id, longLink, shortLink);
    }
}
