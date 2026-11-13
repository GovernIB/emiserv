-- Dades de l'entorn d'integració a PINBAL, a sobre de les dades de prova del context e2e del
-- Liquibase de PINBAL (entitat E2EENT01, procediment E2EPROC01, serveis i usuaris):
--  * el padró (SCDCPAJU) es consulta a l'enrutador de l'EMISERV central, com a producció, amb la CAIB com a
--    emissor i signat amb la clau limit_pinbal de l'organisme cessionari P0700000A;
--  * pbl_ws (rol PBL_WS) és una aplicació de l'entitat que pot consultar el padró pel
--    recobriment;
--  * admin (Keycloak) pot entrar a PINBAL com a administrador.
-- Un sol bloc PL/SQL: les files noves es copien de les del context e2e (%ROWTYPE) perquè no
-- depenguin de les columnes de cada versió de PINBAL.
DECLARE
	v_entitat_usuari pbl_entitat_usuari%ROWTYPE;
	v_usuari pbl_usuari%ROWTYPE;
	v_cessionari core_req_cesionarios_servicios%ROWTYPE;
	v_acl_entry pbl_acl_entry%ROWTYPE;
	v_servei_id core_servicio.id%TYPE;
BEGIN
	INSERT INTO core_emisor_certificado (id, nombre, cif, fechaalta)
	VALUES (909001, 'Govern de les Illes Balears', 'S0711001H', TIMESTAMP '2024-01-01 00:00:00');

	SELECT id INTO v_servei_id FROM core_servicio WHERE codcertificado = 'SCDCPAJU';
	UPDATE core_servicio SET
		urlsincrona = 'http://emiserv-central:8080/emiservback/scspRouting/peticionSincrona',
		urlasincrona = 'http://emiserv-central:8080/emiservback/scspRouting/peticionAsincrona',
		emisor = 909001
	WHERE id = v_servei_id;

	-- Clau de signatura de les peticions de padró (com la del servei Q2827003ATGSS001)
	SELECT * INTO v_cessionari FROM core_req_cesionarios_servicios WHERE id = 900406;
	v_cessionari.id := 909002;
	v_cessionari.servicio := v_servei_id;
	INSERT INTO core_req_cesionarios_servicios VALUES v_cessionari;

	-- Usuaris admin i pbl_ws
	SELECT * INTO v_usuari FROM pbl_usuari WHERE codi = 'pbl_admin';
	v_usuari.codi := 'admin';
	v_usuari.nom := 'Admin Integració';
	v_usuari.nif := '00000010Q';
	INSERT INTO pbl_usuari VALUES v_usuari;
	v_usuari.codi := 'pbl_ws';
	v_usuari.nom := 'Aplicació PINBAL';
	v_usuari.nif := '00000011V';
	INSERT INTO pbl_usuari VALUES v_usuari;

	-- pbl_ws: aplicació de l'entitat E2E Principal
	SELECT * INTO v_entitat_usuari FROM pbl_entitat_usuari WHERE usuari_id = 'pbl_delegat';
	v_entitat_usuari.id := 909003;
	v_entitat_usuari.usuari_id := 'pbl_ws';
	v_entitat_usuari.delegat := 1;
	v_entitat_usuari.representant := 0;
	v_entitat_usuari.auditor := 0;
	v_entitat_usuari.aplicacio := 1;
	INSERT INTO pbl_entitat_usuari VALUES v_entitat_usuari;

	-- pbl_ws: permís de consulta del padró al procediment E2EPROC01 (procediment-servei 901002),
	-- igual que el del delegat (ACL 901305)
	INSERT INTO pbl_acl_sid (id, principal, sid) VALUES (909004, 1, 'pbl_ws');
	SELECT * INTO v_acl_entry FROM pbl_acl_entry WHERE id = 901307;
	v_acl_entry.id := 909005;
	v_acl_entry.sid := 909004;
	SELECT MAX(ace_order) + 1 INTO v_acl_entry.ace_order
	FROM pbl_acl_entry WHERE acl_object_identity = v_acl_entry.acl_object_identity;
	INSERT INTO pbl_acl_entry VALUES v_acl_entry;
END;
/
