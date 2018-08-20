package pharma;
import org.json.JSONObject;

/**
 * This class defines the format needed by the FrontEnd
 * Objects sent to the FrontEnd will be of this class
 * 
 * @author asztrik
 *
 */
public class OLSResponse {

    private String iri; /* ?? would be better as object? how will I persist it? */
    private JSONObject content;

    public OLSResponse(String iri, JSONObject content) {
        this.iri = iri;
        this.content = content;
    }

    public String getIri() {
        return this.iri;
    }

    public JSONObject getContent() {
        return content;
    }
}