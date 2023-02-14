import java.io.InputStream
import java.util.logging.LogManager
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.ParseException

fun main(args: Array<String>) {
    val stream: InputStream = FileOrganiser::class.java.classLoader.getResourceAsStream("logging.properties")
    LogManager.getLogManager().readConfiguration(stream)

    val parser: CommandLineParser = DefaultParser()
    val cmd: CommandLine = try {
        parser.parse(CliOptions().allOptions(), args)
    } catch (e: ParseException) {
        printHelp(e.message)
        return
    }

    FileOrganiser.Builder(
        inputFolderPath = cmd.getOptionValue(CliOptions().inputFolder),
        outputFolderPath = cmd.getOptionValue(CliOptions().outputFolder),
        tree = cmd.hasOption(CliOptions().tree),
        move = cmd.hasOption(CliOptions().move),
        replace = cmd.hasOption(CliOptions().replace)
    ).build().organise()
}

fun printHelp(mgs: String?) {
    val formatter = HelpFormatter()
    formatter.printHelp(mgs, CliOptions().allOptions())
}