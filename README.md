## Deprecated
Replaced by [new implementation](https://github.com/pagopa/interop-probing-core)

---

**PDND Interoperabilità: interop-be-probing-eservice-operations microservice**

**_Installation steps_**:

**1.** At the first release on a new environment, it is necessary to run the following script for the creation of the role flyway_user required for database creation and successive migrations:
   
    CREATE ROLE flyway_user WITH 
	  NOSUPERUSER
	  CREATEDB
	  CREATEROLE
	  NOINHERIT
	  LOGIN
	  NOREPLICATION
	  NOBYPASSRLS
	  CONNECTION LIMIT -1
	  PASSWORD '{PASSWORD}';
   
    GRANT rds_superuser TO flyway_user;
