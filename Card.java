public class Card {
	private String suit;
	private String face;
	
	public Card(String face, String suit) {
		this.suit = suit;
		this.face = face;
	}
	
	public String getFace() {
		return face;
	}
	
	public String getColor() {
		if (suit.equals("Clubs") || suit.equals("Spades")) {
			return "Black";
		} else {
			return "Red";
		}
	}
	
	public String getSuit() {
		return suit;
	}
	
	public String toString() {
		return (face + " of " + suit);
	}
}