package pharma.Term;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.json.JSONObject;

@Entity
public class OboNcitTerm extends AbstractTerm {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

}
