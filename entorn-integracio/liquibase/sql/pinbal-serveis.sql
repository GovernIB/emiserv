-- La resta de serveis que PINBAL consulta a l'EMISERV de la CAIB (els de proves.caib.es), configurats
-- com SCDCPAJU (pinbal.sql): emissor CAIB, signats amb limit_pinbal, a l'entitat E2E Principal i al
-- procediment E2EPROC01, amb permís de consulta per a pbl_delegat i pbl_ws.
--  * Enrutadors (SCDHPAJU) i enrutadors múltiples (SVDSCTFNWS01): /emiservback/scspRouting/...
--  * Backoffices: /emiservapi/interna/scsp/services/...
-- Només es validen amb l'XSD les respostes dels serveis que el fake respon amb dades vàlides
-- (esquemas); la resta no en tenen, com SVDDGTVEHICULOSANCWS01 a les dades e2e.
DECLARE
	v_servei_base core_servicio%ROWTYPE;
	v_config_base pbl_servei_config%ROWTYPE;
	v_entitat_servei pbl_entitat_servei%ROWTYPE;
	v_procediment_servei pbl_procediment_servei%ROWTYPE;
	v_object_identity pbl_acl_object_identity%ROWTYPE;
	v_acl_entry pbl_acl_entry%ROWTYPE;
	v_req_cessionari core_req_cesionarios_servicios%ROWTYPE;
	v_n NUMBER := 0;

	PROCEDURE afegir_servei(p_codi VARCHAR2, p_descripcio VARCHAR2, p_enrutador BOOLEAN, p_esquemas VARCHAR2) IS
		v_servei core_servicio%ROWTYPE := v_servei_base;
		v_config pbl_servei_config%ROWTYPE := v_config_base;
		v_base_url VARCHAR2(200);
	BEGIN
		v_n := v_n + 1;
		v_base_url := CASE WHEN p_enrutador
			THEN 'http://emiserv-central:8080/emiservback/scspRouting/'
			ELSE 'http://emiserv-central:8080/emiservapi/interna/scsp/services/' END;
		v_servei.id := 909100 + v_n;
		v_servei.codcertificado := p_codi;
		v_servei.descripcion := p_descripcio;
		v_servei.urlsincrona := v_base_url || 'peticionSincrona';
		v_servei.urlasincrona := v_base_url || 'peticionAsincrona';
		v_servei.esquemas := p_esquemas;
		INSERT INTO core_servicio VALUES v_servei;

		v_req_cessionari.id := 909100 + v_n;
		v_req_cessionari.servicio := v_servei.id;
		INSERT INTO core_req_cesionarios_servicios VALUES v_req_cessionari;

		v_config.id := 909100 + v_n;
		v_config.servei_id := p_codi;
		INSERT INTO pbl_servei_config VALUES v_config;

		v_entitat_servei.id := 909100 + v_n;
		v_entitat_servei.servei_id := p_codi;
		INSERT INTO pbl_entitat_servei VALUES v_entitat_servei;

		v_procediment_servei.id := 909100 + v_n;
		v_procediment_servei.servei_id := p_codi;
		INSERT INTO pbl_procediment_servei VALUES v_procediment_servei;

		-- Permís de consulta (READ) de pbl_delegat (sid 901302) i pbl_ws (sid 909004)
		v_object_identity.id := 909100 + v_n;
		v_object_identity.object_id_identity := v_procediment_servei.id;
		INSERT INTO pbl_acl_object_identity VALUES v_object_identity;
		v_acl_entry.acl_object_identity := v_object_identity.id;
		v_acl_entry.id := 909200 + 2 * v_n;
		v_acl_entry.ace_order := 0;
		v_acl_entry.sid := 901302;
		INSERT INTO pbl_acl_entry VALUES v_acl_entry;
		v_acl_entry.id := 909201 + 2 * v_n;
		v_acl_entry.ace_order := 1;
		v_acl_entry.sid := 909004;
		INSERT INTO pbl_acl_entry VALUES v_acl_entry;
	END;
BEGIN
	SELECT * INTO v_servei_base FROM core_servicio WHERE codcertificado = 'SCDCPAJU';
	SELECT * INTO v_config_base FROM pbl_servei_config WHERE servei_id = 'SCDCPAJU';
	SELECT * INTO v_entitat_servei FROM pbl_entitat_servei WHERE servei_id = 'SCDCPAJU';
	SELECT * INTO v_procediment_servei FROM pbl_procediment_servei WHERE id = 901002;
	SELECT * INTO v_object_identity FROM pbl_acl_object_identity WHERE id = 901305;
	SELECT * INTO v_acl_entry FROM pbl_acl_entry WHERE id = 901307;
	SELECT * INTO v_req_cessionari FROM core_req_cesionarios_servicios WHERE id = 909002;

	afegir_servei('SCDHPAJU', 'Servei de consulta de padró històric', TRUE, '/schemas/SCDHPAJUv3');
	afegir_servei('SVDSCTFNWS01', 'Servei de consulta de familia nombrosa', TRUE, NULL);
	afegir_servei('SCMCEDU', 'Servei de consulta d''escolarització', FALSE, NULL);
	afegir_servei('SVDCCAACPASWS01', 'Estar al corrent de pagament per ajudes', FALSE, NULL);
	afegir_servei('SVDCCAACPCWS01', 'Estar al corrent de pagament per contractacions', FALSE, NULL);
	afegir_servei('SVDCCAAIRPHWS01', 'Consulta dades de parelles de fet', FALSE, NULL);
	afegir_servei('SVDNIVCAT', 'Consulta de nivell de Català', FALSE, NULL);
	afegir_servei('SVDSCDDWS01', 'Servei de consulta de dades de discapacitat', FALSE, NULL);
	afegir_servei('SVDSCDDWS02', 'Servei de consulta de dades de discapacitat V2', FALSE, NULL);
	afegir_servei('SVDSCDWS01', 'Servei de Canvi de Domicili', FALSE, NULL);
END;
/
