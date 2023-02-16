package net.adamjak.tomas.files_organizer

import org.apache.commons.cli.Option
import org.apache.commons.cli.Options

data class CliOptions(
    val inputFolder: Option = Option.builder()
        .option("i")
        .longOpt("input-folder")
        .desc("Input folder")
        .hasArg()
        .required()
        .build(),
    val outputFolder: Option = Option.builder()
        .option("o")
        .longOpt("output-folder")
        .desc("Output folder")
        .hasArg()
        .required()
        .build(),
    val tree: Option = Option.builder()
        .option("t")
        .longOpt("tree")
        .desc("Organise file on tree structure like year folder, month folder and day folder")
        .hasArg(false)
        .build(),
    val move: Option = Option.builder()
        .option("m")
        .longOpt("move")
        .desc("Files will be moved not copied.")
        .hasArg(false)
        .build(),
    val replace: Option = Option.builder()
        .option("r")
        .longOpt("replace")
        .desc("Replace existing file with copied file.")
        .hasArg(false)
        .build()
) {
    fun allOptions() : Options {
        return Options()
            .addOption(this.inputFolder)
            .addOption(this.outputFolder)
            .addOption(this.tree)
            .addOption(this.move)
            .addOption(this.replace)
    }
}
