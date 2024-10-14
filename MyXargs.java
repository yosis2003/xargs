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



    public static void parseXargsOptions(String[] args, IntegerWrapper maxArgs, StringWrapper replace, BooleanWrapper trace, BooleanWrapper noRunIfEmpty, IntegerWrapper commandStart)
    {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-n":
                    if (i + 1 < args.length) {
                        maxArgs.value = Integer.parseInt(args[++i]);
                    }
                    break;
                case "-I":
                    if (i + 1 < args.length) {
                        replace.value = args[++i];
                    }
                    break;
                case "-t":
                    trace.value = true;
                    break;
                case "-r":
                    noRunIfEmpty.value = true;
                    break;
                default:
                    commandStart.value = i;
                    break;
            }
            if (commandStart.value != -1) {
                break;
            }
        }

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final int MAX_ARGS = 100;
        IntegerWrapper maxArgs = new IntegerWrapper(MAX_ARGS);
        StringWrapper replace = new StringWrapper(null);
        BooleanWrapper trace = new BooleanWrapper(false);
        BooleanWrapper noRunIfEmpty = new BooleanWrapper(false);
        IntegerWrapper commandStart = new IntegerWrapper(-1);

        // Parse options

        parseXargsOptions(args, maxArgs, replace, trace, noRunIfEmpty, commandStart);



        if (commandStart.value < 0) {
            System.err.println("Usage: java MyXargs.java [-n num] [-I replace] [-t] [-r] command");
            return;
        }

        List<String> cmdList = new ArrayList<>();
        for (int i = commandStart.value; i < args.length; i++) {
            cmdList.add(args[i]);
        }
        System.out.println(cmdList);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        List<String> inputArgs = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.replaceAll("[;&|><*?()$]", "");
            inputArgs.addAll(Arrays.asList(line.split(" ")));
        }

        int totalArgs = inputArgs.size();
        int iterations = replace.value != null ? totalArgs : (totalArgs + maxArgs.value - 1) / maxArgs.value;


        for (int argi = 0; argi < iterations; argi++) {
            List<String> execList = new ArrayList<>(cmdList);

            if (replace.value != null) {
                for (int j = 0; j < execList.size(); j++) {
                    if (execList.get(j).contains(replace.value)) {
                        String replacement = execList.get(j).replace(replace.value, inputArgs.get(argi));
                        execList.set(j, replacement);
                    }
                }
            } else {
                for (int j = 0; j < maxArgs.value && (argi * maxArgs.value + j) < totalArgs; j++) {
                    String indexedArgument = inputArgs.get(argi * maxArgs.value + j);
                    if (!indexedArgument.trim().isEmpty())
                    {
                        execList.add(indexedArgument);
                    }
                    
                }
            }

            if (trace.value) {
                System.out.print("+");
                for (String arg : execList) {
                    System.out.print(" " + arg);
                }
                System.out.println();
            }

            if (execList.isEmpty() && noRunIfEmpty.value) {
                continue;
            }
            


            ProcessBuilder pb = new ProcessBuilder(execList);
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
        }
        
    }
}