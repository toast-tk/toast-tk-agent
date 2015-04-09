package com.synaptix.toast.plugin.synaptix.runtime.converter;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DateStringToObject implements StringToObject {

	private static final Logger LOG = LoggerFactory.getLogger(DateStringToObject.class);

	private static final String[] ACCEPTED_FORMAT = new String[]{"dd/MM/yyyy", "dd/MM/yy", "dd/MM/yyyy HH:mm", "dd/MM/yyyy HH:mm:ss"};
	
	DateStringToObject() {

	}

	@Override
	public Object toObject(final String strObject, final Class<?> classObject) {
		return findDate(strObject);
	}

	private static Date findDate(final String dateStr) {
		for(final String acceptedFormat : ACCEPTED_FORMAT) {
			final Date date = tryFormat(dateStr, acceptedFormat);
			if(date != null) {
				return date;
			}
		}
		return null;
	}

	private static Date tryFormat(final String dateStr, final String format) {
		try {
			final SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.parse(dateStr);
		}
		catch(final Exception e) {
			LOG.debug(e.getMessage(), e);
		}
		return null;
	}
}
