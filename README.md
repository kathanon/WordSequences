# WordSequences
- `Filter.java` filters the wordlist to 5-letter words with no words that are anagrams of each other.
- `Words.java` produces the word sequences.
- `words.txt` is the word list I used.
- `filtered.txt` is the output of `Filter.java`, using the `words.txt` in the repo.
- `sequences.txt` is the output of `Words.java`, using the `filtered.txt` in the repo.

This was written as an alternate implementation to the code used in a video by Matt Parker 
(https://www.youtube.com/watch?v=_-AfhLQfb6w). The video describes an implementation using 
search tree pruning, that took fairly long time. At the end, there was mention of another 
implementation using a graph algorithm. It ran faster, in about 15 min.

I contend that search tree pruning is indeed faster, if you prune enough. This implementation 
took 5 min, 15 sec on my machine.

The code is quickly slapped together, ugly, and undocumented - but it is here if someone is interested.
