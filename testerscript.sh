#!/bin/bash

testCommand = $(echo mrbean.txt guyfawkes.txt mugabe.txt brownie.txt linus.txt | java MyXargs.java -n 2 ls)
realCommand = $(echo mrbean.txt guyfawkes.txt mugabe.txt brownie.txt linus.txt | xargs -n 2 ls)

if ["$realCommand" == "$testCommand"]; then
    echo "passed -n 2 case";
else
    echo "failed -n 2";
fi

