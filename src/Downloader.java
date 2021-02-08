import java.util.ArrayList;

public interface Downloader {
	public String choice();//returns a link to the page where we want the media to be downloaded from
	
	public ArrayList<String> htmlParse(String s);//finding every interesting link
	
	public void download(ArrayList<String> urls);//downloading files from a URL list
}
