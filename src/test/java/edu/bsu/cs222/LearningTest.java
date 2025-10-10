package edu.bsu.cs222;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LearningTest {

    @Test
    void testAccessToAPIKey(){
        final String API_KEY = Dotenv.load().get("API_KEY");
        Assertions.assertNotNull(API_KEY);
    }
}
