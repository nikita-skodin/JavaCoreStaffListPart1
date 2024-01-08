package com.skodin;

import com.skodin.application.Application;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Main {
    public static void main(String[] args) {

        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler());

        log.info("Application is running");

        new Application().start();

        log.info("Application is stopped");
    }

    static class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.out.println("Unexpected error\nExit");
            log.fatal("Uncaught exception: " + e);
        }
    }

}
