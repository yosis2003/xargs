/*
 * echo mrbean.txt guyfawkes.txt mugabe.txt brownie.txt linus.txt | java MyXargs.java -n 2 ls
 * the above test should display mr.bean.txt and guyfawkes.txt on their own line, then mugabe and brownie, and then linus
 * 
 * echo | java MyXargs.java -n 2 ls
 * the above test should simply display the contents of your current working directory
 * 
 * echo cpu/0/ | java MyXargs.java -I {} ls /dev/{}
 * the above test should just display the word 'msr' without the quotes
 * 
 * echo os-release | java MyXargs.java -I {} cat /etc/{}
 * the above test should display information about the linux distro you are running
 * 
 * echo Today is $(date) | java MyXargs.java -t echo {}
 * should print something like this: 
 * + echo {} Today is Mon Oct 14 01:35:33 AM PDT 2024
{} Today is Mon Oct 14 01:35:33 AM PDT 2024
 * 
 * 
 * echo file1.txt file2.txt file3.txt | java MyXargs.java -t rm {}
 * the above test should return errors
 * 
 * 
 * echo | java MyXargs.java -t seq 5
 * above test should return:
 * 
 * seq 5
1
2
3
4
5
 *  
 * java MyXargs.java -r
 * above test should return the usage text
 * 
 * seq 100 | java MyXargs.java -n 5 touch
 * above test should create a series of a hundred extensionless files named from 1-100
 * 
 * seq 100 | java MyXargs.java -n 100 rm
 * above test should remove a series of a hundred extensionless files named from 1-100
 * 
 * seq 100 | java MyXargs.java -I {} touch {}.txt
 * above test should create a series of a hundred .txt extensions files named from 1-100
 * 
 * seq 100 | java MyXargs.java -I {} rm {}.txt
 * above test should remove a series of a hundred .txt extensions files named from 1-100
 * 
 * touch compareFile0.txt && echo "Hell" > compareFile0.txt
touch compareFile1.txt && echo "Hello" > compareFile1.txt
touch compareFile2.txt && echo "The industrial revolution and its consequences" > compareFile2.txt
touch compareFile3.txt && echo "LMAO ZEDONG" > compareFile3.txt
echo compareFile0.txt compareFile1.txt compareFile2.txt compareFile3.txt | java MyXargs.java -n 2 diff

the long test above should return:

1c1
< Hell
---
> Hello
1c1
< The industrial revolution and its consequences
---
> LMAO ZEDONG
 * 
 * 
 * echo compareFile0 compareFile1 compareFile2 compareFile3 | java MyXargs.java -I {} rm {}.txt
 * this test should remove the .txt files previously created
 * 
 * 
 * echo / | java MyXargs.java -r ls
 * this test should display the root directory's contents
 * 
 * echo 1 | java MyXargs.java -r seq 1
 * displays the number 1
 */







import java.io.*;
import java.util.*;

class StringWrapper
{
    public String value;

    public StringWrapper(String value)
    {
        this.value = value;
    }
}
class IntegerWrapper
{
    public Integer value;

    public IntegerWrapper(Integer value)
    {
        this.value = value;
    }
}
class BooleanWrapper
{
    public Boolean value;

    public BooleanWrapper(Boolean value)
    {
        this.value = value;
    }
}



public class MyXargs {

    private static void parseXargsOptions(String[] args, IntegerWrapper xargsCommandLengthLimit, StringWrapper replaceToken, BooleanWrapper trace, BooleanWrapper noRunIfEmpty, IntegerWrapper commandStart)
    {
        for (int i = 0; i < args.length; i++) 
        {
            switch (args[i]) 
            {
                case "-n" -> 
                {
                    if (i + 1 < args.length) 
                    {
                        xargsCommandLengthLimit.value = Integer.parseInt(args[++i]);
                    }
                }
                case "-I" -> 
                {
                    if (i + 1 < args.length) {
                        replaceToken.value = args[++i];
                    }
                }
                case "-t" -> trace.value = true;
                case "-r" -> noRunIfEmpty.value = true;
                default -> commandStart.value = i;
            }
            if (commandStart.value != -1) 
            {
                break;
            }
        }
    }
    private static void correctUsageMessagePrinter(IntegerWrapper commandStart)
    {
        if (commandStart.value < 0) 
        {
            System.err.println("Usage: java MyXargs.java [-n num] [-I replace] [-t] [-r] command");
            System.exit(0);
        }
    }
    private static void traceOptionExecutioner(BooleanWrapper trace, List<String> execList)
    {
        if (trace.value) 
        {
            System.out.print("+");
            for (String arg : execList) 
            {
                System.out.print(" " + arg);
            }
            System.out.println();
        }
    }

