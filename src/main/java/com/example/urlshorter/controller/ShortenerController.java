package com.example.urlshorter.controller;

import com.example.urlshorter.dto.InputURLDTO;
import com.example.urlshorter.dto.OutputDTO;
import com.example.urlshorter.service.ShortenerService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * A REST controller for handling all operations related to URL Shortener.
 */

@Slf4j
@RestController
@RequestMapping(value = "/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class ShortenerController {
    @Autowired
    private ShortenerService shortenerService;


    @GetMapping(value = "/get-all")
    public ResponseEntity<List<OutputDTO>> getAllLinks() {
        return ResponseEntity.ok(shortenerService.getAllLinks());
    }


    @PostMapping(value = "/shorten", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OutputDTO> shorten(@Valid @RequestBody InputURLDTO inputURLDTO) {
        return ResponseEntity.ok(shortenerService.shorten(inputURLDTO));
    }

    @PostMapping(value = "/original", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OutputDTO> getOriginal(@Valid @RequestBody InputURLDTO inputURLDTO) {
        return ResponseEntity.ok(shortenerService.getOriginal(inputURLDTO));
    }

}
