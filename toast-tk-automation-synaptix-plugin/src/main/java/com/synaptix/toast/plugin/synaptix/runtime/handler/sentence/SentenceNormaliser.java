package com.synaptix.toast.plugin.synaptix.runtime.handler.sentence;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public final class SentenceNormaliser {

	private static final Pattern WORD_PATTERN = Pattern.compile("[a-zA-Z_.:/0-9]*");

	private SentenceNormaliser() {

	}

	public static List<String> normaliseSentence(final String groupOfWords) {
		final String normalisedGroupOfWords = StringUtils.stripAccents(groupOfWords);
		final List<String> words = new ArrayList<String>();
		final Matcher matcher = splitByWord(normalisedGroupOfWords.toLowerCase());
		while(matcher.find()) {
			final String currentWord = matcher.group();
			if(StringUtils.isNotBlank(currentWord)) {
				words.add(currentWord);
			}
		}
		return words;
	}

	private static Matcher splitByWord(final String normalisedGroupOfWords) {
		return WORD_PATTERN.matcher(normalisedGroupOfWords);
	}
}