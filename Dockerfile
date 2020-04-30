FROM ysihaoy/scala-play

COPY ["build.sbt", "/tmp/build/"]
COPY ["project/plugins.sbt", "project/build.properties", "/tmp/build/project/"]
RUN cd /tmp/build && \
 sbt compile && \
 sbt test:compile && \
 rm -rf /tmp/build

COPY . /root/app/
WORKDIR /root/ap
RUN sbt compile && sbt test:compile

EXPOSE 9000
CMD ["sbt"]