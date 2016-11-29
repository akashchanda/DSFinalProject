/*
 * Authors (group members): Alex Barys, Akash, Chris
 * Email addresses of group members: abarys2015@my.fit.edu, , 
 * Group Name: CSE2010S4GroupA - Shenanigans
 * Course: CSE2010
 * Section: 4
 * Description of the overall algorithm:
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
public class SmartWord {
	String[] guesses = new String[3]; // 3 guesses from SmartWord
	int[] guessLocations = new int[3];
	public static ArrayList<WordObject> dictionary = new ArrayList<WordObject>();
	static int[] letterIndices = new int[26];
	String pattern = "";
	int patternLowRange;
	int patternHighRange;
	ArrayList<String> wrongWords = new ArrayList<String>();
	
	public static void main (String[] args) throws IOException {
		//for testing
		SmartWord testObject = new SmartWord(args[0]);
		System.out.println(letterIndices['s' - 97]);
		for (int i = letterIndices['s' - 97]; i <letterIndices['s' - 97] + 20 ; i++) {
			System.out.println(testObject.dictionary.get(i-1).getWord());
		}
		System.out.println();
		testObject.processOldMessages(args[1]);
		System.out.println(letterIndices['s' - 97]);
		for (int i = letterIndices['s' - 97]; i <letterIndices['s' - 97] + 20 ; i++) {
			System.out.println(testObject.dictionary.get(i-1).occurences);
		}
		System.out.println();
		System.out.println();
		System.out.println();
		for (int i = 0; i < letterIndices.length; i++) {
			System.out.print(letterIndices[i] + ": ");
			System.out.println(testObject.dictionary.get(letterIndices[i]).getWord());
			
		}
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println(dictionary.get(10000).getWord().compareTo(dictionary.get(10005).getWord()));
		System.out.println(dictionary.get(10000).getWord().compareTo(dictionary.get(9995).getWord()));
		System.out.println();
		System.out.println();
		System.out.println();
		for (int i = 0; i < 20; i++) {
			System.out.println(dictionary.get(239413 - i).getWord());
		}
		
		
	}
	
	public SmartWord (String wordFile) throws IOException {
		//creates and populuates the dictionary
		
		if (!wordFile.equals("") && !wordFile.equals(null)) {

			String readLine = "";

			BufferedReader br = new BufferedReader(new FileReader(wordFile));
			int letterIndexCounter = 0;
			
			int start = 0; // Starting index of word based on first letter use counter for last index
			char firstChar = 96; //dictionary always starts with a, so start with before 'a'
			while ((readLine = br.readLine()) != null) {
				WordObject word = new WordObject(readLine.toLowerCase());
				if (dictionary.size() == 0 || !dictionary.get(dictionary.size() - 1).getWord().equals(word.getWord())) {
					dictionary.add(word);
					if(readLine.toLowerCase().charAt(0) != firstChar) {
						firstChar = readLine.toLowerCase().charAt(0);
						letterIndexCounter =  (firstChar - 97);
						start = dictionary.size() - 1;
						letterIndices[letterIndexCounter] = start;
						
					}
				}
			}
		br.close();
		}
	}
	
	public void processOldMessages (String oldMessageFile) throws IOException {
		//process old messages
		if (!oldMessageFile.equals("") && !oldMessageFile.equals(null)) {

			String readLine = "";
			

				BufferedReader br = new BufferedReader(new FileReader(oldMessageFile));
				
				while ((readLine = br.readLine()) != null) {
					String[] allItemsPerLine = readLine.split(" ");
					for (int i = 0; i < allItemsPerLine.length; i++) {
						String word = allItemsPerLine[i].replaceAll("[^a-zA-Z]", "");
						word = word.toLowerCase();
						if (word.length() <= 0) {
							continue;
						}
						//found word, increment occurence, so find word in dictionary
						int index = binarySearchSimple(word);
						if (index == -1) {
							WordObject newWord = new WordObject(word);
							//dictionary.add(newWord);
							//dictionary.get(dictionary.size() - 1).increaseOccurences(i);
							//System.out.println("sorting");
							//Collections.sort(dictionary);
							//System.out.println("sorting done");
						} else {
							dictionary.get(index).increaseOccurences(i);
						}
					}
				}
				br.close();
		}
	}
	
	public static int binarySearchSimple (String word) {
		int low = letterIndices[word.charAt(0) - 97];
		int high;
		if (word.charAt(0) == 'z') {
			high = dictionary.size() - 1;
		} else {
			high = letterIndices[word.charAt(0) - 96] - 1;
		}
		while (low <= high) {
			// Key is in a[lo..hi] or not present.
			int mid = low + (high - low) / 2;
			
			if (word.toLowerCase().compareTo(dictionary.get(mid).getWord().toLowerCase()) < 0) {
				high = mid - 1;
			}
			else if (word.toLowerCase().compareTo(dictionary.get(mid).getWord().toLowerCase()) > 0){
				low = mid + 1;
			}
			else {
				return mid;
			}
		}
		return -1;
	}
	
	public String[] guess (char letter, int letterPosition, int wordPosition) {
		//System.out.printf("start: plr: %d; phr: %d%nLetter is %c%n", patternLowRange, patternHighRange, letter);
		if (letterPosition == 0) {
			patternLowRange = letterIndices[letter - 97];
			if (letter == 'z') {
				patternHighRange = dictionary.size() - 1;
			} else {
				patternHighRange = letterIndices[letter - 96] - 1;
			}
			pattern = pattern + letter;
		} else {
			int[] range = getSmallestRange(letter);
			patternLowRange = range[0];
			patternHighRange = range[1];
		}
		//System.out.printf("before: plr: %d; phr: %d%n", patternLowRange, patternHighRange);
		//average word position factored into probability?
		double[] probs = new double[3];
		//pre-population
		int wordsInGuesses = 0;
		int itemsPassed = 0;
		while (wordsInGuesses < 3) {
			if (wrongWords.contains(dictionary.get(patternLowRange + itemsPassed).getWord())) {
				itemsPassed++;
			} else {
				wordsInGuesses++;
				itemsPassed++;
				guesses[wordsInGuesses - 1] = dictionary.get(patternLowRange + itemsPassed).getWord();
				guessLocations[wordsInGuesses - 1] = patternLowRange + itemsPassed;
				WordObject current = dictionary.get(patternLowRange + itemsPassed);
				double lengthDiff = Double.valueOf((pattern.length() + 1)) / Double.valueOf(current.getWord().length());
				double doubleWordPos = Double.valueOf(wordPosition);
				double averageWordPositionDifference = Math.abs(1 - (current.avgWordPos / wordPosition));
				//double probability = (lengthDiff) * (current.occurences) / (averageWordPositionDifference);
				double probability = (lengthDiff) * 10 * (current.occurences);
				probs[wordsInGuesses - 1] = probability;
			}
			
		}
		//guessing
		for (int i = itemsPassed; i < patternHighRange - patternLowRange + 1; i++) {
			WordObject current = dictionary.get(patternLowRange + i);
			double lengthDiff = Double.valueOf((pattern.length() + 1)) / Double.valueOf(current.getWord().length());
			double doubleWordPos = Double.valueOf(wordPosition);
			double averageWordPositionDifference = Math.abs(1 - (current.avgWordPos / wordPosition));
			//double probability = (lengthDiff) * (current.occurences) / (averageWordPositionDifference);
			double probability = (lengthDiff) * 10 * (current.occurences);
			for (int k = 0; k < 3; k++) {
				if (probability > probs[k] && !wrongWords.contains(current.getWord())) {
					probs[k] = probability;
					guesses[k] = current.getWord();
					guessLocations[k] = patternLowRange + i;
					break;
				}
			}
		}
		//System.out.printf("%s & %s & %s%n", guesses[0], guesses[1], guesses[2]);
		return guesses;
	}
	
	private int[] getSmallestRange (char letter) {
		pattern = pattern + letter;
		int[] lowHigh = new int[2];
		int low = patternLowRange;
		int high = patternHighRange;
		int mid = low + ((high - low) / 2);
		String findFirst = pattern + '`';
		//System.out.printf("before: pattern is %s; findFirst is %s; low is %d; high is %d; mid is %d%n",pattern, findFirst, low, high, mid);
		while (low < high) {
			if (dictionary.get(mid).getWord().compareTo(findFirst) > 0) {
				high = mid;
			} else {
				low = mid + 1;
			}
			mid = low + ((high - low) / 2);
		}
		lowHigh[0] = low + 1;
		//System.out.printf("after: pattern is %s; findFirst is %s; low is %d; high is %d; mid is %d%n",pattern, findFirst, low, high, mid);
		low = patternLowRange;
		high = patternHighRange;
		mid = low + ((high - low) / 2);
		String findLast = pattern + '{';
		while (low < high) {
			if (dictionary.get(mid).getWord().compareTo(findLast) > 0) {
				high = mid;
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
			//System.out.println("reset pattern case 1");
			wrongWords = new ArrayList<String>();
		} else if (correctWord == null) {
			for (int i = 0; i < 3; i++) {
				wrongWords.add(guesses[i]);
			}
		} else {
			guesses = new String[3];
			pattern = "";
			//System.out.println("reset pattern case 2");
			wrongWords = new ArrayList<String>();
		}
	}
	
	
	private class WordObject implements Comparable<WordObject> {
		public String word;
		public int occurences = 0;
		public double avgWordPos;
		WordObject (String s) {
			word = s;
		}
		
		//accessor methods
		public String getWord () {
			return word;
		}
		
		public void increaseOccurences (double newWordPos) {
			avgWordPos = ((avgWordPos * occurences) + newWordPos) / (++occurences);
		}

		@Override
		public int compareTo(WordObject other) {
			return this.getWord().compareTo(other.getWord());
		}
	}
}
