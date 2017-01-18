package actor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mysql.jdbc.Statement;

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
	private static final String SQL_GET_ALL_BY_TITLE = "SELECT * FROM movies WHERE active=1 AND title LIKE ?;";
	private static final String SQL_GET_ALL_ACTORS_BY_MOVIE_ID = "SELECT a.id, a.name FROM movies_has_actors ma JOIN actors a ON a.id = ma.actors_id WHERE ma.movies_id = ?;";
	private static final String SQL_GET_ALL_GENRES_BY_MOVIE_ID = "SELECT g.id, g.name FROM movies_has_genres mg JOIN genres g ON g.id = mg.genres_id WHERE mg.movies_id = ?;";

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

			
			//System.out.println("from soap: " + jArray.toString());
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
	
	public static Integer insertActor(String actorName){
		Connection conn = null;
		PreparedStatement ppst = null;
		ResultSet resultSet = null;
		Integer retVal = null;
		
		try{
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_INSERT_ACTOR, Statement.RETURN_GENERATED_KEYS);
			ppst.setString(1, actorName);
			ppst.setString(2, actorName);
			
			int rowCount = ppst.executeUpdate();
			
			resultSet = ppst.getGeneratedKeys();
			
			if(rowCount > 0 && resultSet.next())
				retVal = resultSet.getInt(1);
			else
				retVal = -1;
			
			ppst.close();
			return retVal;
		} catch (Exception e){
			//TODO log
			return retVal;
		} finally {	
			if(ppst != null)
				try {
					ppst.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			ConnectionPool.getConnectionPool().checkIn(conn);
		}
	}
	
	public String getAllMoviesByTitleLike(String searchText){
		DaoFactory daoFactory = DaoFactory.getInstance();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		JSONArray jArray = new JSONArray();
		
		try {
			conn = daoFactory.checkOut();
			if(searchText == null) searchText = "";
			pstmt = DaoUtils.prepareStatement(conn, SQL_GET_ALL_BY_TITLE, false, new Object[]{"%" + searchText + "%"});
			rs = pstmt.executeQuery();
			while(rs.next()){
				
				JSONObject movieJson = new JSONObject();
				Integer movieId = rs.getInt(1);
				movieJson.put("id", movieId);
				movieJson.put("title", rs.getString(2));
				movieJson.put("releaseDate", rs.getDate(3) + "");
				movieJson.put("storyLine", rs.getString(4));
				movieJson.put("trailerLocationType", rs.getInt(5) + "");
				movieJson.put("trailerLocation", rs.getString(6));
				movieJson.put("runtimeMinutes", rs.getInt(7) + "");
				movieJson.put("rate", rs.getDouble(8) + "");
				
				movieJson.put("actors", getActorsByMovieId(movieId));
				movieJson.put("genres", getGenresByMovieId(movieId));
				
				jArray.put(movieJson);

			}

			System.out.println("from soap: " + jArray.toString());

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				pstmt.close();
				daoFactory.checkIn(conn);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		return jArray.toString();
	}
	
	private String getActorsByMovieId(int movieId){
		DaoFactory daoFactory = DaoFactory.getInstance();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		JSONArray jArray = new JSONArray();
		
		try {
			conn = daoFactory.checkOut();
			
			pstmt = DaoUtils.prepareStatement(conn, SQL_GET_ALL_ACTORS_BY_MOVIE_ID, false, new Object[]{movieId});
			rs = pstmt.executeQuery();
			while(rs.next()){
				JSONObject actorJson = new JSONObject();
				actorJson.put("id", rs.getInt("id"));
				actorJson.put("name", rs.getString("name"));
				jArray.put(actorJson);
			}

			System.out.println("from soap actors: " + jArray.toString());

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				pstmt.close();
				daoFactory.checkIn(conn);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		return jArray.toString();
	}
	
	
	
	private String getGenresByMovieId(int movieId){
		DaoFactory daoFactory = DaoFactory.getInstance();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		JSONArray jArray = new JSONArray();
		
		try {
			conn = daoFactory.checkOut();
			pstmt = DaoUtils.prepareStatement(conn, SQL_GET_ALL_GENRES_BY_MOVIE_ID, false, new Object[] { movieId });
			rs = pstmt.executeQuery();
			while(rs.next()){
				JSONObject genreJson = new JSONObject();
				genreJson.put("id", rs.getInt("id"));
				genreJson.put("name", rs.getString("name"));
				jArray.put(genreJson);
			}

			System.out.println("from soap genres: " + jArray.toString());

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				pstmt.close();
				daoFactory.checkIn(conn);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		return jArray.toString();
	}
	
	
	
}
