- Actualitzar versió SCSP
---------------------------
- Actualització repositori local:
	mvn install:install-file -Dfile=scsp-core-X.X.X.jar -DgroupId=es.scsp -DartifactId=scsp-core -Dversion=X.X.X -Dpackaging=jar -DlocalRepositoryPath=local-repo
	mvn install:install-file -Dfile=scsp-beans-X.X.X.jar -DgroupId=es.scsp -DartifactId=scsp-beans -Dversion=X.X.X -Dpackaging=jar -DlocalRepositoryPath=local-repo
- Canviar versió scsp al pom.xml.
- Revisar applicationContext.xml
	- emiserv-war/src/main/webapp/WEB-INF/applicationContext.xml
- Per evitar traces bug reflections a JBoss:
   <category name="org.reflections.Reflections">
      <priority value="ERROR"/>
   </category>
