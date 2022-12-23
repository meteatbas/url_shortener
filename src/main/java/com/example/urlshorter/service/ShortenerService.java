package com.example.urlshorter.service;

import com.example.urlshorter.dto.InputURLDTO;
import com.example.urlshorter.dto.OutputDTO;
import com.example.urlshorter.dto.Status;
import com.example.urlshorter.repository.ShortenerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.urlshorter.constants.Constant.*;

/**
 * A service class having business logic for url shortener
 */

@Service
@Slf4j
public class ShortenerService {
    @Value("${app.base62digits}")
    private String[] base62digits;

    @Value("${app.base.url}")
    private String url;

    @Autowired
    private ShortenerRepository shortenerRepository;

    /**
     * Returns short link for the given long link.
     * @param inputURLDTO InputURLDTO
     * @return ShortenOutputDTO
     */
    public OutputDTO shorten(InputURLDTO inputURLDTO) {
        Optional<String> shortLinkOptional = shortenerRepository.findByLongLink(inputURLDTO.getLink());
        if (shortLinkOptional.isPresent()) {
            return new OutputDTO(Status.SUCCESS, GENERATED_SHORT_LINK, inputURLDTO.getLink(), shortLinkOptional.get());
        }
        Integer id = shortenerRepository.getMaxId();
        log.debug("Id:" + id);
        if (id == null) {
            id = 1;
        } else {
            id++;
        }
        String shortLink = generateShortLink(id);
        shortenerRepository.insert(id, inputURLDTO.getLink(), shortLink);
        return new OutputDTO(Status.SUCCESS, GENERATED_SHORT_LINK, inputURLDTO.getLink(), shortLink);
    }

    /**
     * Returns long link for the given short link.
     * @param inputURLDTO InputURLDTO
     * @return ShortenOutputDTO
     */
    public OutputDTO getOriginal(InputURLDTO inputURLDTO) {
        Optional<String> longLinkOptional = shortenerRepository.findByShortLink(inputURLDTO.getLink());
        return longLinkOptional.map(s -> new OutputDTO(Status.SUCCESS, RETRIEVED_ORIGINAL_LINK, s, inputURLDTO.getLink())).orElseGet(() -> new OutputDTO(Status.FAILED, SHORT_LINK_NOT_FOUND, null, inputURLDTO.getLink()));
    }

    /**
     * Returns all the long links and short links in db.
     * @return List<ShortenOutputDTO>
     */
    public List<OutputDTO> getAllLinks() {
        return shortenerRepository.getAllLinks();
    }

    /**
     * Generates short link from an int.
     * @param id int
     * @return String
     */
    private String generateShortLink(int id) {
        log.debug("id:" + id);
        StringBuilder sb = new StringBuilder();

        while (id != 0) {
            String c = base62digits[id%62];
            sb.append(c);
            id = id/62;
        }
        sb.reverse();
        log.debug("uri:" + sb);
        return url + sb;
    }
}
