compile: bin
	javac -d bin -cp "lib/*" src/pishen/dblp/*.java src/pishen/exception/*.java
bin:
	mkdir bin
log:
	java -cp "lib/*:bin" pishen/dblp/Main 2>&1 | tee logfile
run:
	java -cp "lib/*:bin" pishen/dblp/Main
