package com.synaptix.toast.plugin.synaptix.runtime.interpreter.algo;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.language.DoubleMetaphone;

public class DoubleMetaPhone implements Algorithm {

	@Override
	public void processPhrase(final String word) {
		final DoubleMetaphone dMetaPhone = new DoubleMetaphone();
		final String encodedValue = dMetaPhone.doubleMetaphone(word);
		NameIndexerLast.addDMetaIndex(encodedValue, word);
	}

	@Override
	public PhraseMatchResults findMatchingPhrases(final String word) {
		final String metaPhoneValue = new DoubleMetaphone().doubleMetaphone(word);
		final PhraseMatchResults results = new PhraseMatchResults();
		results.setAlgoName(PhraseMatchResults.ALGO_DOUBLEMETAPHONE);
		results.setKey(word);

		final Set<String> indexes = NameIndexerLast.getDMetaIndices();
		for (final Iterator<String> values = indexes.iterator(); values.hasNext();) {
			final String code = values.next();
			if (code.contains(metaPhoneValue) || code.startsWith(metaPhoneValue)) {

				final List<String> words = NameIndexerLast.getDoubleMetaPhoneMatches(code);

				for (final Iterator<String> wrds = words.iterator(); wrds.hasNext();) {
					final String phrase = wrds.next();
					if (phrase.startsWith(word) ||	phrase.contains(word)) {
						results.addExactMatch(phrase);
					}
					else {
						results.addNearMatch(phrase);
					}
				}
			}
		}
		return results;
	}
}