package com.example.springwebflux.service;

import com.example.springwebflux.domain.Anime;
import com.example.springwebflux.repository.AnimeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

@ExtendWith(SpringExtension.class)
public class AnimeServiceTest {

    @InjectMocks
    private AnimeService animeService;

    @Mock
    private AnimeRepository animeRepositoryMock;

    @BeforeAll
    public static void blockHoundSetup () {
        BlockHound.install();
    }

    @Test
    public void blockHoundWorks() {
        try {
            FutureTask<?> task = new FutureTask<>(() -> {
                Thread.sleep(0);
                return "";
            });
            Schedulers.parallel().schedule(task);
            task.get(10, TimeUnit.SECONDS);
            Assertions.fail("should fail");
        } catch (Exception e) {
            Assertions.assertTrue(e.getCause() instanceof BlockingOperationError);
        }
    }

    @Test
    @DisplayName("Find all return a Flux of anime")
    public void findAllReturnAFluxOfAnime() {
        // scenery
        BDDMockito.when(animeRepositoryMock.findAll())
                .thenReturn(Flux.just(animeValid()));
        // execution
        // result
        StepVerifier.create(animeService.findAll())
                .expectSubscription()
                .expectNext(animeValid())
                .verifyComplete();
    }

    @Test
    @DisplayName("Find By Id return a Mono with anime when it exist")
    public void findByIdReturnAMonoOfAnimeWhenSucess () {
        // scenery
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.just(animeValid()));
        // execution
        // result
        StepVerifier.create(animeService.findById(1))
                .expectSubscription()
                .expectNext(animeValid())
                .verifyComplete();
    }

    @Test
    @DisplayName("Find By Id returns a Mono Error when anime does not exist")
    public void findByIdReturnsAMonoErrorWhenEmptyMonoIsReturned () {
        // scenery
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());
        // execution
        // result
        StepVerifier.create(animeService.findById(1))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("Save creates an anime when sucessfull")
    public void saveCreateAnimeWhenSucessfull() {
        // scenery
        BDDMockito.when(animeRepositoryMock.save(animeToBeSaved()))
                .thenReturn(Mono.just(animeValid()));
        // execution
        // result
        StepVerifier.create(animeService.save(animeToBeSaved()))
                .expectSubscription()
                .expectNext(animeValid())
                .verifyComplete();
    }

    @Test
    @DisplayName("Delete removes the anime when sucessfull")
    public void deleteRemovesTheAnimeWhenSucessfull () {
        // scenery
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.just(animeValid()));
        BDDMockito.when(animeRepositoryMock.delete(ArgumentMatchers.any(Anime.class)))
                .thenReturn(Mono.empty());
        // execution
        // result
        StepVerifier.create(animeService.delete(1))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @DisplayName("Delete returns Mono error when anime does not exist")
    public void deleteReturnMonoErrorWhenAnimeDoesNotExist () {
        // scenery
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());
        // execution
        // result
        StepVerifier.create(animeService.delete(1))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("Update save updated anime and return empty mono when successfull")
    public void updateSaveUpdatedAnimeAndReturnEmptyMonoWhenSucessfull () {
        // scenery
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.just(animeValid()));
        BDDMockito.when(animeRepositoryMock.save(animeValid()))
                .thenReturn(Mono.empty());
        // execution
        // result
        StepVerifier.create(animeService.update(animeValid()))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @DisplayName("Update return Mono erro when anime does not exists")
    public void updateReturnMonoErroWhenAnimeDoesNotExists () {
        // scenery
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());
        // execution
        // result
        StepVerifier.create(animeService.update(animeValid()))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    private Anime animeToBeSaved() {
        return Anime.builder().name("Hellsing").build();
    }

    private Anime animeValid() {
        return Anime.builder().id(1).name("Hellsing").build();
    }
}
