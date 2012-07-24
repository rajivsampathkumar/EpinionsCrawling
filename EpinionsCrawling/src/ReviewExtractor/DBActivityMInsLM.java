package ReviewExtractor;



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
	
	void insertreview(int docno,String revlink,String reviewtitle,String reviewcontent,String pros,String cons,String revrecom,String recomfor,String amt,String revbline,String revdate,String revstars,String pname,String overallstar,String table)
	{
	try{
		stmt2=connection.prepareStatement("insert into "+table+"(ProductName,OverallStar,ReviewTitle,ReviewContent,Pros,Cons,Recommended,RecommendedFor,AmountPaid,ReviewBottomLine,ReviewDate,ReviewStar,Docno,ReviewLink) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		stmt2.setLong(13,docno);
		stmt2.setString(1,pname);
		stmt2.setString(2,overallstar);
		stmt2.setString(3,reviewtitle);
		stmt2.setString(4,reviewcontent);
		stmt2.setString(5,pros);
		stmt2.setString(6,cons);
		stmt2.setString(7,revrecom);
		stmt2.setString(8,recomfor);
                stmt2.setString(9,amt);
                stmt2.setString(10,revbline);
                stmt2.setString(11,revdate);
                stmt2.setString(12,revstars);
                stmt2.setString(14,revlink);
        stmt2.executeUpdate();
        stmt2.close();
	}catch(Exception e){
	System.out.println("Error while inserting reviews..."+e);	
	}
	}
   	
}
