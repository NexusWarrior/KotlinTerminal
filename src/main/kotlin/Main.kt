/**
 * Список команд, поддерживаемых терминалом.
 *
 * Каждая команда должна быть обработана в [executeCommand].
 */
val COMMAND_LIST = listOf("ls", "cd", "exit")

fun main() {
    while (true) {
        print(getWelcomeMessage())
        val input = readlnOrNull() ?: break

        val parseInput = parseUserInput(input)
        if (parseInput.isEmpty()) continue

        val cmd = parseInput.first()
        val cmdArgs = parseInput.subList(1, parseInput.size)

        if (cmd == "exit") {
            if (cmdArgs.isEmpty()) {
                println("Завершение работы терминала")
                break
            } else {
                println("неверные аргументы")
            }
            continue
        }

        executeCommand(cmd, cmdArgs)
    }
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
 * Выполняет указанную команду с переданными аргументами.
 *
 * Если команда отсутствует в [COMMAND_LIST], выводится сообщение
 * о неизвестной команде.
 *
 * Если команда известна, но аргументы отсутствуют, выводится сообщение
 * о неверных аргументах.
 *
 * В остальных случаях команда и её аргументы выводятся в консоль.
 *
 * @param cmd имя выполняемой команды.
 * @param cmdArgs список аргументов, переданных команде.
 */
fun executeCommand(
    cmd: String,
    cmdArgs: List<String>,
) {
    when {
        cmd !in COMMAND_LIST -> print("неизвестная команда")
        cmdArgs.isEmpty() -> print("неверные аргументы")
        else -> print("$cmd: $cmdArgs")
    }

    println()
}