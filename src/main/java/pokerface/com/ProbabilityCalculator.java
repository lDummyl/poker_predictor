package pokerface.com;

import java.util.ArrayList;
import java.util.List;

public class ProbabilityCalculator {

    private static final int DEFAULT_OPPONENTS = 1;
    private static final double TIE_WEIGHT = 0.5;
    enum Outcome { WIN, TIE, LOSS }

    public static Result compute(List<Card> community, List<Card> hole) {
        return compute(community, hole, DEFAULT_OPPONENTS);
    }

    public static Result compute(List<Card> community, List<Card> hole, int opponentCount) {
        List<Card> known = new ArrayList<>(community);
        known.addAll(hole);

        List<Card> deck = fullDeck();
        deck.removeAll(known);

        List<Card> ourSeven = new ArrayList<>(community);
        ourSeven.addAll(hole);
        int ourScore = HandEvaluator.evaluate(ourSeven);

        List<IndexPair> firstPairs = new ArrayList<>();
        for (int i = 0; i < deck.size(); i++) {
            for (int j = i + 1; j < deck.size(); j++) {
                firstPairs.add(new IndexPair(i, j));
            }
        }

        int[] wins = {0}, ties = {0}, losses = {0};
        Object lock = new Object();

        firstPairs.parallelStream().forEach(pair -> {
            List<Card> oppSeven = new ArrayList<>(community);
            oppSeven.add(deck.get(pair.first()));
            oppSeven.add(deck.get(pair.second()));
            int oppScore = HandEvaluator.evaluate(oppSeven);

            int[] sub;
            if (opponentCount == 1) {
                sub = toInts(compareScores(ourScore, oppScore));
            } else {
                List<Integer> rest = new ArrayList<>(deck.size() - 2);
                for (int k = 0; k < deck.size(); k++) {
                    if (k != pair.first() && k != pair.second()) rest.add(k);
                }
                sub = enumerateRest(rest, deck, community, ourScore, opponentCount - 1, oppScore);
            }

            synchronized (lock) {
                wins[0] += sub[0];
                ties[0] += sub[1];
                losses[0] += sub[2];
            }
        });

        int total = wins[0] + ties[0] + losses[0];
        double winRate = (wins[0] + ties[0] * TIE_WEIGHT) / total;
        return new Result(winRate, wins[0], ties[0], losses[0], total);
    }

    private static Outcome compareScores(int ours, int theirs) {
        if (ours > theirs) return Outcome.WIN;
        if (ours == theirs) return Outcome.TIE;
        return Outcome.LOSS;
    }

    private static int[] toInts(Outcome outcome) {
        return switch (outcome) {
            case WIN  -> new int[]{1, 0, 0};
            case TIE  -> new int[]{0, 1, 0};
            case LOSS -> new int[]{0, 0, 1};
        };
    }

    private static int[] enumerateRest(List<Integer> available, List<Card> deck,
            List<Card> community, int ourScore, int oppsLeft, int bestOppScore) {
        if (oppsLeft == 0) {
            return toInts(compareScores(ourScore, bestOppScore));
        }

        int[] total = {0, 0, 0};
        for (int a = 0; a < available.size(); a++) {
            int i = available.get(a);
            for (int b = a + 1; b < available.size(); b++) {
                int j = available.get(b);

                List<Card> oppSeven = new ArrayList<>(community);
                oppSeven.add(deck.get(i));
                oppSeven.add(deck.get(j));
                int oppScore = HandEvaluator.evaluate(oppSeven);
                int newBest = Math.max(bestOppScore, oppScore);

                List<Integer> rest = new ArrayList<>(available.size() - 2);
                for (int k = 0; k < available.size(); k++) {
                    if (k != a && k != b) rest.add(available.get(k));
                }

                int[] sub = enumerateRest(rest, deck, community, ourScore, oppsLeft - 1, newBest);
                total[0] += sub[0];
                total[1] += sub[1];
                total[2] += sub[2];
            }
        }
        return total;
    }

    private static List<Card> fullDeck() {
        List<Card> deck = new ArrayList<>(52);
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                deck.add(new Card(suit, rank));
            }
        }
        return deck;
    }

    record IndexPair(int first, int second) {}

    public record Result(double winProbability, int wins, int ties, int losses, int total) {
        public double winPct() { return winProbability * 100; }
    }
}
