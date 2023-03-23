rem Maven must be on the executable path.
rem JAVA_HOME must be set.
rem Both MongoDB and Derby must be running.
rem There must be data in the MongoDB 'billing' database.
rem This will be exported to a CSV, which will then be migrated to Derby.

cd %~dp0
md export
call mvn -s \CapstoneCourseware\Amica\AmicaJavaLabs\MavenOffline.xml -Dexec.mainClass=com.amica.billing.db.mongo.ExportMongoToCSV clean compile exec:java
cd ..\Billing5
call mvn -s \CapstoneCourseware\Amica\AmicaJavaLabs\MavenOffline.xml -Dexec.mainClass=com.amica.billing.db.derby.MigrateCSVToDerby -DParserPersistence.customersFile=../Billing6/export/customers.csv -DParserPersistence.invoicesFile=../Billing6/export/invoices.csv clean compile exec:java
cd %~dp0
