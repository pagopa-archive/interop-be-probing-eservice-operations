CREATE VIEW eservice_view AS
SELECT e.id, e.eservice_id , e.eservice_name, e.producer_name , e.version_id , e.state ,e.probing_enabled , e.version_number , MAX(epr.response_received) AS response_received
FROM eservices e
LEFT JOIN eservice_probing_responses epr ON epr.eservice_id = e.id
GROUP BY e.eservice_name, e.producer_name , e.version_id , e.state, e.id;

