package pokerface.com;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class CardProbability {

    private static final Logger LOG = Logger.getLogger(CardProbability.class.getName());

    enum Suit { HEARTS, DIAMONDS, CLUBS, SPADES }

    record Card(Suit suit, String rank) {}

    private static final int CARDS_PER_SUIT = 13;
    private static final int ACE = 1;
    private static final int JACK = 11;
    private static final int QUEEN = 12;
    private static final int KING = 13;
    private static final double PERCENT_MULTIPLIER = 100.0;

    public static void main(String[] args) {
        runSimulation(Suit.HEARTS, true);
    }

    static boolean runSimulation(Suit targetSuit, boolean print) {
        List<Card> deck = createDeck();
        Collections.shuffle(deck, new SecureRandom());

        int remainingTarget = CARDS_PER_SUIT;
        int remainingTotal = deck.size();

        if (print) {
            LOG.info("Target suit: " + targetSuit);
            LOG.info("Initial probability: " + remainingTarget + "/" + remainingTotal + " = "
                    + formatPercent(remainingTarget, remainingTotal));
        }

        boolean valid = true;

        for (Card card : deck) {
            double probability = (double) remainingTarget / remainingTotal;
            boolean matches = card.suit() == targetSuit;

            if (print) {
                double pct = probability * PERCENT_MULTIPLIER;
                LOG.fine(String.format("P(%s) = %d/%d = %.2f%%  |  drawn: %s %s",
                        targetSuit, remainingTarget, remainingTotal, pct, card.suit(), card.rank()));
            }

            if (remainingTarget == 0 && matches) {
                valid = false;
                LOG.severe("BUG: got " + targetSuit + " after probability reached 0!");
            }

            if (matches) remainingTarget--;
            remainingTotal--;
        }

        return valid;
    }

    static List<Card> createDeck() {
        List<Card> deck = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (int i = 1; i <= CARDS_PER_SUIT; i++) {
                String rank = switch (i) {
                    case ACE -> "A";
                    case JACK -> "J";
                    case QUEEN -> "Q";
                    case KING -> "K";
                    default -> String.valueOf(i);
                };
                deck.add(new Card(suit, rank));
            }
        }
        return deck;
    }

    private static String formatPercent(int part, int total) {
        return String.format("%.2f%%", PERCENT_MULTIPLIER * part / total);
    }
}
