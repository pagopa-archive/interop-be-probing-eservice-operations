CREATE SCHEMA IF NOT EXISTS ${schema_name};

CREATE SEQUENCE IF NOT EXISTS ${schema_name}.eservice_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE ${schema_name}.eservices (
   id BIGINT NOT NULL,
   base_path VARCHAR(2048) array NOT NULL,
   eservice_name VARCHAR(255) NOT NULL,
   eservice_technology VARCHAR(255) NOT NULL,
   eservice_id UUID NOT NULL,
   polling_end_time time WITH TIME ZONE NOT NULL,
   polling_frequency INTEGER DEFAULT 5 NOT NULL,
   polling_start_time time WITH TIME ZONE NOT NULL,
   probing_enabled BOOLEAN NOT NULL,
   producer_name VARCHAR(2048) NOT NULL,
   state VARCHAR(255) NOT NULL,
   version_id UUID NOT NULL,
   lock_version INTEGER NOT NULL,
   version_number INTEGER NOT NULL,
   audience VARCHAR(2048) array NOT NULL,
   CONSTRAINT pk_eservices PRIMARY KEY (id)
);

ALTER TABLE ${schema_name}.eservices ADD CONSTRAINT UQ_eservices_eservice_id_version_id UNIQUE (eservice_id, version_id);

CREATE TABLE ${schema_name}.eservice_probing_responses (
	response_received timestamptz NOT NULL,
	status VARCHAR(2) NOT NULL,
	eservices_record_id int8 NOT NULL,
	CONSTRAINT eservice_probing_responses_pkey PRIMARY KEY (eservices_record_id)
);

ALTER TABLE ${schema_name}.eservice_probing_responses ADD CONSTRAINT FK_ESERVICE_ESERVICE_PROBING_RESPONSE FOREIGN KEY (eservices_record_id) REFERENCES ${schema_name}.eservices(id);

CREATE TABLE ${schema_name}.eservice_probing_requests (
	last_request timestamptz NOT NULL,
	eservices_record_id int8 NOT NULL,
	CONSTRAINT eservice_probing_requests_pkey PRIMARY KEY (eservices_record_id)
);

ALTER TABLE ${schema_name}.eservice_probing_requests ADD CONSTRAINT FK_ESERVICE_ESERVICE_PROBING_REQUESTS FOREIGN KEY (eservices_record_id) REFERENCES ${schema_name}.eservices(id);

CREATE VIEW ${schema_name}.eservice_view AS
SELECT e.id, e.eservice_id , e.eservice_name, e.producer_name , e.version_id , e.state , epr.status ,e.probing_enabled , e.version_number , epr.response_received , epreq.last_request, e.polling_frequency, e.polling_start_time, e.polling_end_time, e.base_path, e.eservice_technology, e.audience
FROM ${schema_name}.eservices e
LEFT JOIN ${schema_name}.eservice_probing_responses epr ON epr.eservices_record_id = e.id
LEFT JOIN ${schema_name}.eservice_probing_requests epreq on epreq.eservices_record_id=e.id;

CREATE ROLE "${database_username}" WITH 
	NOSUPERUSER
	NOCREATEDB
	NOCREATEROLE
	NOINHERIT
	LOGIN
	NOREPLICATION
	NOBYPASSRLS
	CONNECTION LIMIT -1
	PASSWORD '${database_password}';

--Grants
GRANT CREATE, USAGE ON SCHEMA ${schema_name} TO "${database_username}";
GRANT SELECT, INSERT, UPDATE ON TABLE ${schema_name}.eservice_probing_requests TO "${database_username}";
GRANT SELECT, INSERT, UPDATE ON TABLE ${schema_name}.eservice_probing_responses TO "${database_username}";
GRANT SELECT, INSERT, UPDATE ON TABLE ${schema_name}.eservices TO "${database_username}";
GRANT SELECT ON TABLE ${schema_name}.eservice_view TO "${database_username}";
GRANT SELECT, USAGE ON SEQUENCE ${schema_name}.eservice_sequence TO "${database_username}";
