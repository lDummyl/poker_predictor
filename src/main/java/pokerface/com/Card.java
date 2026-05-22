package pokerface.com;

public record Card(Suit suit, Rank rank) {

    public enum Suit { S, H, D, C }

    public enum Rank { TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE }

    private static final String[] RANK_LABELS = {"2","3","4","5","6","7","8","9","T","J","Q","K","A"};

    public static Card fromRecognizer(String suitName, String rankName) {
        Suit suit = switch (suitName) {
            case "SPADES" -> Suit.S;
            case "HEARTS" -> Suit.H;
            case "DIAMONDS" -> Suit.D;
            case "CLUBS" -> Suit.C;
            default -> throw new IllegalArgumentException("Unknown suit: " + suitName);
        };
        Rank rank = switch (rankName) {
            case "2" -> Rank.TWO;
            case "3" -> Rank.THREE;
            case "4" -> Rank.FOUR;
            case "5" -> Rank.FIVE;
            case "6" -> Rank.SIX;
            case "7" -> Rank.SEVEN;
            case "8" -> Rank.EIGHT;
            case "9" -> Rank.NINE;
            case "10" -> Rank.TEN;
            case "J" -> Rank.JACK;
            case "Q" -> Rank.QUEEN;
            case "K" -> Rank.KING;
            case "A" -> Rank.ACE;
            default -> throw new IllegalArgumentException("Unknown rank: " + rankName);
        };
        return new Card(suit, rank);
    }

    public int rankValue() {
        return rank.ordinal() + 2;
    }

    @Override
    public String toString() {
        return RANK_LABELS[rank.ordinal()] + suit.name();
    }
}
