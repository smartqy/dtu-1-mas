package searchclient;

import searchclient.cbs.algriothem.CBSRunner;
import searchclient.cbs.model.AppContext;
import searchclient.cbs.model.Environment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class NewSearchClient {

    public static void main(String[] args) throws IOException {
        // Use stderr to print to the console.
        System.err.println("SearchClient initializing. I am sending this using the error output stream.");

        // Send client name to server.
        System.out.println("GHandDirt");

        // We can also print comments to stdout by prefixing with a #.
        System.out.println("#This is a comment.");

        // Provide information about who we are when asked
        System.out.println("#I am a Conflict-Based Search (CBS) client for solving multi-agent pathfinding problems.");
        System.out.println("#I can run in two modes: Basic CBS or MA-CBS (Meta-Agent CBS) when a parameter is provided.");
        System.out.println("#I find optimal collision-free paths for multiple agents in shared environments.");

        String levelFile = "/Users/blackbear/Desktop/dtu/semester1/course/Mas/searchclient/complevels/pooh.lvl";
        BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.US_ASCII));
//        BufferedReader serverMessages = new BufferedReader(new FileReader(levelFile));
        Environment environment = Environment.parseLevel(serverMessages);
        AppContext.init(environment);

        int superB = -1;//watch dog for Max
        if (args.length > 0) {
            superB = Integer.parseInt(args[0]);
            System.err.printf("Parameter of B provided = %d. MA-CBS.\n", superB);
        } else {
            System.err.println("No parameter of B provided. Defaulting to Basic CBS.");
        }

        if (args.length > 1) {
            boolean isEPEA = Boolean.parseBoolean(args[1]);
            environment.setEPEA(isEPEA);
            System.err.printf("Parameter of EPEA provided = %s.\n", isEPEA);
        } else {
            System.err.println("No parameter of EPEA provided. Defaulting to false as Weighted A*.");
        }

        // Search for a plan.
        Action[][] plan = null;
        CBSRunner cbsRunner = new CBSRunner();
        boolean timeout = false;
        try {
            plan = cbsRunner.findSolution(superB);
        } catch (OutOfMemoryError ex) {
            System.err.println("Maximum memory usage exceeded.");
            ex.printStackTrace();
        } catch (TimeoutException e) {
            timeout = true;
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        // Print plan to server.
        if (plan == null) {
            System.err.println("Unable to solve level.");
            System.err.printf("Aborting by time out: %s\n", timeout ? "yes" : "no");
            System.exit(0);
        } else {
            System.err.format("Found solution of length %,d.\n", plan.length);
//            for (int i = 0; i < plan.length; i++) {
//                System.err.format("Step Num %d: ", i);
//                for (int j = 0; j < plan[i].length; j++) {
//                    System.err.format("%s|", plan[i][j].name);
//                }
//                System.err.println();
//            }

            for (Action[] jointAction : plan) {
                System.out.print(jointAction[0].name + "@" + jointAction[0].name);
                for (int action = 1; action < jointAction.length; ++action) {
                    System.out.print("|");
                    System.out.print(jointAction[action].name);
                }
                System.out.println();
                // We must read the server's response to not fill up the stdin buffer and block the server.
                serverMessages.readLine();
            }
        }
    }
}
