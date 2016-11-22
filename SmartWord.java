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
	int[] guessLocations = new int[3];
	ArrayList<WordObject> dictionary = new ArrayList<WordObject>();
	int[] letterIndices = new int[26];
	String pattern = "";
	int patternLowRange;
	int patternHighRange;
	ArrayList<String> wrongWords = new ArrayList<String>();
	
	public SmartWord (String wordFile) throws FileNotFoundException {
		//creates and populuates the dictionary
	}
	
	public void processOldMessages (String oldMessageFile) throws FileNotFoundException {
		//process old messages
	}
	
	public String[] guess (char letter, int letterPosition, int wordPosition) {
		if (letterPosition == 0) {
			patternLowRange = letterIndices[letter - 97];
			patternHighRange = letterIndices[letter - 96] - 1;
		}
		//average word position factored into probability?
		double[] probs = new double[3];
		int[] range = getSmallestRange(letter, letterPosition);
		patternLowRange = range[0];
		patternHighRange = range[1];
		//pre-population
		int wordsInGuesses = 0;
		int itemsPassed = 0;
		while (wordsInGuesses < 3) {
			if (!wrongWords.contains(dictionary.get(range[0] + itemsPassed).getWord())) {
				itemsPassed++;
			} else {
				wordsInGuesses++;
				itemsPassed++;
				guesses[wordsInGuesses] = dictionary.get(range[0] + itemsPassed).getWord();
				guessLocations[wordsInGuesses] = range[0] + itemsPassed;
				WordObject current = dictionary.get(range[0] + itemsPassed);
				double lengthDiff = Double.valueOf((pattern.length() + 1)) / Double.valueOf(current.getWord().length());
				double doubleWordPos = Double.valueOf(wordPosition);
				double averageWordPositionDifference = Math.abs(1 - (current.avgWordPos / wordPosition));
				//double probability = (lengthDiff) * (current.occurences) / (averageWordPositionDifference);
				double probability = (lengthDiff) * (current.occurences);
				probs[wordsInGuesses] = probability;
			}
			
		}
		for (int i = itemsPassed; i < range[1] - range[0] + 1; i++) {
			WordObject current = dictionary.get(range[0] + i);
			double lengthDiff = Double.valueOf((pattern.length() + 1)) / Double.valueOf(current.getWord().length());
			double doubleWordPos = Double.valueOf(wordPosition);
			double averageWordPositionDifference = Math.abs(1 - (current.avgWordPos / wordPosition));
			//double probability = (lengthDiff) * (current.occurences) / (averageWordPositionDifference);
			double probability = (lengthDiff) * (current.occurences);
			for (int k = 0; k < 3; k++) {
				if (probability > probs[k] && !wrongWords.contains(current.getWord())) {
					probs[k] = probability;
					guesses[k] = current.getWord();
					guessLocations[k] = range[0] + i;
					break;
				}
			}
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
		if (isCorrectGuess) {
			for (int i = 0; i < 3; i++) {
				if (guesses[i].equals(correctWord)) {
					dictionary.get(guessLocations[i]).increaseOccurences(dictionary.get(guessLocations[i]).avgWordPos);
				}
			}
			guesses = new String[3];
			pattern = "";
			wrongWords = new ArrayList<String>();
		} else if (correctWord == null) {
			for (int i = 0; i < 3; i++) {
				wrongWords.add(guesses[i]);
			}
		} else {
			guesses = new String[3];
			pattern = "";
			wrongWords = new ArrayList<String>();
		}
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
		
		public void increaseOccurences (double newWordPos) {
			avgWordPos = ((avgWordPos * occurences) + newWordPos) / (++occurences);
		}
	}
}
