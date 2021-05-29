package com.example.springwebflux.controller;

import com.example.springwebflux.domain.Anime;
import com.example.springwebflux.service.AnimeService;
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
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {
    @InjectMocks
    private AnimeController animeController;

    @Mock
    public AnimeService animeServiceMock;

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
    @DisplayName("findAll returns a flux of anime")
    public void findAllReturnsAFluxOfAnime() {
        // scenery
        BDDMockito.when(animeServiceMock.findAll())
                .thenReturn(Flux.just(animeValid()));
        // execution
        // result
        StepVerifier.create(animeController.listAll())
                .expectSubscription()
                .expectNext(animeValid())
                .verifyComplete();
    }

    @Test
    @DisplayName("Find by Id returns a Mono with anime when it exists")
    public void findByIdReturnsAMonoWithAnimeWhenItExists () {
        // scenery
        BDDMockito.when(animeServiceMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.just(animeValid()));
        // execution
        // result
        StepVerifier.create(animeController.findById(1))
                .expectSubscription()
                .expectNext(animeValid())
                .verifyComplete();
    }

    @Test
    @DisplayName("Save creates an anime when successfull")
    public void saveCreatesAnimeWhenSuccessfull () {
        // scenery
        BDDMockito.when(animeServiceMock.save(ArgumentMatchers.any(Anime.class)))
                .thenReturn(Mono.just(animeValid()));
        // execution
        // result
        StepVerifier.create(animeController.save(animeToBeSaved()))
                .expectSubscription()
                .expectNext(animeValid())
                .verifyComplete();
    }

    @Test
    @DisplayName("Delete removes the anime when successfull")
    public void deleteRemovesTheAnimeWhenSuccessfull () {
        // scenery
        BDDMockito.when(animeServiceMock.delete(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());
        // execution
        // result
        StepVerifier.create(animeController.delete(1))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @DisplayName("Update save updated anime and returns empty mono when successfull")
    public void updateSaveUpdatedAnimeAndReturnsEmptyMonoWhenSuccessfull () {
        // scenery
        BDDMockito.when(animeServiceMock.update(ArgumentMatchers.any(Anime.class)))
                .thenReturn(Mono.empty());
        // executation
        // result
        StepVerifier.create(animeController.update(1, animeToBeSaved()))
                .expectSubscription()
                .verifyComplete();
    }

    private Anime animeToBeSaved() {
        return Anime.builder().name("Hellsing").build();
    }

    private Anime animeValid() {
        return Anime.builder().id(1).name("Hellsing").build();
    }
}