.PHONY: clean all test

RM     ?= rm -rf
CC     ?= $(shell which gcc)
CFLAGS ?= -g -Wall -Wformat=0 -fPIC -O0
SRC    ?= qtcreator/src

# Path to JDK include directories
JAVA_HOME ?= /usr/local/java
INCLUDES  := -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux

MYINCLUDES := qtcreator/src
TARGET     := target
JAVA       ?= $(shell which java)
JAVAC      ?= $(shell which javac)

LOG_FILE   ?= /tmp/threadwatcher.out
VERBOSE    ?= 

all: build
build: $(TARGET)/Test.class $(TARGET)/agent.so

$(TARGET)/agent.so: $(TARGET)/agent.o $(TARGET)/events.o $(TARGET)/threadlist.o $(TARGET)/writerthread.o $(TARGET)/pid.o $(TARGET)/config.o
	$(CC) -pthread -fPIC -shared -Wall  -Wl,--no-as-needed -lm -lrt -o $(TARGET)/agent.so $(TARGET)/agent.o $(TARGET)/events.o $(TARGET)/threadlist.o $(TARGET)/writerthread.o $(TARGET)/pid.o $(TARGET)/config.o

$(TARGET)/agent.o: $(SRC)/agent.c $(MYINCLUDES)/agent.h
	$(CC) -c -o $(TARGET)/agent.o $(SRC)/agent.c $(CFLAGS) ${INCLUDES}

$(TARGET)/events.o: $(SRC)/events.c $(MYINCLUDES)/events.h
	$(CC) -c -o $(TARGET)/events.o $(SRC)/events.c $(CFLAGS) ${INCLUDES}

$(TARGET)/writerthread.o: $(SRC)/writerthread.c $(MYINCLUDES)/writerthread.h
	$(CC) -c -o $(TARGET)/writerthread.o $(SRC)/writerthread.c $(CFLAGS) ${INCLUDES}

$(TARGET)/threadlist.o: $(SRC)/threadlist.c $(MYINCLUDES)/threadlist.h
	$(CC) -c -o $(TARGET)/threadlist.o $(SRC)/threadlist.c $(CFLAGS) ${INCLUDES}

$(TARGET)/pid.o: $(SRC)/pid.c $(MYINCLUDES)/pid.h
	$(CC) -c -o $(TARGET)/pid.o $(SRC)/pid.c $(CFLAGS) ${INCLUDES}

$(TARGET)/config.o: $(SRC)/config.c $(MYINCLUDES)/config.h
	$(CC) -c -o $(TARGET)/config.o $(SRC)/config.c $(CFLAGS) ${INCLUDES}

$(TARGET)/Test.class: java/Test.java
	$(JAVAC) java/Test.java -d target

test: $(TARGET)/Test.class $(TARGET)/agent.so
	$(JAVA) -Xss512k -agentpath:target/agent.so=file=${LOG_FILE},$(if ${VERBOSE},verbose=true,) -cp $(TARGET) Test

clean:
	$(RM) $(TARGET)/*.o $(TARGET)/*.so $(TARGET)/*.class

