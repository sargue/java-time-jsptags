import net.sargue.time.jsptags.FormatTag;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

import javax.servlet.jsp.JspException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.*;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * Basic format tests.
 *
 * @author Sergi Baila
 * @link http://blog.agilelogicsolutions.com/2011/02/unit-testing-jsp-custom-tag-using.html
 */
public class FormatTagTest {

    private MockServletContext mockServletContext;

    @Before
    public void setup() throws UnsupportedEncodingException {
        Locale.setDefault(Locale.forLanguageTag("ca"));
        mockServletContext = new MockServletContext();
    }

    @Test
    public void dayOfWeekTest() throws IOException, JspException {
        testPattern("dl. dl. dl. dilluns 1 01 dl. dilluns",
                    DayOfWeek.MONDAY,
                    "E EE EEE EEEE e ee eee eeee");
        testPattern("dt. dt. dt. dimarts 2 02 dt. dimarts",
                    DayOfWeek.TUESDAY,
                    "E EE EEE EEEE e ee eee eeee");
    }

    @Test
    public void instantTest() throws JspException, IOException {
        Instant instant = Instant.parse("2015-11-06T09:45:33.652Z");
        testBasic("06/11/2015", instant);
        testStyle("06/11/15", instant, "S-");
        testStyle("06/11/2015", instant, "M-");
        testStyle("6 / de novembre / 2015", instant, "L-");
        testStyle("divendres, 6 / de novembre / 2015", instant, "F-");
        testStyle("10:45", instant, "-S");
        testStyle("10:45:33", instant, "-M");
        testStyle("10:45:33 CET", instant, "-L");
        testStyle("10:45:33 CET", instant, "-F");
    }

    @Test
    public void localDateTest() throws IOException, JspException {
        LocalDate localDate = LocalDate.parse("2015-11-06");
        testBasic("06/11/2015", localDate);
        testPattern("06/11/2015", localDate, "dd/MM/yyyy");
        testStyle("06/11/15", localDate, "S-");
        testStyle("06/11/2015", localDate, "M-");
        testStyle("6 / de novembre / 2015", localDate, "L-");
        testStyle("divendres, 6 / de novembre / 2015", localDate, "F-");
    }

    @Test
    public void localTimeTest() throws IOException, JspException {
        LocalTime localTime = LocalTime.parse("10:53:55.913");
        testPattern("10:53:55", localTime, "HH:mm:ss");
        testStyle("10:53", localTime, "-S");
        testStyle("10:53:55", localTime, "-M");
        testStyle("10:53:55 CET", localTime, "-L");
        testStyle("10:53:55 CET", localTime, "-F");
    }

    @Test
    public void localDateTimeTest() throws IOException, JspException {
        LocalDateTime localDateTime =
            LocalDateTime.parse("2015-11-06T10:55:53.456");
        testPattern("06/11/2015 10:55:53", localDateTime,
                    "dd/MM/yyyy HH:mm:ss");
        testBasic("06/11/2015", localDateTime);
        testStyle("06/11/15", localDateTime, "S-");
        testStyle("06/11/2015", localDateTime, "M-");
        testStyle("6 / de novembre / 2015", localDateTime, "L-");
        testStyle("divendres, 6 / de novembre / 2015", localDateTime, "F-");
        testStyle("10:55", localDateTime, "-S");
        testStyle("10:55:53", localDateTime, "-M");
        testStyle("10:55:53 CET", localDateTime, "-L");
        testStyle("10:55:53 CET", localDateTime, "-F");
    }

    @Test
    public void monthTest() throws IOException, JspException {
        testPattern("4 04 d’abr. d’abril 4 04 abr. abril",
                    Month.APRIL,
                    "M MM MMM MMMM L LL LLL LLLL");
    }

    @Test
    public void monthDayTest() throws IOException, JspException {
        MonthDay monthDay = MonthDay.parse("--11-06");
        testPattern("11 6", monthDay, "M d");
    }

