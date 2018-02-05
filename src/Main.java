
public class Main {
	public static void main(String[] args) {
		
		IndexTheText indexText=null;
		try {
			indexText = new IndexTheText();
		} catch (Exception e) {
			System.out.println("It is not possible to index.");
			
		}
		if(indexText!=null) {
			indexText.countAndDisplayWords();
		}
		
	}
}
