package pokerface.com;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardProbability {

    enum Suit { HEARTS, DIAMONDS, CLUBS, SPADES }

    record Card(Suit suit, String rank) {}

    private static final int CARDS_PER_SUIT = 13;

    public static void main(String[] args) {
        runSimulation(Suit.HEARTS, true);
    }

    static boolean runSimulation(Suit targetSuit, boolean print) {
        List<Card> deck = createDeck();
        Collections.shuffle(deck);

        int remainingTarget = CARDS_PER_SUIT;
        int remainingTotal = deck.size();

        if (print) {
            System.out.println("Target suit: " + targetSuit);
            System.out.println("Initial probability: " + remainingTarget + "/" + remainingTotal + " = "
                    + formatPercent(remainingTarget, remainingTotal));
            System.out.println("-".repeat(50));
        }

        boolean valid = true;

        for (Card card : deck) {
            double probability = (double) remainingTarget / remainingTotal;
            boolean matches = card.suit() == targetSuit;

            if (print) {
                System.out.printf("P(%s) = %d/%d = %.2f%%  |  drawn: %s %s%n",
                        targetSuit,
                        remainingTarget, remainingTotal,
                        probability * 100,
                        card.suit(), card.rank());
            }

            if (remainingTarget == 0 && matches) {
                valid = false;
                if (print) {
                    System.out.println(">>> BUG: got " + targetSuit + " after probability reached 0!");
                }
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
                    case 1 -> "A";
                    case 11 -> "J";
                    case 12 -> "Q";
                    case 13 -> "K";
                    default -> String.valueOf(i);
                };
                deck.add(new Card(suit, rank));
            }
        }
        return deck;
    }

    private static String formatPercent(int part, int total) {
        return String.format("%.2f%%", 100.0 * part / total);
    }
}
