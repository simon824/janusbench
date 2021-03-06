ARG VERSION


FROM alpine/git
WORKDIR /foundationdb
RUN git clone -b master https://github.com/rngcntr/janusgraph-foundationdb .
# Patch http:// to https:// in pom.xml
RUN sed -i 's/http:/https:/g' pom.xml


FROM maven:3.5-jdk-8-alpine
WORKDIR /foundationdb
COPY --from=0 /foundationdb /foundationdb
RUN mvn package -DskipTests


FROM janusgraph/janusgraph:${VERSION}

ENV JANUS_STORAGE_TIMEOUT=30
ENV janusgraph.graph.replace-instance-if-exists=true

COPY --from=1 /foundationdb/target/janusgraph-foundationdb-0.2.0-distribution.zip /foundationdb/janusgraph-fdb.zip
RUN unzip /foundationdb/janusgraph-fdb.zip -d /foundationdb
WORKDIR /foundationdb/janusgraph-foundationdb-0.2.0
RUN /foundationdb/janusgraph-foundationdb-0.2.0/install.sh ${JANUS_HOME}
WORKDIR ${JANUS_HOME}

COPY --chown=janusgraph:janusgraph src/log4j-server.properties ${JANUS_HOME}/conf/gremlin-server/
COPY --chown=janusgraph:janusgraph src/gremlin-server.yaml ${JANUS_HOME}/conf/gremlin-server/gremlin-server.yaml
COPY --chown=janusgraph:janusgraph src/initialize-server.groovy /opt/initialize-server.groovy

COPY --chown=janusgraph:janusgraph src/foundationdb-clients_6.2.7-1_amd64.deb /data/foundationdb-clients.deb
RUN dpkg -i /data/foundationdb-clients.deb
