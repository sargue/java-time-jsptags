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

import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.jstl.core.Config;
import jakarta.servlet.jsp.jstl.fmt.LocalizationContext;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

import static java.time.format.FormatStyle.*;

/**
 * <p>
 * Utilities in support of tag-handler classes.
 * </p>
 * 
 * @author Jan Luehe
 * @author Jim Newsham
 * @author Sergi Baila
 */
public class Util {

    private static final String REQUEST = "request";

    private static final String SESSION = "session";

    private static final String APPLICATION = "application";

    private static final char HYPHEN = '-';

    private static final char UNDERSCORE = '_';

    private static final Locale EMPTY_LOCALE = new Locale("", "");

    static final String REQUEST_CHAR_SET = "javax.servlet.jsp.jstl.fmt.request.charset";

    /**
     * Converts the given string description of a scope to the corresponding
     * PageContext constant.
     * 
     * The validity of the given scope has already been checked by the
     * appropriate TLV.
     * 
     * @param scope String description of scope
     * 
     * @return PageContext constant corresponding to given scope description
     */
    public static int getScope(String scope) {
        int ret = PageContext.PAGE_SCOPE; // default

        if (REQUEST.equalsIgnoreCase(scope)) {
            ret = PageContext.REQUEST_SCOPE;
        } else if (SESSION.equalsIgnoreCase(scope)) {
            ret = PageContext.SESSION_SCOPE;
        } else if (APPLICATION.equalsIgnoreCase(scope)) {
            ret = PageContext.APPLICATION_SCOPE;
        }
        return ret;
    }

    /**
     * HttpServletRequest.getLocales() returns the server's default locale if
     * the request did not specify a preferred language. We do not want this
     * behavior, because it prevents us from using the fallback locale. We
     * therefore need to return an empty Enumeration if no preferred locale has
     * been specified. This way, the logic for the fallback locale will be able
     * to kick in.
     *
     * @param request the http request
     * @return the locales from the request or an empty enumeration if no
     * preferred locale has been specified
     */
    public static Enumeration getRequestLocales(HttpServletRequest request) {
        Enumeration values = request.getHeaders("accept-language");
        if (values.hasMoreElements()) {
            // At least one "accept-language". Simply return
            // the enumeration returned by request.getLocales().
            // System.out.println("At least one accept-language");
            return request.getLocales();
        } else {
            // No header for "accept-language". Simply return
            // the empty enumeration.
            // System.out.println("No accept-language");
            return values;
        }
    }

    /**
     * See parseLocale(String, String) for details.
     *
     * @param locale the locale string to parse
     * @return {@link java.util.Locale} object corresponding to the given
     * locale string, or the null if the locale string is null or empty
     */
    public static Locale parseLocale(String locale) {
        return parseLocale(locale, null);
    }

    /**
     * Parses the given locale string into its language and (optionally) country
     * components, and returns the corresponding {@link java.util.Locale}
     * object.
     * 
     * If the given locale string is null or empty, a null value is returned.
     * 
     * @param locale the locale string to parse
     * @param variant the variant
     * 
     * @return {@link java.util.Locale} object corresponding to the given
     * locale string, or the null if the locale string is null or empty
     * 
     * @throws IllegalArgumentException if the given locale does not have a
     * language component or has an empty country component
     */
    public static Locale parseLocale(String locale, String variant) {
        Locale ret;
        String language = locale;
        String country = null;
        int index;

        if (locale == null || locale.isEmpty())
            return null;

        if (((index = locale.indexOf(HYPHEN)) > -1)
                || ((index = locale.indexOf(UNDERSCORE)) > -1)) {
            language = locale.substring(0, index);
            country = locale.substring(index + 1);
        }

        if (language.isEmpty()) {
            throw new IllegalArgumentException(Resources
                    .getMessage("LOCALE_NO_LANGUAGE"));
        }

        if (country == null) {
            if (variant != null) {
                ret = new Locale(language, "", variant);
            } else {
                ret = new Locale(language, "");
            }
        } else if (country.length() > 0) {
            if (variant != null) {
                ret = new Locale(language, country, variant);
            } else {
                ret = new Locale(language, country);
            }
        } else {
            throw new IllegalArgumentException(Resources
                    .getMessage("LOCALE_EMPTY_COUNTRY"));
        }

        return ret;
    }

