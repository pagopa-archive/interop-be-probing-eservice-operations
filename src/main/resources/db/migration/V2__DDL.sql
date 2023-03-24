CREATE TABLE public.eservice_probing_responses (
	response_received timestamptz NOT NULL,
	eservice_id int8 NOT NULL,
	CONSTRAINT eservice_probing_responses_pkey PRIMARY KEY (eservice_id)
);

ALTER TABLE public.eservice_probing_responses ADD CONSTRAINT fkoyntlrnuvx1wo1sujl4ikd2wf FOREIGN KEY (eservice_id) REFERENCES public.eservices(id);

delete from eservice_probing_responses ;
delete from eservices;

ALTER TABLE public.eservices ADD version int NOT NULL;
ALTER TABLE public.eservices ADD version_number int NOT NULL;






