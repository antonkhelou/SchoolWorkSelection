#!/bin/bash
# Check for input IP, if none set use localhost
if [ $# -eq 0 ]; then 
	IP="127.0.0.1"
else
	IP=$1
fi

# Launch a 100 clients in the background
for i in {1..100}
do
	./client $IP &
done
