package pokerface.com;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

import org.junit.jupiter.api.Test;

class ProbabilityCalculatorTest {

    private static final List<Card> ROYAL_FLUSH = List.of(
            Card.fromRecognizer("SPADES", "A"),
            Card.fromRecognizer("SPADES", "K"),
            Card.fromRecognizer("SPADES", "Q"),
            Card.fromRecognizer("SPADES", "J"),
            Card.fromRecognizer("SPADES", "10")
    );

    private static final List<Card> FOUR_SPADES_PLUS_BRICK = List.of(
            Card.fromRecognizer("SPADES", "A"),
            Card.fromRecognizer("SPADES", "K"),
            Card.fromRecognizer("SPADES", "Q"),
            Card.fromRecognizer("SPADES", "J"),
            Card.fromRecognizer("DIAMONDS", "2")
    );

    private static final List<Card> ACE_KING_HEARTS = List.of(
            Card.fromRecognizer("HEARTS", "A"),
            Card.fromRecognizer("HEARTS", "K")
    );

    private static final List<Card> RAGS = List.of(
            Card.fromRecognizer("DIAMONDS", "2"),
            Card.fromRecognizer("CLUBS", "3")
    );

    private static final double DELTA = 0.001;
    private static final int EXPECTED_COMBOS_1_OPP = 990;
    private static final int EXPECTED_COMBOS_2_OPP = 893_970;
    private static final int BENCH_RUNS = 10;
    private static final int MIN_PASSED_RUNS = 5;
    private static final double SPEEDUP_THRESHOLD = 3.0;

    @Test
    void royalFlushOnBoard_allTie() {
        ProbabilityCalculator.Result r = ProbabilityCalculator.compute(ROYAL_FLUSH, RAGS);
        assertEquals(0, r.wins());
        assertEquals(0, r.losses());
        assertEquals(r.total(), r.ties());
        assertEquals(0.5, r.winProbability(), DELTA);
    }

    @Test
    void nutsOnRiver_highWinRate() {
        ProbabilityCalculator.Result r = ProbabilityCalculator.compute(ROYAL_FLUSH, ACE_KING_HEARTS);
        assertEquals(0, r.losses());
        assertEquals(r.total(), r.wins() + r.ties());
    }

    @Test
    void deckDoesNotContainKnownCards() {
        ProbabilityCalculator.Result r = ProbabilityCalculator.compute(FOUR_SPADES_PLUS_BRICK, ACE_KING_HEARTS);
        assertEquals(EXPECTED_COMBOS_1_OPP, r.total());
    }

    @Test
    void exampleJsonScenario() {
        ProbabilityCalculator.Result r = ProbabilityCalculator.compute(FOUR_SPADES_PLUS_BRICK, ACE_KING_HEARTS);
        assertTrue(r.winProbability() > 0.50);
        assertTrue(r.winProbability() < 0.60);
    }

    @Test
    void twoOpponents_totalCombos() {
        ProbabilityCalculator.Result r = ProbabilityCalculator.compute(FOUR_SPADES_PLUS_BRICK, ACE_KING_HEARTS, 2);
        assertEquals(EXPECTED_COMBOS_2_OPP, r.total());
        assertTrue(r.winProbability() < 1.0);
    }

    @Test
    void twoOpponents_weakerThanOne() {
        ProbabilityCalculator.Result r1 = ProbabilityCalculator.compute(FOUR_SPADES_PLUS_BRICK, ACE_KING_HEARTS, 1);
        ProbabilityCalculator.Result r2 = ProbabilityCalculator.compute(FOUR_SPADES_PLUS_BRICK, ACE_KING_HEARTS, 2);
        assertTrue(r2.winProbability() < r1.winProbability(),
                "win% against 2 opponents must be lower than against 1");
    }

    @Test
    void parallelFasterThanSequentialForTwoOpponents() throws Exception {
        ProbabilityCalculator.compute(FOUR_SPADES_PLUS_BRICK, ACE_KING_HEARTS, 2);

        int passed = 0;

        try (ForkJoinPool seqPool = new ForkJoinPool(1)) {
            for (int i = 0; i < BENCH_RUNS; i++) {
                long t0 = System.nanoTime();
                seqPool.submit(() -> ProbabilityCalculator.compute(
                        FOUR_SPADES_PLUS_BRICK, ACE_KING_HEARTS, 2)).get();
                long seqNs = System.nanoTime() - t0;

                long t1 = System.nanoTime();
                ProbabilityCalculator.compute(FOUR_SPADES_PLUS_BRICK, ACE_KING_HEARTS, 2);
                long parNs = System.nanoTime() - t1;

                double speedup = (double) seqNs / parNs;

                System.out.printf("run %d: sequential=%dms parallel=%dms speedup=%.2fx%n",
                        i + 1, (long)(seqNs / 1e6), (long)(parNs / 1e6), speedup);

                if (speedup >= SPEEDUP_THRESHOLD) passed++;
            }
        }

        System.out.printf("passed %d/%d runs with speedup >= %.0fx%n",
                passed, BENCH_RUNS, SPEEDUP_THRESHOLD);

        assertTrue(passed >= MIN_PASSED_RUNS,
                "at least " + MIN_PASSED_RUNS + " out of " + BENCH_RUNS
                + " runs must be >= " + SPEEDUP_THRESHOLD + "x faster, got " + passed);
    }
}
