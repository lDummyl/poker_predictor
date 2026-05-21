package pokerface.com;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PokerPredictor {

    public static void main(String[] args) throws Exception {
        String path = args.length > 0 ? args[0] : "example.json";
        TableState state = new ObjectMapper().readValue(new File(path), TableState.class);

        List<Card> community = new ArrayList<>();
        for (RecognizerCard rc : state.getCommunity())
            community.add(Card.fromRecognizer(rc.suit(), rc.rank()));

        List<Card> hole = new ArrayList<>();
        for (RecognizerCard rc : state.getHole())
            hole.add(Card.fromRecognizer(rc.suit(), rc.rank()));

        System.out.println("Community: " + community);
        System.out.println("Hole:      " + hole);
        System.out.println("-".repeat(50));

        ProbabilityCalculator.Result r = ProbabilityCalculator.compute(community, hole);

        System.out.printf("Opponent hands evaluated: %d%n", r.total());
        System.out.printf("Wins:   %d (%.2f%%)%n", r.wins(), 100.0 * r.wins() / r.total());
        System.out.printf("Ties:   %d (%.2f%%)%n", r.ties(), 100.0 * r.ties() / r.total());
        System.out.printf("Losses: %d (%.2f%%)%n", r.losses(), 100.0 * r.losses() / r.total());
        System.out.println("-".repeat(50));
        System.out.printf("Win probability: %.2f%%%n", r.winPct());
    }
}
