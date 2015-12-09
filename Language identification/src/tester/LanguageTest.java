import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.util.Language;

import static org.junit.Assert.*;

/**
 * Created by Jordan on 11/10/2015.
 */
public class LanguageTest {

    Language lang;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCreateLang() throws Exception {
        Language french = new Language("fra", "French");
        assertNotNull(french);
    }

    @Test
    public void testInit() throws Exception {
        Language.init();
        assertEquals(Language.nameOf("fra"), "French");
    }

    @Test
    public void testGetName() throws Exception {
        Language french = new Language("fra", "French");
        assertEquals(french.getName(), "French");
    }

    @Test
    public void testUnknown() throws Exception {
        Language unk = Language.UNKNOWN;
        assertEquals(unk.getName(), Language.UNKNOWN_STRING);
        assertEquals(unk.getCode(), Language.UNKNOWN_CODE);
    }

    @Test
    public void testGetCode() throws Exception {
        Language french = new Language("fra");
        assertEquals(french.getCode(), "fra");
    }

    @Test
    public void testToString() throws Exception {

    }

    @Test
    public void testEquals() throws Exception {

    }
}