import net.sargue.time.jsptags.ParseInstantTag;
import net.sargue.time.jsptags.ParseLocalDateTag;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

import jakarta.servlet.jsp.JspException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Locale;

/**
 * Basic parse tests.
 *
 * @author Sergi Baila
 * @link http://blog.agilelogicsolutions.com/2011/02/unit-testing-jsp-custom-tag-using.html
 */

public class ParseLocalDateTagTest {

    private ParseLocalDateTag parseLocalDateTag;
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
        parseLocalDateTag = new ParseLocalDateTag();
        parseLocalDateTag.setPageContext(mockPageContext);
    }

    @Test
    public void parsePatternToVar() throws JspException {
        parseLocalDateTag.setValue("2015-10-28");
        parseLocalDateTag.setPattern("yyyy-MM-dd");
        parseLocalDateTag.setVar("date");
        parseLocalDateTag.doEndTag();
        Object date = mockPageContext.getAttribute("date");
        Assert.assertTrue(date instanceof LocalDate);
        LocalDate localDate = (LocalDate) date;
        Assert.assertEquals(LocalDate.of(2015, 10, 28), localDate);
    }
}
