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

    public static Card fromRecognizer(String suitName, String rankName) {
        Suit suit = switch (suitName) {
            case "SPADES" -> Suit.S;
            case "HEARTS" -> Suit.H;
            case "DIAMONDS" -> Suit.D;
            case "CLUBS" -> Suit.C;
            default -> throw new IllegalArgumentException("Unknown suit: " + suitName);
        };
        Rank rank = switch (rankName) {
            case "2" -> Rank._2;
            case "3" -> Rank._3;
            case "4" -> Rank._4;
            case "5" -> Rank._5;
            case "6" -> Rank._6;
            case "7" -> Rank._7;
            case "8" -> Rank._8;
            case "9" -> Rank._9;
            case "10" -> Rank.T;
            case "J" -> Rank.J;
            case "Q" -> Rank.Q;
            case "K" -> Rank.K;
            case "A" -> Rank.A;
            default -> throw new IllegalArgumentException("Unknown rank: " + rankName);
        };
        return new Card(suit, rank);
    }

    public int rankValue() {
        return rank.ordinal();
    }

    @Override
    public String toString() {
        return rank.name().replace("_", "") + suit.name();
    }
}
