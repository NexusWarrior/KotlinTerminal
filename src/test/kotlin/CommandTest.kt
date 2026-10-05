import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.Test
import kotlin.test.assertEquals

class CommandTest {

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
            "неизвестная команда: unknown\n",
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
            """
                ls
                -la
                /Users/test
                """.trimIndent() + "\n",
            output,
        )
    }

    /**
     * Перехватывает вывод в стандартный поток.
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