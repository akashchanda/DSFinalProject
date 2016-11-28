/*
  Author: Christopher B. Millsap
  Email: cmillsap2013@my.fit.edu
  Course: CSE2010	
  Section: 4
  Description: This class accompanies alows us to use Collections.binarySearch.
 */

import java.util.Comparator;

public class CustomComparator implements Comparator<wordObject> {
    @Override // To overide the super
    public int compare(wordObject o1, wordObject o2) {
        return o1.getWord().compareTo(o2.getWord()); // Comparing creature names which are strings
    }
}