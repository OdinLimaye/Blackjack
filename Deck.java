import java.util.ArrayList;
import java.util.Random;

public class Deck {
	private static Random rand = new Random();
	private static String[] suits = {"Clubs", "Spades", "Diamonds", "Hearts"};
	private static String[] faces = {"2", "3", "4", "5", "6", "7", "8", "9", "10", 
			"Jack", "Queen", "King", "Ace"};
	private ArrayList<Card> deck;
	private ArrayList<Card> discard;
	
	public Deck() {
		this.deck = new ArrayList<Card>();
		this.discard = new ArrayList<Card>();
		this.populate(1);
	}
	
	public Deck(int num) {
		this.deck = new ArrayList<Card>();
		this.discard = new ArrayList<Card>();
		this.populate(num);
	}
	
	private void populate(int num) {
		for (int i = 0; i < num; i++) {
			for (String suit : suits) {
				for (String face : faces) {
					deck.add(new Card(face, suit));
				}
			}
		}
	}
	
	public void shuffle() { 
		for (int i = 0; i < deck.size(); i++) {
			int index = rand.nextInt(deck.size());
			Card temp = deck.get(i);
			deck.set(i, deck.get(index));
			deck.set(index, temp);
		}
	}
	
	public void reset() {
		for (int i = discard.size() - 1; i >= 0; i--) {
			deck.add(discard.get(i));
			discard.remove(i);
		}
	}
	
	public Card drawFromTop() {
		Card temp = deck.get(deck.size() - 1);
		deck.remove(deck.size() - 1);
		discard.add(temp);
		return temp;
	}
	
	public Card drawFromBottom() {
		Card temp = deck.get(0);
		deck.remove(0);
		discard.add(temp);
		return temp;
	}
	
	public Card drawRandom() {
		int index = rand.nextInt(deck.size());
		Card temp = deck.get(index);
		deck.remove(index);
		return temp;
	}
	
	public String toString() {
		String result = "";
		for (int i = 0; i < deck.size() - 1; i++) {
			result += deck.get(i) + ", ";
		}
		result += deck.get(deck.size() - 1);
		return result;
	}
}