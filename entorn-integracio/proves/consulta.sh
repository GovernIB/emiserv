#!/bin/bash
# Consulta síncrona d'un servei pel recobriment de PINBAL amb l'usuari d'aplicació pbl_ws.
#   Padró (SCDCPAJU, SCDHPAJU): enrutadors, el municipi tria la ruta de l'EMISERV central:
#     040 (Manacor, per defecte) EMISERV cedent amb backoffice
#     026 (Inca)                 emissor SCSP simulat
#     011 (Calvià)               backoffice simulat amb connexió directa
#     033 (Llucmajor)            emissor que respon amb error
#   Família nombrosa (SVDSCTFNWS01): enrutador múltiple (MALLORCA, MENORCA i EIVISSA)
#   La resta (SCMCEDU, SVDCCAACPCWS01...): backoffices de l'EMISERV central
# Ús: proves/consulta.sh [servei] [municipi] [document]
set -e
SERVEI=${1:-SCDCPAJU}
MUNICIPI=${2:-040}
DOCUMENT=${3:-12345678Z}
PINBAL_URL=${PINBAL_URL:-http://localhost:${PINBAL_PORT:-8081}}

case "$SERVEI" in
	SCDCPAJU|SCDHPAJU)
		DADES_ESPECIFIQUES='{
      "DatosEspecificos/Solicitud/ProvinciaSolicitud": "07",
      "DatosEspecificos/Solicitud/MunicipioSolicitud": "'"$MUNICIPI"'",
      "DatosEspecificos/Solicitud/Titular/Documentacion/Tipo": "DNI",
      "DatosEspecificos/Solicitud/Titular/Documentacion/Valor": "'"$DOCUMENT"'"
    }';;
	SVDSCTFNWS01)
		DADES_ESPECIFIQUES='{ "DatosEspecificos/Consulta/TituloFamiliaNumerosa/CodigoComunidadAutonoma": "04" }';;
	*)
		DADES_ESPECIFIQUES='{}';;
esac

curl -sS -u pbl_ws:pbl_ws -H 'Content-Type: application/json' \
	"$PINBAL_URL/pinbalapi/interna/recobriment/v2/serveis/$SERVEI/peticioSincrona" \
	-d @- <<JSON
{
  "dadesComunes": {
    "entitatCif": "P0700000A",
    "procedimentCodi": "E2EPROC01",
    "serveiCodi": "$SERVEI",
    "funcionari": { "nom": "Funcionari de proves", "nif": "00000000T" },
    "departament": "Departament de proves",
    "consentiment": "Si",
    "finalitat": "Prova de l'entorn d'integració"
  },
  "solicitud": {
    "titular": { "documentTipus": "DNI", "documentNumero": "$DOCUMENT", "nom": "PERSONA", "llinatge1": "DE PROVES" },
    "expedient": "EXP-INTEGRACIO",
    "dadesEspecifiques": $DADES_ESPECIFIQUES
  }
}
JSON
echo
