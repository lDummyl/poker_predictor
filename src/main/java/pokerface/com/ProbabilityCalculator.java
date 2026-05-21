package pokerface.com;

import java.util.ArrayList;
import java.util.List;

public class ProbabilityCalculator {

    public static Result compute(List<Card> community, List<Card> hole) {
        List<Card> known = new ArrayList<>();
        known.addAll(community);
        known.addAll(hole);

        List<Card> deck = fullDeck();
        deck.removeAll(known);

        List<Card> ourSeven = new ArrayList<>(community);
        ourSeven.addAll(hole);
        int ourScore = HandEvaluator.evaluate(ourSeven);

        int wins = 0, ties = 0, losses = 0;
        int total = 0;

        for (int i = 0; i < deck.size(); i++) {
            for (int j = i + 1; j < deck.size(); j++) {
                List<Card> oppSeven = new ArrayList<>(community);
                oppSeven.add(deck.get(i));
                oppSeven.add(deck.get(j));
                int oppScore = HandEvaluator.evaluate(oppSeven);
                if (ourScore > oppScore) wins++;
                else if (ourScore == oppScore) ties++;
                else losses++;
                total++;
            }
        }

        double winRate = (wins + ties / 2.0) / total;
        return new Result(winRate, wins, ties, losses, total);
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

    public record Result(double winProbability, int wins, int ties, int losses, int total) {
        public double winPct() { return winProbability * 100; }
    }
}
