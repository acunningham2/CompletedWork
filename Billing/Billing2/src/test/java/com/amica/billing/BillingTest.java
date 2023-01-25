package com.amica.billing;

import static com.amica.billing.TestUtility.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Unit test for the {@link Billing} class.
 * This test focuses on the test data set defined in {@link TestUtillity}
 * and prepared data files that reflect that data. We make a copy of the
 * data files at the start of each test case, create the Billing object
 * to load them, and check its getters and query methods.
 * A few more test cases drive updates to the object, and assure that
 * they are reflected in updates to the staged data files.
 * 
 * @author Will Provost
 */
public class BillingTest {

	public static final String SOURCE_FOLDER = "src/test/resources/data";
	public static Billing billing;
	public static Consumer<Customer> customerConsumer = Mockito.mock(Consumer.class);
	public static Consumer<Invoice> invoiceConsumer = Mockito.mock(Consumer.class);

	/**
	 * Assure that the necessary folders are in place, and make a copy
	 * of the source data files. Install mock objects as listeners for changes.
	 */
	@BeforeEach
	public void setUp() throws IOException {
		Files.createDirectories(Paths.get(TEMP_FOLDER));
		Files.createDirectories(Paths.get(OUTPUT_FOLDER));
		Files.copy(Paths.get(SOURCE_FOLDER, CUSTOMERS_FILENAME), 
				Paths.get(TEMP_FOLDER, CUSTOMERS_FILENAME),
				StandardCopyOption.REPLACE_EXISTING);
		Files.copy(Paths.get(SOURCE_FOLDER, INVOICES_FILENAME), 
				Paths.get(TEMP_FOLDER, INVOICES_FILENAME),
				StandardCopyOption.REPLACE_EXISTING);

		billing = new Billing(TEMP_FOLDER + "/" + CUSTOMERS_FILENAME,
								TEMP_FOLDER + "/" + INVOICES_FILENAME);


		billing.addCustomerListener(customerConsumer);
		billing.addInvoiceListener(invoiceConsumer);
	}

	@Test
	public void orderedByNumbersTest() {
		assertThat(billing.getInvoicesOrderedByNumber(), hasNumbers(1,2,3,4,5,6));
	}

	@Test
	public void orderedByDateTest() {
		assertThat(billing.getInvoicesOrderedByDate(), hasNumbers(4,6,1,2,5,3));
	}

	@Test
	public void groupedByCustomerTest() {
		Map<Customer, List<Invoice>> invoices = billing.getInvoicesGroupedByCustomer();
		assertThat(invoices.get(GOOD_CUSTOMERS.get(0)).stream(), hasNumbers(1));
		assertThat(invoices.get(GOOD_CUSTOMERS.get(1)).stream(), hasNumbers(2,3,4));
		assertThat(invoices.get(GOOD_CUSTOMERS.get(2)).stream(), hasNumbers(5,6));
	}

	@Test
	public void overdueInvoicesTest() {
		assertThat(billing.getOverdueInvoices(AS_OF_DATE), hasNumbers(4,6,1));
	}

	@Test
	public void createCustomerTest() {
		billing.createCustomer("Adam", "C", Terms.CASH);
		try (Stream<String> lines = Files.lines(Path.of(TEMP_FOLDER + "/" + CUSTOMERS_FILENAME))) {
			assertThat(lines.collect(Collectors.joining("\n")), containsStringIgnoringCase("Adam,C,CASH"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Mockito.verify(customerConsumer).accept(billing.getCustomers().get("Adam C"));
	}

	@Test
	public void createInvoiceTest() {
		billing.createCustomer("Adam", "C", Terms.CASH);
		billing.createInvoice("Adam C", 1200);
		try (Stream<String> lines = Files.lines(Path.of(TEMP_FOLDER + "/" + INVOICES_FILENAME))) {
			assertThat(lines.collect(Collectors.joining("\n")), containsStringIgnoringCase("Adam,C,1200.00"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Mockito.verify(invoiceConsumer).accept(billing.getInvoices().get(6));
	}

	@Test
	public void payInvoiceTest() {
		billing.createCustomer("Adam", "C", Terms.CASH);
		billing.createInvoice("Adam C", 1200);
		billing.payInvoice(7);
		try (Stream<String> lines = Files.lines(Path.of(TEMP_FOLDER + "/" + INVOICES_FILENAME))) {
			assertThat(lines.collect(Collectors.joining("\n")), containsStringIgnoringCase("7,Adam,C,1200.00,2023-01-25,2023-01-25"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Mockito.verify(invoiceConsumer, Mockito.times(3)).accept(billing.getInvoices().get(6));
	}


	
}
