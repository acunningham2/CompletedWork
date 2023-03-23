package com.amica.billing;


import static com.amica.billing.TestUtility.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.amica.billing.db.Migration;

/**
 * Integration test for the {@link Reporter} that uses MongoDB.
 * We configure a {@link Migration} 
 * utility, and reset the database before each test.
 * 
 * @author Will Provost
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes=ReporterMongoIntegrationTest.Config.class)
public class ReporterMongoIntegrationTest extends ReporterIntegrationTestBase {
	
	@ComponentScan
	@EnableAutoConfiguration
	@EnableMongoRepositories
	@PropertySource(value={
			"classpath:test.properties", 
			"classpath:migration.properties"
		})
	public static class Config {
	}
	
	@Autowired
	private Migration migration;
	
	/**
	 * Let the base class set up the reporter, and make sure that we
	 * reset the database before the next test.
	 */
	@Override
	@BeforeEach
	public void setUp() throws IOException {
		super.setUp();
		migration.migrate();
	}
}
