import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.IntPredicate;

public class Filter {
	public static void main(String[] args) {
		var comb = new WordList();
		var list = new WordList(Path.of("words.txt"), s -> filter(s, comb));
		try (var out = new PrintStream(Files.newOutputStream(Path.of("filtered.txt")))) {
			for (var word : list) {
				out.println(word);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Wrote list of " + list.n + " words.");
	}
	
	public static boolean filter(String str, WordList comb) {
		if (str.length() != 5) return false;
		
		var sorted = sortLetters(str);
		if (sorted.length() != 5) return false;
		
		if (comb.contains(sorted)) return false;
		comb.add(sorted);
				
		return true;
	}
	
	public static String sortLetters(String str) {
		var ints = str.chars().sorted().distinct().toArray();
		var chars = new char[ints.length];
		for (int i = 0; i < ints.length; i++) {
			chars[i] = (char) ints[i];
		}
		return new String(chars);
	}
}
