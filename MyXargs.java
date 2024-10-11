import java.io.*;
import java.util.*;

public class MyXargs {

    public static void main(String[] args) throws IOException, InterruptedException {
        final int MAX_ARGS = 100;
        int maxArgs = MAX_ARGS;
        String replace = null;
        boolean usesI = false, trace = false, noRunIfEmpty = false;
        int commandStart = -1;

        // Parse options
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-n":
                    if (i + 1 < args.length) {
                        maxArgs = Integer.parseInt(args[++i]);
                    }
                    break;
                case "-I":
                    if (i + 1 < args.length) {
                        replace = args[++i];
                        usesI = true;
                    }
                    break;
                case "-t":
                    trace = true;
                    break;
                case "-r":
                    noRunIfEmpty = true;
                    break;
                default:
                    commandStart = i;
                    break;
            }
            if (commandStart != -1) {
                break;
            }
        }

        if (commandStart < 0) {
            System.err.println("Usage: java MyXargs.java [-n num] [-I replace] [-t] [-r] command");
            return;
        }

        List<String> cmdList = new ArrayList<>();
        for (int i = commandStart; i < args.length; i++) {
            cmdList.add(args[i]);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        List<String> inputArgs = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.replaceAll("[;&|><*?()$]", "");
            inputArgs.addAll(Arrays.asList(line.split(" ")));
        }

        int totalArgs = inputArgs.size();
        int iterations = replace != null ? totalArgs : (totalArgs + maxArgs - 1) / maxArgs;

        for (int argi = 0; argi < iterations; argi++) {
            List<String> execList = new ArrayList<>(cmdList);

            if (replace != null) {
                for (int j = 0; j < execList.size(); j++) {
                    if (execList.get(j).equals(replace)) {
                        execList.set(j, inputArgs.get(argi));
                    }
                }
            } else {
                for (int j = 0; j < maxArgs && (argi * maxArgs + j) < totalArgs; j++) {
                    execList.add(inputArgs.get(argi * maxArgs + j));
                }
            }

            if (trace) {
                System.out.print("+");
                for (String arg : execList) {
                    System.out.print(" " + arg);
                }
                System.out.println();
            }

            if (execList.isEmpty() && noRunIfEmpty) {
                continue;
            }

            ProcessBuilder pb = new ProcessBuilder(execList);
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
        }
    }
}