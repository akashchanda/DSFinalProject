/*
  Author: Christopher B. Millsap
  Email: cmillsap2013@my.fit.edu
  Course: CSE2010	
  Section: 4
  Description: This class is the dictionary object. Used to store, search, and increment occurenses of words.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

public class dictionary {
	private range[] rangeOfWords = new range[26]; // Which index the corresponding letter to word starts in dictionary
	private wordObject[] dictionary;
	private String fileName;

	/**
	 * @param file_
	 * @Description
	 * The constructor for the class
	 * to run: dictionary d = new dictionary("word file as string goes here");
	 */
	public dictionary(String file_) {
		initializeRange();
		fileName = file_;
		getFile(fileName);
		
		//testing(); // Comment out on live run
	}

	/**
	 * @Description
	 * This method is used to test the functionality of the program
	 */
	@SuppressWarnings("unused")
	private void testing() {
		for (int i = 0; i < rangeOfWords.length; i++) {
			System.out.println("Char: " + ((char) (i + 97)) + ": ");
			System.out.println("Begining: " + rangeOfWords[i].getBegining());
			System.out.println("End: " + rangeOfWords[i].getEnd());
		}
	}

	/**
	 * @Description
	 * Holds the range of each character in the alphabet for the given dictionary words
	 * Range(-1,-1) means no range set yet. Will mean no word with that character in dictionarry
	 */
	private void initializeRange() {
		for (int i = 0; i < rangeOfWords.length; i++) {
			rangeOfWords[i] = new range(-1,-1);
		}
	}

	/**
	 * @param filenameGetFile
	 * @Description
	 * Opens the file and parses the words to an index in the array
	 */
	private void getFile(String filenameGetFile) {
		if (!filenameGetFile.equals("") && !filenameGetFile.equals(null)) {

			String readLine = "";
			
			try {

				BufferedReader br = new BufferedReader(new FileReader(filenameGetFile));
				
				final int size2 = getFileLineNumber(filenameGetFile); // Method returns size

				int counter = 0; // Increment the index in the dictionary array
				int letterIndexCounter = 0;
				
				int start = 0; // Starting index of word based on first letter use counter for last index
				
				dictionary = new wordObject[size2];
				char firstChar = 0;
				
				while ((readLine = br.readLine()) != null) {
					
					if(counter <= 0) {
						firstChar = readLine.toLowerCase().charAt(0);
					}
					
					wordObject wO = new wordObject(readLine.toLowerCase(), 0);
					dictionary[counter] = wO;
					counter++;
										
					if(readLine.toLowerCase().charAt(0) == firstChar) {
						letterIndexCounter = (int) (firstChar - 97);
						rangeOfWords[letterIndexCounter].setBegining(start);
						rangeOfWords[letterIndexCounter].setEnd(counter - 1);
					} else {
						firstChar = readLine.toLowerCase().charAt(0);
						start = counter - 1;
						
						letterIndexCounter = (int) (firstChar - 97);
						rangeOfWords[letterIndexCounter].setBegining(start);
						rangeOfWords[letterIndexCounter].setEnd(counter - 1);
					}
				}
				br.close();
			} catch (IOException e) {
				System.err.println("Error Happened: " + e);
			}
		}

	}

	/**
	 * @param filenameGetFile
	 * @return
	 * @throws IOException
	 * @Description
	 * Calculates the number of lines in the file to determin the size of the array.
	 */
	private int getFileLineNumber(String filenameGetFile) throws IOException {
		File file = new File(filenameGetFile);
		FileReader fileReader = new FileReader(file);
		LineNumberReader lnr = new LineNumberReader(fileReader);
		lnr.skip(Long.MAX_VALUE);
		final int size = lnr.getLineNumber() + 1; // Add 1 because line index
													// starts at 0
		// Finally, the LineNumberReader object should be closed to prevent
		// resource leak
		lnr.close();
		return size;
	}

	public wordObject[] getDictionary() {
		return dictionary;
	}
	/**
	 * @param index
	 * @Description
	 * Sets the occurences of each word based on index
	 */
	public void setOccurences(int index) {
		if(!(index >= dictionary.length) && !(index < 0)) {
			dictionary[index].incrementOccurences();
		}
	}
	/**
	 * @param word
	 * @return range with indexes to search
	 */
	public range getRangeofWord(String word) {
		
		word = word.replaceAll("[^a-zA-Z]", "");
		word = word.toLowerCase();
		
		range r = null;
		if(word.equals(null) || word.equals("")) {
			return new range(-1, -1); // No character found
		}
		char c = word.charAt(0);
		int index = ((int) c) - 97;
		
		if(!(index >= rangeOfWords.length) && !(index < 0)) {
			r = rangeOfWords[index];			
			return r;
		} else {
			return new range(-1, -1); // No character found
		}
	}
	/**
	 * @param word
	 * @Description
	 * Updates word's occurences using a binary search method within a range
	 * O(log(n-(range(r1, r2).length))) worst total but O(log(n)) worst
	 * O(1) best case
	 */
	public void updateWord(String word) {
		word = word.replaceAll("[^a-zA-Z]", "");
		word = word.toLowerCase();
		
		range r = getRangeofWord(word);
		int index = binarySearch(word, this.getDictionary(), r.getBegining(), r.getEnd());
		setOccurences(index);
	}
	
	/**
	 * @param word
	 * @return int of occurences
	 */
	public int getOccurences(String word) {
		word = word.replaceAll("[^a-zA-Z]", "");
		word = word.toLowerCase();
		
		range r = getRangeofWord(word);
		int index = binarySearch(word, this.getDictionary(), r.getBegining(), r.getEnd());

		if(!(index >= dictionary.length) && !(index < 0)) {
			return dictionary[index].getOccurences();
		} else {
			return -1;
		}	
	}
	/**
	 * @param word
	 * @return word index -1 if not found
	 */
	public int getWordIndex(String word) {
		word = word.replaceAll("[^a-zA-Z]", "");
		word = word.toLowerCase();
		
		range r = getRangeofWord(word);
		int index = binarySearch(word, this.getDictionary(), r.getBegining(), r.getEnd());

		if(!(index >= dictionary.length) && !(index < 0)) {
			return index;
		} else {
			return -1;
		}	
	}

	private int binarySearch(String key, wordObject[] a, int lo, int hi) {

		if (lo > -1 && hi > -1) {

			while (lo <= hi) {
				// Key is in a[lo..hi] or not present.
				int mid = lo + (hi - lo) / 2;
				
				if (key.toLowerCase().compareTo(a[mid].getWord().toLowerCase()) < 0) {
					hi = mid - 1;
				}
				else if (key.toLowerCase().compareTo(a[mid].getWord().toLowerCase()) > 0){
					lo = mid + 1;
				}
				else {
					return mid;
				}
			}
			return -1;
		} else {
			return -1;
		}
	}

	/**
	 * @param index
	 * @return wordObject with occurences 
	 */
	public wordObject getWord(int index) {
		if(!(index >= dictionary.length) && !(index < 0)) {
			return dictionary[index];
		} else {
			return new wordObject("", -1); // No word or out of bounds
		}
	}
}
