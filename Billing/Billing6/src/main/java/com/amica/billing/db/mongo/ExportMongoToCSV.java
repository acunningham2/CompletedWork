package com.amica.billing.db.mongo;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.amica.billing.db.CustomerRepository;
import com.amica.billing.db.Export;
import com.amica.billing.db.Migration;

/**
 * Utility application that replicates all data from the prepared
 * CSV-format files to a MongoDB database.
 * 
 * @author Will Provost
 */
@ComponentScan(basePackageClasses={
		com.amica.billing.parse.ParserPersistence.class, 
		CustomerRepository.class
	})
@EnableAutoConfiguration
@EnableMongoRepositories(basePackageClasses=CustomerRepository.class)
@PropertySource(value={"classpath:DB.properties","classpath:export.properties"})
public class ExportMongoToCSV {
	
	/**
	 * Run the Spring application, get the {@link Migration} bean,
	 * call its {@link Migration#migrate migrate method},
	 * and then use the repository beans to report the counts of
	 * customers and invoices in the replicated database.
	 */
	public static void main(String[] args) {
		
		try ( FileReader in = new FileReader("src/main/resources/export.properties"); ) {
			Properties exportProps = new Properties();
			exportProps.load(in);
			try ( FileWriter out = new FileWriter(exportProps.getProperty("ParserPersistence.customersFile")); ) {}
			try ( FileWriter out = new FileWriter(exportProps.getProperty("ParserPersistence.invoicesFile")); ) {}
		} catch (IOException ex) {
			System.out.println("Couldn't read export properties.");
			ex.printStackTrace();
		}
		
		try ( ConfigurableApplicationContext context =
				SpringApplication.run(ExportMongoToCSV.class); ) {
			context.getBean(Export.class).export();
		}
	}
}
