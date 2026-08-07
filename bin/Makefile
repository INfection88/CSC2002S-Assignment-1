JAVAC = javac
JAVA = java
JFLAGS = --release 11 -d bin
SOURCES = FirelineSerial.java FireMap.java TerrainType.java FirelineParallel.java
ARGS ?= 300 300 42 wildfire output/fireline

.PHONY: all run clean

all:
	mkdir -p bin
	$(JAVAC) $(JFLAGS) $(SOURCES)

run-parallel: all
	$(JAVA) -cp bin FirelineParallel $(ARGS)

run-serial: all
	$(JAVA) -cp bin FirelineSerial $(ARGS)


clean:
	rm -rf bin output
