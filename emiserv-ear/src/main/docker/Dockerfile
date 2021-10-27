#FROM daggerok/jboss-eap-7.3:7.3.0-alpine
FROM daggerok/jboss-eap-7.2:7.2.71-alpine

ARG KEYCLOAK_ADAPTER_VERSION=12.0.4
ARG KEYCLOAK_ADAPTER_FILE=keycloak-oidc-wildfly-adapter-${KEYCLOAK_ADAPTER_VERSION}.tar.gz
ARG DATASOURCES_FILE=configuration/datasources.xml
ARG SYSTEM_PROPS_FILE=configuration/system-properties.xml
ARG KEYCLOAK_FILE=configuration/keycloak.xml
ARG LOGGING_FILE=configuration/logging.xml
ARG APP_NAME=emiserv

ENV KEYCLOAK_URL http://docker.limit.es:21234/auth/
ENV KEYCLOAK_REALM GOIB
#ENV KEYCLOAK_URL https://dev.caib.es/auth
#ENV KEYCLOAK_REALM webdes
ENV KEYCLOAK_RESOURCE goib-default
#ENV DATABASE_URL jdbc:oracle:thin:@10.35.3.242:1521:orcl
#ENV DATABASE_DRIVER oracle
#ENV DATABASE_USER emiservnew
#ENV DATABASE_PASSWD emiservnew
ENV DATABASE_URL jdbc:postgresql://10.35.3.242:5432/emiserv
ENV DATABASE_DRIVER postgresql
ENV DATABASE_USER emiserv
ENV DATABASE_PASSWD emiserv

WORKDIR ${JBOSS_HOME}
RUN wget https://github.com/keycloak/keycloak/releases/download/${KEYCLOAK_ADAPTER_VERSION}/${KEYCLOAK_ADAPTER_FILE}
RUN tar xvzf ${KEYCLOAK_ADAPTER_FILE}
RUN rm ${KEYCLOAK_ADAPTER_FILE}
WORKDIR ${JBOSS_HOME}/bin
RUN ./jboss-cli.sh --file=adapter-install-offline.cli
ADD --chown=${JBOSS_USER} modules/ ${JBOSS_HOME}/modules/
ADD --chown=${JBOSS_USER} lib/ ${JBOSS_HOME}/standalone/lib/
WORKDIR ${JBOSS_HOME}/standalone/configuration
ADD --chown=${JBOSS_USER} ${DATASOURCES_FILE} ${DATASOURCES_FILE}
RUN sed -i '/<datasources>/,/<\/datasources>/d' standalone.xml
RUN sed -e '/<subsystem xmlns="urn:jboss:domain:datasources:5.0">/ {' -e "r ${DATASOURCES_FILE}" -e '}' -i standalone.xml
ADD --chown=${JBOSS_USER} ${SYSTEM_PROPS_FILE} ${SYSTEM_PROPS_FILE}
RUN sed -e '/<\/extensions>/ {' -e "r ${SYSTEM_PROPS_FILE}" -e '}' -i standalone.xml
ADD --chown=${JBOSS_USER} ${KEYCLOAK_FILE} ${KEYCLOAK_FILE}
RUN sed -e '/<subsystem xmlns="urn:jboss:domain:keycloak:1.1"\/>/ {' -e "r ${KEYCLOAK_FILE}" -e 'd' -e '}' -i standalone.xml
ADD --chown=${JBOSS_USER} ${LOGGING_FILE} ${LOGGING_FILE}
RUN sed -i '/<subsystem xmlns="urn:jboss:domain:logging:6.0">/,/<\/subsystem>/d' standalone.xml
RUN sed -e '/<profile>/ {' -e "r ${LOGGING_FILE}" -e '}' -i standalone.xml
RUN rm ${DATASOURCES_FILE} ${SYSTEM_PROPS_FILE} ${KEYCLOAK_FILE} ${LOGGING_FILE}
RUN rm -fr standalone_xml_history
ADD --chown=${JBOSS_USER} webapps/ ${JBOSS_HOME}/../webapps/
RUN mkdir /home/jboss/webapps/${APP_NAME}
RUN echo 'JAVA_OPTS="$JAVA_OPTS -agentlib:jdwp=transport=dt_socket,address=8787,server=y,suspend=n"' >> ${JBOSS_HOME}/bin/standalone.conf
RUN echo 'JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.trustStore=${JBOSS_HOME}/../webapps/config/keystores/truststore.jks -Djavax.net.ssl.trustStorePassword=tecnologies"' >> ${JBOSS_HOME}/bin/standalone.conf
COPY --chown=${JBOSS_USER} maven/${project.build.finalName}.ear ${JBOSS_HOME}/standalone/deployments/
RUN sed -i '/#!\/bin\/sh/a sudo chown jboss $JBOSS_HOME/standalone/log' ${JBOSS_HOME}/bin/standalone.sh
WORKDIR ${JBOSS_HOME}
EXPOSE 8080
EXPOSE 8787
