package edu.bsu.cs222;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;
public class PlayerTest {
    @Test
    void getScoreTest(){
        Player Burrow = new Player();
        Burrow.setPassTD(3); //12 points
        Burrow.setPassYd(250); //10 points
        Burrow.setRushYd(30);//3 points
        Burrow.setReceptions(2);//2 points
        Burrow.setReceivingYd(30);//3 points
        Burrow.setReceivingTD(0);
        Burrow.setRushTD(1);//7 points
        Burrow.setFumbles(1);//-2 points
        Burrow.setInterceptions(1);//-2 points
        Assertions.assertEquals(33, Burrow.getScore());
    }
    @Test
    void getCompletionPCTTest(){
        Player Burrow = new Player();
        Burrow.setCompletions(25);
        Burrow.setPassAtt(40);
        Assertions.assertEquals(0.625, Burrow.getCompletionPCT());
    }
    @Test
    void getCompletionPCTRoundDownTest(){
        Player Burrow = new Player();
        Burrow.setCompletions(25);
        Burrow.setPassAtt(39);
        Assertions.assertEquals(0.641, Burrow.getCompletionPCT());
    }
    @Test
    void getCompletionPCTRoundUpTest(){
        Player Burrow = new Player();
        Burrow.setCompletions(25);
        Burrow.setPassAtt(37);
        Assertions.assertEquals(0.676, Burrow.getCompletionPCT());
    }

    @Test
    void testShortNameConstructing(){
        Player chris = new Player("Chris Burke");
        Assertions.assertEquals("C. Burke", chris.getShortName());

    }
}
