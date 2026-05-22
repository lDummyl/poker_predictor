package pokerface.com;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class TableState {

    @JsonProperty(required = true)
    private List<RecognizerCard> community;

    @JsonProperty(required = true)
    private List<RecognizerCard> hole;

    private int opponents = 1;

    public TableState() {}

    public List<RecognizerCard> getCommunity() { return community; }
    public void setCommunity(List<RecognizerCard> community) { this.community = community; }
    public List<RecognizerCard> getHole() { return hole; }
    public void setHole(List<RecognizerCard> hole) { this.hole = hole; }
    public int getOpponents() { return opponents; }
    public void setOpponents(int opponents) { this.opponents = opponents; }
}
