
CLASSNAME=GigaSort

all: ${CLASSNAME}.class
	jar cf gs.jar ${CLASSNAME}*.class

${CLASSNAME}.class: ${CLASSNAME}.java
	/usr/local/hadoop/bin/hadoop com.sun.tools.javac.Main ${CLASSNAME}.java

clean:
	rm *.class gs.jar
