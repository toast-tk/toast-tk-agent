package com.synaptix.toast.plugin.synaptix.runtime.handler.sentence;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.synaptix.swing.DayDate;
import com.synaptix.swing.utils.PlanningHelper;
import com.synaptix.toast.plugin.synaptix.runtime.handler.ActionTimelineInfo;

public class SentenceFinder {

	public final String sentence;

	public final ActionTimelineInfo actionTimelineInfo;

	public final NormalisedSentence normalisedSentence;

	private Date realDateMin;

	private Date realDateMax;

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
		this.sentence = sentence.replace("service ", "");
		this.normalisedSentence = new NormalisedSentence(this.sentence);
		this.currentPosition = 0;
		final String actionFinded = findAction();
		final String quantifier = findQuantifier();
		findDates();
		final String taskType = findName();
		final String name = findContainer();
		final String ressourceName = findRessourceName();
		final DayDate dayDateMin = realDateMin != null && date != null ? PlanningHelper.convertDateToDayDate(date, realDateMin) : null;
		final DayDate dayDateMax = realDateMax != null && date != null ? PlanningHelper.convertDateToDayDate(date, realDateMax) : null;
		this.actionTimelineInfo = new ActionTimelineInfo(actionFinded, taskType, dayDateMin, dayDateMax, name, ressourceName, quantifier);
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
		buildInterval(dates);
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
			if(isASignificativeWord(normalisedWord)) {
				sb.append(word);
			}
			incrementPosition();
		}
		return sb.toString();
	}

	private static boolean isASignificativeWord(final NormalisedWord normalisedWord) {
		return !normalisedWord.isQuantifier && !normalisedWord.isArticle && !normalisedWord.isDate;
	}

	private String findContainer() {
		final StringBuilder sb = new StringBuilder();
		boolean findDu = false;
		for(int index = currentPosition; index < normalisedSentence.words.size(); ++index) {
			final NormalisedWord normalisedWord = normalisedSentence.words.get(index);
			final String word = normalisedWord.word;
			if(word.equals("de")) {
				return sb.toString();
			}
			if(findDu && isASignificativeWord(normalisedWord)) {
				sb.append(word);
			}

			if(!findDu) {
				findDu = word.equals("du");
			}
			incrementPosition();
		}
		return sb.toString();
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