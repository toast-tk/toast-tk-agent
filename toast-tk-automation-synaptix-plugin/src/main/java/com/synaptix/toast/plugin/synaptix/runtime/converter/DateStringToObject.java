package com.synaptix.toast.plugin.synaptix.runtime.converter;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DateStringToObject implements StringToObject {

	private static final Logger LOG = LoggerFactory.getLogger(DateStringToObject.class);

	DateStringToObject() {

	}

	@Override
	public Object toObject(final String strObject, final Class<?> classObject) {
		return findDate(strObject);
	}

	private static Date findDate(final String dateStr) {
		final String[] acceptedFormats = new String[]{"dd/MM/yyyy HH:mm", "dd/MM/yyyy HH:mm:ss", "dd/MM/yyyy", "dd/MM/yy"};
		for(final String acceptedFormat : acceptedFormats) {
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
