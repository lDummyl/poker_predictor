package pokerface.com;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class ProbabilityCalculatorTest {

    @Test
    void royalFlushOnBoard_allTie() {
        List<Card> community = List.of(
                Card.fromRecognizer("SPADES", "A"),
                Card.fromRecognizer("SPADES", "K"),
                Card.fromRecognizer("SPADES", "Q"),
                Card.fromRecognizer("SPADES", "J"),
                Card.fromRecognizer("SPADES", "10")
        );
        List<Card> hole = List.of(
                Card.fromRecognizer("DIAMONDS", "2"),
                Card.fromRecognizer("CLUBS", "3")
        );
        ProbabilityCalculator.Result r = ProbabilityCalculator.compute(community, hole);
        assertEquals(0, r.wins());
        assertEquals(0, r.losses());
        assertEquals(r.total(), r.ties());
        assertEquals(0.5, r.winProbability(), 0.001);
    }

    @Test
    void nutsOnRiver_highWinRate() {
        List<Card> community = List.of(
                Card.fromRecognizer("SPADES", "A"),
                Card.fromRecognizer("SPADES", "K"),
                Card.fromRecognizer("SPADES", "Q"),
                Card.fromRecognizer("SPADES", "J"),
                Card.fromRecognizer("SPADES", "10")
        );
        List<Card> hole = List.of(
                Card.fromRecognizer("HEARTS", "A"),
                Card.fromRecognizer("HEARTS", "K")
        );
        ProbabilityCalculator.Result r = ProbabilityCalculator.compute(community, hole);
        assertEquals(0, r.losses());
        assertEquals(r.total(), r.wins() + r.ties());
    }

    @Test
    void deckDoesNotContainKnownCards() {
        List<Card> community = List.of(
                Card.fromRecognizer("SPADES", "A"),
                Card.fromRecognizer("SPADES", "K"),
                Card.fromRecognizer("SPADES", "Q"),
                Card.fromRecognizer("SPADES", "J"),
                Card.fromRecognizer("DIAMONDS", "2")
        );
        List<Card> hole = List.of(
                Card.fromRecognizer("HEARTS", "A"),
                Card.fromRecognizer("HEARTS", "K")
        );
        ProbabilityCalculator.Result r = ProbabilityCalculator.compute(community, hole);
        assertEquals(990, r.total());
    }

    @Test
    void exampleJsonScenario() {
        List<Card> community = List.of(
                Card.fromRecognizer("SPADES", "A"),
                Card.fromRecognizer("SPADES", "K"),
                Card.fromRecognizer("SPADES", "Q"),
                Card.fromRecognizer("SPADES", "J"),
                Card.fromRecognizer("DIAMONDS", "2")
        );
        List<Card> hole = List.of(
                Card.fromRecognizer("HEARTS", "A"),
                Card.fromRecognizer("HEARTS", "K")
        );
        ProbabilityCalculator.Result r = ProbabilityCalculator.compute(community, hole);
        assertTrue(r.winProbability() > 0.50);
        assertTrue(r.winProbability() < 0.60);
    }
}
