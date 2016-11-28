/*
  Author: Christopher B. Millsap
  Email: cmillsap2013@my.fit.edu
  Course: CSE2010	
  Section: 4
  Description: This class is the setOccurences object. Used to set occurences of words and is the parent of the dictionary class.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class setOccurences {

	private String fileName;
	private String dictionary;
	private dictionary d;
	
	/**
	 * @return dictionary type
	 * @Description
	 * Use this to return all the methods availible in dictionary
	 */
	public dictionary getD() {
		return d;
	}

	public setOccurences(String dictionary_, String oldText_) {
		dictionary = dictionary_;
		fileName = oldText_;
		
		updateOccurences(fileName, dictionary);
	}

	/**
	 * @param fileName2
	 * @param fileNameDictionary
	 * @Description
	 * Updates the words in the dictionary with their occurences in the oldText file
	 * (oldtext, dictionaryFile)
	 */
	private void updateOccurences(String fileName2, String fileNameDictionary) {
		d = new dictionary(fileNameDictionary); // words
		
		if (!fileName2.equals("") && !fileName2.equals(null)) {

			String readLine = "";
			
			try {

				BufferedReader br = new BufferedReader(new FileReader(fileName2));
				
				while ((readLine = br.readLine()) != null) {
					String[] allItemsPerLine = readLine.split(" ");
					for (int i = 0; i < allItemsPerLine.length; i++) {
						String word = allItemsPerLine[i].replaceAll("[^a-zA-Z]", "");
						word = word.toLowerCase();
						d.updateWord(word);
					}
				}
				br.close();
			} catch (IOException e) {
				System.err.println("Error Happened: " + e);
			}
		}
		
	}
}
