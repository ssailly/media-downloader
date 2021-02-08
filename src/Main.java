import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner sc=new Scanner(System.in);
		System.out.println("Reddit : 1\n4chan : 2");
		String choice=sc.nextLine();
		while(!(choice.equals("1")||choice.equals("2"))) {
			System.out.println("Invalid choice. Try again.");
			choice=sc.nextLine();			
		}
		char c=choice.charAt(0);
		switch (c) {
			case '1':
				redditDownload();
				break;
			case '2':
				chan4Download();
				break;
		}
	}
	
	public static void redditDownload() {
		RedditDownloader d=new RedditDownloader();
		d.download(d.htmlParse(d.choice()));
	}
	
	public static void chan4Download() {
		Chan4Downloader d=new Chan4Downloader();
		d.download(d.htmlParse(d.choice()));		
	}
}
