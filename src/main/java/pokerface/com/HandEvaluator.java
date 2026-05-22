package pokerface.com;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HandEvaluator {

    private static final int CARDS_FOR_EVALUATION = 7;
    private static final int HAND_SIZE = 5;
    private static final int MAX_RANK = 14;
    private static final int FREQ_SIZE = MAX_RANK + 1;

    private static final int SCORE_RANK_SHIFT = 20;
    private static final int SCORE_KICKER_SHIFT = 16;
    private static final int SCORE_KICKER_BITS = 4;

    public static int evaluate(List<Card> sevenCards) {
        if (sevenCards.size() != CARDS_FOR_EVALUATION)
            throw new IllegalArgumentException("Need exactly " + CARDS_FOR_EVALUATION + " cards");
        List<List<Card>> combos = combinations(new ArrayList<>(sevenCards), HAND_SIZE);
        int bestScore = -1;
        for (List<Card> combo : combos) {
            int score = scoreFive(combo);
            if (score > bestScore) bestScore = score;
        }
        return bestScore;
    }

    private static int scoreFive(List<Card> cards) {
        List<Integer> ranks = new ArrayList<>();
        for (Card c : cards) ranks.add(c.rankValue());
        ranks.sort(Collections.reverseOrder());

        boolean flush = allSameSuit(cards);
        boolean straight = isStraight(ranks);

        int[] freq = new int[FREQ_SIZE];
        for (int r : ranks) freq[r]++;

        List<int[]> groups = new ArrayList<>();
        for (int r = MAX_RANK; r >= 0; r--) {
            if (freq[r] > 0) groups.add(new int[]{r, freq[r]});
        }
        groups.sort((a, b) -> b[1] != a[1] ? b[1] - a[1] : b[0] - a[0]);

        int[] kickers = new int[HAND_SIZE];
        int ki = 0;
        for (int[] g : groups) {
            for (int j = 0; j < g[1]; j++) kickers[ki++] = g[0];
        }

        HandRank rank = rankHand(straight, flush, groups);
        return packScore(rank, kickers);
    }

    private static HandRank rankHand(boolean straight, boolean flush, List<int[]> groups) {
        if (straight && flush) return HandRank.STRAIGHT_FLUSH;
        if (groups.get(0)[1] == 4) return HandRank.FOUR_OF_A_KIND;
        if (groups.get(0)[1] == 3 && groups.size() > 1 && groups.get(1)[1] == 2) return HandRank.FULL_HOUSE;
        if (flush) return HandRank.FLUSH;
        if (straight) return HandRank.STRAIGHT;
        if (groups.get(0)[1] == 3) return HandRank.THREE_OF_A_KIND;
        if (groups.get(0)[1] == 2 && groups.size() > 1 && groups.get(1)[1] == 2) return HandRank.TWO_PAIR;
        if (groups.get(0)[1] == 2) return HandRank.ONE_PAIR;
        return HandRank.HIGH_CARD;
    }

    private static boolean allSameSuit(List<Card> cards) {
        Card.Suit firstSuit = cards.get(0).suit();
        for (int i = 1; i < cards.size(); i++) {
            if (cards.get(i).suit() != firstSuit) return false;
        }
        return true;
    }

    private static boolean isStraight(List<Integer> ranks) {
        if (ranks.get(0) == 14 && ranks.get(1) == 5 && ranks.get(2) == 4
                && ranks.get(3) == 3 && ranks.get(4) == 2) return true;
        for (int i = 0; i < HAND_SIZE - 1; i++) {
            if (ranks.get(i) - 1 != ranks.get(i + 1)) return false;
        }
        return true;
    }

    private static int packScore(HandRank rank, int[] kickers) {
        int score = rank.value << SCORE_RANK_SHIFT;
        for (int i = 0; i < HAND_SIZE; i++)
            score |= kickers[i] << (SCORE_KICKER_SHIFT - i * SCORE_KICKER_BITS);
        return score;
    }

    private static <T> List<List<T>> combinations(List<T> list, int k) {
        List<List<T>> result = new ArrayList<>();
        combine(list, k, 0, new ArrayList<>(), result);
        return result;
    }

    private static <T> void combine(List<T> list, int k, int start, List<T> curr, List<List<T>> result) {
        if (curr.size() == k) { result.add(new ArrayList<>(curr)); return; }
        for (int i = start; i < list.size(); i++) {
            curr.add(list.get(i));
            combine(list, k, i + 1, curr, result);
            curr.removeLast();
        }
    }
}
