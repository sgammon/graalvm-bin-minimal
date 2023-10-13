package com.example

import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Engine
import org.graalvm.polyglot.PolyglotException
import org.graalvm.polyglot.Source
import picocli.CommandLine
import picocli.CommandLine.Command
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.system.exitProcess


@Command(name = "demo", description = ["..."],
    mixinStandardHelpOptions = true)
class DemoCommand : Runnable {
    private val engine: Engine = Engine.newBuilder("js")
        .allowExperimentalOptions(true)
        .option("compiler.Inlining", "true")
        .option("compiler.InlineAcrossTruffleBoundary", "true")
        .option("engine.PreinitializeContexts", "js")
        .option("engine.WarnOptionDeprecation", "false")
        .option("engine.CachePreinitializeContext", "true")
        .build()

    private val context: Context = Context.newBuilder()
        .engine(engine)
        .allowAllAccess(true)
        .allowExperimentalOptions(true)
        .option("js.ecmascript-version", "2022")
        .build()

    override fun run() {
        // business logic here
        val input = BufferedReader(InputStreamReader(System.`in`))
        val output = System.out

        val languages: Set<String> = context.engine.languages.keys
        output.println("Shell for $languages:")
        var language = languages.iterator().next()

        while (true) {
            try {
                output.print("$language> ")
                val line = input.readLine()
                if (line == null) {
                    break
                } else if (languages.contains(line)) {
                    language = line
                    continue
                }
                val source: Source = Source.newBuilder("js", line, "<shell>")
                    .interactive(true).buildLiteral()
                context.eval(source)
            } catch (t: PolyglotException) {
                if (t.isExit) {
                    break
                }
                t.printStackTrace()
            }
        }
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            exitProcess(CommandLine(DemoCommand()).execute(*args))
        }
    }
}
