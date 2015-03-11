package com.synaptix.toast.plugin.synaptix.runtime.interpreter.algo;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.language.Soundex;

public class SoundEx implements Algorithm {

    @Override
	public void processPhrase(final String word) {
        final Soundex algo = new Soundex();
        final String encodedValue = algo.soundex(word);

        NameIndexer.addWordIndex(encodedValue, word);

    }

    @Override
	public PhraseMatchResults findMatchingPhrases(final String word) {
        final Soundex algo = new Soundex();
        final String value = algo.soundex(word);

        return searchIndexer(word, value);

    }

    private PhraseMatchResults searchIndexer(final String word, final String value) {

        List<String> partialMatches = NameIndexer.getSoundExCodeMatches(value);
        final PhraseMatchResults results = new PhraseMatchResults();
        results.setAlgoName(PhraseMatchResults.ALGO_SOUNDEX);
        results.setKey(word);

        if (partialMatches != null) {

            //Check if the phrase is present
            for (final Iterator<String> matchItr = partialMatches.iterator(); matchItr.hasNext();) {
                final String result =  matchItr.next();

                if (result.contains(word) || result.startsWith(word)) {
                    results.addExactMatch(result);
                } else {
                    results.addNearMatch(result);
                }
            }
        }

        //Loop thru all valid combinations for e.g. if the encoded value is A123
        //Loop thru from A100 to A199.

        for(int i=0; i<10; i++) {
            final StringBuilder soundExString = new StringBuilder();
            soundExString.append(getFirstAlphabet(value));
            soundExString.append(getFirstDigit(value));
            soundExString.append("0");
            soundExString.append(i);

            final String searchValue = soundExString.toString();
            if (value != searchValue) {
                partialMatches = NameIndexer.getSoundExCodeMatches(searchValue);
                //System.out.println("Code: " + searchValue + " " + partialMatches);
                if (partialMatches != null) {

                    //Check if the phrase is present
                    for (final Iterator<String> matchItr = partialMatches.iterator();   matchItr.hasNext();) {
                        final String result =  matchItr.next();

                        if (result.contains(word) || result.startsWith(word)) {
                            results.addExactMatch(result);
                        } else {
                            results.addNearMatch(result);
                        }
                    }
                }
            }
        }

        for(int i=10; i<100; i++) {
            final StringBuilder soundExString = new StringBuilder();
            soundExString.append(getFirstAlphabet(value));
            soundExString.append(getFirstDigit(value));
            soundExString.append(i);

            final String searchValue = soundExString.toString();
            if (value != searchValue) {
                partialMatches = NameIndexer.getSoundExCodeMatches(searchValue);
                //System.out.println("Code: " + searchValue + " " + partialMatches);
                if (partialMatches != null) {

                    //Check if the phrase is present
                    for (final Iterator<String> matchItr = partialMatches.iterator(); matchItr.hasNext();) {
                        final String result =  matchItr.next();

                        if (result.contains(word)) {
                            results.addExactMatch(result);
                        } else {
                            results.addNearMatch(result);
                        }
                    }
                }
            }
        }

        return results;

    }

    private static String getFirstAlphabet(final String word) {
        return word.substring(0, 1);
    }

    private static String getFirstDigit(final String word) {
        return word.substring(1, 2);
    }
}