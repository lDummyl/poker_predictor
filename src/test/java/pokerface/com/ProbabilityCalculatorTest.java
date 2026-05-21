package pokerface.com;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class ProbabilityCalculatorTest {

    @Test
    void royalFlushOnBoard_allTie() {
        List<Card> community = Card.parseAll("AS", "KS", "QS", "JS", "TS");
        List<Card> hole = Card.parseAll("2D", "3C");
        ProbabilityCalculator.Result r = ProbabilityCalculator.compute(community, hole);
        assertEquals(0, r.wins());
        assertEquals(0, r.losses());
        assertEquals(r.total(), r.ties());
        assertEquals(0.5, r.winProbability(), 0.001);
    }

    @Test
    void nutsOnRiver_highWinRate() {
        List<Card> community = Card.parseAll("AS", "KS", "QS", "JS", "TS");
        List<Card> hole = Card.parseAll("AH", "KH");
        ProbabilityCalculator.Result r = ProbabilityCalculator.compute(community, hole);
        assertEquals(0, r.losses());
        assertEquals(r.total(), r.wins() + r.ties());
    }

    @Test
    void deckDoesNotContainKnownCards() {
        List<Card> community = Card.parseAll("AS", "KS", "QS", "JS", "2D");
        List<Card> hole = Card.parseAll("AH", "KH");
        ProbabilityCalculator.Result r = ProbabilityCalculator.compute(community, hole);
        assertEquals(990, r.total());
    }

    @Test
    void exampleJsonScenario() {
        List<Card> community = Card.parseAll("AS", "KS", "QS", "JS", "2D");
        List<Card> hole = Card.parseAll("AH", "KH");
        ProbabilityCalculator.Result r = ProbabilityCalculator.compute(community, hole);
        assertTrue(r.winProbability() > 0.50);
        assertTrue(r.winProbability() < 0.60);
    }
}
