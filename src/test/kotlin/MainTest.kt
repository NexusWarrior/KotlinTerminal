package tests

import COMMAND_LIST
import executeCommand
import getWelcomeMessage
import parseUserInput
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MainTest {

    /**
     * Проверяет обработку пустой строки и строки, содержащей только пробелы.
     */
    @Test
    fun `should return empty list for blank input`() {
        assertTrue(parseUserInput("").isEmpty())
        assertTrue(parseUserInput("   ").isEmpty())
    }

    /**
     * Проверяет разделение команды и аргумента по пробелу.
     */
    @Test
    fun `should split input by whitespace`() {
        val result = parseUserInput("ls file.txt")

        assertEquals(
            listOf("ls", "file.txt"),
            result,
        )
    }

    /**
     * Проверяет удаление пробелов в начале и конце входной строки.
     */
    @Test
    fun `should trim input before parsing`() {
        val result = parseUserInput("   ls   file.txt   ")

        assertEquals(
            listOf("ls", "file.txt"),
            result,
        )
    }

    /**
     * Проверяет обработку команды с несколькими аргументами.
     */
    @Test
    fun `should parse multiple arguments`() {
        val result = parseUserInput("ls -la /Users/test")

        assertEquals(
            listOf("ls", "-la", "/Users/test"),
            result,
        )
    }

    /**
     * Проверяет разделение аргументов при использовании табуляции и нескольких пробелов.
     */
    @Test
    fun `should split input with tabs and multiple spaces`() {
        val result = parseUserInput("ls\t\t-la    file.txt")

        assertEquals(
            listOf("ls", "-la", "file.txt"),
            result,
        )
    }

    /**
     * Проверяет сохранение обычных аргументов без специальных символов.
     */
    @Test
    fun `should preserve normal arguments`() {
        val result = parseUserInput("cd /Users/andrew")

        assertEquals(
            listOf("cd", "/Users/andrew"),
            result,
        )
    }

    /**
     * Проверяет замену существующей переменной окружения.
     */
    @Test
    fun `should replace existing environment variable`() {
        val home = System.getenv("HOME")

        if (home != null) {
            val result = parseUserInput($$"$HOME")

            assertEquals(
                listOf(home),
                result,
            )
        }
    }

    /**
     * Проверяет замену переменной окружения внутри списка аргументов.
     */
    @Test
    fun `should replace environment variable inside multiple arguments`() {
        val home = System.getenv("HOME")

        if (home != null) {
            val result = parseUserInput($$"cd $HOME")

            assertEquals(
                listOf("cd", home),
                result,
            )
        }
    }

    /**
     * Проверяет удаление символа '$' после успешной замены переменной окружения.
     */
    @Test
    fun `should remove dollar sign before environment variable name`() {
        val home = System.getenv("HOME")

        if (home != null) {
            val result = parseUserInput($$"$HOME")

            assertTrue(result.first() != $$"$HOME")
            assertEquals(home, result.first())
        }
    }

    /**
     * Проверяет обработку неизвестной переменной окружения.
     */
    @Test
    fun `should ignore unknown environment variable`() {
        val result = parseUserInput($$"$VARIABLE_THAT_DOES_NOT_EXIST")

        assertTrue(result.isEmpty())
    }

    /**
     * Проверяет наличие имени текущего пользователя в приветственном сообщении.
     */
    @Test
    fun `should contain current username in welcome message`() {
        val username = System.getProperty("user.name")

        val result = getWelcomeMessage()

        assertContains(result, username)
    }

    /**
     * Проверяет формат окончания строки приглашения терминала.
     */
    @Test
    fun `should contain terminal prompt`() {
        val result = getWelcomeMessage()

        assertTrue(result.endsWith(":~$ "))
    }

    /**
     * Проверяет наличие всех поддерживаемых команд.
     */
    @Test
    fun `should contain all supported commands`() {
        assertEquals(
            listOf("ls", "cd", "exit"),
            COMMAND_LIST,
        )
    }

    /**
     * Проверяет сообщение об ошибке для неизвестной команды.
     */
    @Test
    fun `should print unknown command message`() {
        val output = captureOutput {
            executeCommand("unknown", listOf("arg"))
        }

        assertEquals(
            "неизвестная команда\n",
            output,
        )
    }

    /**
     * Проверяет сообщение об ошибке при отсутствии аргументов.
     */
    @Test
    fun `should print invalid arguments message`() {
        val output = captureOutput {
            executeCommand("ls", emptyList())
        }

        assertEquals(
            "неверные аргументы\n",
            output,
        )
    }

    /**
     * Проверяет вывод команды и переданных аргументов.
     */
    @Test
    fun `should print command and arguments`() {
        val output = captureOutput {
            executeCommand(
                "ls",
                listOf("-la", "/Users/test"),
            )
        }

        assertEquals(
            "ls: [-la, /Users/test]\n",
            output,
        )
    }

    /**
     * Перехватывает вывод в стандартный поток для проверки результата функции.
     */
    private fun captureOutput(block: () -> Unit): String {
        val output = ByteArrayOutputStream()
        val originalOut = System.out

        System.setOut(PrintStream(output))

        try {
            block()
        } finally {
            System.setOut(originalOut)
        }

        return output.toString()
    }
}