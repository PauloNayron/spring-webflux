package com.example.springwebflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;

@SpringBootApplication
public class SpringWebfluxApplication {
    static {
        BlockHound.install(builder -> {
            builder.allowBlockingCallsInside("java.util.UUID", "randomUUID");
        });
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringWebfluxApplication.class, args);
    }

}
