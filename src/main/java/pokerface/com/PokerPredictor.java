package pokerface.com;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PokerPredictor {

    public static void main(String[] args) throws Exception {
        String path = args.length > 0 ? args[0] : "example.json";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(path));
        JsonNode communityNode = root.get("community");

        List<Card> community;
        List<Card> hole;

        if (communityNode.isArray() && !communityNode.isEmpty() && communityNode.get(0).isObject()) {
            RecognizerTableState state = mapper.treeToValue(root, RecognizerTableState.class);
            community = new ArrayList<>();
            for (RecognizerCard rc : state.getCommunity())
                community.add(Card.fromRecognizer(rc.suit(), rc.rank()));
            hole = new ArrayList<>();
            for (RecognizerCard rc : state.getHole())
                hole.add(Card.fromRecognizer(rc.suit(), rc.rank()));
        } else {
            TableState state = mapper.treeToValue(root, TableState.class);
            community = new ArrayList<>();
            for (String s : state.getCommunity()) community.add(Card.parse(s));
            hole = new ArrayList<>();
            for (String s : state.getHole()) hole.add(Card.parse(s));
        }

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
