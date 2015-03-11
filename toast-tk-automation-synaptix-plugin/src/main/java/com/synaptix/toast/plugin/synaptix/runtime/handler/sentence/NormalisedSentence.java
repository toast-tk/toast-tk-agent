package com.synaptix.toast.plugin.synaptix.runtime.handler.sentence;

import java.util.ArrayList;
import java.util.List;

public class NormalisedSentence {

	public final List<NormalisedWord> words;

	public String lastWord;

	public NormalisedSentence(final String groupOfWords) {
		final List<String> normaliseSentence = SentenceNormaliser.normaliseSentence(groupOfWords);
		final int nbWord = normaliseSentence.size();
		this.words = new ArrayList<NormalisedWord>(nbWord);
		for(int index = 0; index < nbWord - 1; ++index) {
			final String currentWord = normaliseSentence.get(index);
			words.add(new NormalisedWord(currentWord));
		}
		this.lastWord = normaliseSentence.get(nbWord - 1);
	}
}