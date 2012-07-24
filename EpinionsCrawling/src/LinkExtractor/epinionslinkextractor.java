package LinkExtractor;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 *
 * @author developer
 */
public class epinionslinkextractor {

    /**
     * @param args the command line arguments
     */static String src=null,linksrc=null;
     static DBActivityMInsLM db;
     static int totalpages=0,mainpagestotal=0;
     static String strurl,strdbnameip, strdbuser, strdbpass, strdb,strouttablename,strlinktablename;
     Pattern regex ;
		Matcher regexMatcher;
		epinionslinkextractor() throws FileNotFoundException
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
    		       		   
    	   read.close();
    	   
    	   linkextractor();
    	  
    	}catch(Exception cnfe){
    		System.out.println("Error..."+cnfe);
    	}
		}
		
		
    public static void main(String[] args) throws FileNotFoundException {
        // TODO code application logic here
    	@SuppressWarnings("unused")
		epinionslinkextractor pd=new epinionslinkextractor();
    }
    
    static void linkextractor()
    {

        
        String templink=null;
        String strpname = null;
        String strstars = null;
        String strtotalreviews = null,price=null;
        String divsrc=null,reviewlink=null,nextlink=null;
        try{
            db=new DBActivityMInsLM(strdbnameip,strdbuser,strdbpass,strdb);
            List<String> links = new ArrayList<String>();
           
            WebDriver driver=new FirefoxDriver();
            driver.get(strurl);
           while(driver.getPageSource().contains("Next"))
           {
            linksrc=driver.getPageSource().toString();
            Pattern srcregex = Pattern.compile("<div class=\"productInfo left\">.*?</div>\\s*</div>",
            		Pattern.CANON_EQ | Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
            	Matcher srcregexMatcher = srcregex.matcher(linksrc);
            	System.out.println("productname  totalrev  link");
            	while (srcregexMatcher.find()) {
            		divsrc=srcregexMatcher.group(); 
            		Pattern regex = Pattern.compile("<h2><a.*?>(.*?)</a></h2>\\s*<div class=\"productReviews\">\\s*.*?.*?<a href=\"(.*?)\">(.*?)</a>",
            				Pattern.CANON_EQ);
            			Matcher regexMatcher = regex.matcher(divsrc);
            			while (regexMatcher.find()) {
            				strpname=regexMatcher.group(1);
            				strtotalreviews=regexMatcher.group(3);
            				reviewlink="http://www.epinions.com"+regexMatcher.group(2);
            				db.insertData(strpname, strtotalreviews, reviewlink, strouttablename);
            				System.out.println(strpname+strtotalreviews+reviewlink);
            				
            			} 	
            	}
            	Pattern regex = Pattern.compile("<div class=\"paging2 left\">.*?&nbsp;<a href=\"(.*?)\">Next",
            			Pattern.CANON_EQ | Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
            		Matcher regexMatcher = regex.matcher(linksrc);
            		if (regexMatcher.find()) {
            			nextlink="http://www.epinions.com"+regexMatcher.group(1);
            		} 
            		driver.navigate().to(nextlink);
           }
        }catch(Exception e)
        {
            System.out.println(e);
        }
    
    }
    
    
}
