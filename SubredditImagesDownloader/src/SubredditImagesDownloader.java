import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


//does not handle downloading images hosted on imgur/redgifs yet
public class SubredditImagesDownloader {
	static final String redditRegex="https:\\/\\/i\\.redd\\.it\\/.{13}\\..{3,4}";
	static String subredditName; 
	
	static String sortBy() {
		String res="";
		Scanner sc=new Scanner(System.in);
		System.out.println("How to sort?\n1: Hot\n2: Today's top\n3: New");
		String s=sc.nextLine();
	    while(!(s.equals("1")||s.equals("2")||s.equals("3"))) {
	      System.out.println("Invalid choice. Try again.");
	      s=sc.nextLine();
	    }
	    int select=Integer.parseInt(s);
		switch(select) {
			case 1:
				res="hot";
				break;
			case 2:
				res="top";
				break;
			case 3:
				res="new";
				break;
		}
		return res;
	}
	
	//finding every interesting link in the subreddit
	static ArrayList<String> htmlParse(String sortBy) {
		Scanner sc=new Scanner(System.in);
		ArrayList<String> res=new ArrayList<String>();
		try {
			System.out.println("Enter subreddit name (e.g., memes)");
			subredditName=(sc.next()).toLowerCase();
			String subredditLink="https://www.reddit.com/r/"+subredditName+"/"+sortBy;
			Document doc=Jsoup.connect(subredditLink).cookie("over18", "1").maxBodySize(0).get();
			Elements elts=doc.select("script[id*=data]");
			Pattern pattern=Pattern.compile(redditRegex);
			for(Element elt:elts) {
				Matcher matcher=pattern.matcher(elt.toString());
				while(matcher.find()) {
					String toAdd=matcher.group();
					if(toAdd.charAt(toAdd.length()-1)=='"') toAdd=toAdd.substring(0, toAdd.length()-1);//removing " from the end of the url
					res.add(toAdd);
				}
			}
		} catch(org.jsoup.HttpStatusException e) {
			System.out.println("Invalid name.");
			htmlParse(sortBy);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}
		return res;
	}
	
	//downloading a file from a URL list
	static void download(ArrayList<String> urls) {
		try{
			Files.createDirectory(Paths.get(subredditName));//creating directory for what we are going to download
		}
		catch(Exception e) {}//directory already exists => do nothing
		try{
			for(String url:urls) {
				ReadableByteChannel readableByteChannel = Channels.newChannel((new URL(url)).openStream());
				FileOutputStream fileOutputStream = new FileOutputStream(new File(subredditName+"/"+url.substring(18)));//to keep only the interesting part of the url
				fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		download(htmlParse(sortBy()));
	}
}