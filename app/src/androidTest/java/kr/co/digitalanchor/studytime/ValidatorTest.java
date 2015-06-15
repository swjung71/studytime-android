package kr.co.digitalanchor.studytime;

import junit.framework.TestCase;

import kr.co.digitalanchor.utils.StringValidator;

/**
 * Created by Thomas on 2015-06-15.
 */
public class ValidatorTest extends TestCase {

    public void testSimple() {

        assertEquals(false, StringValidator.isEmail("ewrrew.asdfawsdf"));
    }
}
