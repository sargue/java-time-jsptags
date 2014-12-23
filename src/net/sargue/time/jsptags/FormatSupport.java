/*
 * Copyright 1999-2004 The Apache Software Foundation.
 * Modifications, Copyright 2005 Stephen Colebourne, 2014 Sergi Baila
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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.text.DateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

/**
 * Support for tag handlers for &lt;formatDate&gt;, the date and time
 * formatting tag in JSTL 1.0.
 *
 * @author Jan Luehe
 * @author Jim Newsham
 * @author Sergi Baila
 */
public abstract class FormatSupport extends TagSupport {

    /** The value attribute. */
    protected Object value;
    /** The pattern attribute. */
    protected String pattern;
    /** The style attribute. */
    protected String style;
    /** The zoneId attribute. */
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
    public FormatSupport() {
        super();
        init();
    }

    private void init() {
        var = null;
        value = null;
        pattern = null;
        style = null;
        zoneId = null;
        locale = null;
        scope = PageContext.PAGE_SCOPE;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setVar(String var) {
        this.var = var;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setScope(String scope) {
        this.scope = Util.getScope(scope);
    }

    /*
     * Formats the given instant or partial.
     */
    public int doEndTag() throws JspException {
        if (value == null) {
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
            // use a medium date (no time) style by default; same as jstl
            formatter = Util.createFormatterForStyle("M-");
        }

        // set formatter locale
        Locale locale = this.locale;
        if (locale == null) {
            locale = Util.getFormattingLocale(pageContext, true,
                    DateFormat.getAvailableLocales());
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

        // format value
        String formatted;
        if (value instanceof TemporalAccessor) {
            formatted = formatter.format((TemporalAccessor) value);
        } else {
            throw new JspException(
                "value attribute of format tag must be a TemporalAccessor," +
                " was: " + value.getClass().getName()); 
        }

        if (var != null) {
            pageContext.setAttribute(var, formatted, scope);
        } else {
            try {
                pageContext.getOut().print(formatted);
            } catch (IOException ioe) {
                throw new JspTagException(ioe.toString(), ioe);
            }
        }

        return EVAL_PAGE;
    }

    // Releases any resources we may have (or inherit)
    public void release() {
        init();
    }
}
