package com.synaptix.toast.plugin.synaptix.runtime.handler.sentence;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jfree.util.Log;

import com.synaptix.swing.DayDate;
import com.synaptix.swing.utils.PlanningHelper;
import com.synaptix.toast.plugin.synaptix.runtime.handler.ActionTimelineInfo;

public class SentenceFinder {

	private static final Comparator<DayDate> DAY_DATE_COMPARATOR = new Comparator<DayDate>() {
		@Override
		public int compare(final DayDate o1, final DayDate o2) {
			return (int) (o1.getTimeInMinutes() - o2.getTimeInMinutes());
		}
	};

	public final String sentence;

	public final ActionTimelineInfo actionTimelineInfo;

	public final NormalisedSentence normalisedSentence;

	private Date realDateMin;

	private Date realDateMax;

	private DayDate dayDateMin = null;
	
	private DayDate dayDateMax;
	
	private int currentPosition;

	public SentenceFinder(
			final String sentence
	) {
		this(sentence, new Date());
	}

	public SentenceFinder(
			final String sentence,
			final Date date
	) {
		this.sentence = sentence.replace("timeline ", "");
		this.normalisedSentence = new NormalisedSentence(this.sentence);
		this.currentPosition = 0;
		final String actionFinded = findAction();
		final String quantifier = findQuantifier();
		this.dayDateMin = null;
		this.dayDateMax = null;
		findDates();
		final String taskType = findName();
		final String name = findContainer();
		final String ressourceName = findRessourceName();
		this.actionTimelineInfo = new ActionTimelineInfo(
				actionFinded, 
				taskType, 
				realDateMin != null && date != null ? PlanningHelper.convertDateToDayDate(date, realDateMin) : this.dayDateMin, 
						realDateMax != null && date != null ? PlanningHelper.convertDateToDayDate(date, realDateMax) : this.dayDateMax, 
				name, 
				ressourceName, 
				quantifier
		);
	}

	public boolean isAValidSentence() {
		return
				StringUtils.isNotBlank(actionTimelineInfo.action)
				&&
				StringUtils.isNotBlank(actionTimelineInfo.container)
				&&
				StringUtils.isNotBlank(actionTimelineInfo.taskType)
				&&
				(
						realDateMin != null
						||
						StringUtils.isNotBlank(actionTimelineInfo.quantifier)
				)
		;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb
		.append(sentence)
		.append(" -> ")
		.append(actionTimelineInfo)
		;
		return sb.toString();
	}

	private String findAction() {
		final StringBuilder sb = new StringBuilder();
		for(final NormalisedWord normalisedWord : normalisedSentence.words) {
			if(!normalisedWord.isEndActionWord) {
				incrementPosition();
				sb.append(normalisedWord.word);
			}
			else {
				break;
			}
		}
		return sb.toString();
	}

	private String findQuantifier() {
		for(final NormalisedWord normalizedWord : normalisedSentence.words) {
			if(normalizedWord.isQuantifier) {
				return normalizedWord.word;
			}
		}
		return "";
	}

	private void findDates() {
		final List<Date> dates = new ArrayList<Date>(4);
		for(final NormalisedWord normalisedWord : normalisedSentence.words) {
			if(normalisedWord.isDate) {
				dates.add(normalisedWord.date);
			}
		}
		if(!dates.isEmpty()) {
			buildInterval(dates);
		}
		else {
			findRelativeDates();
		}
	}

	private List<String> findRelativeDayDatePattern() {
		final List<String> dayDatePatterns = new ArrayList<String>(2);
		final int length = sentence.length();
		int openingIndex = -1;
		for(int index = 0; index < length; ++index) {
			final char currentChar = sentence.charAt(index);
			if(currentChar == '(') {
				openingIndex = index;
			}
			else if(currentChar == ')' && openingIndex != -1) {
				dayDatePatterns.add(sentence.substring(openingIndex, Math.min(index + 1, length)));
				openingIndex = -1;
			}
		}
		return dayDatePatterns;
	}

