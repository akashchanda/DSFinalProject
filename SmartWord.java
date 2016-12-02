/*
 * Authors (group members): Alex Barys, Akash Chanda, Chris Millsap
 * Email addresses of group members: abarys2015@my.fit.edu, achanda2015@my.fit.edu, cmillsap2013@my.fit.edu
 * Group Name: CSE2010S4GroupA - Shenanigans
 * Course: CSE2010
 * Section: 4
 * Description of the overall algorithm: We take in the dictionary words and store them in an
 * arrayList object as objects of the WordObject private class. Each WordObject object holds
 * the word itself, the number of times the word occurs, and the average position in a text the word
 * occurs at.  We also store in an array of size 26 the index in the dictionary array of the first 
 * occurrence of a word with each latter, so that the first index of the letterIndices array stores the
 * value of the index of the first word in the dictionary arrayList that begins with the letter a, 
 * and the second index in letterIndices stores the first occurrence of a word that begins with the
 * letter b, and so on. Then we parse through the old_texts file, and for every word in there, we use binary
 * search to find that word in the arrayList of the dictionary, incrementing its occurrences and
 * updating its average position in the text the word occurs at. Here, to make the binary search quicker,
 * our initial bounds to search in the entire dictionary arrayList are set by the letterIndices array, to
 * skip a large part of the binary search and make the entire search quicker. If the word from old_texts is not
 * in the dictionary, we add it and re-sort the arrayList, and update the letterIndices array for the
 * first occurrence of a word with each letter.  When the guess algorithm is called by EvalSmartWord.java,
 * it does a form of inexact binary search to find the upper and lower bounds of the words in the
 * dictionary arrayList that begin with the pattern, which is the letters given for current word
 * we are attempting to guess.  Once that smallest range of words that begin with the pattern is found,
 * every word in that range is given a probability number, and the words with the top three probability numbers
 * are put in to the guesses array, and that is then given back to EvalSmartWord.
 * 
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
	double averageSentenceLength = 0.0;
	int globalWordPos;
	
	/**
	 * main method, used for testing of parts of the program only as it is not executed when EvalSmartWord.java
	 * gets run and calls SmartWord.java
	 * @param args command line arguments
	 * @throws IOException
	 */
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
	/**
	 * constructor for the SmartWord class, forces the fileName of the dictionary to
	 * be given at construction
	 * also uses that fileName to parse the dictionary file and fill the dictionary
	 * arrayList with the words in the dictionary
	 * also then the array that stores the index of the first occurrence of a word
	 * beginning with each letter (the first word beginning with a, then with b, etc)
	 * is initially generated here
	 * @param wordFile the name of the file that holds the words to be in the dictionary
	 * @throws IOException
	 */
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
	
	/**
	 * processes the old messages of the person whose new messages we will be guessing on
	 * increments the occurrences of each word that occurs in the old messages while updating
	 * the average position in the message of the word
	 * words that are found in the old messages and are not in the dictionary are added
	 * to the dictionary
	 * @param oldMessageFilem the file name of the text file with the old messages in it
	 * @throws IOException
	 */
	public void processOldMessages (String oldMessageFile) throws IOException {
		//hardcoded input files
		String customInputFileName = "addin1.txt";
		//process old messages
		if (!oldMessageFile.equals("") && !oldMessageFile.equals(null)) {

			String readLine = "";
			

				BufferedReader br = new BufferedReader(new FileReader(oldMessageFile));
				double totalSentLength = 0;
				int numOfSents = 0;
				boolean done = false;
				boolean initDone = false;
				//while ((readLine = br.readLine()) != null)
				while (!done) {
					String[] allItemsPerLine = readLine.split(" ");
					totalSentLength += allItemsPerLine.length;
					numOfSents++;
					for (int i = 0; i < allItemsPerLine.length; i++) {
						String word = allItemsPerLine[i].replaceAll("[^a-zA-Z]", "");
						word = word.toLowerCase();
						if (word.length() <= 0) {
							continue;
						}
						//found word, increment occurrence, so find word in dictionary
						int index = binarySearchSimple(word);
						if (index == -1) {
							WordObject newWord = new WordObject(word);
							dictionary.add(newWord);
							dictionary.get(dictionary.size() - 1).increaseOccurences((double)(i + 1) / (double)allItemsPerLine.length);
							Collections.sort(dictionary);
							for (int j = newWord.getWord().charAt(0) - 96; j < 26; j++) {
								letterIndices[j]++;
							}
						} else {
							dictionary.get(index).increaseOccurences((double)(i + 1) / (double)allItemsPerLine.length);
						}
					}
					if ((readLine = br.readLine()) == null) {
						done = true;
						initDone = true;
						if (!initDone) {
							br = new BufferedReader(new FileReader(customInputFileName));
							readLine = br.readLine();
							initDone = true;
						} else {
							done = true;
						}
						
					}
				}
				averageSentenceLength = totalSentLength /  numOfSents;
				br.close();
		}
	}
	
	/**
	 * simple and basic binary search done on the dictionary to find a word
	 * @param word the word to find in the dictionary array because it has
	 * occurred in the old messages file
	 * @return returns -1 if the given word was not found in the dictionary
	 * arrayList; returns the index that the given word was found at in the
	 * dictionary arrayList if it was found
	 */
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
	
	/**
	 * called by EvalSmartWord.java to give guesses for a word as it is being "typed"
	 * uses the letterIndices array and an inexact binary search to find the most narrow
	 * range in the dictionary arrayList that could contain the word; in other words,
	 * gives all of the words in the dictionary that begin with the pattern based on
	 * all of the letters given so far
	 * then, using that range of possible words, assigns as probability to each word,
	 * keeping the highest three probability words
	 * @param letter the letter that was just "entered by the user"
	 * @param letterPosition the position in the word of the letter just entered
	 * @param wordPosition the position in the text message of the word being types
	 * @return returns a string array of size three, that holds three string words
	 * that are the programs guesses for what word is being typed
	 */
	public String[] guess (char letter, int letterPosition, int wordPosition) {
		wordPosition++; //done to change zero-indexing to indexing starting at 1
		globalWordPos = wordPosition;
		double relativeWordPos = wordPosition / (averageSentenceLength);
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
		double[] probs = new double[3];
		//pre-population
		int wordsInGuesses = 0;
		int itemsPassed = 0;
		//System.out.printf("FIRST: LOW IS %d; HIGH IS %d; WORD AT LOW IS %s%n", patternLowRange, patternHighRange, dictionary.get(patternLowRange).getWord());
		//System.out.printf("PATTERN IS %s; PATTERN LENGTH IS %d%n", pattern, pattern.length());
		while (wordsInGuesses < 3) {
			if (wrongWords.contains(dictionary.get(patternLowRange + itemsPassed).getWord())) {
				itemsPassed++;
			} else {
				wordsInGuesses++;
				guesses[wordsInGuesses - 1] = dictionary.get(patternLowRange + itemsPassed).getWord();
				guessLocations[wordsInGuesses - 1] = patternLowRange + itemsPassed;
				WordObject current = dictionary.get(patternLowRange + itemsPassed);
				double probability = getProbability(current, wordPosition, relativeWordPos);
				itemsPassed++;
				probs[wordsInGuesses - 1] = probability;
			}
			
		}
		
		//guessing		
		
		//System.out.printf("SECOND: LOW IS %d; HIGH IS %d; WORD AT LOW IS %s%n", patternLowRange + itemsPassed, patternHighRange, dictionary.get(patternLowRange + itemsPassed).getWord());
		for (int i = patternLowRange + itemsPassed; i < patternHighRange; i++) {
			WordObject current = dictionary.get(i);
			double probability = getProbability(current, wordPosition, relativeWordPos);
			for (int k = 0; k < 3; k++) {
				if (probability > probs[k] && !wrongWords.contains(current.getWord())) {
					probs[k] = probability;
					guesses[k] = current.getWord();
					guessLocations[k] = i;
					break;
				}
			}
		}
		return guesses;
	}
	
	public double getProbability (WordObject current, int wordPosition, double relativeWordPos) {
		double lengthDiff = 1 - (Math.abs(Double.valueOf(pattern.length()) -
				Double.valueOf(current.getWord().length())) / (Double.valueOf(current.getWord().length())));
		double doubleWordPos = Double.valueOf(wordPosition);
		//double averageWordPositionDifference = (current.avgWordPos - wordPosition == 0 ? (0.99) :
		//	(Math.abs(current.avgWordPos - wordPosition)));
		double averageWordPositionDifference = 1 - (Math.abs(current.avgWordPos - relativeWordPos)) ;
		if (relativeWordPos > 1.0) {
			averageWordPositionDifference = 1 - (Math.abs(current.avgWordPos - 1.0)) ;
		}
		//other possible probability equations that are being tested
		//double probability = (lengthDiff) * (current.occurences) / (averageWordPositionDifference);
		//double probability = Math.pow(lengthDiff, 2) * Math.pow(current.occurences, 2);
		double probability;
		if (current.getWord().length() >= 4 && pattern.equals(current.getWord())){
			//System.out.println("222THIS HAS HAPPENED");
			//System.out.printf("PATTERN IS %s%n", pattern);
			probability = Double.POSITIVE_INFINITY;
		} else if (current.occurences == 0) {
			probability = lengthDiff * 0.5;
		} else {
			if (current.getWord().length() >= 4) {
				//comment/uncomment to change whether avgWordPos is being factored in to probablity
				probability = lengthDiff * current.occurences;
				//probability = lengthDiff * current.occurences * (1 + averageWordPositionDifference);
			} else {
				probability = lengthDiff * current.occurences;
			}
			
			
		}
		//*  (1 + (1 / averageWordPositionDifference))
		// * (1 + averageWordPositionDifference)
		return probability;
	}
	
	/**
	 * this method finds the most narrow, or smallest, range of words in the dictionary
	 * that are compatible with the pattern after each letter is given
	 * uses two inexact binary searches, the first to find the first occurrence
	 * of a word beginning with the pattern, and the second to find the last
	 * occurrence of a word beginning with the pattern
	 * to do this, what is actually being searched for is an imaginary "word"
	 * that begins with the pattern but ends with a '`' (for the first occurrence)
	 * or '{' (for the last occurrence).  When searching for the pattern + '`',
	 * this will search for the imaginary spot of the first possible occurrence of a
	 * word beginning with the pattern because the ascii value, the int value, of the
	 * '`' char is less than the ascii (int) value of the first char possible after
	 * cleansing the words to all lower-case a-z letters, an 'a'. This imaginary word
	 * would lie just before the first occurrence of a word beginning with the pattern.
	 * Doing the same thing with appending a '{' char to the end of the pattern
	 * searches for an imaginary word (the pattern +'{') that would lie directly
	 * after the last occurrence of a word beginning with the pattern, but before
	 * the next word after that in the dictionary, allowing an inexact binary
	 * search to find the last occurrence of a word beginning with the pattern.
	 * @param letter the new letter given, or "typed by the user"
	 * @return an int array of size two, where the first index of the array
	 * holds the low end of the range, and the second index of the array holds the high range
	 */
	private int[] getSmallestRange (char letter) {
		pattern = pattern + letter;
		int[] lowHigh = new int[2];
		int low = patternLowRange;
		int high = patternHighRange;
		int mid = low + ((high - low) / 2);
		String findFirst = pattern + '`';
		while (low < high) {
			if (dictionary.get(mid).getWord().compareTo(findFirst) > 0) {
				high = mid;
			} else {
				low = mid + 1;
			}
			mid = low + ((high - low) / 2);
		}
		lowHigh[0] = low - 1;
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
	
	/**
	 * this method is called by EvalSmartWord.java after each guess is made, and
	 * is given values based on whether the word is done and whether one of the
	 * guesses made was correct or not
	 * then, based on that input either:
	 * 1) if one of the guesses was correct, the rest of the word will be
	 * skipped, so the relevant data must be cleared in preparation for the next
	 * word, and the number of occurrences of that word are incremeneted because
	 * it has occurred again
	 * 2) if none of the guesses were correct, and the given word is null, that means
	 * that the word is not yet done, so add all of the three things that were guessed last
	 * time to a list of words that are known to be wrong, so that none of those words are
	 * guessed again
	 * 3) if none of the guesses were correct, and the given word is not null, that
	 * means that the word is done and it was not ever guessed correctly
	 * relevant data must be cleared in preparation for the next word that will
	 * begin next
	 * @param isCorrectGuess a boolean value that indicates if one of the three
	 * guesses made was correct or not, true if one was, false otherwise
	 * @param correctWord null if the corect word is not yet known, which means
	 * the word is not yet done being "typed", or if the word is done being
	 * "typed," the word itself so that we know the word is done and to move
	 * on to the next word
	 */
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
			int index = binarySearchSimple(correctWord);
			if (index == -1) {
				WordObject newWord = new WordObject(correctWord);
				dictionary.add(newWord);
				dictionary.get(dictionary.size() - 1).increaseOccurences((double)(globalWordPos) / ( averageSentenceLength));
				Collections.sort(dictionary);
				for (int j = newWord.getWord().charAt(0) - 96; j < 26; j++) {
					letterIndices[j]++;
				}
			} else {
				dictionary.get(index).increaseOccurences((double)(globalWordPos) / ( averageSentenceLength));
			}
		}
	}
	
	/**
	 * holds information relevant to each word in the dictionary
	 * includes the word itself, the number of occurrences of the word,
	 * and the average position in a text message of the word
	 * @author Alex
	 *
	 */
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
			int oldOc = occurences;
			occurences++;
			avgWordPos = ((avgWordPos * (double) oldOc) + newWordPos) / (double) (occurences);
			//System.out.printf("avgWordPos: %f; newWordPos: %f%n",avgWordPos, newWordPos);
		}

		@Override
		public int compareTo(WordObject other) {
			return this.getWord().compareTo(other.getWord());
		}
	}
}
