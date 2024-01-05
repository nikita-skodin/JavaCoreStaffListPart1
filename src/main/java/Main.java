import application.Application;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Main {

    // TODO think about command line arguments
    public static void main(String[] args) {
        log.info("Application is running");

        new Application().start();

        log.info("Application is stopped");
    }

}