	private void findRelativeDates() {
		final List<String> relativeDayDatePatterns = findRelativeDayDatePattern();
		final List<DayDate> dayDates = new ArrayList<DayDate>(2);
		for(final String dayDatePattern : relativeDayDatePatterns) {
			final DayDate dayDate = convertDayDate(dayDatePattern);
			if(dayDate != null) {
				dayDates.add(dayDate);
			}
		}
		if(!dayDates.isEmpty()) {
			buildDayDateInterval(dayDates);
		}
	}

	private static DayDate convertDayDate(final String currentDayDateWord) {
		int nbComma = 0;
		int nbDay = 0;
		int nbHour = 0;
		int nbMinutes = 0;
		final int length = currentDayDateWord.length();
		final StringBuilder sb = new StringBuilder(2);
		for(int index = 0; index < length; ++index) {
			final char currentChar = currentDayDateWord.charAt(index);
			if(Character.isDigit(currentChar)) {
				sb.append(currentChar);
			}
			else if(currentChar == ',') {
				if(sb.length() > 0) {
					if(nbComma == 0) {
						nbDay = convertInteger(sb.toString());
						sb.setLength(0);
					}
					else if(nbComma == 1) {
						nbHour = convertInteger(sb.toString());
						sb.setLength(0);
					}
					else if(nbComma == 2) {
						nbMinutes = convertInteger(sb.toString());
						sb.setLength(0);
					}
				}
				++nbComma;
			}
		}
		return new DayDate(nbDay, nbHour, nbMinutes);
	}
	
	private void buildDayDateInterval(final List<DayDate> dates) {
		if(!dates.isEmpty()) {
			Collections.sort(dates, DAY_DATE_COMPARATOR);
			this.dayDateMin = dates.get(0);
			if(dates.size() > 1) {
				this.dayDateMax = dates.get(1);
			}
			else {
				normaliseInterval();
			}
		}
	}
	
	private static int convertInteger(final String strToConvert) {
		try {
			return Integer.parseInt(strToConvert);
		}
		catch(final Exception e) {
			Log.error(e.getMessage(), e);
		}
		return 0;
	}
	
	private void buildInterval(final List<Date> dates) {
		if(!dates.isEmpty()) {
			Collections.sort(dates);
			realDateMin = dates.get(0);
			if(dates.size() > 1) {
				realDateMax = dates.get(1);
			}
			else {
				normaliseInterval();
			}
		}
	}

	private void normaliseInterval() {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(realDateMin);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		realDateMin = cal.getTime();
		cal.add(Calendar.DAY_OF_YEAR, 1);
		realDateMax = cal.getTime();
	}

	private String findName() {
		final StringBuilder sb = new StringBuilder();
		for(int index = currentPosition; index < normalisedSentence.words.size(); ++index) {
			final NormalisedWord normalisedWord = normalisedSentence.words.get(index);
			final String word = normalisedWord.word;
			if(word.equals("du")) {
				return sb.toString();
			}
			if(normalisedWord.isASignificativeWord()) {
				sb.append(word);
			}
			incrementPosition();
		}
		return sb.toString();
	}

	private String findContainer() {
		final StringBuilder sb = new StringBuilder();
		boolean findDe = false;
		boolean findDu = false;
		for(int index = currentPosition; index < normalisedSentence.words.size(); ++index) {
			final NormalisedWord normalisedWord = normalisedSentence.words.get(index);
			final String word = normalisedWord.word;
			if(!findDe) {
				findDe = word.equals("de");
			}
			else {
				if(!findDu) {
					findDu = word.equals("du");
				}
				if(!findDu) {
					sb.append(word);
				}
			}
			incrementPosition();
		}
		return sb.toString().replace(normalisedSentence.lastWord, "");
	}

	private String findRessourceName() {
		final StringBuilder sb = new StringBuilder();
		for(int index = currentPosition; index < normalisedSentence.words.size(); ++index) {
			final NormalisedWord normalisedWord = normalisedSentence.words.get(index);
			final String word = normalisedWord.word;
			if(!normalisedWord.isArticle) {
				sb.append(word);
			}
			incrementPosition();
		}
		sb.append(normalisedSentence.lastWord);
		return sb.toString();
	}

	private int incrementPosition() {
		return ++currentPosition;
	}
}