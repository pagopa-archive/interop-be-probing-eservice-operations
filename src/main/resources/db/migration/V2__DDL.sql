CREATE TABLE public.eservice_probing_responses (
	response_received timestamptz NOT NULL,
	eservice_id int8 NOT NULL,
	CONSTRAINT eservice_probing_responses_pkey PRIMARY KEY (eservice_id)
);

ALTER TABLE public.eservice_probing_responses ADD CONSTRAINT fkoyntlrnuvx1wo1sujl4ikd2wf FOREIGN KEY (eservice_id) REFERENCES public.eservices(id);


CREATE VIEW eservice_view AS
SELECT e.id, e.eservice_id , e.eservice_name, e.producer_name , e.version_id , e.state ,e.probing_enabled , MAX(epr.response_received) AS response_received
FROM eservices e
LEFT JOIN eservice_probing_responses epr ON epr.eservice_id = e.id
GROUP BY e.eservice_name, e.producer_name , e.version_id , e.state, e.id;

delete from eservice_probing_responses ;
delete from eservices;

ALTER TABLE public.eservices ADD version int NOT NULL;
ALTER TABLE public.eservices ADD version_number int NOT NULL;



