package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class DaoUtils {
	public static PreparedStatement prepareStatement(Connection conn, String sql, boolean returnKeys,Object[] values) {
		try {
			PreparedStatement stmt=conn.prepareStatement(sql,returnKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
			setValues(stmt,values);
			return stmt;
		} catch(Exception ex) {
			return null;
		}
	}
	
	private static void setValues(PreparedStatement pstmt, Object[] values) {
		try {
			for(int i=0;i<values.length;i++) {
				pstmt.setObject(i+1, values[i]);
			}
		} catch(Exception ex) {}
	}
}
