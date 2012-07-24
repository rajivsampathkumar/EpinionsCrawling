package ReviewLinkExtractor;



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
	void insertlink(String link,int docno,String table)
	{
		try{
		stmt3=connection.prepareStatement("insert into "+table+"(Link,Flag,Docno) values(?,?,?)");
		
		stmt3.setString(1,link);
		stmt3.setLong(2,0);
		stmt3.setLong(3,docno);
		stmt3.executeUpdate();
		
		stmt3.close();
		}catch(Exception e){
            System.out.println("Error while inserting..."+e);
        }
	}
	
  
}
