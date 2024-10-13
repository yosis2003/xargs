#!/bin/bash


#works
echo mrbean.txt guyfawkes.txt mugabe.txt brownie.txt linus.txt | java MyXargs.java -n 2 ls
echo ====================================
echo | java MyXargs.java -n 2 ls
echo ====================================
#works
echo cpu/0/ | java MyXargs.java -I {} ls /dev/{}
echo ====================================
#works
echo os-release | java MyXargs.java -I {} cat /etc/{}
echo ====================================
#works
echo Today is $(date) | java MyXargs.java -t echo {}
echo ====================================
#works
echo file1.txt file2.txt file3.txt | java MyXargs.java -t rm {}
echo ====================================
echo | java MyXargs.java -t seq 5
echo ====================================




