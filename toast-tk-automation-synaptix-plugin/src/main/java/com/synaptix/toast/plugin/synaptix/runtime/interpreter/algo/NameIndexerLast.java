package com.synaptix.toast.plugin.synaptix.runtime.interpreter.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NameIndexerLast {
	private static Map<String, ArrayList<String>> metaIndex = new HashMap<String, ArrayList<String>>();

    private static Map<String, ArrayList<String>> doubleMetaIndex = new HashMap<String, ArrayList<String>>();

    static void addDMetaIndex(final String index, final String word) {
        final ArrayList<String> words = doubleMetaIndex.get(index);

        if (words != null) {
            words.add(word);
        }
        else {
            final ArrayList<String> wordList = new ArrayList<String>();
            wordList.add(word);
            doubleMetaIndex.put(index, wordList);
        }
    }

    static void addMetaIndex(final String index, final String word) {
        final ArrayList<String> words = metaIndex.get(index);

        if (words != null) {
            words.add(word);
        } else {
            final ArrayList<String> wordList = new ArrayList<String>();
            wordList.add(word);
            metaIndex.put(index, wordList);
        }
    }

    static Set<String> getMetaIndices() {
        return metaIndex.keySet();
    }

    static Set<String> getDMetaIndices() {
        return doubleMetaIndex.keySet();
    }

    static List<String> getMetaPhoneMatches(final String metaPhoneCode) {
        return metaIndex.get(metaPhoneCode);
    }

    static List<String> getDoubleMetaPhoneMatches(final String metaPhoneCode) {
        return doubleMetaIndex.get(metaPhoneCode);
    }
}
