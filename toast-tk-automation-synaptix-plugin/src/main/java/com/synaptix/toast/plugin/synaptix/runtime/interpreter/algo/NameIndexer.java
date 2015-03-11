package com.synaptix.toast.plugin.synaptix.runtime.interpreter.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NameIndexer {
	private static Map<String, ArrayList<String>> wordIndex = new HashMap<String,  ArrayList<String>>();

	private static Algorithm[] algo = { new SoundEx(), new MetaPhone(), new DoubleMetaPhone()};

	private NameIndexer() {

	}

	static void indexName(String name) {
		if (name == null) {
			return;
		}
		name = name.toUpperCase();

		for (int i = 0; i < algo.length; i++) {
			final Algorithm algorithm = algo[i];
			algorithm.processPhrase(name);
		}
	}

	static void printIndex() {
		System.out.println(wordIndex);
	}

	static void addWordIndex(final String index, final String word) {
		final ArrayList<String> words = wordIndex.get(index);

		if (words != null) {
			words.add(word);
		}
		else {
			final ArrayList<String> wordList = new ArrayList<String>();
			wordList.add(word);
			wordIndex.put(index, wordList);
		}
	}

	static List<String> getSoundExCodeMatches(final String soundExCode) {
		return wordIndex.get(soundExCode);
	}

	public static List<PhraseMatchResults> findMatchingPhrases(String word) {
		if (word == null) {
			return new ArrayList<PhraseMatchResults>();
		}
		word = word.toUpperCase();

		final List<PhraseMatchResults> results = new ArrayList<PhraseMatchResults>();
		for (int i = 0; i < algo.length; i++) {
			final Algorithm algorithm = algo[i];
			results.add(algorithm.findMatchingPhrases(word));
		}
		return results;
	}
}
