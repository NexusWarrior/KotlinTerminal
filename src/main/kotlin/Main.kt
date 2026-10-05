/**
 * Список команд, поддерживаемых терминалом.
 *
 * Каждая команда должна быть обработана в [executeCommand].
 */
val COMMAND_LIST = listOf("ls", "cd", "exit")

/**
 * Параметр командной строки для указания пути к VFS.
 */
const val VFS_ARGUMENT = "--vfs"

/**
 * Параметр командной строки для указания пути к стартовому скрипту.
 */
const val SCRIPT_ARGUMENT = "--script"

/**
 * Хранит конфигурацию эмулятора.
 *
 * @property vfsPath путь к физическому расположению VFS.
 * @property scriptPath путь к стартовому скрипту.
 */
data class AppConfig(
    val vfsPath: String?,
    val scriptPath: String?,
)

/**
 * Результат выполнения команды терминала.
 */
enum class CommandResult {
    SUCCESS,
    ERROR,
    EXIT,
}

/**
 * Формирует приветственное сообщение терминала
 * с именем текущего пользователя и именем компьютера.
 *
 * Имя пользователя получается из системного свойства `user.name`.
 * Имя компьютера сначала ищется в переменной окружения `COMPUTERNAME`,
 * затем в `HOSTNAME`. Если обе переменные отсутствуют, используется
 * значение `"Unknown"`.
 *
 * @return строка приветствия в формате `username@hostname:~$ `
 */
fun getWelcomeMessage(): String {
    val username = System.getProperty("user.name")
    val hostName = System.getenv("COMPUTERNAME") ?: System.getenv("HOSTNAME") ?: "Unknown"

    return "$username@$hostName:~$ "
}

/**
 * Разбирает пользовательский ввод на отдельные токены.
 *
 * Строка сначала очищается от пробелов в начале и конце,
 * после чего разбивается по одному или нескольким пробельным символам.
 *
 * Если токен начинается с символа $, он рассматривается как имя
 * переменной окружения. Символ $ удаляется, после чего выполняется
 * поиск значения переменной через [System.getenv].
 *
 * Если переменная окружения не существует, такой токен удаляется
 * из результирующего списка.
 *
 * Например:
 *
 * ```kotlin
 * parseUserInput("ls -la")
 * // ["ls", "-la"]
 *
 * parseUserInput("echo $HOME")
 * // ["echo", "/home/user"]
 * ```
 *
 * @param line строка, введённая пользователем в терминале.
 * @return список разобранных токенов без пустых элементов.
 */
fun parseUserInput(line: String): List<String> {
    val trimmed = line.trim()
    if (trimmed.isEmpty()) return listOf()

    return trimmed
        .split("\\s+".toRegex())
        .mapNotNull { token ->
            if (token.startsWith("$") && token.length > 1) {
                val envKey = token.substring(1)
                System.getenv(envKey)
            } else {
                token
            }
        }
}

/**
 * Выполняет команду терминала.
 *
 * @param command имя команды.
 * @param args аргументы команды.
 * @return результат выполнения команды.
 */
fun executeCommand(
    command: String,
    args: List<String>,
): CommandResult =
    when (command) {
        "ls" -> {
            if (args.isEmpty()) {
                println("неверные аргументы")
                CommandResult.ERROR
            } else {
                println("ls")
                args.forEach(::println)
                CommandResult.SUCCESS
            }
        }

        "cd" -> {
            if (args.size == 1) {
                println("cd: [${args.first()}]")
                CommandResult.SUCCESS
            } else {
                println("неверные аргументы")
                CommandResult.ERROR
            }
        }

        else -> {
            println("неизвестная команда: $command")
            CommandResult.ERROR
        }
    }

/**
 * Разбирает параметры командной строки.
 *
 * Поддерживаемые параметры:
 * `--vfs <path>` — путь к VFS.
 * `--script <path>` — путь к стартовому скрипту.
 *
 * @param args аргументы командной строки.
 * @return конфигурация эмулятора.
 * @throws IllegalArgumentException если параметр неизвестен
 * или отсутствует его значение.
 */
fun parseCommandLineArguments(args: Array<String>): AppConfig {
    var vfsPath: String? = null
    var scriptPath: String? = null

    var index = 0

    while (index < args.size) {
        when (args[index]) {
            VFS_ARGUMENT -> {
                if (index + 1 >= args.size) {
                    throw IllegalArgumentException(
                        "для параметра $VFS_ARGUMENT не указано значение",
                    )
                }

                vfsPath = args[index + 1]
                index += 2
            }

            SCRIPT_ARGUMENT -> {
                if (index + 1 >= args.size) {
                    throw IllegalArgumentException(
                        "для параметра $SCRIPT_ARGUMENT не указано значение",
                    )
                }

                scriptPath = args[index + 1]
                index += 2
            }

            else -> {
                throw IllegalArgumentException(
                    "неизвестный параметр: ${args[index]}",
                )
            }
        }
    }

    return AppConfig(
        vfsPath = vfsPath,
        scriptPath = scriptPath,
    )
}

/**
 * Выполняет одну строку пользовательского ввода.
 *
 * @param input строка, введённая пользователем.
 * @return результат выполнения команды.
 */
fun executeInput(input: String): CommandResult {
    val parseInput = parseUserInput(input)

    if (parseInput.isEmpty()) {
        return CommandResult.SUCCESS
    }

    val command = parseInput.first()
    val args = parseInput.subList(1, parseInput.size)

    if (command == "exit") {
        if (args.isEmpty()) {
            println("Завершение работы терминала")
            return CommandResult.EXIT
        }

        println("неверные аргументы")
        return CommandResult.ERROR
    }

    return executeCommand(command, args)
}

/**
 * Выполняет команды из стартового скрипта.
 *
 * Пустые строки и строки, начинающиеся с `//`, игнорируются.
 * Перед каждой командой выводится приглашение терминала.
 *
 * @param scriptPath путь к стартовому скрипту.
 * @return `true`, если скрипт выполнен без ошибок.
 */
fun executeStartupScript(scriptPath: String): Boolean {
    var hasErrors = false

    java.io.File(scriptPath).useLines { lines ->
        for (line in lines) {
            val input = line.trim()

            if (input.isEmpty() || input.startsWith("//")) {
                continue
            }

            println("${getWelcomeMessage()}$input")

            when (executeInput(input)) {
                CommandResult.SUCCESS -> Unit

                CommandResult.ERROR -> {
                    println("Ошибка выполнения стартового скрипта: $input")
                    hasErrors = true
                }

                CommandResult.EXIT -> {
                    return@useLines
                }
            }
        }
    }

    return !hasErrors
}

/**
 * Запускает эмулятор терминала.
 *
 * @param args параметры командной строки.
 */
fun main(args: Array<String>) {
    val config = parseCommandLineArguments(args)

    println("Конфигурация эмулятора:")
    println("vfs=${config.vfsPath ?: "не задан"}")
    println("script=${config.scriptPath ?: "не задан"}")

    if (config.scriptPath != null) {
        val scriptSucceeded = executeStartupScript(config.scriptPath)

        if (!scriptSucceeded) {
            println("Стартовый скрипт завершён с ошибками.")
        }
    }

    while (true) {
        print(getWelcomeMessage())

        val input = readlnOrNull() ?: break

        if (executeInput(input) == CommandResult.EXIT) {
            break
        }
    }
}