    private static void startCommandExecutionerProcess (List<String> execList) throws IOException, InterruptedException
    {
        ProcessBuilder pb = new ProcessBuilder(execList);
        pb.inheritIO();
        Process process = pb.start();
        process.waitFor();
    }
    private static int loopRangeDeterminer(int numberOfTokensInCommandline, IntegerWrapper xargsCommandLengthLimit, StringWrapper replaceToken)
    {
        return (replaceToken.value != null ? numberOfTokensInCommandline : (numberOfTokensInCommandline + xargsCommandLengthLimit.value - 1) / xargsCommandLengthLimit.value);
    }
    // I purposely named this method this way to indicate to the next user that this thing might need more work
    private static void logicAndExecutionLoop (StringWrapper replaceToken, IntegerWrapper xargsCommandLengthLimit, List<String> commandlineArgumentList, BooleanWrapper trace, BooleanWrapper noRunIfEmpty, List<String> xargsCmdList) throws IOException, InterruptedException
    {
        int numberOfTokensInCommandline = commandlineArgumentList.size();
        int iterations = loopRangeDeterminer(numberOfTokensInCommandline, xargsCommandLengthLimit, replaceToken);

        for (int argi = 0; argi < iterations; argi++) 
        {
            List<String> execList = new ArrayList<>(xargsCmdList);

            if (replaceToken.value != null) 
            {
                for (int j = 0; j < execList.size(); j++) 
                {
                    if (execList.get(j).contains(replaceToken.value)) 
                    {
                        String replacement = execList.get(j).replace(replaceToken.value, commandlineArgumentList.get(argi));
                        execList.set(j, replacement);
                    }
                }
            }
            else 
            {
                for (int j = 0; j < xargsCommandLengthLimit.value && (argi * xargsCommandLengthLimit.value + j) < numberOfTokensInCommandline; j++) 
                {
                    String currentIndexedArgument = commandlineArgumentList.get(argi * xargsCommandLengthLimit.value + j);
                    if (!currentIndexedArgument.trim().isEmpty())
                    {
                        execList.add(currentIndexedArgument);
                    }                    
                }
            }
            traceOptionExecutioner(trace, execList);
            if (execList.isEmpty() && noRunIfEmpty.value) 
            {
                continue;
            }
            startCommandExecutionerProcess(execList);
        }

    }

    public static void main(String[] args) throws IOException, InterruptedException 
    {
        final int MAX_ARGS_FOR_XARGS = 100;
        IntegerWrapper xargsCommandLengthLimit = new IntegerWrapper(MAX_ARGS_FOR_XARGS);
        StringWrapper replaceToken = new StringWrapper(null);
        BooleanWrapper trace = new BooleanWrapper(false);
        BooleanWrapper noRunIfEmpty = new BooleanWrapper(false);
        IntegerWrapper commandStart = new IntegerWrapper(-1);

        parseXargsOptions(args, xargsCommandLengthLimit, replaceToken, trace, noRunIfEmpty, commandStart);
        correctUsageMessagePrinter(commandStart);

        List<String> xargsCmdList = new ArrayList<>();
        for (int i = commandStart.value; i < args.length; i++) 
        {
            xargsCmdList.add(args[i]);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        List<String> commandlineArgumentList = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) 
        {
            line = line.replaceAll("[;&|><*?()$]", "");
            commandlineArgumentList.addAll(Arrays.asList(line.split(" ")));
        }

        logicAndExecutionLoop(replaceToken, xargsCommandLengthLimit, commandlineArgumentList, trace, noRunIfEmpty, xargsCmdList);
        
    }
}