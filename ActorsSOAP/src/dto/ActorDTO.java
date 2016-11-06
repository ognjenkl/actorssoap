package dto;

import java.io.Serializable;

/**
 * @author ognjen
 *
 */
public class ActorDTO implements Serializable {
	
	int id;
	String name;
	
	public ActorDTO() {
		id = -1;
		name = null;
	}

	public ActorDTO(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
}
