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

import javax.servlet.jsp.JspTagException;
import java.time.ZoneId;
import java.util.Locale;

/**
 * <p>
 * A handler for &lt;format&gt; that supports rtexprvalue-based attributes.
 * </p>
 *
 * @author Jan Luehe
 * @author Jim Newsham
 * @author Sergi Baila
 */
@SuppressWarnings("UnusedDeclaration")
public class FormatTag extends FormatSupport {

    /**
     * Sets the value attribute.
     *
     * @param value  the value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Sets the style attribute.
     *
     * @param style  the style
     */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * Sets the pattern attribute.
     *
     * @param pattern  the pattern
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * Sets the zone attribute.
     *
     * @param dtz  the zone
     * @throws JspTagException incorrect zone or dtz parameter
     */
    public void setZoneId(Object dtz) throws JspTagException {
        if (dtz == null || (dtz instanceof String && ((String) dtz).isEmpty())) {
            this.zoneId = null;
        } else if (dtz instanceof ZoneId) {
            this.zoneId = (ZoneId) dtz;
        } else if (dtz instanceof String) {
            try {
                this.zoneId = ZoneId.of((String) dtz);
            } catch (IllegalArgumentException iae) {
                throw new JspTagException("Incorrect Zone: " + dtz);
            }
        } else
            throw new JspTagException("Can only accept ZoneId or String objects.");
    }

    /**
     * Sets the style attribute.
     *
     * @param loc  the locale
     * @throws JspTagException parameter not a Locale or String
     */
    public void setLocale(Object loc) throws JspTagException {
        if (loc == null) {
            this.locale = null;
        } else if (loc instanceof Locale) {
            this.locale = (Locale) loc;
        } else if (loc instanceof String) {
            this.locale = Util.parseLocale((String) loc);
        } else
            throw new JspTagException("Can only accept Locale or String objects.");
    }

}
