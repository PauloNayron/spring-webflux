package com.example.springwebflux.controller;

import com.example.springwebflux.domain.Anime;
import com.example.springwebflux.exception.CustomAttributes;
import com.example.springwebflux.repository.AnimeRepository;
import com.example.springwebflux.service.AnimeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

@ExtendWith(SpringExtension.class)
@WebFluxTest
@Import({AnimeService.class, CustomAttributes.class})
class AnimeControllerTestComponent {

    @MockBean
    private AnimeRepository animeRepositoryMock;

    @Autowired
    private WebTestClient testClient;

    private final Anime anime = Anime.builder().id(1).name("Hellsing").build();

    @BeforeAll
    public static void blockHoundSetup () {
        BlockHound.install();
    }

    @Test
    public void blockHoundWorks () {
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
    @DisplayName("List all returns a Flux of anime")
    public void listAllReturnsAFluxOfAnime () {
        // scenery
        BDDMockito.when(animeRepositoryMock.findAll())
                .thenReturn(Flux.just(anime));
        // execution
        // verify
        testClient
                .get()
                .uri("/anime")
                .exchange()
                .expectStatus()
                    .isOk()
                .expectBodyList(Anime.class)
                    .hasSize(1)
                    .contains(anime);
    }

    @Test
    @DisplayName("Find by id returns a Mono with Anime when it exists")
    public void findByIdReturnAMonoWithAnimeWhenItExists () {
        // scenery
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.just(anime));
        // execution
        // verify
        testClient
                .get()
                    .uri("/anime/{id}", 1)
                .exchange()
                .expectStatus()
                    .isOk()
                .expectBody(Anime.class)
                    .isEqualTo(anime);
    }

    @Test
    @DisplayName("Find by id return Mono error when anime does not exist")
    public void findByIdReturnMonoErrorWhenAnimeDoesNotExist () {
        // scenery
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());
        // execution
        // verify
        testClient
                .get()
                    .uri("anime/{id}", 1)
                .exchange()
                .expectStatus()
                    .isNotFound()
                .expectBody()
                    .jsonPath("$.status").isEqualTo(404)
                    .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
    }

    @Test
    @DisplayName("Save creates an anime when successful")
    public void saveCreatesAnAnimeWhenSuccessful () {
        // scenery
        BDDMockito.when(animeRepositoryMock.save(ArgumentMatchers.any(Anime.class)))
                .thenReturn(Mono.just(anime));
        // execution
        // verify
        testClient
                .post()
                .uri("/anime")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(animeToBeSaved()))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Anime.class)
                .isEqualTo(anime);
    }

    @Test
    @DisplayName("Save return mono error when name is empty")
    public void saveReturnErrorWhenNameIsEmpty () {
        // scenery
        // execution
        // verify
        testClient
                .post()
                .uri("/anime")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(animeToBeSavedInvalid()))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody().jsonPath("$.status").isEqualTo(400);
    }

    @Test
    @DisplayName("Delete removes the anime when successful")
    public void deleteRemovesTheAnimeWhenSuccessful () {
        // scenery
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.just(anime));
        BDDMockito.when(animeRepositoryMock.delete(ArgumentMatchers.any(Anime.class)))
                .thenReturn(Mono.empty());
        // execution
        // verify
        testClient
                .delete()
                .uri("/anime/{id}", 1)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("Delete returns Mono Error when anime does not exist")
    public void deleteReturnMonoErrorWhenAnimeDoesNotExist () {
        // scenery
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());
        // execution
        // verify
        testClient
                .delete()
                .uri("/anime/{id}", 1)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                    .jsonPath("$.status").isEqualTo(404)
                    .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
    }

    @Test
    @DisplayName("Update save updated anime and returns empty Mono when successful")
    public void updateSaveUpdatedAnimeAndReturnsEmptyMonoWhenSuccessfull () {
        // scenery
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.just(anime));
        BDDMockito.when(animeRepositoryMock.save(ArgumentMatchers.any(Anime.class)))
                .thenReturn(Mono.empty());
        // execution
        // verify
        testClient
                .put()
                .uri("/anime/{id}", 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(anime))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("Update returns Mono Error when does not exist")
    public void updateReturnsMonoErrorWhenDoesNotExist () {
        // scenery
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());
        // execution
        // verify
        testClient
                .put()
                .uri("/anime/{id}", 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(anime))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                    .jsonPath("$.status").isEqualTo(404)
                    .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
    }

    private Anime animeToBeSavedInvalid() {
        return Anime.builder().name("").build();
    }

    private Anime animeToBeSaved() {
        return Anime.builder().name("Hellsing").build();
    }
}