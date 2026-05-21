package pokerface.com;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HandEvaluator {

    public static int evaluate(List<Card> sevenCards) {
        if (sevenCards.size() != 7) throw new IllegalArgumentException("Need exactly 7 cards");
        List<List<Card>> combos = combinations(new ArrayList<>(sevenCards), 5);
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

        boolean flush = true;
        Card.Suit firstSuit = cards.get(0).suit();
        for (int i = 1; i < 5; i++) {
            if (cards.get(i).suit() != firstSuit) { flush = false; break; }
        }

        boolean straight = isStraight(ranks);

        int[] freq = new int[15];
        for (int r : ranks) freq[r]++;

        List<int[]> groups = new ArrayList<>();
        for (int r = 14; r >= 0; r--) {
            if (freq[r] > 0) groups.add(new int[]{r, freq[r]});
        }
        groups.sort((a, b) -> b[1] != a[1] ? b[1] - a[1] : b[0] - a[0]);

        int[] kickers = new int[5];
        int ki = 0;
        for (int[] g : groups) {
            for (int j = 0; j < g[1]; j++) kickers[ki++] = g[0];
        }

        int rankCode;
        if (straight && flush) rankCode = 8;
        else if (groups.get(0)[1] == 4) rankCode = 7;
        else if (groups.get(0)[1] == 3 && groups.size() > 1 && groups.get(1)[1] == 2) rankCode = 6;
        else if (flush) rankCode = 5;
        else if (straight) rankCode = 4;
        else if (groups.get(0)[1] == 3) rankCode = 3;
        else if (groups.get(0)[1] == 2 && groups.size() > 1 && groups.get(1)[1] == 2) rankCode = 2;
        else if (groups.get(0)[1] == 2) rankCode = 1;
        else rankCode = 0;

        int score = rankCode << 20;
        for (int i = 0; i < 5; i++) score |= kickers[i] << (16 - i * 4);
        return score;
    }

    private static boolean isStraight(List<Integer> ranks) {
        if (ranks.get(0) == 14 && ranks.get(1) == 5 && ranks.get(2) == 4
                && ranks.get(3) == 3 && ranks.get(4) == 2) return true;
        for (int i = 0; i < 4; i++) {
            if (ranks.get(i) - 1 != ranks.get(i + 1)) return false;
        }
        return true;
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
            curr.remove(curr.size() - 1);
        }
    }
}
