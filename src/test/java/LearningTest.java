import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LearningTest {

    @Test
    void testAccessToAPIKey(){
        Assertions.assertNotNull(Config.API_KEY);
    }
}
