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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
public class SmartWord {
    private static final double MULTIPLIER_IF_ZERO_OCCURRENCES = 0.5;
    private static final double SCALE_FACTOR_OF_WORD_POS = .8;
    private static final int WORD_LENGTH_CUTOFF_FOR_USING_POS_INFO = 3;
    private static final int WORD_LENGTH_CUTOFF_FOR_EXACT_MATCH = 4;
    private static final double FRACTION_OF_AVG_SENT_LENGTH = 3.0;
    private static final int NUMBER_OF_GUESSES_TO_PROVIDE = 3;
    private static final int ASCII_VALUE_OF_A = 97;
    private static final int NUMBER_OF_LETTERS_IN_THE_ALPHABET = 26;
    //global variables used to store information for use in various parts of program
    //called by EvalSmartWord separately
    String[] guesses = new String[NUMBER_OF_GUESSES_TO_PROVIDE]; // 3 guesses from SmartWord
    int[] guessLocations = new int[NUMBER_OF_GUESSES_TO_PROVIDE];
    public static final ArrayList<WordObject> dictionary = new ArrayList<WordObject>();
    static final int[] letterIndices = new int[NUMBER_OF_LETTERS_IN_THE_ALPHABET];
    String pattern = "";
    int patternLowRange;
    int patternHighRange;
    ArrayList<String> wrongWords = new ArrayList<String>();
    double averageSentenceLength = 0.0;
    int globalWordPos;
    double upperLimit, lowerLimit;

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
    public SmartWord (final String wordFile) throws IOException {
        //creates and populuates the dictionary
        if (!wordFile.equals("") && !wordFile.equals(null)) {
            String readLine = "";
            final BufferedReader br = new BufferedReader(new FileReader(wordFile));
            int letterIndexCounter = 0;
            int start = 0; // Starting index of word based on first letter
                           //use counter for last index
            char firstChar = ASCII_VALUE_OF_A - 1; //dictionary always starts with a,
                                 //so start with before 'a'
            while ((readLine = br.readLine()) != null) {
                final WordObject word = new WordObject(readLine.toLowerCase());
                if (dictionary.size() == 0 || !dictionary.get(dictionary.size() - 1).
                        getWord().equals(word.getWord())) {
                    dictionary.add(word);
                    if (readLine.toLowerCase().charAt(0) != firstChar) {
                        firstChar = readLine.toLowerCase().charAt(0);
                        letterIndexCounter =  (firstChar - ASCII_VALUE_OF_A);
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
     * increments the occurrences of each word that occurs in the old messages while
     * updating the average position in the message of the word
     * words that are found in the old messages and are not in the dictionary are added
     * to the dictionary
     * @param oldMessageFilem the file name of the text file with the old messages in it
     * @throws IOException
     */
    public void processOldMessages (final String oldMessageFile) throws IOException {
        //hardcoded input file name
        final String customInputFileName = "addin1.txt";
        //process old messages
        if (!oldMessageFile.equals("") && !oldMessageFile.equals(null)) {
            String readLine = "";
            BufferedReader br = new BufferedReader(new FileReader(oldMessageFile));
            double totalSentLength = 0;
            int numOfSents = 0;
            boolean done = false;
            boolean initDone = false;
            while (!done) {
                final String[] allItemsPerLine = readLine.split(" ");
                totalSentLength += allItemsPerLine.length;
                numOfSents++;
                for (int i = 0; i < allItemsPerLine.length; i++) {
                    final String word = (allItemsPerLine[i].replaceAll("[^a-zA-Z]", ""))
                            .toLowerCase();
                    if (word.length() <= 0) {
                        continue;
                    }
                    //found word, increment occurrence, so find word in dictionary
                    final int index = binarySearchSimple(word);
                    if (index == -1) {
                        final WordObject newWord = new WordObject(word);
                        final int indexToInsertAt = findInsertPos(word);
                        dictionary.add(indexToInsertAt, newWord);
                        dictionary.get(indexToInsertAt).
                                increaseOccurences((double) (i + 1)
                                        / (double) allItemsPerLine.length);
                        for (int j = newWord.getWord().charAt(0) - (ASCII_VALUE_OF_A - 1);
                                j < letterIndices.length; j++) {
                            letterIndices[j]++;
                        }
                    } else {
                        dictionary.get(index).increaseOccurences((double) (i + 1)
                                / (double) allItemsPerLine.length);
                    }
                }
                //the following if statement is used to incorporate additional input
                //files into the program that are hardcoded in
                readLine = br.readLine();
                if (readLine == null) {
                    //next two lines prevent any additional input files from being read
                    //remove them or comment them out to allow the additional input
                    //files to be incorporated into the program
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
            averageSentenceLength = totalSentLength / numOfSents;
            upperLimit = averageSentenceLength + (averageSentenceLength
                    / FRACTION_OF_AVG_SENT_LENGTH);
            lowerLimit = averageSentenceLength - (averageSentenceLength
                    / FRACTION_OF_AVG_SENT_LENGTH);
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
    public static int binarySearchSimple (final String word) {
        int low = letterIndices[word.charAt(0) - ASCII_VALUE_OF_A];
        int high;
        if (word.charAt(0) == 'z') {
            high = dictionary.size() - 1;
        } else {
            high = letterIndices[word.charAt(0) - (ASCII_VALUE_OF_A - 1)] - 1;
        }
        while (low <= high) {
            // Key is in a[lo..hi] or not present.
            final int mid = low + (high - low) / 2;
            if (word.toLowerCase().compareTo(dictionary.get(mid).
                    getWord().toLowerCase()) < 0) {
                high = mid - 1;
            } else if (word.toLowerCase().compareTo(dictionary.
                    get(mid).getWord().toLowerCase()) > 0) {
                low = mid + 1;
            } else {
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
    public String[] guess (final char letter, final int letterPosition,
            int wordPosition) {
        wordPosition++; //done to change zero-indexing to indexing starting at 1
        //preliminary calculations
        globalWordPos = wordPosition;
        final double relativeWordPos;
        if (wordPosition <= lowerLimit) {
            relativeWordPos = 0.0;
        } else if (wordPosition >= upperLimit) {
            relativeWordPos = 1.0;
        } else {
            relativeWordPos = wordPosition / (upperLimit - lowerLimit);
        }
        //finding the range of the dictionary arraylist to search through for the three
        //highest probability words
        if (letterPosition == 0) {
            patternLowRange = letterIndices[letter - ASCII_VALUE_OF_A];
            if (letter == 'z') {
                patternHighRange = dictionary.size() - 1;
            } else {
                patternHighRange = letterIndices[letter - (ASCII_VALUE_OF_A - 1)] - 1;
            }
            pattern = pattern + letter;
        } else {
            final int[] range = getSmallestRange(letter);
            patternLowRange = range[0];
            patternHighRange = range[1];
        }
        final double[] probs = new double[NUMBER_OF_GUESSES_TO_PROVIDE];
        //pre-population
        int wordsInGuesses = 0;
        int itemsPassed = 0;
        while (wordsInGuesses < NUMBER_OF_GUESSES_TO_PROVIDE) {
            if (wrongWords.contains(dictionary.
                    get(patternLowRange + itemsPassed).getWord())) {
                itemsPassed++;
            } else {
                wordsInGuesses++;
                final int currentIndex = patternLowRange + itemsPassed;
                guesses[wordsInGuesses - 1] = dictionary.get(currentIndex).getWord();
                guessLocations[wordsInGuesses - 1] = currentIndex;
                final WordObject current = dictionary.get(currentIndex);
                final double probability = getProbability(current, relativeWordPos);
                itemsPassed++;
                probs[wordsInGuesses - 1] = probability;
            }
        }

        //guessing
        for (int i = patternLowRange + itemsPassed; i < patternHighRange; i++) {
            final WordObject current = dictionary.get(i);
            final double probability = getProbability(current, relativeWordPos);
            for (int k = 0; k < probs.length; k++) {
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

    /**
     * performs the math to calculate the probability score of a given word using
     * given values
     * @param current the current word to calculate the probability of
     * @param relativeWordPos the relative word position calculated by the equation
     * at the start of the guess method; used to compare against the words average
     * word position for use in the probability equation
     * @return the double value of the probability of the word
     */
    public double getProbability (final WordObject current, double relativeWordPos) {
        if (relativeWordPos > 1.0) {
            relativeWordPos = 1.0;
        }
        final double lengthDiff = 1 - (Math.abs(Double.valueOf(pattern.length()
                - current.getWord().length()))
                / (Double.valueOf(current.getWord().length())));
        final double averageWordPositionDifference = 1
                - (Math.abs(current.avgWordPos - relativeWordPos));
        final double probability;
        if (current.getWord().length() >= WORD_LENGTH_CUTOFF_FOR_EXACT_MATCH
                && pattern.equals(current.getWord())) {
            probability = Double.POSITIVE_INFINITY;
        } else if (current.occurences == 0) {
            probability = lengthDiff * MULTIPLIER_IF_ZERO_OCCURRENCES;
        } else {
            if (current.getWord().length() >= WORD_LENGTH_CUTOFF_FOR_USING_POS_INFO) {
                probability = lengthDiff * current.occurences
                        * Math.pow((1 + averageWordPositionDifference), SCALE_FACTOR_OF_WORD_POS);
            } else {
                probability = lengthDiff * current.occurences;
            }
        }
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
     * holds the low end of the range, and the second index of the array holds
     * the high range
     */
    private int[] getSmallestRange (final char letter) {
        pattern = pattern + letter;
        final int[] lowHigh = new int[2];
        int low = patternLowRange;
        int high = patternHighRange;
        int mid = low + ((high - low) / 2);
        final String findFirst = pattern + '`';
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
        final String findLast = pattern + '{';
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
     * word, and the number of occurrences of that word are incremented because
     * it has occurred again
     * 2) if none of the guesses were correct, and the given word is null, that means
     * that the word is not yet done, so add all of the three things that were guessed
     * last time to a list of words that are known to be wrong, so that none of those
     * words are guessed again
     * 3) if none of the guesses were correct, and the given word is not null, that
     * means that the word is done and it was not ever guessed correctly
     * relevant data must be cleared in preparation for the next word that will
     * begin next
     * @param isCorrectGuess a boolean value that indicates if one of the three
     * guesses made was correct or not, true if one was, false otherwise
     * @param correctWord null if the correct word is not yet known, which means
     * the word is not yet done being "typed", or if the word is done being
     * "typed," the word itself so that we know the word is done and to move
     * on to the next word
     */
    public void feedback (final boolean isCorrectGuess, final String correctWord) {
        if (isCorrectGuess) {
            for (int i = 0; i < guesses.length; i++) {
                if (guesses[i].equals(correctWord)) {
                    dictionary.get(guessLocations[i]).
                    increaseOccurences(dictionary.get(guessLocations[i]).avgWordPos);
                }
            }
            guesses = new String[NUMBER_OF_GUESSES_TO_PROVIDE];
            pattern = "";
            wrongWords = new ArrayList<String>();
        } else if (correctWord == null) {
            for (int i = 0; i < guesses.length; i++) {
                wrongWords.add(guesses[i]);
            }
        } else {
            guesses = new String[NUMBER_OF_GUESSES_TO_PROVIDE];
            pattern = "";
            wrongWords = new ArrayList<String>();
            final int index = binarySearchSimple(correctWord);
            if (index == -1) {
                final WordObject newWord = new WordObject(correctWord);
                final int indexToInsertAt = findInsertPos(correctWord);
                dictionary.add(indexToInsertAt, newWord);
                dictionary.get(indexToInsertAt).
                        increaseOccurences((double) (globalWordPos)
                                / (averageSentenceLength));
                for (int j = newWord.getWord().charAt(0) - (ASCII_VALUE_OF_A - 1);
                        j < letterIndices.length; j++) {
                    letterIndices[j]++;
                }
            } else {
                dictionary.get(index).increaseOccurences((double) (globalWordPos)
                        / (averageSentenceLength));
            }
        }
    }

    /**
     * binary search to find the position in the dictionary ArrayList that a new word
     * should be inserted at to maintain the sorted order of the ArrayList
     * @param word the word to find the position to insert it at
     * @return an int value that gives the index of the position to insert the word at
     */
    public static int findInsertPos (final String word) {
        int low = letterIndices[word.charAt(0) - ASCII_VALUE_OF_A];
        int high;
        if (word.charAt(0) == 'z') {
            high = dictionary.size() - 2;
        } else {
            high = letterIndices[word.charAt(0) - (ASCII_VALUE_OF_A - 1)] - 1;
        }
        while (low <= high) {
            // Key is in a[lo..hi] or not present.
            final int mid = low + (high - low) / 2;
            if (word.toLowerCase().compareTo(dictionary.get(mid).
                    getWord().toLowerCase()) < 0) {
                high = mid - 1;
            } else if (word.toLowerCase().compareTo(dictionary.get(mid).
                    getWord().toLowerCase()) > 0) {
                low = mid + 1;
            } else {
                return mid;
            }
        }
        return low;
    }

    /**
     * holds information relevant to each word in the dictionary
     * includes the word itself, the number of occurrences of the word,
     * and the average position in a text message of the word
     * @author Alex Barys, Akash Chanda, and Chris Millsap
     *
     */
    private class WordObject implements Comparable<WordObject> {
        public final String word;
        public int occurences = 0;
        public double avgWordPos;

        WordObject (final String s) {
            word = s;
        }

        //accessor methods
        public String getWord () {
            return word;
        }
        /**
         * increments the occurrences of the word object and updates the word objects
         * average position in a sentence
         * @param newWordPos the position in the sentence the new occcurrence of the word
         * was found at
         */
        public void increaseOccurences (final double newWordPos) {
            final int oldOc = occurences;
            occurences++;
            avgWordPos = ((avgWordPos * (double) oldOc) + newWordPos)
                    / (double) (occurences);
        }

        @Override
        public int compareTo (final WordObject other) {
            return this.getWord().compareTo(other.getWord());
        }
    }
}
