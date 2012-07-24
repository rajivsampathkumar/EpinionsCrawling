package LinkExtractor;



/*

*/
import java.sql.*;

public class DBActivityMInsLM{

	Connection connection;
	//Connection mdbcon;
	boolean isCon;
	PreparedStatement stmt=null,stmt2=null,stmt3=null;
	DBActivityMInsLM(String dbname,String user,String pass,String db){
	    try{
	    	
	    	//MS SQL SERVER 
	    	//Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
	    	//connection = DriverManager.getConnection("jdbc:microsoft:sqlserver://"+dbname+":1433/"+db+"",""+user+"",""+pass+"");

	    	//MySQL 
		Class.forName("com.mysql.jdbc.Driver");						
		connection=DriverManager.getConnection("jdbc:mysql://"+dbname+":3306/"+db+"",""+user+"",""+pass+"");
		isCon=true;
                System.out.println("connected");
		}catch(ClassNotFoundException cnfe){
			System.out.println("Error..."+cnfe);
		}
		catch(SQLException sqle){
			System.out.println("SQL Error..."+sqle);
		}
	}
        
	void closeDB()throws Exception{
	        connection.close();
	}
		
   void insertData(String pname,String totalrev,String link,String table)throws Exception
	{

		try{

		stmt=connection.prepareStatement("insert into "+table+"(ProductName,TotalReview,ReviewLink,Flag) values(?,?,?,?)");
		
		
		stmt.setString(1,pname);
		stmt.setString(2,totalrev);
		stmt.setString(3,link);
		stmt.setInt(4, 0);
		
		
        stmt.executeUpdate();

	        stmt.close();
            }catch(Exception e){
                System.out.println("Error while inserting..."+e);
            }
	}
	
}
