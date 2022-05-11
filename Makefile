.PHONY: all build run clean

MVN  ?= $(shell which mvn)
JAVA ?= $(shell which java)

AGENT      := ./agent
TARGET     := ./target
AGENT_FILE := ${AGENT}/target/agent.so
JAR_FILE   := ${TARGET}/threadwatcher.jar
LOG_FILE   := ${TARGET}/watch_result.log

all: build
build: ${JAR_FILE} ${AGENT_FILE}

${JAR_FILE}:
	$(MVN) package

${AGENT_FILE}:
	$(MAKE) -C ${AGENT} build

${LOG_FILE}:
	$(MAKE) -C ${AGENT} test LOG_FILE="$(shell readlink -f ${LOG_FILE})"

run: ${JAR_FILE} ${LOG_FILE}
	$(JAVA) -jar "${JAR_FILE}" "${LOG_FILE}"

clean:
	$(MAKE) -C ${AGENT} clean
	$(MVN) clean

