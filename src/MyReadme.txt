Build "client" and "server" dir under this dir

rmiregistry &
javac -d client *.java
javac -d server *.java
java -classpath client FileClient 127.0.0.1
java -classpath . -Djava.rmi.server.codebase=file:./ FileServerImpl