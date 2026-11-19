#!/bin/bash
# Consulta asíncrona del padró pel recobriment de PINBAL amb l'usuari d'aplicació pbl_ws: una
# sol·licitud per municipi, que l'EMISERV central encamina cadascuna per la seva ruta (el servei
# ha de tenir «Processar les asíncrones com a síncrones» a l'EMISERV central):
#   040 (Manacor)   EMISERV cedent (ruta d'emissor SCSP) → backoffice de l'ajuntament
#   011 (Calvià)    connexió directa al backoffice de l'ajuntament (ruta BACKOFFICE)
#   026 (Inca)      emissor SCSP simulat
#   033 (Llucmajor) emissor que respon amb error (es reintenta i acaba amb error)
# Envia la petició i en consulta la resposta cada 10 s fins que la té (Ctrl+C per aturar-ho;
# l'estat de cada sol·licitud es veu a Auditoria → Consultes asíncrones de l'EMISERV central).
# Ús: proves/consulta-asincrona.sh [servei] [municipi...]   (per defecte: SCDCPAJU 040 011)
set -e
SERVEI=${1:-SCDCPAJU}
shift || true
MUNICIPIS=("$@")
[ ${#MUNICIPIS[@]} -eq 0 ] && MUNICIPIS=(040 011)
PINBAL_URL=${PINBAL_URL:-http://localhost:${PINBAL_PORT:-8081}}
API="$PINBAL_URL/pinbalapi/interna/recobriment/v2"

SOLICITUDS=""
n=0
for MUNICIPI in "${MUNICIPIS[@]}"; do
	n=$((n + 1))
	# DNI vàlid (lletra de control): PINBAL valida el document del titular
	NUMERO=$((12345670 + n))
	LLETRES=TRWAGMYFPDXBNJZSQVHLCKE
	DOCUMENT="$NUMERO${LLETRES:$((NUMERO % 23)):1}"
	[ -n "$SOLICITUDS" ] && SOLICITUDS="$SOLICITUDS,"
	SOLICITUDS="$SOLICITUDS
    {
      \"titular\": { \"documentTipus\": \"DNI\", \"documentNumero\": \"$DOCUMENT\", \"nom\": \"PERSONA $n\", \"llinatge1\": \"DE PROVES\" },
      \"expedient\": \"EXP-INTEGRACIO-$MUNICIPI\",
      \"dadesEspecifiques\": {
        \"DatosEspecificos/Solicitud/ProvinciaSolicitud\": \"07\",
        \"DatosEspecificos/Solicitud/MunicipioSolicitud\": \"$MUNICIPI\",
        \"DatosEspecificos/Solicitud/Titular/Documentacion/Tipo\": \"DNI\",
        \"DatosEspecificos/Solicitud/Titular/Documentacion/Valor\": \"$DOCUMENT\"
      }
    }"
done

CONFIRMACIO=$(curl -sS -u pbl_ws:pbl_ws -H 'Content-Type: application/json' \
	"$API/serveis/$SERVEI/peticioAsincrona" -d @- <<JSON
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
  "solicituds": [$SOLICITUDS
  ]
}
JSON
)
echo "$CONFIRMACIO"
ID_PETICIO=$(sed -n 's/.*"idPeticio" *: *"\([^"]*\)".*/\1/p' <<<"$CONFIRMACIO")
if [ -z "$ID_PETICIO" ]; then
	echo "No s'ha obtingut l'identificador de la petició" >&2
	exit 1
fi
echo "Petició $ID_PETICIO (municipis ${MUNICIPIS[*]}): esperant la resposta..."

# PINBAL retorna l'estat de la consulta (PENDENT, ENCUA, PROCESSANT, TRAMITADA o ERROR)
while true; do
	sleep 10
	RESPOSTA=$(curl -sS -u pbl_ws:pbl_ws "$API/consultes/$ID_PETICIO/resposta")
	ESTAT=$(sed -n 's/.*"estat" *: *"\([A-Z]*\)".*/\1/p' <<<"$RESPOSTA" | head -1)
	case "$ESTAT" in
		TRAMITADA|ERROR)
			echo "$RESPOSTA"
			break;;
		*)
			echo "$(date +%T) ${ESTAT:-sense resposta}";;
	esac
done
