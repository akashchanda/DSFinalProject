import java.util.Comparator;

/**
 *@authors Alexander Barys, Akash Chanda, Christopher Millsap
 *@Description
 * Object to keep the String word and ammount of occurences together
 */

public class wordObject implements Comparator<String>{
	
	// Variables in each instance of the object
	private String word;
	private int occurences;
	
	/**
	 * @param w
	 * @param occ
	 * @Description
	 * Constructor (String w, int occ) w: word to add, occ: ammount of occurences
	 */
	wordObject(String w, int occ) {
		word = w;
		occurences = occ;
	}
	
	/**
	 * @return String representation of word in wordObject
	 */
	public String getWord() {
		return word;
	}

	/**
	 * @param word
	 * Sets the word in wordObject
	 */
	public void setWord(String word) {
		this.word = word;
	}

	/**
	 * @return int of
	 */
	public int getOccurences() {
		return occurences;
	}

	/**
	 * @param occurences
	 * @return int of occurences by 1
	 */
	public int incrementOccurences() {
		return this.occurences = this.getOccurences() + 1;
	}
	@Override
	public int compare(String o1, String o2) {
		return o1.compareTo(o2);
	}
}