    /**
     * Stores the given locale in the response object of the given page context,
     * and stores the locale's associated charset in the
     * javax.servlet.jsp.jstl.fmt.request.charset session attribute, which may
     * be used by the <requestEncoding> action in a page invoked by a form
     * included in the response to set the request charset to the same as the
     * response charset (this makes it possible for the container to decode the
     * form parameter values properly, since browsers typically encode form
     * field values using the response's charset).
     * 
     * @param pc the page context whose response object is assigned the
     * given locale
     * @param locale the response locale
     */
    static void setResponseLocale(PageContext pc, Locale locale) {
        // set response locale
        ServletResponse response = pc.getResponse();
        response.setLocale(locale);

        // get response character encoding and store it in session attribute
        if (pc.getSession() != null) {
            try {
                pc.setAttribute(REQUEST_CHAR_SET, response
                        .getCharacterEncoding(), PageContext.SESSION_SCOPE);
            } catch (IllegalStateException ex) {
                // invalidated session ignored
            }
        }
    }

    /**
     * Returns the formatting locale to use with the given formatting action in
     * the given page.
     * 
     * @param pc The page context containing the formatting action @param
     * fromTag The formatting action @param format <tt>true</tt> if the
     * formatting action is of type <formatXXX> (as opposed to <parseXXX>), and
     * <tt>false</tt> otherwise (if set to <tt>true</tt>, the formatting
     * locale that is returned by this method is used to set the response
     * locale).
     *
     * @param avail the array of available locales
     *
     * @return the formatting locale to use
     */
    static Locale getFormattingLocale(PageContext pc, boolean format, Locale[] avail) {

        LocalizationContext locCtxt;

        // Use locale from default I18N localization context, unless it is null
        if ((locCtxt = getLocalizationContext(pc)) != null) {
            if (locCtxt.getLocale() != null) {
                if (format) {
                    setResponseLocale(pc, locCtxt.getLocale());
                }
                return locCtxt.getLocale();
            }
        }

        /*
         * Establish formatting locale by comparing the preferred locales (in
         * order of preference) against the available formatting locales, and
         * determining the best matching locale.
         */
        Locale match;
        Locale pref = getLocale(pc, Config.FMT_LOCALE);
        if (pref != null) {
            // Preferred locale is application-based
            match = findFormattingMatch(pref, avail);
        } else {
            // Preferred locales are browser-based
            match = findFormattingMatch(pc, avail);
        }
        if (match == null) {
            // Use fallback locale.
            pref = getLocale(pc, Config.FMT_FALLBACK_LOCALE);
            if (pref != null) {
                match = findFormattingMatch(pref, avail);
            }
        }
        if (format && (match != null)) {
            setResponseLocale(pc, match);
        }

        return match;
    }

    /**
     * Setup the available formatting locales that will be used by
     * getFormattingLocale(PageContext).
     */
    static Locale[] availableFormattingLocales;
    static {
        Locale[] dateLocales = DateFormat.getAvailableLocales();
        Set<Locale> numberLocales = new HashSet<>(Arrays.asList(NumberFormat.getAvailableLocales()));
        ArrayList<Locale> locales = new ArrayList<>();
        for (Locale dateLocale : dateLocales)
            if (numberLocales.contains(dateLocale))
                locales.add(dateLocale);
        availableFormattingLocales = new Locale[locales.size()];
        availableFormattingLocales = locales.toArray(availableFormattingLocales);
    }

