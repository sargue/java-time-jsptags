import net.sargue.time.jsptags.FormatTag;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

import javax.servlet.jsp.JspException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.*;
import java.util.Locale;

/**
 * Basic tests. Just checking there are no exceptions (I'm not checking the output).
 *
 * @author Sergi Baila
 * @link http://blog.agilelogicsolutions.com/2011/02/unit-testing-jsp-custom-tag-using.html
 */
public class FormatTagTest {

    private FormatTag formatTag;
    private MockPageContext mockPageContext;

    @Before
    public void setup() throws UnsupportedEncodingException {
        Locale.setDefault(Locale.forLanguageTag("ca"));
        // mock ServletContext
        MockServletContext mockServletContext = new MockServletContext();
        // mock PageContext
        mockPageContext = new MockPageContext(mockServletContext);
        mockPageContext.getRequest().setCharacterEncoding("UTF-8");
        mockPageContext.getResponse().setCharacterEncoding("UTF-8");
        formatTag = new FormatTag();
        formatTag.setPageContext(mockPageContext);
    }

    @After
    public void print() throws UnsupportedEncodingException {
        System.out.println(((MockHttpServletResponse) mockPageContext.getResponse()).getContentAsString());
    }

    @Test
    public void dayOfWeekTest() throws IOException, JspException {
        for (DayOfWeek o : DayOfWeek.values()) {
            mockPageContext.getOut().println("DayOfWeek: " + o);
            formatPattern(o, "E EE EEE EEEE e ee eee eeee");
        }
    }

    @Test
    public void instantTest() throws JspException, IOException {
        Object o = Instant.now();
        mockPageContext.getOut().println("Instant: " + o);
        format(o);
        formatPattern(o, "G u y D M d Q Y w W E e F a h K k H m s S A n N VV z O X x Z");
        formatStyle(o, "S-");
        formatStyle(o, "M-");
        formatStyle(o, "L-");
        formatStyle(o, "F-");
        formatStyle(o, "-S");
        formatStyle(o, "-M");
        formatStyle(o, "-L");
        formatStyle(o, "-F");
    }

    @Test
    public void localDateTest() throws IOException, JspException {
        Object o = LocalDate.now();
        mockPageContext.getOut().println("LocalDate: " + o);
        format(o);
        formatPattern(o, "dd/MM/yyyy");
        formatStyle(o, "S-");
        formatStyle(o, "M-");
        formatStyle(o, "L-");
        formatStyle(o, "F-");
    }

    @Test
    public void localTimeTest() throws IOException, JspException {
        Object o = LocalTime.now();
        mockPageContext.getOut().println("LocalTime: " + o);
        formatPattern(o, "HH:mm:ss");
        formatStyle(o, "-S");
        formatStyle(o, "-M");
        formatStyle(o, "-L");
        formatStyle(o, "-F");
    }

    @Test
    public void localDateTimeTest() throws IOException, JspException {
        Object o = LocalDateTime.now();
        mockPageContext.getOut().println("LocalDateTime: " + o);
        format(o);
        formatPattern(o, "dd/MM/yyyy HH:mm:ss");
        formatStyle(o, "S-");
        formatStyle(o, "M-");
        formatStyle(o, "L-");
        formatStyle(o, "F-");
        formatStyle(o, "-S");
        formatStyle(o, "-M");
        formatStyle(o, "-L");
        formatStyle(o, "-F");
    }

    @Test
    public void monthTest() throws IOException, JspException {
        for (Month o : Month.values()) {
            mockPageContext.getOut().println("Month: " + o);
            formatPattern(o, "M MM MMM MMMM L LL LLL LLLL");
        }

    }

    @Test
    public void monthDayTest() throws IOException, JspException {
        MonthDay o = MonthDay.now();
        mockPageContext.getOut().println("MonthDay: " + o);
        formatPattern(o, "M d");
    }

    @Test
    public void offsetDateTimeTest() throws IOException, JspException {
        Object o = OffsetDateTime.now();
        mockPageContext.getOut().println("OffsetDateTime: " + o);
        format(o);
        formatPattern(o, "G u y D M d Q Y w W E e F a h K k H m s S A n N VV z O X x Z");
        formatStyle(o, "S-");
        formatStyle(o, "M-");
        formatStyle(o, "L-");
        formatStyle(o, "F-");
        formatStyle(o, "-S");
        formatStyle(o, "-M");
        formatStyle(o, "-L");
        formatStyle(o, "-F");
    }

    @Test
    public void offsetTimeTest() throws IOException, JspException {
        Object o = OffsetTime.now();
        mockPageContext.getOut().println("OffsetTime: " + o);
        formatPattern(o, "HH:mm:ss");
        formatStyle(o, "-S");
        formatStyle(o, "-M");
        formatStyle(o, "-L");
        formatStyle(o, "-F");
    }

    @Test
    public void yearTest() throws IOException, JspException {
        Year o = Year.now();
        mockPageContext.getOut().println("Year: " + o);
        formatPattern(o, "u uu uuu uuuu y yy yyyy yyyy G GG GGG GGGG");
    }

    @Test
    public void yearMonthTest() throws IOException, JspException {
        YearMonth o = YearMonth.now();
        mockPageContext.getOut().println("YearMonth: " + o);
        formatPattern(o, "G u y M L Q QQ QQQ QQQQ");
    }

    @Test
    public void zonedDateTime() throws IOException, JspException {
        Object o = ZonedDateTime.now();
        mockPageContext.getOut().println("ZonedDateTime: " + o);
        format(o);
        formatPattern(o, "G u y D M d Q Y w W E e F a h K k H m s S A n N VV z O X x Z");
        formatStyle(o, "S-");
        formatStyle(o, "M-");
        formatStyle(o, "L-");
        formatStyle(o, "F-");
        formatStyle(o, "-S");
        formatStyle(o, "-M");
        formatStyle(o, "-L");
        formatStyle(o, "-F");
    }

    private void format(Object o) throws JspException, IOException {
        format(o, null, null);
    }

    private void formatPattern(Object o, String pattern) throws JspException, IOException {
        format(o, pattern, null);
    }

    private void formatStyle(Object o, String style) throws JspException, IOException {
        mockPageContext.getOut().print("Style: " + style + " => ");
        format(o, null, style);
    }

    private void format(Object o, String pattern, String style) throws JspException, IOException {
        formatTag.setPattern(pattern);
        formatTag.setStyle(style);
        formatTag.setValue(o);
        formatTag.doEndTag();
        mockPageContext.getOut().println();
    }
}
