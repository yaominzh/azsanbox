# Chapter 9: Logging

When Java was released, it did not include anything in the standard library for logging. Developers were stuck with `System.out.println` and `System.err.println`. And while some would argue [that's sufficient](https://12factor.net/logs), it's almost always better to have more control over your logs. As such, the Log4j framework became popular in the early 2000s. A few years later Java finally included a logging API (`java.util.logging` or JUL) in the standard library, but many people found it lacking and stuck with Log4j. Yet library developers found themselves in a difficult position trying to accommodate both Log4j users and JUL users.

The solution was to use logging facades, which led to the development of Apache Commons Logging and Simple Logging Facade (SLF4J). And in 2006, yet another framework, Logback, started to gain traction. (Side note: the same developer, Ceki Gulcu, wrote Log4j, SLF4J, and Logback!) While the effort to make logging in Java easy for developers is commendable, the result is a sea of possible combinations and headaches for something that should be extremely easy.

## `java.util.Logging`

Since it's usually a good idea to prefer a standard library implementation to a third-party implementation, using `java.util.Logging` or JUL is an obvious choice. To configure the logger, you need to create a `logging.properties` file *and* set a JVM property `java.util.logging.config.file` to the filepath for the configuration file. There are many configuration options depending on the log handler type. A log handler is a general category of log message destination, e.g. `FileHandler`, `SocketHandler`, etc. Each handler has different configuration options, which can be found in the respective JavaDoc (for example, [`FileHandler`](https://docs.oracle.com/javase/7/docs/api/java/util/logging/FileHandler.html)). Here is a sample configuration from the example code that sends log data to both a file and standard out.

{lang="text",title="logging.properties"}
<<(code/logging/jul/src/main/resources/logging.properties)

This file shows how different levels can be set for both packages and handlers. The root level is set to `WARN`, but anything under `com.letstalkdata` is set to `FINE`. This controls what log levels are allowed. Then we configure the settings for each handler: the `FileHandler` will show anything at or above `WARN`, while `ConsoleHandler` will show anything at or above `FINE` (for the allowed packages). This is a little confusing, so I recommend playing with the settings in the example code to see the results. For example, if you drop the root logger to `INFO`, you should see some Hibernate log statements.

Next you will need to create a `java.util.logging.Logger` for each class that should generate log messages. In order for the package-specific logging configuration to work, `Logger`s should be named the fully qualified class name.

For example:

{lang="java", title="OrderService.java"}
<<(code/logging/jul/src/main/java/com/letstalkdata/iscream/service/OrderService.java)

The JDK logging API is somewhat cumbersome due to its two main method types: `Logger.level(String message)` where *level* is one of `severe`, `warning`, `info`, etc. and `Logger.log(Level level, String message, Object[] parameters)`. Here are how the two styles are used:

{lang="java", title="OrderService.java", crop-start-line=22, crop-end-line=26, starting-line-number=22}
<<(code/logging/jul/src/main/java/com/letstalkdata/iscream/service/OrderService.java)

{lang="java", title="OrderMaker.java", crop-start-line=32, crop-end-line=42, starting-line-number=32}
<<(code/logging/jul/src/main/java/com/letstalkdata/iscream/OrderMaker.java)

If you run the example code you will see the log messages dump to the console and to a file. There's also an intentional failure so you can see what it looks like to log an exception.

## Log4j

One of the first third-party logging frameworks for Java was Log4j, and it still remains quite popular today. There are two main concepts in Log4j: appenders and loggers. An appender is something that log entries can be written to. For example `stdout`, a file, a database, etc. A logger produces log messages and it can send those messages to one or more appender.

Configuration for Log4j is done using XML, JSON, YAML, or a properties file. The structure is the same for each, but the syntax obviously differs. For the examples, I use YAML, but keep in mind that it could easily be translated to any of the other formats. The first section of the configuration sets overall properties. The most important thing here is the pattern saved in the `format` property.

{lang="yml", title="log4j2.yml", crop-start-line=1, crop-end-line=9, starting-line-number=1}
<<(code/logging/log4j2/src/main/resources/log4j2.yml)

I> ### Tell Me More: Log4j Patterns
I>
I> The patterns for Log4j are dizzying to look at, and it's usually not a good idea to write one from scratch. Instead, you should use your company's standard format for logging. However, if you need to make adjustments, consult the [Pattern Layout documentation](https://logging.apache.org/log4j/2.0/manual/layouts.html#PatternLayout).
I>
I> Warning: it's very easy to create expensive logging patterns! The documentation notes certain things that are expensive, such as the file, method, line, etc. of the log statement. Unless you really need these, avoid them. (Remember that a stack trace will give you fully qualified classes and line numbers for errors.)

Next, I define two appenders: a console appender that writes to `stdout` and a rolling file appender that writes to `iscream.log`. There are plenty more [types of appenders](https://logging.apache.org/log4j/2.0/manual/appenders.html) to suit your needs.

{lang="yml", title="log4j2.yml", crop-start-line=11, crop-end-line=26, starting-line-number=11}
<<(code/logging/log4j2/src/main/resources/log4j2.yml)

Finally, the loggers:

{lang="yml", title="log4j2.yml", crop-start-line=29, crop-end-line=48, starting-line-number=29}
<<(code/logging/log4j2/src/main/resources/log4j2.yml)

All Log4j files must define a root logger and usually define a custom logger for the project itself. The name of the logger is very important: if you follow the convention of naming loggers for their class you can control logging at the package level. This is extremely helpful if you need different layers of your application to log at different levels or if you want to enable debugging for third-party code. This specific configuration will direct debug (and above) statements from `com.letstalkdata` to `stdout`, warn (and above) statements from `com.letstalkdata` to `stdout` and `iscream.log`, and error (and above) statements from all packages to `stdout` and `iscream.log`. As with the JUL example, I recommend playing with these settings to learn how to adjust logging for your application.

In code, the logging will look similar to the JUL syntax. With Log4j we use the  
 `org.apache.logging.log4j.LogManager` to create `org.apache.logging.log4j.Logger`s.

{lang="java", title="OrderService.java", crop-start-line=15, crop-end-line=15, starting-line-number=15}
<<(code/logging/log4j2/src/main/java/com/letstalkdata/iscream/service/OrderService.java)

The syntax to write logs is a little more friendly. Methods exist for all of the log levels: trace, debug, info, warn, error, fatal. Furthermore, the methods are overloaded with options to log `Throwable`s and include parameterized log messages.

{lang="java", title="OrderService.java", crop-start-line=22, crop-end-line=26, starting-line-number=22}
<<(code/logging/log4j2/src/main/java/com/letstalkdata/iscream/service/OrderService.java)

{lang="java", title="OrderMaker.java", crop-start-line=31, crop-end-line=50, starting-line-number=31}
<<(code/logging/log4j2/src/main/java/com/letstalkdata/iscream/OrderMaker.java)

{icon=fast-backward}
G> ### Legacy Watch: Log4j 1
G>
G> In 2014, Log4j released a backwards incompatible version 2. It included many improvements, but because of compatibility, you're still likely to see legacy "Log4j1" in applications and examples. Migration is relatively simple, but logging tends to be an "it ain't broken don't fix it" situation. Most notably, Log4j1 configuration files look different. I have included a sample Log4j1 project in the example code that logs (almost) identically to the Log4j2 project.

## Logback

The [documentation for Logback](https://logback.qos.ch/index.html) starts off, "Logback is intended as a successor to the popular Log4j project". Despite the popularity of Log4j, many lessons were learned over the years and so the creators of Log4j decided to start a new project. Although it reached a 1.0.0 release in 2011, it is only within recent years that Logback has started gaining significant adoption.

Logback configuration can be done with a `logback.xml` file or a `logback.groovy` file. Just like Log4j, Logback has the concept of appenders and loggers, and much of the terminology is similar. Here is the configuration file that logs to the console and a rolling file:

{lang="xml", title="logback.xml"}
<<(code/logging/logback/src/main/resources/logback.xml)

The [patterns for Logback](https://logback.qos.ch/manual/layouts.html) are very similar for Log4j, although there is one very handy improvement. When using `%c`, you can specify a desired length and Logback will intelligently abbreviate packages where necessary to meet that desired length.

In a major departure from previous logging frameworks, Logback decided to not provide their own API for logging. Instead, they rely on the SLF4J facade API. This means that in code, you use a `org.slf4j.LoggerFactory` to create `org.slf4j.Logger`s. Since SLF4J is so widely used even outside of a Logback context, the next section will discuss it in detail.

## SLF4J

When developing a Java library, there is the problem of deciding how to do logging in a way that is both informative but not intrusive. Writing directly to STDOUT would be obnoxious, but choosing a logging provider forces consumers of the library to configure that provider. The solution is to use a logging facade.

Q> ### But what is it, *really*?
Q>
Q> When configured correctly, SLF4J is the front-end for all logging. Libraries and your client code send log messages to SLF4J using the SLF4J API. Then SLF4J *internally* sends log messages to the logging provider of your choice: Log4j, JUL, etc.

If you buy into using a logging facade, you'll have a somewhat steeper learning curve, but the end result is that your logging life will be much simpler. And thankfully, the SLF4J API is quite pleasant to use.

To get started with SLF4J, you will need to include a "binder" which does the actual work of translating between SLF4J and a logging provider. In the example code, I will use Log4j which means I include the `log4j-slf4j-impl` binder. If you're keeping track at home this means we now have four dependencies just to do logging! The SLF4J API `slf4j-api` talks to the binder `log4j-slf4j-impl` which sends log messages to Log4j `log4j-core` which depends on Jackson  `jackson-dataformat-yaml` for configuration.

If you read the section on Logback, you know that we use the SLF4J `LoggerFactory` to create logger instances.

{lang="java", title="OrderService.java", crop-start-line=15, crop-end-line=16, starting-line-number=15}
<<(code/logging/slf4j/src/main/java/com/letstalkdata/iscream/service/OrderService.java)

SLF4J uses five of the six levels available in Log4j: trace, debug, info, warn, and error. Like Log4j, there are methods for all of these levels and they are overridden with options to pass in parameters and/or a `Throwable`.

{lang="java", title="OrderService.java", crop-start-line=23, crop-end-line=27, starting-line-number=23}
<<(code/logging/slf4j/src/main/java/com/letstalkdata/iscream/service/OrderService.java)

{lang="java", title="OrderMaker.java", crop-start-line=31, crop-end-line=50, starting-line-number=31}
<<(code/logging/slf4j/src/main/java/com/letstalkdata/iscream/OrderMaker.java)

## Java Commons Logging (JCL)

As an alternative to SLF4J, Apache Java Commons Logging (JCL) is another facade framework to make logging easier. It is configured similarly to SLF4J in that you must include the link between the facade and the provider on the classpath. SLF4J called the link a "binder" and JCL calls it a "bridge". Again, we end up needing four actual dependencies: The JCL API `commons-logging` talks to the bridge `log4j-jcl` which sends log messages to Log4j `log4j-core` which depends on Jackson  `jackson-dataformat-yaml` for configuration.

We can then create `org.apache.commons.logging.Log`s from a  
`org.apache.commons.logging.LogFactory`.

{lang="java", title="OrderService.java", crop-start-line=15, crop-end-line=15, starting-line-number=15}
<<(code/logging/commons/src/main/java/com/letstalkdata/iscream/service/OrderService.java)

The six log levels in Log4j are available in JCL (including "fatal" which SLF4J drops). However, the logging methods are a bit more restrictive. There is no support for parameters, so you must prepare your log messages first.

{lang="java", title="OrderService.java", crop-start-line=22, crop-end-line=26, starting-line-number=22}
<<(code/logging/commons/src/main/java/com/letstalkdata/iscream/service/OrderService.java)

{lang="java", title="OrderMaker.java", crop-start-line=31, crop-end-line=50, starting-line-number=31}
<<(code/logging/commons/src/main/java/com/letstalkdata/iscream/OrderMaker.java)

## Summary

Trying to sort out all of the options for logging in Java can be frustrating, but hopefully there is already an established standard for your codebase. What's most important is that you understand the two approaches: directly using a provider or using a facade. Once you determine the approach used in your codebase, it's really only a matter of using the correct API.

## Suggested Resources

"Apache Log4j 2". Apache Logging Services, 2017.  
https://logging.apache.org/log4j/2.0/manual/index.html

"Apache Commons Logging". Apache Commons, 2014.  
https://commons.apache.org/proper/commons-logging/index.html

"Documentation". SLF4J, 2017. https://www.slf4j.org/docs.html.

"Java Logging Overview". Oracle, 2001.  
https://docs.oracle.com/javase/8/docs/technotes/guides/logging/overview.html

"Logback documentation". Logback, 2017.  
https://logback.qos.ch/documentation.html
