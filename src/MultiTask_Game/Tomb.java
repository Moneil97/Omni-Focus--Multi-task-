package MultiTask_Game;

import java.util.ArrayList;
import java.util.Scanner;

public class Tomb {
	
	static String charactersInOrder[] = { "a", "b", "c", "d", "e", "f", "g", "h", "i",
			"j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
			"w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z", " ", "0", "1", "2", "3", "4", "5", "6", "7",
			"8", "9", ".", ",", ":", "!", "@", "#", "$", "%", "^", "&", "*",
			"(", ")", "_", "-", "{", "}", "[", "]", ";", /* Issue:":", */"<",
			">", "?", "\"", "/", "\\", "~", "+", "=" };
	
	static int times = 0;
	static String message = "";
	static ArrayList<Integer> numbersRandom = new ArrayList<Integer>();
	static ArrayList<String> charactersRandom = new ArrayList<String>();

	public static void main(String[] args) {
		System.out.println(decrypt(encrypt("buttT")));
	}

	private static String encrypt(String input) {

		String charactersInOrder[] = getCharactersInOrder(); //returns full, ordered character list
		ArrayList<String> charactersRandom = getNewCharactersArray(); //returns shuffled character array
		ArrayList<Integer> numbersRandom = getNewNumberArray();//returns array of random non-repeating numbers 
		int times = getNewTimes();
		String encrypt = "";

		for (int a = 0; a < times; a++) {
			encrypt = "";
			for (int x = 0; x < input.length(); x++) {
				for (int i = 0; i < charactersRandom.size(); i++) {
					if (input.substring(x, x + 1).equals(charactersInOrder[i])) {
						encrypt += charactersRandom.get(numbersRandom.get(i));
						break;
					}
				}
			}
			input = encrypt;
		}
		String output = times + "`";

		int x = 0;
		int y = 0;
		boolean done = false;
		boolean finished1 = false, finished2 = false;

		while (!done) {
			done = true;
			if (x < encrypt.length()) {
				output += encrypt.substring(x, x + 1) + "`";
				x++;
				done = false;
			} else {
				if (!finished1) {
					output += "|`";
					finished1 = true;
				} else {
					// Fill in random Characters
					output += charactersRandom.get(rand(0, charactersRandom.size() - 1))+ "`";
				}

			}
			if (y < numbersRandom.size()) {
				output += numbersRandom.get(y) + "`" + charactersRandom.get(y)
						+ "`";
				y++;
				done = false;
			} else {
				if (!finished2) {
					output += "|`|`";
					finished2 = true;
				} else {
					output += numbersRandom.get(rand(0,numbersRandom.size() - 1))+ "`"
							+ charactersRandom.get(rand(0, charactersRandom.size() - 1)) + "`";
				}
			}
		}
		return output;
	}	
	

	private static String decrypt(String input) {
		Scanner s = new Scanner(input);
		s.useDelimiter("`");
		boolean finished1 = false, finished2 = false, finished3 = false;

		times = s.nextInt();

		while (s.hasNext()) {
			String next = s.next();
			if (!next.equals("|") && !finished1)
				message += next;
			else
				finished1 = true;
			if (s.hasNext()) {
				next = s.next();
				if (!next.equals("|") && !finished2)
					numbersRandom.add(Integer.parseInt(next));
				else
					finished2 = true;
			}
			if (s.hasNext()) {
				next = s.next();
				if (!next.equals("|") && !finished3)
					charactersRandom.add(next);
				else
					finished3 = true;
			}
		}
		s.close();
		
		String decrypt = "";

		for (int a = 0; a < times; a++) {
			decrypt = "";
			for (int x = 0; x < message.length(); x++) {
				for (int i = 0; i < charactersRandom.size(); i++) {
					if (message.substring(x, x + 1).equals(
							charactersRandom.get(numbersRandom.get(i)))) {
						decrypt += charactersInOrder[i];
						break;
					}
				}
			}			
			message = decrypt;
		}
		return message;
	}

	public static String[] getCharactersInOrder() {
		return charactersInOrder;
	}

	public static ArrayList<String> getNewCharactersArray() {
		ArrayList<String> charactersRandom = new ArrayList<String>();
		int random;

		for (int i = 0; i < charactersInOrder.length; i++) {
			while (true) {
				random = rand(0, charactersInOrder.length - 1);
				if (!charactersRandom.contains(charactersInOrder[random])) {
					charactersRandom.add(charactersInOrder[random]);
					break;
				}
			}
		}
		return charactersRandom;
	}

	public static ArrayList<Integer> getNewNumberArray() {
		ArrayList<Integer> numbersRandom = new ArrayList<Integer>();
		int min = 0;
		int max = charactersInOrder.length - 1;
		int random;

		for (int i = min; i < max + 1; i++) {
			while (true) {
				random = rand(min, max);
				if (!numbersRandom.contains(random)) {
					numbersRandom.add(random);
					break;
				}
			}
		}
		return numbersRandom;
	}

	public static int getNewTimes() {
		return rand(1, 59);
	}

	public static int rand(int min, int max) {
		// [min, max]
		return min + (int) (Math.random() * ((max - min) + 1));
	}

}
