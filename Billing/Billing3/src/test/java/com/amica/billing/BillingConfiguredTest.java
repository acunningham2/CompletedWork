package com.amica.billing;

import com.amica.esa.componentconfiguration.manager.ComponentConfigurationManager;
import org.junit.jupiter.api.BeforeAll;

public class BillingConfiguredTest extends BillingTest {

    public static final String CONFIG_CUSTOMERS_FILENAME = "customers.csv";
    public static final String CONFIG_INVOICES_FILENAME = "invoices.csv";

    @BeforeAll
    public static void init() {
        System.setProperty("env.name", "Configured");
        System.setProperty("component.name", "BillingTest");
        ComponentConfigurationManager.getInstance().initialize();
    }

    @Override
    protected String getCustomersFilename() {
        return CONFIG_CUSTOMERS_FILENAME;
    }

    @Override
    protected String getInvoicesFilename() {
        return CONFIG_INVOICES_FILENAME;
    }

    @Override
    protected Billing createTestTarget() {
        return new Billing();
    }
}
