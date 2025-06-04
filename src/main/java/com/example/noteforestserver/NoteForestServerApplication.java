package com.example.noteforestserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NoteForestServerApplication {



    public static void main(String[] args) {
//        String currentDir = System.getProperty("user.dir");

//        System.out.println(currentDir);
        SpringApplication.run(NoteForestServerApplication.class, args);
    }

}
