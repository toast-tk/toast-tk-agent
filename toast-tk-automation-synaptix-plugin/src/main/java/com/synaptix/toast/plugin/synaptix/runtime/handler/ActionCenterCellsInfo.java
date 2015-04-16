package com.synaptix.toast.plugin.synaptix.runtime.handler;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synaptix.toast.plugin.synaptix.runtime.converter.DateStringToObject;

public final class ActionCenterCellsInfo {
	
	private static final Logger LOG = LoggerFactory.getLogger(ActionCenterCellsInfo.class);

	public final Date datePrevision;

	public final String nomFlux;

	public final String centerCellsPanelName;
	
	ActionCenterCellsInfo(final String command) {
		this.datePrevision = roundToDay(extractDate(command), Calendar.getInstance());
		final String[] extractedCenterCellsPanelInfo = extractCenterCellsPanelInfo(command);
		this.centerCellsPanelName = extractedCenterCellsPanelInfo[0];
		this.nomFlux = extractedCenterCellsPanelInfo[1];
	}
	
	private static String[] extractCenterCellsPanelInfo(final String command) {
		final int indexBeginParenthesis = command.indexOf('(');
		if(indexBeginParenthesis != -1) {
			final int indexEndParenthesis = command.indexOf(')');
			if(indexEndParenthesis != -1) {
				final String infoCordonnees = command.substring(indexBeginParenthesis + 1, indexEndParenthesis);
				final String[] splits = infoCordonnees.split(":");
				return splits;
			}
		}
		return null;
	}
	
	private static Date extractDate(final String command) {
		final int indexEndParenthesis = command.indexOf(')');
		if(indexEndParenthesis != -1) {
			final String lastPartDate = command.substring(indexEndParenthesis + 1).trim();
			LOG.info("lastPartDate = {}", lastPartDate);
			final Date findDate = DateStringToObject.findDate(lastPartDate);
			LOG.info("findDate = {}", findDate);
			return findDate;
		}
		return null;
	}
	
	protected static Date roundToDay(
			final Date date,
			final Calendar cal
	) {
		if(date != null) {
			cal.setTime(date);
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.HOUR, 0);
		}
		return date;
	}
}