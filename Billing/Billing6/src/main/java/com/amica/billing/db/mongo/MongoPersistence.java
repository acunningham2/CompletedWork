package com.amica.billing.db.mongo;

import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.amica.billing.Customer;
import com.amica.billing.Invoice;
import com.amica.billing.db.CachingPersistence;
import com.amica.billing.db.CustomerRepository;
import com.amica.billing.db.InvoiceRepository;

import lombok.AllArgsConstructor;

/**
 * Persistence implementation using MongoDB.
 * We trigger an initial load when configured as a Spring bean,
 * and support re-loading programmatically after that.
 * 
 * @author Will Provost
 */
@Component
@Primary
@AllArgsConstructor
public class MongoPersistence extends CachingPersistence {

	private CustomerRepository customers;
	private InvoiceRepository invoices;

	@Override
	@PostConstruct
	public void load() {
		super.load();
	}
	
	protected Stream<Customer> readCustomers() {
		return customers.streamAllBy();
	}
	
	protected Stream<Invoice> readInvoices() {
		return invoices.streamAllBy();
	}
	
	protected void writeCustomer(Customer customer) {
		customers.save(customer);
	}
	
	protected void writeInvoice(Invoice invoice) {
		invoices.save(invoice);
	}
}
