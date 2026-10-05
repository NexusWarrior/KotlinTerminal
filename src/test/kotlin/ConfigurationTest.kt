import kotlin.test.Test
import kotlin.test.assertEquals

class ConfigurationTest {

    /**
     * Проверяет разбор параметра VFS.
     */
    @Test
    fun `should parse vfs argument`() {
        val config = parseCommandLineArguments(
            arrayOf("--vfs", "data/vfs"),
        )

        assertEquals("data/vfs", config.vfsPath)
        assertEquals(null, config.scriptPath)
    }

    /**
     * Проверяет разбор параметра стартового скрипта.
     */
    @Test
    fun `should parse script argument`() {
        val config = parseCommandLineArguments(
            arrayOf("--script", "scripts/startup.txt"),
        )

        assertEquals(null, config.vfsPath)
        assertEquals("scripts/startup.txt", config.scriptPath)
    }

    /**
     * Проверяет разбор обоих параметров.
     */
    @Test
    fun `should parse both arguments`() {
        val config = parseCommandLineArguments(
            arrayOf(
                "--vfs",
                "data/vfs",
                "--script",
                "scripts/startup.txt",
            ),
        )

        assertEquals("data/vfs", config.vfsPath)
        assertEquals("scripts/startup.txt", config.scriptPath)
    }
}