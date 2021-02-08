import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Chan4Downloader implements Downloader{
	static String default4chanURL="https://boards.4chan.org/";
	static String mediaURL, board, op, title;
	
	public String choice() {
		Scanner sc=new Scanner(System.in);
		boolean hasToRedo=true;
		
		//choose board
		System.out.println("Choose a board (e.g., b):");
		while(hasToRedo) {
			board=sc.nextLine();
			try {
				Jsoup.connect(default4chanURL+board+"/").maxBodySize(1).get();
				hasToRedo=false;
			} catch(Exception e) {
				System.out.println("Invalid choice. Try again.");
			}
			hasToRedo=true;
			
			//choose thread
			System.out.println("Choose an op's number (e.g., 813245607):");
			while(hasToRedo) {
				op=sc.nextLine();
				try {
					Document doc=Jsoup.connect(default4chanURL+board+"/thread/"+op).get();
					title=doc.title();
					hasToRedo=false;
				} catch(Exception e) {
					System.out.println("Invalid choice. Try again.");
				}
			}
		}
		sc.close();
		mediaURL="https://i.4cdn.org/"+board+"/";
		return (default4chanURL+board+"/thread/"+op);
	}
	
	public ArrayList<String> htmlParse(String threadURL) {
		ArrayList<String> res=new ArrayList<String>();
		try {
			Document doc=Jsoup.connect(threadURL).maxBodySize(0).get();
			Elements elts=doc.select("a.fileThumb");
			for(Element elt:elts) {
				String eltString=elt.toString().substring(27);
				int i=0;
				while(eltString.charAt(i)!='"') i++;
				eltString=eltString.substring(0, i);
				res.add("https:"+eltString);
			}
		} catch(IOException e) {System.out.println("Error.");}
		return res;
	}
	
	public void download(ArrayList<String> urls) {
		String directoryName=title.substring(1, title.length()-8).replace("?", "").replace("/", "").replace("\\", "").replace(":", "").replace("*", "").replace("\"", "").replace("<", "").replace(">", "").replace("|", "").replace(".", "");
		int i=directoryName.length()-1;
		while(directoryName.charAt(i)!='-') i--;
		directoryName=directoryName.substring(0, i-1);
		try{
			Files.createDirectory(Paths.get(directoryName));//creating directory for what we are going to download
		}
		catch(Exception e) {}//directory already exists => do nothing
		try{
			for(int j=0;j<urls.size();j++) {
				String url=urls.get(j);
				System.out.print("Media "+(j+1)+"/"+urls.size()+". ");
				ReadableByteChannel readableByteChannel = Channels.newChannel((new URL(url)).openStream());
				System.out.print(". ");
				FileOutputStream fileOutputStream = new FileOutputStream(new File(directoryName+"/"+url.substring(20+board.length())));
				System.out.print(". ");
				fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
				System.out.println("OK");
			}
			System.out.println("Done!");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
