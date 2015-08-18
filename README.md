Connect Four
============
This is an extremely basic Connect Four app made for a computer science course. It's licensed as public domain, so do whatever you very well please with it (except plagiarize it as your own work, because that's not cool).

Also, it uses jansi for colored console text. If you don't want to have to shade the library, replace instances of `AnsiConsole` with `System`,
and replace final globals beginning with `ANSI` with empty strings (or remove the declarations and references entirely).

Compiling
---------
This app uses Gradle to manage compilation. To build:

1. `git clone https://github.com/mproncace/ConnectFour`
2. `cd ConnectFour`
3. `./gradlew` (Linux/Unix/OS X) or `gradlew` (Windows)
