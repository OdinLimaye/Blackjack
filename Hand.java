import java.util.ArrayList;

public class Hand {
	private ArrayList<Card> hand;
	
	public Hand() {
		this.hand = new ArrayList<Card>();
	}
	
	public void add(Card card) {
		hand.add(card);
	}
	
	public void clear() {
		for (int i = hand.size() - 1; i >= 0; i--) {
			hand.remove(i);
		}
	}
	
	public void remove(int index) {
		hand.remove(index);
	}
	
	public Card get(int index) {
		return hand.get(index);
	}
	
	public int length() {
		return hand.size();
	}
	
	public int numAces() {
		int count = 0;
		for (Card c : hand) {
			if (c.getFace().equals("Ace")) {
				count++;
			}
		}
		return count;
	}
	
	public String toString() {
		if (hand.size() == 0) {
			return "Empty";
		}
		String output = "" + hand.get(0);
		for (int i = 1; i < hand.size(); i++) {
			output += ", " + hand.get(i);
		}
		return output;
	}
}