#!/bin/bash


#works
echo mrbean.txt guyfawkes.txt mugabe.txt brownie.txt linus.txt | java MyXargs.java -n 2 ls
echo ====================================
#works
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
#works
echo | java MyXargs.java -t seq 5
echo ====================================
#works
echo java MyXargs.java -r
echo ====================================
#works
echo "file creation"
seq 100 | java MyXargs.java -n 5 touch
sleep 5
echo ====================================
echo "file deletion"
seq 100 | java MyXargs.java -n 100 rm
echo ====================================
echo "file creation with .txt"
seq 100 | java MyXargs.java -I {} touch {}.txt
sleep 5
echo ====================================
echo "file deletion with .txt"
seq 100 | java MyXargs.java -I {} rm {}.txt
echo ====================================
touch compareFile0.txt && echo "Hell" > compareFile0.txt
touch compareFile1.txt && echo "Hello" > compareFile1.txt
touch compareFile2.txt && echo "The industrial revolution and its consequences" > compareFile2.txt
touch compareFile3.txt && echo "LMAO ZEDONG" > compareFile3.txt
sleep 2
echo compareFile0.txt compareFile1.txt compareFile2.txt compareFile3.txt | java MyXargs.java -n 2 diff 
echo ====================================
echo removing test files
echo compareFile0 compareFile1 compareFile2 compareFile3 | java MyXargs.java -I {} rm {}.txt
echo ====================================
echo / | java MyXargs.java -r ls
echo ====================================
echo 1 | java MyXargs.java -r seq 1
echo ====================================
echo TESTSOVER!

