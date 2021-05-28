package com.example.springwebflux.service;

import com.example.springwebflux.domain.Anime;
import com.example.springwebflux.repository.AnimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AnimeService {
    private final AnimeRepository animeRepository;

    public Flux<Anime> findAll() {
        return animeRepository.findAll();
    }

    public Mono<Anime> findById(int id) {
        return animeRepository.findById(id)
                .switchIfEmpty(monoResponseStatusNotFoundException(id));
    }

    private <T> Mono<T> monoResponseStatusNotFoundException(int id) {
        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Anime ".concat(String.valueOf(id)).concat(" not found.")));
    }

    public Mono<Anime> save(Anime anime) {
        return animeRepository.save(anime);
    }
}