    @Test
    public void offsetDateTimeTest() throws IOException, JspException {
        OffsetDateTime offsetDateTime =
            OffsetDateTime.parse("2015-11-06T10:58:21.207+01:00");
        testBasic("06/11/2015", offsetDateTime);
        testStyle("06/11/15", offsetDateTime, "S-");
        testStyle("06/11/2015", offsetDateTime, "M-");
        testStyle("6 / de novembre / 2015", offsetDateTime, "L-");
        testStyle("divendres, 6 / de novembre / 2015", offsetDateTime, "F-");
        testStyle("10:58", offsetDateTime, "-S");
        testStyle("10:58:21", offsetDateTime, "-M");
        testStyle("10:58:21 CET", offsetDateTime, "-L");
        testStyle("10:58:21 CET", offsetDateTime, "-F");
    }

    @Test
    public void offsetTimeTest() throws IOException, JspException {
        OffsetTime offsetTime = OffsetTime.parse("11:01:39.810+01:00");
        testPattern("11:01:39", offsetTime, "HH:mm:ss");
        testStyle("11:01", offsetTime, "-S");
        testStyle("11:01:39", offsetTime, "-M");
        testStyle("11:01:39 CET", offsetTime, "-L");
        testStyle("11:01:39 CET", offsetTime, "-F");
    }

    @Test
    public void yearTest() throws IOException, JspException {
        Year year = Year.parse("2015");
        testPattern("2015 15 2015 2015 2015 15 2015 2015 dC dC dC AD",
                    year,
                    "u uu uuu uuuu y yy yyyy yyyy G GG GGG GGGG");
    }

    @Test
    public void yearMonthTest() throws IOException, JspException {
        YearMonth yearMonth = YearMonth.parse("2015-11");
        testPattern("dC 2015 2015 11 11 4 04 4T 4t trimestre",
                    yearMonth,
                    "G u y M L Q QQ QQQ QQQQ");
    }

    @Test
    public void zonedDateTime() throws IOException, JspException {
        ZonedDateTime zonedDateTime =
            ZonedDateTime.parse("2015-11-06T11:04:47.409+01:00[Europe/Paris]");
        testBasic("06/11/2015", zonedDateTime);
        testStyle("06/11/15", zonedDateTime, "S-");
        testStyle("06/11/2015", zonedDateTime, "M-");
        testStyle("6 / de novembre / 2015", zonedDateTime, "L-");
        testStyle("divendres, 6 / de novembre / 2015", zonedDateTime, "F-");
        testStyle("11:04", zonedDateTime, "-S");
        testStyle("11:04:47", zonedDateTime, "-M");
        testStyle("11:04:47 CET", zonedDateTime, "-L");
        testStyle("11:04:47 CET", zonedDateTime, "-F");

        ZonedDateTime pstZonedDateTime =
            zonedDateTime.withZoneSameInstant(ZoneId.of("America/Los_Angeles"));
        System.out.println(pstZonedDateTime);
        testBasic("06/11/2015", pstZonedDateTime);
        testStyle("06/11/15", pstZonedDateTime, "S-");
        testStyle("06/11/2015", pstZonedDateTime, "M-");
        testStyle("6 / de novembre / 2015", pstZonedDateTime, "L-");
        testStyle("divendres, 6 / de novembre / 2015", pstZonedDateTime, "F-");
        testStyle("02:04", pstZonedDateTime, "-S");
        testStyle("02:04:47", pstZonedDateTime, "-M");
        testStyle("02:04:47 PST", pstZonedDateTime, "-L");
        testStyle("02:04:47 PST", pstZonedDateTime, "-F");    }

    private void testBasic(String expected, Object o)
        throws JspException, IOException
    {
        assertEquals(expected, format(o, null, null));
    }

    private void testStyle(String expected, Object o, String style)
        throws JspException, IOException
    {
        assertEquals(expected, format(o, null, style));
    }

    private void testPattern(String expected, Object data, String pattern)
        throws JspException, IOException
    {
        assertEquals(expected, format(data, pattern, null));
    }

    private String format(Object o, String pattern, String style)
        throws JspException, IOException
    {
        MockPageContext mockPageContext = new MockPageContext(
            mockServletContext);
        mockPageContext.getRequest().setCharacterEncoding("UTF-8");
        mockPageContext.getResponse().setCharacterEncoding("UTF-8");
        FormatTag formatTag = new FormatTag();
        formatTag.setPageContext(mockPageContext);

        formatTag.setPattern(pattern);
        formatTag.setStyle(style);
        formatTag.setValue(o);
        formatTag.doEndTag();
        return mockPageContext.getContentAsString();
    }
}
