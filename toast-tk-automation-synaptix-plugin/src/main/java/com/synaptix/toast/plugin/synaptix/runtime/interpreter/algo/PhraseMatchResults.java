package com.synaptix.toast.plugin.synaptix.runtime.interpreter.algo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class PhraseMatchResults {

	public static final String ALGO_SOUNDEX = "SOUNDEX";
	public static final String ALGO_METAPHONE = "METAPHONE";
	public static final String ALGO_DOUBLEMETAPHONE = "DOUBLEMETAPHONE";
	private final Set<String> exactMatches = new HashSet<String>();
	private final Set<String> nearMatches = new HashSet<String>();
	private String key = null;

	private String algoName = null;

	public String getAlgoName() {
		return algoName;
	}

	public void setAlgoName(final String algoName) {
		this.algoName = algoName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public void addExactMatch(final String word) {
		this.exactMatches.add(word);
	}

	public void addNearMatch(final String word) {
		if (withinJWRange(word)) {
			this.nearMatches.add(word);
		}
	}

	private boolean withinJWRange(final String word) {
		/*float dist = new JaroWinkler().getSimilarity(word, this.key);
		if (dist > SIMILARITY_DIST) {
			return Boolean.TRUE;
		}*/
		return Boolean.FALSE;
	}

	public void addNearMatch(final List<String> words) {
		this.nearMatches.addAll(words);
	}

	public void addExactMatch(final List<String> words) {
		this.exactMatches.addAll(words);
	}

	public List<String> getExactMatches() {
		return sortExactSet();
	}

	public List<String> getNearMatches() {
		return sortNearSet();
	}

	private List<String> sortExactSet() {
		final List<String> exactMatchList = new ArrayList<String>();
		exactMatchList.addAll(exactMatches);

		final SortComparator comp = new SortComparator();
		comp.setKey(this.key);
		Collections.sort(exactMatchList, comp);

		return exactMatchList;
	}

	private List<String> sortNearSet() {
		final List<String> nearMatchList = new ArrayList<String>();
		nearMatchList.addAll(nearMatches);
		final JaroWinklerDistanceComparator comp = new JaroWinklerDistanceComparator();
		comp.setKey(this.key);
		Collections.sort(nearMatchList, comp);

		return nearMatchList;
	}

	class SortComparator implements Comparator<String> {

		private String key = null;

		public String getKey() {
			return key;
		}

		public void setKey(final String key) {
			this.key = key;
		}

		@Override
		public int compare(final String o1, final String o2) {

			final int index1 = o1.indexOf(key);
			final int index2 = o2.indexOf(key);

			if(index1 >=0 && index2 >=0) {

				if (index1 == index2) {
					return 0;
				}

				if (index1 > index2) {
					return 1;
				}

				if (index1 < index2) {
					return -1;
				}
			}

			if (index1 >=0 && index2 < 0) {
				return -1;
			}

			if (index1 <0 && index2 >= 0) {
				return 1;
			}

			return -1;
		}

	}

	class LevensteinDistanceComparator implements Comparator<String> {

		private String key = null;

		public String getKey() {
			return key;
		}

		public void setKey(final String key) {
			this.key = key;
		}

		@Override
		public int compare(final String o1, final String o2) {

			final int dist1 = StringUtils.getLevenshteinDistance(key, o1);
			final int dist2 = StringUtils.getLevenshteinDistance(key, o2);

			if(dist1 >=0 && dist2 >=0) {

				if (dist1 == dist2) {
					return 0;
				}

				if (dist1 > dist2) {
					return 1;
				}

				if (dist1 < dist2) {
					return -1;
				}
			}
			return 1;
		}

	}

	class JaroWinklerDistanceComparator implements Comparator<String> {

		private String key = null;

		public String getKey() {
			return key;
		}

		public void setKey(final String key) {
			this.key = key;
		}

		@Override
		public int compare(final String o1, final String o2) {

			/*JaroWinkler algo = new JaroWinkler();
			float fdist1 = algo.getSimilarity(key, o1);
			float fdist2 = algo.getSimilarity(key, o2);

			int dist1 = (int)fdist1;
			int dist2 = (int)fdist2;

			if(dist1 >=0 && dist2 >=0) {

				if (dist1 == dist2) {
					return 0;
				}

				if (dist1 > dist2) {
					return 1;
				}

				if (dist1 < dist2) {
					return -1;
				}
			}*/
			return 1;
		}

	}

}