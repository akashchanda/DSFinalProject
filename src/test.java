/*
  Author: Christopher B. Millsap
  Email: cmillsap2013@my.fit.edu
  Course: CSE2010	
  Section: 4
  Description: Test class to assess the methods in the child classes.
 */

public class test {
	public static void main(String[] args) {
		
		run r = new run("trump_old.txt", "words.txt");
		
//		testGetAllOccurences(r); O(n) on dictionary only use if you really want to check
//		System.out.println();
		testgetSingleWordOccurences(r, "work", true); // Set to true to use typed word
		System.out.println();
		testgetRangeOfWord(r, "work", true); // Set to true to use typed word
		System.out.println();
		testGetWord(r, "work", true); // Set to true to use typed word
		System.out.println();
		testGetWordObject(r);
		System.out.println();
	}

	// Get a random word
	private static void testGetWordObject(run r) {
		int index = randomNumber(r);
		wordObject r_2 = r.getWordObject(index);
		System.out.println("Get Random wordObject item name: " + r_2);
		System.out.println("Word: " + r_2.getWord());
		System.out.println("Occurences: " + r_2.getOccurences() + " |Message: if '0' then no occurences. If '-1' word does not exist");
		
		
	}

	private static void testGetWord(run r, String wordT, boolean howToTest) {
		int index = randomNumber(r);
		String r_3 = r.getWord(index);
		if(howToTest) {
			r_3 = wordT;
			index = r.getWordIndex(wordT);
		}
		System.out.println("Index in dictionary: " + index + " |Word: " + r_3);
		System.out.println("Index -1 means not found");
	}

	private static int randomNumber(run r) {
		int Max = r.getAllOccurences().length - 1;
		int Min = 0; 
		int index = (int)(Math.random() * ((Max - Min) + 1));
		return index;
	}

	private static void testgetRangeOfWord(run r, String wordT, boolean howTest) {
		int index = randomNumber(r);
		String word = r.getWord(index);
		
		if(howTest) {
			word = wordT;
		}
		
		range r_4 = r.getRangeOfWord(word);
		System.out.println("Word: " + word + " |Range in dictionary: (" + r_4.getBegining()+ "," +r_4.getEnd()+ ")");
		
	}

	private static void testgetSingleWordOccurences(run r, String wordT, boolean howTest) {
		int index = randomNumber(r);
		String word = r.getWord(index);
		if(howTest) {
			word = wordT;
		}
		int t_1 = r.getSingleWordOccurences(word);
		System.out.println("Word: " + word + " |Number of occurences: " + t_1);
		System.out.println("-1 occurences means no occurences.");		
	}

	@SuppressWarnings("unused")
	private static void testGetAllOccurences(run r) {
		System.out.println("_________ALL WORD OCCURENCES_________");
		for (int i = 0; i < r.getAllOccurences().length; i++) {
			
			System.out.println("Word: " + r.getAllOccurences()[i].getWord());
			System.out.println("Occurences: " + r.getAllOccurences()[i].getOccurences());

		}
		System.out.println("_____________________________________");
	}
}
