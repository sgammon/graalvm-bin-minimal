package com.example;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(name = "sample", mixinStandardHelpOptions = true, version = "sample 4.0",
        description = "Prints a sample message")
class DemoCommandJ implements Callable<Integer> {
    @Option(names = {"-v", "--verbose"})
    private Boolean verbose = false;

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        if (this.verbose) {
            System.out.print("Hi!");
        }
        return 0;
    }

    // this example implements Callable, so parsing, error handling and handling user
    // requests for usage help or version help can be done with one line of code.
    public static void main(String... args) {
        int exitCode = new CommandLine(new DemoCommandJ()).execute(args);
        System.exit(exitCode);
    }
}