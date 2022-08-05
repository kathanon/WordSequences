import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.function.Predicate;

public class WordList implements Iterable<String> {
	public static final int LETTERS = 26;
	public static final LetterList GUARD = new LetterList();
	
	public static class LetterList {
		public boolean word = false;
		public LetterList[] next = null;
		
		public LetterList add(char c) {
			if (next == null) {
				next = new LetterList[LETTERS];
			}
			int i = (int) (c - 'a');
			if (next[i] == null) {
				next[i] = new LetterList();
			}
			return next[i];
		}
		
		public LetterList next(char c) {
			return (next == null) ? GUARD : next[c - 'a'];
		}
	}
	
	public LetterList head;
	public int n = 0;
	
	public WordList() {
		head = new LetterList();
	}
	
	public WordList(Path p) {
		this();
		importFile(p);
	}
	
	public WordList(Path p, Predicate<String> filter) {
		this();
		importFile(p, filter);
	}
	
	public void add(String word) {
		var cur = head;
		for (char c : word.toCharArray()) {
			cur = cur.add(c);
		}
		cur.word = true;
		n++;
	}

	public boolean contains(String word) {
		var cur = head;
		for (char c : word.toCharArray()) {
			cur = cur.next(c);
			if (cur == null) return false;
		}
		return cur.word;
	}
	
	public void importFile(Path p) {
		importFile(p, s -> true);
	}
	
	public void importFile(Path p, Predicate<String> filter) {
		try (BufferedReader reader = Files.newBufferedReader(p)) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (filter.test(line)) {
					add(line);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Iterable<char[]> filterChars(char[] buf, boolean[] forbid, char startAt) {
		return new Iterable<char[]>() {
			@Override
			public Iterator<char[]> iterator() {
				return new FilterIterator(buf, forbid, startAt);
			}
		};
	}
	
	private class FilterIterator implements Iterator<char[]> {
		public int i = 0;

		private Deque<LetterList> stack = new ArrayDeque<>();
		private LetterList cur = head;
		private char[] buffer;
		private boolean[] forbid;
		private static final char INIT_CHAR = 'a' - 1;
		private boolean stepped = false;
		
		public FilterIterator(char[] buf, boolean[] forbid, char startAt) {
			this.buffer = buf;
			this.forbid = forbid;
			Arrays.fill(buffer, INIT_CHAR);
			buffer[0] = startAt;
			buffer[0]--;
		}
		
		private void step() {
			do {
				if (i >= buffer.length && cur.next != null) {
					int len = buffer.length;
					buffer = Arrays.copyOf(buffer, 2 * len);
					Arrays.fill(buffer, len, buffer.length, INIT_CHAR);
				}
				
				LetterList next = null;
				while (next == null) {
					if (cur.next == null || buffer[i] >= 'z') {
						if (i < buffer.length) buffer[i] = INIT_CHAR;
						i--;
						cur = stack.poll();
						if (cur == null) {
							buffer = null;
							return;
						}
						next = null;
					} else {
						buffer[i]++;
						if (!forbid[buffer[i] - 'a']) {
							next = cur.next[buffer[i] - 'a'];
						}
					}
				}
				
				stack.push(cur);
				cur = next;
				i++;
			} while (!cur.word);
			stepped = true;
		}
		
		@Override
		public boolean hasNext() {
			if (!stepped) step();
			return buffer != null;
		}

		@Override
		public char[] next() {
			if (!stepped) step();
			stepped = false;
			return buffer;
		}
	}
	
	private static final char[] BUFFER = new char[10];
	private static final boolean[] FORBID_NONE = new boolean[LETTERS];
	
	public Iterator<String> iterator() {
		return new Iterator<String>() {
			private FilterIterator iter = new FilterIterator(BUFFER, FORBID_NONE, 'a');
			
			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public String next() {
				return new String(iter.next(), 0, iter.i);
			}
		};
	}
}
