uadetector-udf
==============

UDF for uadetector 

To use in Hive, transfer the resulting jar file at target/uadetector-udf-0.0.1-jar-with-dependencies.jar to a location accessible by Hive and run the following Hive commands:

hive> ADD JAR /path/to/uadetector-udf-0.0.1-jar-with-dependencies.jar;
hive> CREATE TEMPORARY FUNCTION ua_family AS 'com.sovrn.udf.uadetector.UadetectorUDF';
