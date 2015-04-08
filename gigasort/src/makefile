

all: Gigasort.class
	jar cf gs.jar Gigasort*.class

Gigasort.class: Gigasort.java
	/usr/local/hadoop/bin/hadoop com.sun.tools.javac.Main Gigasort.java