    /**
     * Returns the locale specified by the named scoped attribute or context
     * configuration parameter.
     * 
     * <p> The named scoped attribute is searched in the page, request, session
     * (if valid), and application scope(s) (in this order). If no such
     * attribute exists in any of the scopes, the locale is taken from the named
     * context configuration parameter.
     * 
     * @param pageContext the page in which to search for the named scoped
     * attribute or context configuration parameter @param name the name of the
     * scoped attribute or context configuration parameter
     * 
     * @return the locale specified by the named scoped attribute or context
     * configuration parameter, or <tt>null</tt> if no scoped attribute or
     * configuration parameter with the given name exists
     */
    static Locale getLocale(PageContext pageContext, String name) {
        Locale loc = null;

        Object obj = Config.find(pageContext, name);
        if (obj != null) {
            if (obj instanceof Locale) {
                loc = (Locale) obj;
            } else {
                loc = parseLocale((String) obj);
            }
        }

        return loc;
    }

    // *********************************************************************
    // Private utility methods

    /**
     * Determines the client's preferred locales from the request, and compares
     * each of the locales (in order of preference) against the available
     * locales in order to determine the best matching locale.
     * 
     * @param pageContext Page containing the formatting action @param avail
     * Available formatting locales
     * 
     * @return Best matching locale, or <tt>null</tt> if no match was found
     */
    private static Locale findFormattingMatch(PageContext pageContext,
            Locale[] avail) {
        Locale match = null;
        for (Enumeration enum_ = Util
                .getRequestLocales((HttpServletRequest) pageContext
                        .getRequest()); enum_.hasMoreElements();) {
            Locale locale = (Locale) enum_.nextElement();
            match = findFormattingMatch(locale, avail);
            if (match != null) {
                break;
            }
        }

        return match;
    }

    /**
     * Returns the best match between the given preferred locale and the given
     * available locales.
     * 
     * The best match is given as the first available locale that exactly
     * matches the given preferred locale ("exact match"). If no exact match
     * exists, the best match is given to an available locale that meets the
     * following criteria (in order of priority): - available locale's variant
     * is empty and exact match for both language and country - available
     * locale's variant and country are empty, and exact match for language.
     * 
     * @param pref the preferred locale @param avail the available formatting
     * locales
     * 
     * @return Available locale that best matches the given preferred locale, or
     * <tt>null</tt> if no match exists
     */
    private static Locale findFormattingMatch(Locale pref, Locale[] avail) {
        Locale match = null;
        boolean langAndCountryMatch = false;
        for (Locale locale : avail) {
            if (pref.equals(locale)) {
                // Exact match
                match = locale;
                break;
            } else if (!"".equals(pref.getVariant())
                       && "".equals(locale.getVariant())
                       && pref.getLanguage().equals(locale.getLanguage())
                       && pref.getCountry().equals(locale.getCountry())) {
                // Language and country match; different variant
                match = locale;
                langAndCountryMatch = true;
            } else if (!langAndCountryMatch
                       && pref.getLanguage().equals(locale.getLanguage())
                       && ("".equals(locale.getCountry()))) {
                // Language match
                if (match == null) {
                    match = locale;
                }
            }
        }
        return match;
    }

    /**
     * Gets the default I18N localization context.
     * 
     * @param pc Page in which to look up the default I18N localization context
     * @return the localization context
     */
    public static LocalizationContext getLocalizationContext(PageContext pc) {
        LocalizationContext locCtxt;

        Object obj = Config.find(pc, Config.FMT_LOCALIZATION_CONTEXT);
        if (obj == null) {
            return null;
        }

        if (obj instanceof LocalizationContext) {
            locCtxt = (LocalizationContext) obj;
        } else {
            // localization context is a bundle basename
            locCtxt = getLocalizationContext(pc, (String) obj);
        }

        return locCtxt;
    }

