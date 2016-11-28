/*
  Author: Christopher B. Millsap
  Email: cmillsap2013@my.fit.edu
  Course: CSE2010	
  Section: 4
  Description: Runs the dictionary processor.
 */
public class run {
	/**
	 * No access when null
	 */
	private setOccurences s;
	
	/**
	 * @param oldText
	 * @param dictionary
	 * @Description
	 * This constructor is used to protect access to methods when no data is present
	 * (oldText, dictionaryFile)
	 */
	public run(String oldText, String dictionary) {
		if(oldText.equals("") || dictionary.equals("")) {
			System.out.println("Please enter a valid file name");
		} else {
			s = new setOccurences(dictionary, oldText);
		}	
	}
	
	/**
	 * @return entire dictionary with occurenceses for each word from parsed files
	 */
	public wordObject[] getAllOccurences() {
		return s.getD().getDictionary();
	}
	/**
	 * @param word
	 * @return the integer representation of the occurences of a single word
	 */
	public int getSingleWordOccurences(String word) {
		return s.getD().getOccurences(word);
	}
	
	/**
	 * @param word
	 * @return range of word in dictionary
	 * @Description
	 * Returns a range object(-1,-1) for not found
	 */
	public range getRangeOfWord(String word) {
		return s.getD().getRangeofWord(word);
	}
	
	/**
	 * @param index
	 * @return word in dictionary from index as string
	 */
	public String getWord(int index) {
		if(index > s.getD().getDictionary().length || index < 0) {
			return "";
		} else {
			return s.getD().getDictionary()[index].getWord();
		}	
	}
	/**
	 * @param index
	 * @return wordObject from index
	 */
	public wordObject getWordObject(int index) {
		return s.getD().getWord(index);
	}
	public int getWordIndex(String word) {
		return s.getD().getWordIndex(word);
	}
}
