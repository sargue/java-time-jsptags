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
import java.time.ZoneId;
import java.time.ZoneOffset;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.jstl.core.Config;
import jakarta.servlet.jsp.tagext.BodyTagSupport;
import jakarta.servlet.jsp.tagext.Tag;

/**
 * Support for tag handlers for &lt;timeZone&gt;.
 * 
 * @author Jan Luehe
 * @author Jim Newsham
 * @author Sergi Baila
 */
public abstract class ZoneIdSupport extends BodyTagSupport {

	private static final long serialVersionUID = 1L;

	/** The config key for the time zone. */
	public static final String FMT_TIME_ZONE = "net.sargue.time.zoneId";

	/** The value attribute. */
	protected Object value;

	/** The zone. */
	private ZoneId zoneId;

	/**
	 * Constructor.
	 */
	public ZoneIdSupport() {
		super();
		init();
	}

	private void init() {
		value = null;
	}

	/**
	 * 
	 * @return the zone
	 */
	public ZoneId getZoneId() {
		return zoneId;
	}

	public int doStartTag() throws JspException {
		if (value == null) {
			zoneId = ZoneOffset.UTC;
		} else if (value instanceof String) {
			try {
				zoneId = ZoneId.of((String) value);
			} catch (IllegalArgumentException iae) {
				zoneId = ZoneOffset.UTC;
			}
		} else {
			zoneId = (ZoneId) value;
		}
		return EVAL_BODY_BUFFERED;
	}

	public int doEndTag() throws JspException {
		try {
			pageContext.getOut().print(bodyContent.getString());
		} catch (IOException ioe) {
			throw new JspTagException(ioe.toString(), ioe);
		}
		return EVAL_PAGE;
	}

	@Override
	public void release() {
		init();
		super.release();
	}

	/**
	 * Determines and returns the time zone to be used by the given action.
	 * <p>
	 * If the given action is nested inside a &lt;zoneId&gt; action, the time zone
	 * is taken from the enclosing &lt;zoneId&gt; action.
	 * <p>
	 * Otherwise, the time zone configuration setting
	 * <tt>net.sargue.time.jsptags.ZoneIdSupport.FMT_TIME_ZONE</tt> is used.
	 * 
	 * @param pc      the page containing the action for which the time zone needs
	 *                to be determined
	 * @param fromTag the action for which the time zone needs to be determined
	 * 
	 * @return the time zone, or <tt> null </tt> if the given action is not nested
	 *         inside a &lt;zoneId&gt; action and no time zone configuration setting
	 *         exists
	 */
	static ZoneId getZoneId(final PageContext pc, final Tag fromTag) {
		ZoneId tz = null;

		final Tag t = findAncestorWithClass(fromTag, ZoneIdSupport.class);
		if (t != null) {
			// use time zone from parent <timeZone> tag
			final ZoneIdSupport parent = (ZoneIdSupport) t;
			tz = parent.getZoneId();
		} else {
			// get time zone from configuration setting
			final Object obj = Config.find(pc, FMT_TIME_ZONE);
			if (obj != null) {
				if (obj instanceof ZoneId) {
					tz = (ZoneId) obj;
				} else {
					try {
						tz = ZoneId.of((String) obj);
					} catch (IllegalArgumentException iae) {
						tz = ZoneOffset.UTC;
					}
				}
			}
		}

		return tz;
	}

}
