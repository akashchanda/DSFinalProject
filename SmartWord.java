/*
 * Authors (group members): Alex Barys, Akash, Chris
 * Email addresses of group members: abarys2015@my.fit.edu, , 
 * Group Name: CSE2010S4GroupA
 * Course: CSE2010
 * Section: 4
 * Description of the overall algorithm:
 */
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
public class SmartWord {
	String[] guesses = new String[3]; // 3 guesses from SmartWord
	ArrayList<WordObject> dictionary = new ArrayList<WordObject>();
	int[] letterIndices = new int[26];
	String pattern = "";
	int patternLowRange;
	int patternHighRange;
	
	public SmartWord (String wordFile) throws FileNotFoundException {
		//creates and populuates the dictionary
	}
	
	public void processOldMessages (String oldMessageFile) throws FileNotFoundException {
		//process old messages
	}
	
	public String[] guess (char letter, int letterPosition, int wordPosition) {
		//average word position factored into probability?
		
		int[] range = getSmallestRange(letter, letterPosition);
		for (int i = 0; i < range[1] - range[0] + 1; i++) {
			WordObject current = dictionary.get(range[0] + i);
			double lengthDiff = Double.valueOf((pattern.length() + 1)) / Double.valueOf(current.getWord().length());
			double doubleWordPos = Double.valueOf(wordPosition);
			double averageWordPositionDifference = Math.abs(1 - (current.avgWordPos / wordPosition));
			double probability = (lengthDiff) * (current.occurences) / (averageWordPositionDifference);
		}
		
		
		
		return guesses;
	}
	
	private int[] getSmallestRange (char letter, int letterPos) {
		int[] lowHigh = new int[2];
		int low = patternLowRange;
		int high = patternHighRange;
		int mid = low + ((high - low) / 2);
		String findFirst = pattern + letter + '`';
		while (low < high) {
			if (dictionary.get(mid).getWord().compareTo(findFirst) > 0) {
				high = mid - 1;
			} else {
				low = mid + 1;
			}
			mid = low + ((high - low) / 2);
		}
		lowHigh[0] = low + 1;
		
		low = patternLowRange;
		high = patternHighRange;
		mid = low + ((high - low) / 2);
		String findLast = pattern + letter + '`';
		while (low < high) {
			if (dictionary.get(mid).getWord().compareTo(findLast) > 0) {
				high = mid - 1;
			} else {
				low = mid + 1;
			}
			mid = low + ((high - low) / 2);
		}
		lowHigh[1] = low;
		
		return lowHigh;
	}
	
	public void feedback (boolean isCorrectGuess, String correctWord) {
		
	}
	
	
	private class WordObject {
		public String word;
		public int occurences = 0;
		public double avgWordPos;
		WordObject (String s) {
			word = s;
		}
		
		public String getWord () {
			return word;
		}
	}
}
