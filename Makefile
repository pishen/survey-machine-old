compile: bin
	javac -d bin -cp "lib/*" src/pishen/dblp/*.java
bin:
	mkdir bin
runBG:
	java -cp "lib/*:bin" pishen/dblp/Main > log 2>&1 &
run:
	java -cp "lib/*:bin" pishen/dblp/Main
