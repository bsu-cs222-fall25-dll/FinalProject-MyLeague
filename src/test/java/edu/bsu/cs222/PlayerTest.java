package edu.bsu.cs222;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.HashMap;

import static edu.bsu.cs222.Position.*;

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
    void testKickerPoints(){
        Player YoungHoe_Koo = new Player();
        YoungHoe_Koo.setExtraPointAttempts(3);
        YoungHoe_Koo.setExtraPointsMade(2);
        YoungHoe_Koo.setFieldGoalAttempts(4);
        YoungHoe_Koo.setFieldGoalsMade(3);
        Assertions.assertEquals(9, YoungHoe_Koo.getScore());
    }

    @Test
    void testShortNameConstructing(){
        Player chris = new Player("Chris Burke");
        Assertions.assertEquals("C. Burke", chris.getShortName());

    }

    @Test
    void testCustomPlayerEqualsWithDifferentMemoryAddress(){
        Player player0 = new Player("Chris", "12345");
        Player player1 = new Player("Chris", "12345");
        Assertions.assertEquals(player0, player1);
    }

    @Test
    void testCustomPlayerEqualsWithSameMemoryAddress(){
        Player player0 = new Player("Chris", "12345");
        Assertions.assertEquals(player0, player0);
    }

    @Test
    void testCustomerPlayerEqualsWithNull(){
        Player player0 = new Player("Chris", "12345");
        Assertions.assertNotEquals(null, player0);
    }

    @Test
    void testCustomerPlayerEqualsWithDifferentObject(){
        Player player0 = new Player("Chris", "12345");
        Assertions.assertNotEquals(new Object(), player0);
    }

    @Test
    void testCustomPlayerMapContainsPlayer(){
        Player player0 = new Player("Chris", "12345");
        Player player1 = new Player("Chris", "12345");
        HashMap<Player, Position> map = new HashMap<>();
        map.put(player0, QB);
        Assertions.assertTrue(map.containsKey(player1));
    }
}
