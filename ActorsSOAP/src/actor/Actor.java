package actor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.webresources.JarResource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dao.DaoFactory;
import dao.DaoUtils;
import dto.ActorDTO;

/**
 * @author ognjen
 *
 */
public class Actor {
	private static final String SQL_GET_ACTORS = "SELECT * FROM zndfilm.actors WHERE name like ?;";

	public String addActor(String actor){
		System.out.print("SOAP add actor: " + actor);
		return actor + " dodavanje";
		//implement adding actor to database
	}
	
	public String getActors(String actorName){
		DaoFactory daoFactory = DaoFactory.getInstance();
		Connection conn;
		PreparedStatement pstmt;
		ResultSet rs;
		JSONArray jArray = new JSONArray();
		
		try {
			conn = daoFactory.checkOut();
			if(actorName == null) actorName = "";
			pstmt = DaoUtils.prepareStatement(conn, SQL_GET_ACTORS, false, new Object[]{actorName + "%"});
			rs = pstmt.executeQuery();
			int count = 0;
			while(rs.next()){
				JSONObject jObject = new JSONObject();
				jObject.put("id", rs.getInt("id")+"");
				jObject.put("name", rs.getString("name"));
				jArray.put(jObject);
			}

			
			System.out.println("from soap: " + jArray.toString());
			daoFactory.checkIn(conn);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return jArray.toString();
	}
	
}
