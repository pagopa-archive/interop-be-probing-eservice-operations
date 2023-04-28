
CREATE VIEW ${schema_name}.eservice_view AS
SELECT e.id, e.eservice_id , e.eservice_name, e.producer_name , e.version_id , e.state ,e.probing_enabled , e.version_number , epr.response_received, epreq.last_request, e.polling_frequency, e.polling_start_time, e.polling_end_time, e.base_path, e.eservice_technology
FROM eservices e
LEFT JOIN eservice_probing_responses epr ON epr.eservices_record_id = e.id
LEFT JOIN eservice_probing_requests epreq on epreq.eservices_record_id=e.id;