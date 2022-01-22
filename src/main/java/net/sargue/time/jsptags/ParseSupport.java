/*
 * Copyright 1999-2004 The Apache Software Foundation.
 * Modifications, Copyright 2005 Stephen Colebourne, 2014-2015 Sergi Baila
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sargue.time.jsptags;

import java.io.IOException;
import java.text.DateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.Locale;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

/**
 * Support for tag handlers for the date and time parsing tags.
 *
 * @author Jan Luehe
 * @author Jim Newsham
 * @author Sergi Baila
 */
public abstract class ParseSupport extends BodyTagSupport {

	private static final long serialVersionUID = 1L;

	/** The value attribute. */
	protected String value;
	/** Status of the value. */
	protected boolean valueSpecified;
	/** The pattern attribute. */
	protected String pattern;
	/** The style attribute. */
	protected String style;
	/** The zone attribute. */
	protected ZoneId zoneId;
	/** The locale attribute. */
	protected Locale locale;
	/** The var attribute. */
	private String var;
	/** The scope attribute. */
	private int scope;

	/**
	 * Constructor.
	 */
	public ParseSupport() {
		super();
		init();
	}

	private void init() {
		value = null;
		valueSpecified = false;
		pattern = null;
		style = null;
		zoneId = null;
		locale = null;
		scope = PageContext.PAGE_SCOPE;
	}

	/**
	 * 
	 * @param var the variable to store the result in
	 */
	public void setVar(final String var) {
		this.var = var;
	}

	/**
	 * @param scope the scope to store the variable in
	 * @see #setVar(String)
	 */
	public void setScope(final String scope) {
		this.scope = Util.getScope(scope);
	}

	/**
	 * Sets the value attribute.
	 *
	 * @param value the value
	 */
	public void setValue(final String value) {
		this.value = value;
		this.valueSpecified = true;
	}

	/**
	 * Sets the style attribute.
	 *
	 * @param style the style
	 */
	public void setStyle(final String style) {
		this.style = style;
	}

	/**
	 * Sets the pattern attribute.
	 *
	 * @param pattern the pattern
	 */
	public void setPattern(final String pattern) {
		this.pattern = pattern;
	}

	/**
	 * Sets the zone attribute.
	 *
	 * @param dtz the zone
	 * @throws JspTagException incorrect zone or zone parameter
	 */
	public void setZoneId(final Object dtz) throws JspTagException {
		if (dtz == null) {
			this.zoneId = null;
		} else if (dtz instanceof ZoneId) {
			this.zoneId = (ZoneId) dtz;
		} else if (dtz instanceof String) {
			try {
				final String sZone = (String) dtz;
				this.zoneId = sZone.isEmpty() ? null : ZoneId.of(sZone);
			} catch (final IllegalArgumentException iae) {
				throw new JspTagException("Incorrect Zone: " + dtz);
			}
		} else {
			throw new JspTagException("Can only accept ZoneId or String objects.");
		}
	}

	/**
	 * Sets the style attribute.
	 *
	 * @param loc the locale
	 * @throws JspTagException parameter not a Locale or String
	 */
	public void setLocale(final Object loc) throws JspTagException {
		if (loc == null) {
			this.locale = null;
		} else if (loc instanceof Locale) {
			this.locale = (Locale) loc;
		} else if (loc instanceof String) {
			locale = Util.parseLocale((String) loc);
		} else {
			throw new JspTagException("Can only accept Locale or String objects.");
		}
	}

	@Override
	public int doEndTag() throws JspException {
		String input = null;

		// determine the input by...
		if (valueSpecified) {
			// ... reading 'value' attribute
			input = value;
		} else {
			// ... retrieving and trimming our body
			if (bodyContent != null && bodyContent.getString() != null) {
				input = bodyContent.getString().trim();
			}
		}

		if ((input == null) || input.equals("")) {
			if (var != null) {
				pageContext.removeAttribute(var, scope);
			}
			return EVAL_PAGE;
		}

		// Create formatter
		DateTimeFormatter formatter;
		if (pattern != null) {
			formatter = DateTimeFormatter.ofPattern(pattern);
		} else if (style != null) {
			formatter = Util.createFormatterForStyle(style);
		} else {
			formatter = Util.createFormatterForStyle("FF");
		}

		// set formatter locale
		Locale locale = this.locale;
		if (locale == null) {
			locale = Util.getFormattingLocale(pageContext, true, DateFormat.getAvailableLocales());
		}
		if (locale != null) {
			formatter = formatter.withLocale(locale);
		}

		// set formatter timezone
		ZoneId tz = this.zoneId;
		if (tz == null) {
			tz = ZoneIdSupport.getZoneId(pageContext, this);
		}
		if (tz != null) {
			formatter = formatter.withZone(tz);
		}

		// Parse date
		TemporalAccessor parsed = null;
		try {
			parsed = formatter.parse(input, temporalQuery());
		} catch (final DateTimeParseException e) {
			throw new JspException(Resources.getMessage("PARSE_DATE_PARSE_ERROR", input), e);
		}

		if (var != null) {
			pageContext.setAttribute(var, parsed, scope);
		} else {
			try {
				pageContext.getOut().print(parsed);
			} catch (final IOException ioe) {
				throw new JspTagException(ioe.toString(), ioe);
			}
		}

		return EVAL_PAGE;
	}

	/**
	 * Abstract method to define the query used to format the input with each
	 * specific tag.
	 *
	 * @return the temporal query used to parse the input
	 */
	protected abstract TemporalQuery<TemporalAccessor> temporalQuery();

	@Override
	public void release() {
		init();
		super.release();
	}

}
