package pokerface.com;

import java.util.ArrayList;
import java.util.List;

public record Card(Suit suit, Rank rank) {

    public enum Suit { S, H, D, C }

    public enum Rank { _2, _3, _4, _5, _6, _7, _8, _9, T, J, Q, K, A }

    private static final String RANK_CHARS = "23456789TJQKA";

    public static Card parse(String s) {
        if (s.length() != 2) throw new IllegalArgumentException("Invalid card: " + s);
        int idx = RANK_CHARS.indexOf(s.charAt(0));
        if (idx < 0) throw new IllegalArgumentException("Invalid rank: " + s.charAt(0));
        Rank rank = Rank.values()[idx];
        Suit suit = Suit.valueOf(String.valueOf(s.charAt(1)));
        return new Card(suit, rank);
    }

    public static List<Card> parseAll(String... cards) {
        List<Card> result = new ArrayList<>();
        for (String c : cards) result.add(parse(c));
        return result;
    }

    public int rankValue() {
        return rank.ordinal();
    }

    @Override
    public String toString() {
        return rank.name().replace("_", "") + suit.name();
    }
}
