# KotlinTerminal

Простая консольная утилита на Kotlin, имитирующая базовый командный интерпретатор (терминал) для работы с файловой
системой и выполнения вспомогательных операций прямо в REPL-режиме.

Проект создан как стандартный модуль IntelliJ IDEA без внешних систем сборки (Gradle/Maven) и использует только
стандартную библиотеку Kotlin и Java I/O / NIO.

---

## 1. Общее описание

Программа запускает бесконечный цикл ввода-вывода (REPL). При старте терминал определяет текущую рабочую директорию
пользователя, выводит приглашение ко вводу и ожидает команды.

Команда разбивается на имя и аргументы по пробелам, после чего вызывается соответствующий обработчик. Работа
продолжается до тех пор, пока пользователь не введет команду завершения работы.

---

## 2. Описание функций и команд

### Встроенные команды

- **`ls`** - заглушка (в будущем будет реализация)
- **`cd`** - заглушка
- **`exit`** - завершает выполнение программы и закрывает сессию терминала.

### Настройки

Вся конфигурация задается внутри исходного кода (`src/main/kotlin/Main.kt`):

- Формат приглашения командной строки (промпт, например `user@terminal:path$ `).
- Кодировка вывода (по умолчанию UTF-8).
- Флаги обработки ошибок (перехват исключений ввода-вывода `IOException` без аварийного падения процесса).

---

## 3. Сборка и запуск

Проект организован как чистый проект IntelliJ IDEA.

### Запуск через IntelliJ IDEA

1. Откройте папку `KotlinTerminal` в IntelliJ IDEA.
2. Убедитесь, что в настройках проекта задан JDK (File → Project Structure → Project → SDK — версия 17 или новее).
3. Перейдите к файлу `src/main/kotlin/Main.kt`.
4. Нажмите зеленую кнопку **Run** рядом с функцией `fun main()`.

### Сборка и запуск через консоль (Kotlin CLI)

Если установлен компилятор `kotlinc`:

1. Скомпилируйте файл в исполняемый JAR:
```bash
kotlinc src/main/kotlin/Main.kt -include-runtime -d KotlinTerminal.jar
```

2. Запустите собранный архив:

```bash
java -jar KotlinTerminal.jar
```

### Запуск тестов

Пока не реализовано

---

## 4. Примеры использования

Пример стандартной интерактивной сессии:

```text
KotlinTerminal v1.0

shohorovandrey@Unknown:~$ ld
неизвестная команда
shohorovandrey@Unknown:~$ ls
неверные аргументы
shohorovandrey@Unknown:~$ ls root
ls: [root]
shohorovandrey@Unknown:~$ cd root cd cd ls ld
cd: [root, cd, cd, ls, ld]
shohorovandrey@Unknown:~$ cd $PATH
cd: [/Users/shohorovandrey/.kimi-code/bin:/Users/shohorovandrey/.local/bin:/opt/homebrew/bin:/opt/homebrew/sbin:/Library/Frameworks/Python.framework/Versions/3.13/bin:/usr/local/bin:/System/Cryptexes/App/usr/bin:/usr/bin:/bin:/usr/sbin:/sbin:/var/run/com.apple.security.cryptexd/codex.system/bootstrap/usr/local/bin:/var/run/com.apple.security.cryptexd/codex.system/bootstrap/usr/bin:/var/run/com.apple.security.cryptexd/codex.system/bootstrap/usr/appleinternal/bin:/pkg/env/global/bin:/Users/shohorovandrey/.docker/bin:/Users/shohorovandrey/Library/Application Support/JetBrains/Toolbox/scripts]
shohorovandrey@Unknown:~$ exit ddd
неверные аргументы
shohorovandrey@Unknown:~$ exit
Завершение работы терминала

Process finished with exit code 0
```
