# Java Version Checker

Java Version Checker (JVC for short) is a tool for analyzing `.jar` files and report minimum Java versions for compiled classes.

# License

This tool is released under the Apache License version 2.0, as stated in the `LICENSE` file.

# Building

`jvc` relies on Java 7 and Maven, clone this repository and run:

```bash
$ mvn assembly:assembly
$ cp target/jvc-1.0-jar-with-dependencies.jar jvc.jar
$ mvn clean
```

Then you will have `jvc.jar` in the main directory.

# Usage

Just double-click `jvc.jar` or run via terminal:

```bash
$ java -jar jvc.jar
```

Click the `Open` button and select your `.jar` file to be analyzed.

![Quack](http://i.imgur.com/mzmwJ7r.png)

Click the `Run` button and wait a couple of seconds.

![Quack](http://i.imgur.com/ttyOvD7.png)

Done, the analysis is complete. This tool requires `jar` and `javap` to be in your system path.
