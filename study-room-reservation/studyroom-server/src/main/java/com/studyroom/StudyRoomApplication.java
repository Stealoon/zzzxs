package com.studyroom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableCaching
@EnableScheduling
public class StudyRoomApplication {

    private static final Logger log = LoggerFactory.getLogger(StudyRoomApplication.class);
    public static void main(String[] args) {

        SpringApplication.run(StudyRoomApplication.class, args);
        log.info("server started");
    }
}
