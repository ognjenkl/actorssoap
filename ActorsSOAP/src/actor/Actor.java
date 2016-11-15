package actor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dao.ConnectionPool;
import dao.DaoFactory;
import dao.DaoUtils;
/**
 * @author ognjen
 *
 */
public class Actor {
	private static final String SQL_GET_ACTORS = "SELECT * FROM zndfilm.actors WHERE name like ?;";
	private static final String SQL_INSERT_ACTOR = "insert into actors (name) select * from (select ?) as tmp where not exists ( select name from actors where name = ?) limit 1;";

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
	
	public static boolean insertActor(String actorName){
		Connection conn = null;
		PreparedStatement ppst = null;
		boolean retVal = false;
		
		try{
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_INSERT_ACTOR);
			ppst.setString(1, actorName);
			ppst.setString(2, actorName);
			
			
			if (ppst.executeUpdate() > 0)
				retVal = true;
		
			ppst.close();
			return retVal;
		} catch (Exception e){
			//to do log
			return retVal;
		} finally {
			ConnectionPool.getConnectionPool().checkIn(conn);
		}
	}
	
}