    /**
     * Gets the resource bundle with the given base name, whose locale is
     * determined as follows:
     * 
     * Check if a match exists between the ordered set of preferred locales and
     * the available locales, for the given base name. The set of preferred
     * locales consists of a single locale (if the
     * {@link Config#FMT_LOCALE} configuration setting is
     * present) or is equal to the client's preferred locales determined from
     * the client's browser settings.
     * 
     * <p>
     * If no match was found in the previous step, check if a match exists
     * between the fallback locale (given by the
     * {@link Config#FMT_FALLBACK_LOCALE} configuration
     * setting) and the available locales, for the given base name.
     * 
     * @param pc Page in which the resource bundle with the given base
     * name is requested
     * @param basename Resource bundle base name
     * 
     * @return Localization context containing the resource bundle with the
     * given base name and the locale that led to the resource bundle match, or
     * the empty localization context if no resource bundle match was found
     */
    public static LocalizationContext getLocalizationContext(PageContext pc,
            String basename) {
        LocalizationContext locCtxt = null;
        ResourceBundle bundle;

        if ((basename == null) || basename.equals("")) {
            return new LocalizationContext();
        }

        // Try preferred locales
        Locale pref = getLocale(pc, Config.FMT_LOCALE);
        if (pref != null) {
            // Preferred locale is application-based
            bundle = findMatch(basename, pref);
            if (bundle != null) {
                locCtxt = new LocalizationContext(bundle, pref);
            }
        } else {
            // Preferred locales are browser-based
            locCtxt = findMatch(pc, basename);
        }

        if (locCtxt == null) {
            // No match found with preferred locales, try using fallback locale
            pref = getLocale(pc, Config.FMT_FALLBACK_LOCALE);
            if (pref != null) {
                bundle = findMatch(basename, pref);
                if (bundle != null) {
                    locCtxt = new LocalizationContext(bundle, pref);
                }
            }
        }

        if (locCtxt == null) {
            // try using the root resource bundle with the given basename
            try {
                bundle = ResourceBundle.getBundle(basename, EMPTY_LOCALE,
                        Thread.currentThread().getContextClassLoader());
                if (bundle != null) {
                    locCtxt = new LocalizationContext(bundle, null);
                }
            } catch (MissingResourceException mre) {
                // do nothing
            }
        }

        if (locCtxt != null) {
            // set response locale
            if (locCtxt.getLocale() != null) {
                setResponseLocale(pc, locCtxt.getLocale());
            }
        } else {
            // create empty localization context
            locCtxt = new LocalizationContext();
        }

        return locCtxt;
    }

    /**
     * Determines the client's preferred locales from the request, and compares
     * each of the locales (in order of preference) against the available
     * locales in order to determine the best matching locale.
     * 
     * @param pageContext the page in which the resource bundle with the given
     * base name is requested @param basename the resource bundle's base name
     * 
     * @return the localization context containing the resource bundle with the
     * given base name and best matching locale, or <tt>null</tt> if no
     * resource bundle match was found
     */
    private static LocalizationContext findMatch(PageContext pageContext,
            String basename) {
        LocalizationContext locCtxt = null;

        // Determine locale from client's browser settings.
        for (Enumeration enum_ = Util
                .getRequestLocales((HttpServletRequest) pageContext
                        .getRequest()); enum_.hasMoreElements();) {
            Locale pref = (Locale) enum_.nextElement();
            ResourceBundle match = findMatch(basename, pref);
            if (match != null) {
                locCtxt = new LocalizationContext(match, pref);
                break;
            }
        }

        return locCtxt;
    }

