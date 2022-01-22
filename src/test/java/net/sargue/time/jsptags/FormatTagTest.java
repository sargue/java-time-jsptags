package net.sargue.time.jsptags;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

import jakarta.servlet.jsp.JspException;

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
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));
		mockServletContext = new MockServletContext();
	}

	@Test
	public void dayOfWeekTest() throws IOException, JspException {
		assertEquals("dl. dl. dl. dilluns 1 01 dl. dilluns",
				format(DayOfWeek.MONDAY, "E EE EEE EEEE e ee eee eeee", null));
		assertEquals("dt. dt. dt. dimarts 2 02 dt. dimarts",
				format(DayOfWeek.TUESDAY, "E EE EEE EEEE e ee eee eeee", null));
	}

	@Test
	public void instantTest() throws JspException, IOException {
		Instant instant = Instant.parse("2015-11-06T09:45:33.652Z");
		assertEquals("06/11/2015", format(instant, null, null));
		assertEquals("06/11/15", format(instant, null, "S-"));
		assertEquals("06/11/2015", format(instant, null, "M-"));
		assertEquals("6 / de novembre / 2015", format(instant, null, "L-"));
		assertEquals("divendres, 6 / de novembre / 2015", format(instant, null, "F-"));
		assertEquals("10:45", format(instant, null, "-S"));
		assertEquals("10:45:33", format(instant, null, "-M"));
		assertEquals("10:45:33 CET", format(instant, null, "-L"));
		assertEquals("10:45:33 CET", format(instant, null, "-F"));
	}

	@Test
	public void localDateTest() throws IOException, JspException {
		LocalDate localDate = LocalDate.parse("2015-11-06");
		assertEquals("06/11/2015", format(localDate, null, null));
		assertEquals("06/11/2015", format(localDate, "dd/MM/yyyy", null));
		assertEquals("06/11/15", format(localDate, null, "S-"));
		assertEquals("06/11/2015", format(localDate, null, "M-"));
		assertEquals("6 / de novembre / 2015", format(localDate, null, "L-"));
		assertEquals("divendres, 6 / de novembre / 2015", format(localDate, null, "F-"));
	}

	@Test
	public void localTimeTest() throws IOException, JspException {
		LocalTime localTime = LocalTime.parse("10:53:55.913");
		assertEquals("10:53:55", format(localTime, "HH:mm:ss", null));
		assertEquals("10:53", format(localTime, null, "-S"));
		assertEquals("10:53:55", format(localTime, null, "-M"));
		assertEquals("10:53:55 CET", format(localTime, null, "-L"));
		assertEquals("10:53:55 CET", format(localTime, null, "-F"));
	}

	@Test
	public void localDateTimeTest() throws IOException, JspException {
		LocalDateTime localDateTime = LocalDateTime.parse("2015-11-06T10:55:53.456");
		assertEquals("06/11/2015 10:55:53", format(localDateTime, "dd/MM/yyyy HH:mm:ss", null));
		assertEquals("06/11/2015", format(localDateTime, null, null));
		assertEquals("06/11/15", format(localDateTime, null, "S-"));
		assertEquals("06/11/2015", format(localDateTime, null, "M-"));
		assertEquals("6 / de novembre / 2015", format(localDateTime, null, "L-"));
		assertEquals("divendres, 6 / de novembre / 2015", format(localDateTime, null, "F-"));
		assertEquals("10:55", format(localDateTime, null, "-S"));
		assertEquals("10:55:53", format(localDateTime, null, "-M"));
		assertEquals("10:55:53 CET", format(localDateTime, null, "-L"));
		assertEquals("10:55:53 CET", format(localDateTime, null, "-F"));
	}

	@Test
	public void monthTest() throws IOException, JspException {
		assertEquals("4 04 d’abr. d’abril 4 04 abr. abril", format(Month.APRIL, "M MM MMM MMMM L LL LLL LLLL", null));
	}

	@Test
	public void monthDayTest() throws IOException, JspException {
		MonthDay monthDay = MonthDay.parse("--11-06");
		assertEquals("11 6", format(monthDay, "M d", null));
	}

	@Test
	public void offsetDateTimeTest() throws IOException, JspException {
		OffsetDateTime offsetDateTime = OffsetDateTime.parse("2015-11-06T10:58:21.207+01:00");
		assertEquals("06/11/2015", format(offsetDateTime, null, null));
		assertEquals("06/11/15", format(offsetDateTime, null, "S-"));
		assertEquals("06/11/2015", format(offsetDateTime, null, "M-"));
		assertEquals("6 / de novembre / 2015", format(offsetDateTime, null, "L-"));
		assertEquals("divendres, 6 / de novembre / 2015", format(offsetDateTime, null, "F-"));
		assertEquals("10:58", format(offsetDateTime, null, "-S"));
		assertEquals("10:58:21", format(offsetDateTime, null, "-M"));
		assertEquals("10:58:21 CET", format(offsetDateTime, null, "-L"));
		assertEquals("10:58:21 CET", format(offsetDateTime, null, "-F"));
	}

	@Test
	public void offsetTimeTest() throws IOException, JspException {
		OffsetTime offsetTime = OffsetTime.parse("11:01:39.810+01:00");
		assertEquals("11:01:39", format(offsetTime, "HH:mm:ss", null));
		assertEquals("11:01", format(offsetTime, null, "-S"));
		assertEquals("11:01:39", format(offsetTime, null, "-M"));
		assertEquals("11:01:39 CET", format(offsetTime, null, "-L"));
		assertEquals("11:01:39 CET", format(offsetTime, null, "-F"));
	}

	@Test
	public void yearTest() throws IOException, JspException {
		Year year = Year.parse("2015");
		assertEquals("2015 15 2015 2015 2015 15 2015 2015 dC dC dC AD",
				format(year, "u uu uuu uuuu y yy yyyy yyyy G GG GGG GGGG", null));
	}

	@Test
	public void yearMonthTest() throws IOException, JspException {
		YearMonth yearMonth = YearMonth.parse("2015-11");
		assertEquals("dC 2015 2015 11 11 4 04 4T 4t trimestre", format(yearMonth, "G u y M L Q QQ QQQ QQQQ", null));
	}

	@Test
	public void zonedDateTime() throws IOException, JspException {
		ZonedDateTime zonedDateTime = ZonedDateTime.parse("2015-11-06T11:04:47.409+01:00[Europe/Paris]");
		assertEquals("06/11/2015", format(zonedDateTime, null, null));
		assertEquals("06/11/15", format(zonedDateTime, null, "S-"));
		assertEquals("06/11/2015", format(zonedDateTime, null, "M-"));
		assertEquals("6 / de novembre / 2015", format(zonedDateTime, null, "L-"));
		assertEquals("divendres, 6 / de novembre / 2015", format(zonedDateTime, null, "F-"));
		assertEquals("11:04", format(zonedDateTime, null, "-S"));
		assertEquals("11:04:47", format(zonedDateTime, null, "-M"));
		assertEquals("11:04:47 CET", format(zonedDateTime, null, "-L"));
		assertEquals("11:04:47 CET", format(zonedDateTime, null, "-F"));

		ZonedDateTime pstZonedDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("America/Los_Angeles"));
		System.out.println(pstZonedDateTime);
		assertEquals("06/11/2015", format(pstZonedDateTime, null, null));
		assertEquals("06/11/15", format(pstZonedDateTime, null, "S-"));
		assertEquals("06/11/2015", format(pstZonedDateTime, null, "M-"));
		assertEquals("6 / de novembre / 2015", format(pstZonedDateTime, null, "L-"));
		assertEquals("divendres, 6 / de novembre / 2015", format(pstZonedDateTime, null, "F-"));
		assertEquals("02:04", format(pstZonedDateTime, null, "-S"));
		assertEquals("02:04:47", format(pstZonedDateTime, null, "-M"));
		assertEquals("02:04:47 PST", format(pstZonedDateTime, null, "-L"));
		assertEquals("02:04:47 PST", format(pstZonedDateTime, null, "-F"));
	}

	private String format(Object o, String pattern, String style) throws JspException, IOException {
		MockPageContext mockPageContext = new MockPageContext(mockServletContext);
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
