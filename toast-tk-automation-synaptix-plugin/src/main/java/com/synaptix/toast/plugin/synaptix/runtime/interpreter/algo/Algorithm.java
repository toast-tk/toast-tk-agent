package com.synaptix.toast.plugin.synaptix.runtime.interpreter.algo;

public interface Algorithm {

	void processPhrase(final String word);

    PhraseMatchResults findMatchingPhrases(final String word);
}