    /**
     * Gets the resource bundle with the given base name and preferred locale.
     * 
     * This method calls java.util.ResourceBundle.getBundle(), but ignores its
     * return value unless its locale represents an exact or language match with
     * the given preferred locale.
     * 
     * @param basename the resource bundle base name @param pref the preferred
     * locale
     * 
     * @return the requested resource bundle, or <tt>null</tt> if no resource
     * bundle with the given base name exists or if there is no exact- or
     * language-match between the preferred locale and the locale of the bundle
     * returned by java.util.ResourceBundle.getBundle().
     */
    private static ResourceBundle findMatch(String basename, Locale pref) {
        ResourceBundle match = null;

        try {
            ResourceBundle bundle = ResourceBundle.getBundle(basename, pref,
                    Thread.currentThread().getContextClassLoader());
            Locale avail = bundle.getLocale();
            if (pref.equals(avail)) {
                // Exact match
                match = bundle;
            } else {
                /*
                 * We have to make sure that the match we got is for the
                 * specified locale. The way ResourceBundle.getBundle() works,
                 * if a match is not found with (1) the specified locale, it
                 * tries to match with (2) the current default locale as
                 * returned by Locale.getDefault() or (3) the root resource
                 * bundle (basename). We must ignore any match that could have
                 * worked with (2) or (3). So if an exact match is not found, we
                 * make the following extra tests: - avail locale must be equal
                 * to preferred locale - avail country must be empty or equal to
                 * preferred country (the equality match might have failed on
                 * the variant)
                 */
                if (pref.getLanguage().equals(avail.getLanguage())
                        && ("".equals(avail.getCountry()) || pref.getCountry()
                                .equals(avail.getCountry()))) {
                    /*
                     * Language match. By making sure the available locale does
                     * not have a country and matches the preferred locale's
                     * language, we rule out "matches" based on the container's
                     * default locale. For example, if the preferred locale is
                     * "en-US", the container's default locale is "en-UK", and
                     * there is a resource bundle (with the requested base name)
                     * available for "en-UK", ResourceBundle.getBundle() will
                     * return it, but even though its language matches that of
                     * the preferred locale, we must ignore it, because matches
                     * based on the container's default locale are not portable
                     * across different containers with different default
                     * locales.
                     */
                    match = bundle;
                }
            }
        } catch (MissingResourceException mre) {
            throw new IllegalStateException("Shouldn't happen?");
        }

        return match;
    }

    /*
    This section is based on joda-time DateTimeFormat to handle the two character style pattern missing in Java Time.
     */

    /**
     * Creates a formatter from a two character style pattern. The first character
     * is the date style, and the second character is the time style. Specify a
     * character of 'S' for short style, 'M' for medium, 'L' for long, and 'F'
     * for full. A date or time may be ommitted by specifying a style character '-'.
     *
     * @param style  two characters from the set {"S", "M", "L", "F", "-"}
     * @throws JspException if the style is invalid
     * @return a formatter for the specified style
     */
    public static DateTimeFormatter createFormatterForStyle(String style)
        throws JspException
    {
        if (style == null || style.length() != 2) {
            throw new JspException("Invalid style specification: " + style);
        }
        FormatStyle dateStyle = selectStyle(style.charAt(0));
        FormatStyle timeStyle = selectStyle(style.charAt(1));
        if (dateStyle == null && timeStyle == null) {
            throw new JspException("Style '--' is invalid");
        }
        return createFormatterForStyleIndex(dateStyle, timeStyle);
    }

    /**
     * Gets the formatter for the specified style.
     *
     * @param dateStyle  the date style
     * @param timeStyle  the time style
     * @return the formatter
     */
    private static DateTimeFormatter createFormatterForStyleIndex(FormatStyle dateStyle, FormatStyle timeStyle)
            throws JspException {
        if (dateStyle == null && timeStyle == null)
            throw new JspException("Both styles cannot be null.");
        else if (dateStyle != null && timeStyle != null)
            return DateTimeFormatter.ofLocalizedDateTime(dateStyle, timeStyle);
        else if (dateStyle == null)
            return DateTimeFormatter.ofLocalizedTime(timeStyle);
        else
            return DateTimeFormatter.ofLocalizedDate(dateStyle);
    }

    /**
     * Gets the FormatStyle style code from first character.
     *
     * @param ch the one character style code
     * @return the FormatStyle
     */
    private static FormatStyle selectStyle(char ch) throws JspException {
        switch (ch) {
            case 'S':
                return SHORT;
            case 'M':
                return MEDIUM;
            case 'L':
                return LONG;
            case 'F':
                return FULL;
            case '-':
                return null;
            default:
                throw new JspException("Invalid style character: " + ch);
        }
    }
}
