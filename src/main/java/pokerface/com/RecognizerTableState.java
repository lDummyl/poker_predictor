package pokerface.com;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class RecognizerTableState {

    @JsonProperty(required = true)
    private List<RecognizerCard> community;

    @JsonProperty(required = true)
    private List<RecognizerCard> hole;

    public RecognizerTableState() {}

    public List<RecognizerCard> getCommunity() { return community; }
    public void setCommunity(List<RecognizerCard> community) { this.community = community; }
    public List<RecognizerCard> getHole() { return hole; }
    public void setHole(List<RecognizerCard> hole) { this.hole = hole; }
}
