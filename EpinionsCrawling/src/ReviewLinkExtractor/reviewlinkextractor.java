package ReviewLinkExtractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;


public class reviewlinkextractor {
	static String strurl,strdbnameip, strdbuser, strdbpass, strdb,strouttablename,strinputtablename;
	static DBActivityMInsLM db;
	reviewlinkextractor() throws FileNotFoundException
	{
		Properties props = new Properties();
	    Reader read=(Reader)new FileReader(new File("settings.properties"));
	try{
		
		System.out.println("start1");
		props.load(read);
	}catch(Exception e)
	{
	}
	try{
		
	
		   strurl=props.getProperty("MainURL");
		   strdbnameip=props.getProperty("DataBaseIP");
		   strdbuser=props.getProperty("DataBaseUser");
		   strdbpass=props.getProperty("DataBasePassword");
		   strdb=props.getProperty("DataBase");
		   strouttablename=props.getProperty("OutputTableName");
		   strinputtablename=props.getProperty("InputTableName");
		   System.out.println(strouttablename+strinputtablename);
	   read.close();
	   
	   linkextractor();
	  
	}catch(Exception cnfe){
		System.out.println("Error..."+cnfe);
	}
	}
	
	
public static void main(String[] args) throws FileNotFoundException {
    // TODO code application logic here
	@SuppressWarnings("unused")
	reviewlinkextractor pd=new reviewlinkextractor();
}

static void linkextractor()
{
	int docno=1,id=0;
    String reviewlink=null,revlink=null,nextlink=null;
    
    String src=null;
    
    Connection connection=null;
	PreparedStatement stmt=null,stmtupdate=null;
	db=new DBActivityMInsLM(strdbnameip,strdbuser,strdbpass,strdb);
	try
	{
		Class.forName("com.mysql.jdbc.Driver");						
		connection=DriverManager.getConnection("jdbc:mysql://"+strdbnameip+":3306/"+strdb+"",""+strdbuser+"",""+strdbpass+"");
		
		
	stmt = connection.prepareStatement("SELECT * FROM "+strinputtablename+" where Flag=0");
	WebDriver driver=new FirefoxDriver();     
    driver.get("http://www.google.com/");
    ResultSet rs = stmt.executeQuery();
    while (rs.next())
    {
        revlink = rs.getString("ReviewLink");
        id=rs.getInt("ID");

        driver.navigate().to(revlink);
        Thread.sleep(1000);
        src=driver.getPageSource().toString();

        if(!driver.getPageSource().contains("Next"))
	    {
        	System.out.println("IF condition");
	    	Pattern regex = Pattern.compile("<h2 class=\"review_title.*?><a\\s*href=\"(.*?)\">",
	    			Pattern.CANON_EQ | Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
	    		Matcher regexMatcher = regex.matcher(src);
	    		while (regexMatcher.find()) {
	    			reviewlink="http://www.epinions.com"+regexMatcher.group(1);
	        		db.insertlink(reviewlink, docno,strouttablename);
	    			System.out.println(reviewlink+ docno+strouttablename);
	        		docno++;  
	    		} 
        	   	docno=1;
	    }else{
	    	src=driver.getPageSource().toString();
	    	Pattern regex = Pattern.compile("<div id=\"tableFooter\".*?>.*?Page.*?- <a href=\"(.*?)\">View all",
	    			Pattern.CANON_EQ | Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
	    		Matcher regexMatcher = regex.matcher(src);
	    		if (regexMatcher.find()) {
	    			nextlink="http://www.epinions.com"+regexMatcher.group(1); 
	    		} 
	    		driver.navigate().to(nextlink);
	    		src=driver.getPageSource().toString();
	        	Pattern linregex = Pattern.compile("<h2 class=\"review_title.*?><a\\s*href=\"(.*?)\">",
		    			Pattern.CANON_EQ | Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
		    		Matcher linregexMatcher = linregex.matcher(src);
		    		while (linregexMatcher.find()) {
		    			reviewlink="http://www.epinions.com"+linregexMatcher.group(1);
		        		db.insertlink(reviewlink, docno,strouttablename);
		    			System.out.println(reviewlink+ docno+strouttablename);
		        		docno++;  
		    		} 
		    		docno=1;
	    }
        
        
	stmtupdate=connection.prepareStatement("update "+strinputtablename+" set Flag=1 where ID="+id);
	stmtupdate.executeUpdate();
	//Thread.sleep(1000);
    }
    stmt.close();
    stmtupdate.close();
}catch(Exception e)
{
	System.out.println(e);
}}
}

