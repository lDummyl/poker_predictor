package pokerface.com;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class TableState {

    @JsonProperty(required = true)
    private List<String> community;

    @JsonProperty(required = true)
    private List<String> hole;

    public TableState() {}

    public List<String> getCommunity() { return community; }
    public void setCommunity(List<String> community) { this.community = community; }
    public List<String> getHole() { return hole; }
    public void setHole(List<String> hole) { this.hole = hole; }
}
