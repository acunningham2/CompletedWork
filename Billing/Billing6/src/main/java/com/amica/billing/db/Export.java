package com.amica.billing.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amica.billing.db.mongo.MongoPersistence;
import com.amica.billing.parse.ParserPersistence;

import lombok.Setter;

/**
 * Component that can replicate data from a {@link Persistence data source}
 * to a {@link Persistence target}. The source and target are autowired,
 * but can also be set programmatically.
 * 
 * @author Will Provost
 */
 @Component
public class Export {

	@Autowired
	@Setter
	private MongoPersistence source;
	
	@Autowired
	@Setter
	private ParserPersistence target;
	
	/**
	 * Reload the source and the target, for a clean slate.
	 * Save every object found in the source data set to the target. 
	 */
	public void export() {
		source.load();
		source.getCustomers().values().forEach(target::saveCustomer);
		source.getInvoices().values().forEach(target::saveInvoice);
	}
}
