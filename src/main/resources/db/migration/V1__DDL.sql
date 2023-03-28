CREATE SCHEMA IF NOT EXISTS ${schema_name};

CREATE SEQUENCE  IF NOT EXISTS ${schema_name}.eservice_sequence START WITH 1 INCREMENT BY 1;

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
   CONSTRAINT pk_eservices PRIMARY KEY (id)
);

ALTER TABLE ${schema_name}.eservices ADD CONSTRAINT uc_c5bc699f9ffb5e4293b59694a UNIQUE (eservice_id, version_id);