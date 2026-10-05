import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StartupScriptTest {

    /**
     * Проверяет выполнение команд стартового скрипта
     * и игнорирование комментариев.
     */
    @Test
    fun `should execute commands from startup script and ignore comments`() {
        val script = createStartupScript(
            """
            // Комментарий
            cd test

            // Ещё один комментарий
            cd another
            """.trimIndent(),
        )

        val output = captureOutput {
            executeStartupScript(script)
        }

        assertTrue(output.contains("cd: [test]"))
        assertTrue(output.contains("cd: [another]"))
        assertFalse(output.contains("Комментарий"))
        assertFalse(output.contains("Ещё один комментарий"))

        deleteScript(script)
    }

    /**
     * Проверяет сообщение об ошибке при выполнении
     * некорректной команды в стартовом скрипте.
     */
    @Test
    fun `should report error during startup script execution`() {
        val script = createStartupScript(
            """
            unknown
            cd test
            """.trimIndent(),
        )

        var result = true

        val output = captureOutput {
            result = executeStartupScript(script)
        }

        assertFalse(result)
        assertTrue(
            output.contains(
                "Ошибка выполнения стартового скрипта: unknown",
            ),
        )
        assertTrue(output.contains("cd: [test]"))

        deleteScript(script)
    }

    /**
     * Создаёт временный стартовый скрипт.
     *
     * @param content содержимое скрипта.
     * @return путь к созданному файлу.
     */
    private fun createStartupScript(content: String): String {
        val script = kotlin.io.path.createTempFile(
            prefix = "startup",
            suffix = ".txt",
        )

        script.toFile().writeText(content)

        return script.toString()
    }

    /**
     * Удаляет временный стартовый скрипт.
     *
     * @param scriptPath путь к скрипту.
     */
    private fun deleteScript(scriptPath: String) {
        java.io.File(scriptPath).delete()
    }

    /**
     * Перехватывает вывод в стандартный поток.
     *
     * @param block код, вывод которого необходимо перехватить.
     * @return перехваченный вывод.
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