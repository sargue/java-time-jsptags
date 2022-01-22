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

import java.time.ZoneId;
import java.time.ZoneOffset;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.jstl.core.Config;
import jakarta.servlet.jsp.tagext.TagSupport;

/**
 * Support for tag handlers for &lt;setDateTimeZone&gt;.
 * 
 * @author Jan Luehe
 * @author Jim Newsham
 * @author Sergi Baila
 */
public abstract class SetZoneIdSupport extends TagSupport {

	private static final long serialVersionUID = 1L;

	/** The value attribute. */
	protected Object value;
	/** The scope attribute. */
	private int scope;
	/** The var attribute. */
	private String var;

	/**
	 * Constructor.
	 */
	public SetZoneIdSupport() {
		super();
		init();
	}

	// resets local state
	private void init() {
		value = null;
		var = null;
		scope = PageContext.PAGE_SCOPE;
	}

	/**
	 * 
	 * @param scope the scope to store the variable in
	 * @see #setVar(String)
	 */
	public void setScope(final String scope) {
		this.scope = Util.getScope(scope);
	}

	/**
	 * 
	 * @param var the variable to store the result in
	 */
	public void setVar(final String var) {
		this.var = var;
	}

	public int doEndTag() throws JspException {
		ZoneId dateTimeZone;
		if (value == null) {
			dateTimeZone = ZoneOffset.UTC;
		} else if (value instanceof String) {
			try {
				dateTimeZone = ZoneId.of((String) value);
			} catch (IllegalArgumentException iae) {
				dateTimeZone = ZoneOffset.UTC;
			}
		} else {
			dateTimeZone = (ZoneId) value;
		}

		if (var != null) {
			pageContext.setAttribute(var, dateTimeZone, scope);
		} else {
			Config.set(pageContext, ZoneIdSupport.FMT_TIME_ZONE, dateTimeZone, scope);
		}

		return EVAL_PAGE;
	}

	@Override
	public void release() {
		init();
		super.release();
	}

}
