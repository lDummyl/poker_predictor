package pokerface.com;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.RepeatedTest;
import pokerface.com.CardProbability.Suit;

class CardProbabilityTest {

    @RepeatedTest(10)
    void noTargetSuitCardsAfterZeroProbability() {
        assertTrue(
                CardProbability.runSimulation(Suit.HEARTS, false),
                "BUG: drawn HEARTS after its probability reached 0"
        );
    }
}
