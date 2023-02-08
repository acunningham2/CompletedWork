package com.amica.billing;

import com.amica.billing.parse.FlatParser;
import com.amica.billing.parse.Parser;
import com.amica.billing.parse.QuotedCSVParser;
import com.amica.esa.componentconfiguration.manager.ComponentConfigurationManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static com.amica.billing.ParserFactory.createParser;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.mock;

public class ParserFactoryConfiguredTest extends ParserFactoryTest{
    private Parser mockParser = mock(Parser.class);
    private Supplier<Parser> mockParserFactory = () -> mockParser;

    @BeforeAll
    public static void init() {
        System.setProperty("env.name", "Quoted");
        System.setProperty("component.name", "BillingTest");
        ComponentConfigurationManager.getInstance().initialize();
    }

    @Test
    public void defaultParserTest() {
        assertThat(createParser("any"), instanceOf(FlatParser.class));
    }

    @Test
    public void quotedParserTest() {
        assertThat(createParser(".csv"), instanceOf(QuotedCSVParser.class));
    }
}
