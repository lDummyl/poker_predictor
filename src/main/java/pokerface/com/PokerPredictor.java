package pokerface.com;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PokerPredictor {

    private static final Logger LOG = Logger.getLogger(PokerPredictor.class.getName());
    private static final String DEFAULT_INPUT = "example.json";

    public static void main(String[] args) throws IOException {
        String path = args.length > 0 ? args[0] : DEFAULT_INPUT;
        TableState state = new ObjectMapper().readValue(new File(path), TableState.class);

        List<Card> community = new ArrayList<>();
        for (RecognizerCard rc : state.getCommunity())
            community.add(Card.fromRecognizer(rc.suit(), rc.rank()));

        List<Card> hole = new ArrayList<>();
        for (RecognizerCard rc : state.getHole())
            hole.add(Card.fromRecognizer(rc.suit(), rc.rank()));

        int opps = state.getOpponents();

        LOG.info(() -> "Community: " + community);
        LOG.info(() -> "Hole:      " + hole);
        LOG.info(() -> "Opponents: " + opps);

        ProbabilityCalculator.Result r = ProbabilityCalculator.compute(community, hole, opps);

        LOG.info(() -> String.format("Combos evaluated: %d", r.total()));
        LOG.info(() -> String.format("Wins:   %d (%.2f%%)", r.wins(), 100.0 * r.wins() / r.total()));
        LOG.info(() -> String.format("Ties:   %d (%.2f%%)", r.ties(), 100.0 * r.ties() / r.total()));
        LOG.info(() -> String.format("Losses: %d (%.2f%%)", r.losses(), 100.0 * r.losses() / r.total()));
        LOG.info(() -> String.format("Win probability: %.2f%%", r.winPct()));
    }
}
