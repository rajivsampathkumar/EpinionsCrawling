package ReviewExtractor;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omg.CORBA.REBIND;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;



/**
 *
 * @author developer
 */
public class reviewextractor {
    static String revlink;
    static String reviewtitle=null;
        static String reviewcontent;
        static String revstars=null,reviewsrc=null,pname=null,revdate=null,revbline=null,overallstar=null;
        static String pros;
        static String cons;
        static String strurl,strdbnameip, strdbuser, strdbpass, strdb,strouttablename,strintablename,revrecom,amt,recomfor=null;
        static int docno=0,id=0;
        public reviewextractor() throws FileNotFoundException {
			// TODO Auto-generated constructor stub
        	Properties props = new Properties();
    	    Reader read=(Reader)new FileReader(new File("settings.properties"));
    	try{
    		
    		System.out.println("start1");
    		props.load(read);
    	}catch(Exception e)
    	{
    	}
    	try{
    		strdbnameip=props.getProperty("DataBaseIP");
    		   strdbuser=props.getProperty("DataBaseUser");
    		   strdbpass=props.getProperty("DataBasePassword");
    		   strdb=props.getProperty("DataBase");
    		   strouttablename=props.getProperty("OutputTableName");
    		   strintablename=props.getProperty("InputTableName");
    		   
    	   read.close();
    	   
    	   
    	  
    	}catch(Exception cnfe){
    		System.out.println("Error..."+cnfe);
    	}
		}
     public static void main(String[] args) throws FileNotFoundException {
    	 @SuppressWarnings("unused")
		reviewextractor pd=new reviewextractor();
         Connection connection;
	PreparedStatement stmt=null,stmtupdate=null;
        
        
        List<String> reviewlinks = new ArrayList<String>();
        
         try{
             DBActivityMInsLM db=new DBActivityMInsLM(strdbnameip,strdbuser,strdbpass,strdb);
             Class.forName("com.mysql.jdbc.Driver");						
		connection=DriverManager.getConnection("jdbc:mysql://"+strdbnameip+":3306/"+strdb+"",""+strdbuser+"",""+strdbpass+"");
		
                stmt = connection.prepareStatement("SELECT * FROM "+strintablename+" where Flag=0");

                 ResultSet rs = stmt.executeQuery();
             WebDriver driver=new FirefoxDriver();     
             driver.get("http://www.google.com/");
             while (rs.next()){
              revlink = rs.getString("Link");
              docno=rs.getInt("Docno");
              id=rs.getInt("ID");
             reviewlinks.add(revlink);
             System.out.println(docno);
                   driver.navigate().to(revlink);
             reviewsrc=driver.getPageSource().toString();
             //System.out.println(reviewsrc);
             
             Pattern pregex = Pattern.compile("<h1 class=\"product_title\">(.*?)</h1>\\s*<span class=\"iReviewStarsMedYellow (.*?)\">",
            			Pattern.CANON_EQ | Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
            		Matcher pregexMatcher = pregex.matcher(reviewsrc);
            		if(pregexMatcher.find()) {
            			pname=pregexMatcher.group(1).trim();
            			overallstar=pregexMatcher.group(2).replace("med","").trim();
            		} 
             
             
             Pattern titregex = Pattern.compile("<h1 class=\\\"summary title.*?>(.*?)</h1>.*?class=\\\"value-title\\\" title=\\\"(.*?)\\\">.*?Written:(.*?)<.*?>",
            			Pattern.CANON_EQ | Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
            		Matcher titregexMatcher = titregex.matcher(reviewsrc);
            		if (titregexMatcher.find()) {
            		reviewtitle=titregexMatcher.group(1).trim(); 
            		revstars=titregexMatcher.group(2).trim();
            		revdate=titregexMatcher.group(3).replaceAll("&nbsp;","-").replaceAll("'","").trim();
            		} 
            		
         
             
             Pattern revregex = Pattern.compile("<div class=\"user_review_full\">(.*?)Recommended:</b>(.*?)<",
 		Pattern.CANON_EQ | Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
 				
             Matcher revregexMatcher = revregex.matcher(reviewsrc);
 				
             Pattern prosregex = Pattern.compile("Pros:</b>(.*?)<",
 						Pattern.CANON_EQ | Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
 					Matcher prosregexMatcher = prosregex.matcher(reviewsrc);
 					Pattern consregex = Pattern.compile("Cons:</b>(.*?)<",
 							Pattern.CANON_EQ | Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
 						Matcher consregexMatcher = consregex.matcher(reviewsrc);
 						if (prosregexMatcher.find()) {
								pros=prosregexMatcher.group(1).replaceAll("<.*?>", "").replace("&amp;", "").trim(); 
							} else pros=null;
								if (consregexMatcher.find()) {
									cons=consregexMatcher.group(1).replaceAll("<.*?>", "").replace("&amp;", "").trim();
								} else cons=null;   
						Pattern regex = Pattern.compile("The Bottom Line:\\s*</b>(.*?)<",
							Pattern.CANON_EQ | Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
									Matcher regexMatcher = regex.matcher(reviewsrc);
									if (regexMatcher.find()) {
										revbline=regexMatcher.group(1).trim();
									} else revbline=null;	
								
 				if(revregexMatcher.find()) {
 						reviewcontent = revregexMatcher.group(1).replaceAll("<.*?>", "").trim().replaceAll("(\\s+\\s+\\s+)", "");
 						revrecom= revregexMatcher.group(2).trim();
                                 }
 				Pattern amtregex = Pattern.compile("Amount Paid.*?:</b>(.*?)<",
 						Pattern.CANON_EQ | Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
 					Matcher amtregexMatcher = amtregex.matcher(reviewsrc);
 					if (amtregexMatcher.find()) {
 						amt="$"+amtregexMatcher.group(1).trim();
 					} else amt=null;
 					Pattern recomregex = Pattern.compile("Recommended for:</b>(.*?)<",
 							Pattern.CANON_EQ | Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
 						Matcher recomregexMatcher = recomregex.matcher(reviewsrc);
 						if (recomregexMatcher.find()) {
 							recomfor=recomregexMatcher.group(1).trim();
 						} else recomfor=null;
 				
 				
                                 System.out.println(reviewtitle+"\t\n"+reviewcontent+"\n\t"+pros+cons+revrecom+recomfor+amt+revbline+revdate+revstars+pname+overallstar);
               
               System.out.println(revlink);        
                                 db.insertreview(docno,revlink,reviewtitle,reviewcontent,pros,cons,revrecom,recomfor,amt,revbline,revdate,revstars,pname,overallstar,strouttablename);
                                 
             stmtupdate=connection.prepareStatement("update "+strintablename+" set Flag=1 where ID="+id);
             stmtupdate.executeUpdate();
             }
                 
                 stmt.close();
               stmtupdate.close();
              
          }catch(Exception e)
          {
              System.out.println(e);
          }
      }
     
 }




