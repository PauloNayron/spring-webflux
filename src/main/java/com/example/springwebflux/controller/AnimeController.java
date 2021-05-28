package com.example.springwebflux.controller;

import com.example.springwebflux.domain.Anime;
import com.example.springwebflux.service.AnimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/anime")
@Slf4j
public class AnimeController {
    private final AnimeService animeService;
    @GetMapping
    public Flux<Anime> listAll() {
        return animeService.findAll();
    }

    @GetMapping(path = "/{id}")
    public Mono<Anime> findById(@PathVariable int id) {
        return animeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Anime> save(@Valid @RequestBody Anime anime) {
        return animeService.save(anime);
    }
}
