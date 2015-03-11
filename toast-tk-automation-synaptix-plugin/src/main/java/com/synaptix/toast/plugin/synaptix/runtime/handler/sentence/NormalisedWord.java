package com.synaptix.toast.plugin.synaptix.runtime.handler.sentence;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NormalisedWord {

	private static final Logger LOG = LoggerFactory.getLogger(NormalisedWord.class);

	public final String word;

	public final boolean isNumeric;

	public final boolean isEndActionWord;

	public final boolean isQuantifier;

	public final Date date;

	public final boolean isDate;

	public final boolean isArticle;

	public NormalisedWord(final String word) {
		this.isNumeric = isNumeric(word);
		this.isEndActionWord = isEndActionWord(word);
		this.date = computeDate(word);
		this.isDate = isDate();
		this.isQuantifier = isQuantifier();
		this.isArticle = isArticle(word);
		this.word = normaliseWord(word);
	}

	private static boolean isEndActionWord(final String word) {
		return "sur".equals(word);
	}

	private static boolean isNumeric(final String word) {
		final int length = word.length();
		for(int index = 0; index < length; ++index) {
			final char currentChar = word.charAt(index);
			if(Character.isDigit(currentChar)) {
				return true;
			}
		}
		return false;
	}

	private static Date computeDate(final String word) {
		final SimpleDateFormat[] acceptedFormats = new SimpleDateFormat[]{
				new SimpleDateFormat("dd/MM/yyyyHH:mm"),
				new SimpleDateFormat("dd/MM/yyyy"),
		};
		for(final SimpleDateFormat acceptedFormat : acceptedFormats) {
			try {
				return acceptedFormat.parse(word);
			}
			catch(final Exception e) {
				LOG.debug(e.getMessage(), e);
			}
		}
		return null;
	}

	private boolean isDate() {
		return date != null;
	}

	private boolean isQuantifier() {
		return isNumeric && ! isDate;
	}

	private static boolean isArticle(final String word) {
		return
				word.equals("sur")
				||
				word.equals("de")
				||
				word.equals("du")
				||
				word.equals("l")
				||
				word.equals("la")
				||
				word.equals("le")
				||
				word.equals("au")
				;
	}

	private String normaliseWord(final String word) {
		final String normalisedWord;
		if(isQuantifier) {
			final int length = word.length();
			final StringBuilder sb = new StringBuilder();
			for(int index = 0; index < length; ++index) {
				final char currentChar = word.charAt(index);
				if(Character.isDigit(currentChar)) {
					sb.append(currentChar);
				}
			}
			normalisedWord = sb.toString();
		}
		else {
			normalisedWord = word;
		}
		return StringUtils.stripAccents(normalisedWord);
	}

	@Override
	public String toString() {
		return word;
	}
}