import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException

fun main(args: Array<String>) {
    val parser : CommandLineParser = DefaultParser()
    val cmd : CommandLine = try {
        parser.parse(baseOptions(), args)
    } catch (e: ParseException) {
        printHelp(e.message)
        return
    }

    FileOrganiser.Builder(
        inputFolderPath = cmd.getOptionValue("i"),
        outputFolderPath = cmd.getOptionValue("o"),
        tree = cmd.hasOption("tf")
    )
}

fun baseOptions() : Options {
    return Options()
        .addOption(Option.builder().option("i").longOpt("input-folder").hasArg().required().desc("Input folder").build())
        .addOption(Option.builder().option("o").longOpt("output-folder").hasArg().required().desc("Output folder").build())
        .addOption("tf", "tree-folders", false, "Organise file on tree structure like year folder, month folder and day folder")
}

fun printHelp(mgs: String?) {
    val formatter = HelpFormatter()
    formatter.printHelp(mgs, baseOptions())
}