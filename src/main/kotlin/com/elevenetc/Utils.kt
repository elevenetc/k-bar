package com.elevenetc

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import java.util.regex.Pattern

@Throws(IOException::class)
fun execute(command: Command): List<String> {

    val params = LinkedList(Arrays.asList<String>(*command.params))
    params.add(0, command.cmd)

    val pb = ProcessBuilder(params).redirectErrorStream(true)
    val process = pb.start()
    return readOut(process, command.filter)
}

class Command {

    var cmd: String
    var params: Array<out String>
    var filter: String? = null

    constructor(cmd: String, vararg params: String) {
        this.cmd = cmd
        this.params = params
    }

    constructor(cmd: String) {
        this.cmd = cmd
        this.params = emptyArray()
    }

    fun withFiler(filter: String): Command {
        this.filter = filter
        return this
    }
}

private fun readOut(process: Process, filter: String?): List<String> {

    val result = LinkedList<String>()
    var pattern: Pattern? = null

    if (filter != null) {
        pattern = Pattern.compile(filter)
    }


    try {
        val br = BufferedReader(InputStreamReader(process.inputStream))

        for (line in br.lines())
            if (line.isNotEmpty())
                if (pattern != null) {
                    val matcher = pattern.matcher(line)
                    while (matcher.find()) {
                        result.add(matcher.group())
                    }
                } else {
                    result.add(line)
                }

        val success = process.waitFor()
        if (success != 0) {
            throw ProcessExecutionError(success, result)
        }

    } catch (e: InterruptedException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return result
}

internal class ProcessExecutionError(
    val exitCode: Int,
    val errorStream: List<String>
) : RuntimeException()


fun showNotification(title: String, message: String) {
    Thread(Runnable {
        try {
            val m = message.replace("\"", "'")
            execute(Command("osascript", "-e", "display notification \"$m\" with title \"$title\""))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }).start()
}