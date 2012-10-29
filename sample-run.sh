#!/bin/bash
rm out/test/*
rmdir out/test
rmdir out

export CLASSPATH=target/classes/:lib/*:import/
java com.astav.jsontojava.JsonToJava sample.json out test TestOutput regex-sample.json false

