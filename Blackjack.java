import java.util.ArrayList;
import java.util.Scanner;

public class Blackjack {
	public static Scanner scan = new Scanner(System.in);
	public static ArrayList<String> users;
	public static ArrayList<Integer> money;
	public static Deck deck;
	
	public static void main(String[] args) {
		intro();
		users = getUsers();
		money = getStartAmount();
		int start = money.get(0);
		deck = new Deck(1);
		
		int round = 1;
		while (users.size() > 0) {
			playRound(round);
			endRound(start);
			round++;
		}
		System.out.println("\nThe game is over.");
	}
	
	public static void endRound(int start) {
		for (int i = 0; i < users.size(); i++) {
			System.out.println(users.get(i) + ", your balance is " + money.get(i) + "$.");
			if (money.get(i) == 0) {
				System.out.println("You lost all of your money and are being automatically removed from the game.");
				users.remove(i);
				money.remove(i);
			} else {
				System.out.print("Enter \"y\" to keep playing, or enter \"n\" to quit the game: ");
				String choice = scan.nextLine();
				while (!(choice.equalsIgnoreCase("y") || choice.equalsIgnoreCase("n"))) {
					System.out.print("Invalid choice. Enter your choice again: ");
					choice = scan.nextLine();
				}
				System.out.println();
				if (choice.equalsIgnoreCase("n")) {
					System.out.println(users.get(i) + ", you have quit the game.");
					System.out.println("You starting balance was " + start + "$, and you ended with " + money.get(i) + "$.");
					if (money.get(i) >= start) {
						System.out.println("Your net gain was " + (money.get(i) - start) + "$.");
					} else {
						System.out.println("Your net loss was " + (start - money.get(i)) + "$.");
					}
					users.remove(i);
					money.remove(i);
					i--;
				}
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public static void playRound(int round) {
		deck.reset();
		deck.shuffle();
		System.out.println("Round " + round + ":");
		int[] pot = getPot();
		
		ArrayList<Hand>[] hands = new ArrayList[users.size()];
		Hand dealer = dealHands(hands);
		printHands(hands, dealer);
		printChoices();
		
		ArrayList<Integer>[] choices = new ArrayList[users.size()];
		for (int i = 0; i < choices.length; i++) {
			choices[i] = new ArrayList();
			choices[i].add(-1);
		}
		for (int i = 0; i < users.size(); i++) {
			for (int j = 0; j < hands[i].size(); j++) {
				j += playTurn(i, j, pot, choices, hands, dealer);
			}
		}
		dealerTurn(dealer, choices, hands, pot);
		System.out.println();
	}
	
	public static void dealerTurn(Hand dealer, ArrayList<Integer>[] choices, ArrayList<Hand>[] hands, int[] pot) {
		System.out.println("It is now the dealer's turn.\n");
		System.out.println("Dealer's full hand: " + dealer);
		while (sumOf(dealer) < 17) {
			System.out.println("The dealer hits.");
			Card temp = deck.drawFromTop();
			dealer.add(temp);
			System.out.println("Dealer's new hand: " + dealer);
		}
		if (sumOf(dealer) > 21) {
			System.out.println("The dealer's hand is bust. All hands still in play beat the dealer.");
			for (int i = 0; i < users.size(); i++) {
				for (int j = 0; j < hands[i].size(); j++) {
					if (choices[i].get(j) == 1) {
						money.set(i, money.get(i) + 2 * pot[i]);
					} else if (choices[i].get(j) == 3) {
						money.set(i, money.get(i) + 4 * pot[i]);
					}
				}
			}
		} else {
			System.out.println("The dealer stands.");
			for (int i = 0; i < users.size(); i++) {
				for (int j = 0; j < hands[i].size(); j++) {
					if (choices[i].get(j) == 1) {
						if (sumOf(hands[i].get(j)) > sumOf(dealer)) {
							money.set(i, money.get(i) + 2 * pot[i]);
							System.out.println(users.get(i) + ", your hand beats the dealer.");
						} else if (sumOf(hands[i].get(j)) == sumOf(dealer)) {
							System.out.println(users.get(i) + ", your hand ties with the dealer.");
							money.set(i, money.get(i) + pot[i]);
						} else {
							System.out.println(users.get(i) + ", your hand loses to the dealer.");
						}
					} else if (choices[i].get(j) == 3) {
						if (sumOf(hands[i].get(j)) > sumOf(dealer)) {
							System.out.println(users.get(i) + ", your hand beats the dealer.");
							money.set(i, money.get(i) + 4 * pot[i]);
						} else if (sumOf(hands[i].get(j)) == sumOf(dealer)) {
							System.out.println(users.get(i) + ", your hand ties with the dealer.");
							money.set(i, money.get(i) + 2 * pot[i]);
						} else {
							System.out.println(users.get(i) + ", your hand loses to the dealer.");
						}
					}
				}
			}
		}
	}
	
	public static int playTurn(int i, int j, int[] pot, ArrayList<Integer>[] choices, ArrayList<Hand>[] hands, Hand dealer) {
		int split = 0;
		choices[i].set(j, getInitialChoice(i, j, pot, hands, dealer));
		while (choices[i].get(j) == 2) {
			hit(i, j, hands);
			if (isBust(i, j, hands)) {
				break;
			} else {
				choices[i].set(j, getChoice(i, j, hands, dealer));
			}
		}
		if (choices[i].get(j) == 3) {
			doubleDown(i, j, hands, pot);
		} else if (choices[i].get(j) == 4) {
			split(i, j, hands, choices, pot);
			split--;
		} else if (choices[i].get(j) == 5){
			surrender(i, pot);
		}
		if (isBust(i, j, hands)) {
			bust(i, j, choices);
		}
		return split;
	}
	
	public static void split(int i, int j, ArrayList<Hand>[] hands, ArrayList<Integer>[] choices, int[] pot) {
		money.set(i, money.get(i) - pot[i]);
		Card temp = hands[i].get(j).get(1);
		choices[i].add(-1);
		hands[i].get(j).remove(1);
		hands[i].add(new Hand());
		hands[i].get(j + 1).add(temp);
		hands[i].get(j).add(deck.drawFromTop());
		hands[i].get(j + 1).add(deck.drawFromTop());
		System.out.println("Your new hands are: ");
		System.out.println(hands[i].get(j));
		System.out.println(hands[i].get(j + 1) + "\n");
	}
	
	public static void bust(int i, int j, ArrayList<Integer>[] choices) {
		choices[i].set(j, 0);
		System.out.println("Your hand is bust, and you have lost all of your wager.");
		System.out.println();
	}
	
	public static boolean isBust(int i, int j, ArrayList<Hand>[] hands) {
		return (sumOf(hands[i].get(j)) > 21);
	}
	
	public static void hit(int i, int j, ArrayList<Hand>[] hands) {
		Card temp = deck.drawFromTop();
		System.out.println("You drew the " + temp);
		hands[i].get(j).add(temp);
		System.out.println("Your new hand: " + hands[i].get(j));
	}
	
	public static void doubleDown(int i, int j, ArrayList<Hand>[] hands, int[] pot) {
		Card temp = deck.drawFromTop();
		System.out.println("You drew the " + temp);
		hands[i].get(j).add(temp);
		money.set(i, money.get(i) - pot[i]);
		System.out.println("Your wager is now " + (pot[i] * 2) + "$.");
		System.out.println("Your new hand: " + hands[i].get(j));
		System.out.println();
	}
	
	public static void surrender(int i, int[] pot) {
		money.set(i, money.get(i) + (pot[i] / 2));
		pot[i] = 0;
	}
	
	public static int sumOf(Hand hand) {
		int sum = 0;
		for (int i = 0; i < hand.length(); i++) {
			sum += valueOf(hand.get(i));
		}
		int i = 0;
		while (i < hand.numAces() && sum > 21) {
			sum -= 10;
			i++;
		}
		return sum;
	}
	
	public static int valueOf(Card c) {
		String face = c.getFace();
		String nums = "2345678910";
		String faces = "JackQueenKing";
		if (nums.contains(face)) {
			return Integer.parseInt(face);	
		} else if (faces.contains(face)) {
			return 10;
		} else {
			return 11;
		}
	}
	
	public static int getChoice(int i, int j, ArrayList<Hand>[] hands, Hand dealer) {
		System.out.println("Dealer is showing: " + dealer.get(0));
		System.out.println("Your hand: " + hands[i].get(j));
		System.out.println(users.get(i) + ", your balance is " + money.get(i) + "$.");
		String prompt = "Enter your choice: ";
		System.out.print(prompt);
		int choice = getValidInt(prompt, 5);
		while (choice != 1 && choice != 2) {
			System.out.println("This choice is no longer available.\n");
			System.out.print(prompt);
			choice = getValidInt(prompt, 5);
		}
		System.out.println();
		return choice;
	}
	
	public static int getInitialChoice(int i, int j, int[] pot, ArrayList<Hand>[] hands, Hand dealer) {
		System.out.println("Dealer is showing: " + dealer.get(0));
		System.out.println("Your hand: " + hands[i].get(j));
		System.out.println(users.get(i) + ", your balance is " + money.get(i) + "$.");
		String prompt = "Enter your choice: ";
		System.out.print(prompt);
		int choice = getValidInt(prompt, 5);
		while ((choice == 3 && money.get(i) < pot[i]) || (choice == 4 && (money.get(i) < pot[i] || !canSplit(hands[i].get(j))))) {
			System.out.println("You cannot choose this action.\n");
			System.out.println(prompt);
			choice = getValidInt(prompt, 5);
		}
		System.out.println();
		return choice;
	}
	
	public static boolean canSplit(Hand hand) {
		return (valueOf(hand.get(0)) == valueOf(hand.get(1))); 
	}
	
	public static void printChoices() {
		System.out.println("Type 1 to stand.");
		System.out.println("Type 2 to hit.");
		System.out.println("Type 3 to double down.");
		System.out.println("Type 4 to split.");
		System.out.println("Type 5 to surrender.");
		System.out.println();
	}
	
	public static Hand dealHands(ArrayList<Hand>[] hands) {
		Hand dealer = new Hand();
		for (int i = 0; i < users.size(); i++) {
			hands[i] = new ArrayList<Hand>();
			hands[i].add(new Hand());
			hands[i].get(0).add(deck.drawFromTop());
		}
		dealer.add(deck.drawFromTop());
		for (int i = 0; i < users.size(); i++) {
			hands[i].get(0).add(deck.drawFromTop());
		}
		dealer.add(deck.drawFromTop());
		return dealer;
	}
	
	public static void printHands(ArrayList<Hand>[] hands, Hand dealer) {
		System.out.println("Dealer is showing: " + dealer.get(0));
		System.out.println();
		for (int i = 0; i < users.size(); i++) {
			System.out.println(users.get(i) + "'s hand: " + hands[i].get(0));
		}
		System.out.println();
	}
	
	public static int[] getPot() {
		int[] pot = new int[users.size()];
		for (int i = 0; i < users.size(); i++) {
			System.out.println(users.get(i) + ", your balance is " + money.get(i) + "$.");
			String prompt = "Enter your starting wager for this round: ";
			System.out.print(prompt);
			int bet = getValidInt(prompt, money.get(i));
			System.out.println();
			money.set(i, money.get(i) - bet);
			pot[i] += bet;
		}
		return pot;
	}
	
	public static ArrayList<Integer> getStartAmount() {
		ArrayList<Integer> money = new ArrayList<Integer>();
		String prompt = "Enter the starting amount of money for each player: ";
		System.out.print(prompt);
		int num = getValidInt(prompt, Integer.MAX_VALUE);
		System.out.println();
		int size = users.size();
		for (int i = 0; i < size; i++) {
			money.add(num);
		}
		return money;
	}
	
	public static void intro() {
		System.out.println("This program allows users to play Blackjack against a computer dealer.");
		System.out.println("The dealer stands on 17's and above and hits on 16's and below.");
		System.out.println("The dealer does not hit on soft 17's.\n");
	}
	
	public static ArrayList<String> getUsers() {
		String prompt = "Enter the number of players (7 max): ";
		System.out.print(prompt);
		int size = getValidInt(prompt, 7);
		System.out.println();
		ArrayList<String> users = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			System.out.print("Player " + (i + 1) + ", enter your name: ");
			users.add(scan.nextLine());
		}
		System.out.println();
		return users;
	}
	
	public static int getValidInt(String prompt, int max) {
		int num = getInt(prompt);
		while (num <= 0 || num > max) {
			System.out.println("Invalid input; you must enter a valid number.\n");
			System.out.print(prompt);
			num = getInt(prompt);
		}
		return num;
	}
	
	public static int getInt(String prompt) {
		while (!scan.hasNextInt()) {
			scan.next();
			scan.nextLine();
			System.out.println("Invalid input; you must enter a number.\n");
			System.out.print(prompt);
		}
		int x = scan.nextInt();
		scan.nextLine();
		return x;
	}
}