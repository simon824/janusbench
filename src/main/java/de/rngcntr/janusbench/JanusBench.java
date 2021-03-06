package de.rngcntr.janusbench;

import java.io.IOException;
import java.util.concurrent.Callable;

import de.rngcntr.janusbench.subcommands.ListSubcommand;
import de.rngcntr.janusbench.subcommands.RunSubcommand;
import de.rngcntr.janusbench.util.ExitCode;
import de.rngcntr.janusbench.util.ResultLogger;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * The main class of <code>janusbench</code>.
 * This class handles command line interactions of <code>janusbench</code> calls.
 *
 * @author Florian Grieskamp
 */
@Command(name = "janusbench", subcommands = {RunSubcommand.class, ListSubcommand.class},
         mixinStandardHelpOptions = true,
         versionProvider = de.rngcntr.janusbench.util.VersionProvider.class)
public class JanusBench implements Callable<Integer> {

    public Integer call() throws Exception {
        CommandLine.usage(new JanusBench(), System.out);
        return ExitCode.INVALID_INPUT;
    }

    public static void main(final String[] args) throws IOException {
        int exitCode = new CommandLine(new JanusBench()).execute(args);

        ResultLogger.getInstance().close();
        System.exit(exitCode);
    }
}
