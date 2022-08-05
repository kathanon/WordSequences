import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Words {
	public static void main(String[] args) {
		var list = new WordList(Path.of("filtered.txt"));
		var used = new boolean[WordList.LETTERS];
		var words = new char[5][5];

		try (var out = new PrintStream(Files.newOutputStream(Path.of("sequences.txt")))) {
			int longest = findSequence(words, list, used, 0, 0, out);
			System.out.println("Longest sequence was: " + longest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static final int PROGRESS_INCREMENTS = 2;
	private static int lastPercent = 0;

	private static void printProgress(int progress, int n) {
		int percent = progress * 100 / n;
		if (percent > lastPercent && (percent % PROGRESS_INCREMENTS) == 0) {
			System.out.println(percent + "%");
			lastPercent = percent;
		}
	}
	
	private static int n5;
	
	private static int findSequence(char[][] words, WordList list, boolean[] used, int longest, int level, PrintStream out) {
		int progress = 0;
		if (level == 0) n5 = 0;
		
		char start = (level > 0) ? words[level - 1][0] : 'a';
		for (char[] word : list.filterChars(words[level], used, start)) {
			setUsed(word, used, true);
			boolean increase = longest == level;
			if (increase) {
				longest++;
				printWords(words, longest, System.out);
			}
			if (level == 4) {
				n5++;
				printWords(words, longest, out);
			} else {
				longest = findSequence(words, list, used, longest, level + 1, out);
			}
			setUsed(word, used, false);
			if (level == 0) printProgress(++progress, list.n);
		}
		
		if (level == 0) System.out.println(n5 + " 5-word sequences found.");
		return longest;
	}

	private static void printWords(char[][] words, int n, PrintStream out) {
		for (int i = 0; i < n; i++) {
			out.print(new String(words[i]) + " ");
		}
		out.println();
	}

	private static void setUsed(char[] word, boolean[] used, boolean value) {
		for (char c : word) {
			used[c - 'a'] = value;
		}
	}
}
