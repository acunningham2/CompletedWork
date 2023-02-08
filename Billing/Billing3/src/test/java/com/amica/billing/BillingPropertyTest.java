package com.amica.billing;

import com.amica.escm.configuration.properties.PropertiesConfiguration;

import java.util.Properties;

import static com.amica.billing.TestUtility.TEMP_FOLDER;

public class BillingPropertyTest extends BillingTest {
    public static final String CONFIG_CUSTOMERS_FILENAME = "customers_configured.csv";
    public static final String CONFIG_INVOICES_FILENAME = "invoices_configured.csv";

    protected String getCustomersFilename() {
        return CONFIG_CUSTOMERS_FILENAME;
    }

    protected String getInvoicesFilename() {
        return CONFIG_INVOICES_FILENAME;
    }

    protected Billing createTestTarget() {
        Properties properties = new Properties();
        properties.put(Billing.customersProp, TEMP_FOLDER + "/" + getCustomersFilename());
        properties.put(Billing.invoicesProp, TEMP_FOLDER + "/" + getInvoicesFilename());
        return new Billing(new PropertiesConfiguration(properties));
    }
}
