
/**
 * @Description
 * Saves the begining index of the word and the ending index of the word based on the first letter
 * range(-1,-1) means no range
 */
public class range {
	
	private int begining;
	private int end;
	
	public range (int n, int n_1) {
		begining = n;
		end = n_1;
	}
	
	public void setBegining(int begining) {
		this.begining = begining;
	}

	public void setEnd(int end) {
		this.end = end;
	}
	
	public int getBegining() {
		return begining;
	}

	public int getEnd() {
		return end;
	}
}
