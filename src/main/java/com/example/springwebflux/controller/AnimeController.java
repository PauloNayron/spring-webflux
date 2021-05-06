package com.example.springwebflux.controller;

import com.example.springwebflux.domain.Anime;
import com.example.springwebflux.repository.AnimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
@RequestMapping("/anime")
@Slf4j
public class AnimeController {
    private final AnimeRepository animeRepository;
    @GetMapping
    public Flux<Anime> listAll() {
        return animeRepository.findAll();
    }
}