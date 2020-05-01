{mainmatter}

# Chapter 1: Getting Started

## Who this book is for

As the title suggests, this is book is for people who are using Java in a business setting. From my experience, learning the Java ecosystem is nearly as arduous a task as learning the language. For experienced programmers, learning the language is probably barely a challenge compared to learning the ecosystem. However, while there are a great number of tools to learn the Java language, few resources exist to introduce people to the ecosystem. This book is your guide to the frameworks, tools, and libraries used to build professional Java software.

If you are a recent graduate or a self-taught programmer looking to break into the industry, this book will give you the real world knowledge hiring managers are interested in. It's unlikely that you'll ever have to write a sorting algorithm, but you can bet you're going to encounter a Spring MVC web application that uses Hibernate for persistence. On the other hand, if you are already a professional developer, you already understand the concepts, and are more likely to ask yourself, "How does Java do...?"

What this book does *not* do is teach you Java! I assume that you have a good working knowledge of the standard library. If you do need to learn Java, I can recommend *Head First Java* (O'Reilly, 2005) by Kathy Sierra and Bert Bates, followed by a more recent book that dives into Java 8.

If you're ready to learn how to work with enterprise Java applications, forge on ahead.

## How to use this book

Each chapter of this book focuses on a general concept and, to some extent, the chapters build on each other. So if you have the time, I recommend reading from beginning to end. However, if you are on a deadline, you can skip chapters that aren't relevant to you.

I find that explanations only go so far, so this book places a heavy emphasis on code. Relevant code is included in the text, but for brevity, some boiler plate is omitted. You can always find complete working projects in the [public GitHub repository for this book](https://github.com/phillipjohnson/java-for-the-real-world-code).

Code referenced from an example will look like this:

{lang="java", title="OrderService.java", starting-line-number=20, crop-start-line=20, crop-end-line=26}
<<(code/databases/hibernate-xml/src/main/java/com/letstalkdata/iscream/service/OrderService.java)

Other code used for discussion or demonstration will look like this:

{lang="java", line-numbers="off"}
~~~~~~~~
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
~~~~~~~~

References to code or commands that appear in-line will `look like this`.

As you have probably noticed from the size of this book, it is not a complete manual on any of the tools that I discuss! In many cases, there are entire books written on the tools themselves. It's my goal to give you a brief overview of the tool and the most common ways it is used. If you want to learn more, I include a list of resources at the end of each chapter for further reading.

Sprinkled throughout the book you'll find the following asides.

Q> ### But what is it, *really*?
Q>
Q> Framework developers love to use fancy words to describe their tools. For example, when you read, "Apache Maven is a software project management and comprehension tool," you still don't really know what it is. Look for these asides to quickly learn what a tool *actually does*.

{icon=thumbs-o-down}
G> ### Java Wart
G>
G> Java is a relatively old language and is known for being particularly backwards compatible. As such, there are many traditions that feel outdated and some large swaths of the standard library that have been deprecated. I refer to these unfortunate blemishes as "warts".

{icon=fast-backward}
G> ### Legacy Watch
G>
G> A Legacy Watch is a warning to be aware of something that you're more likely to encounter in legacy applications. For brand new projects, you should try to avoid anything I mention in a Legacy Watch.

{icon=fast-forward}
G> ### Hipster Watch
G>
G> A Hipster Watch is the opposite of a Legacy Watch. It alerts you to something that is relatively new in the Java world and may not yet have widespread adoption. This isn't necessarily bad, just something to be aware of.

I> ### Tell Me More
I>
I> These sections will give you a bit more detailed information about something mentioned in the main text. They aren't critical, but may be worth reading if something piqued your interest.

## Setting up Your Workspace

### Installing Java

There's many options to installing Java depending on your operating system and preferences.

**[Homebrew](https://brew.sh) (macOS)**: `brew cask install java`

**[Chocolatey](https://chocolatey.org) (Windows)**: `choco install jdk9`

**Apt-Get (Linux)**: `sudo apt-get install default-jdk`

**[SDKMAN!](http://sdkman.io) (Unix-like)**: `sdk install java`

**Official installer (all)**: Go to [Oracle's website](http://www.oracle.com/technetwork/java/javase/downloads/index.html) and follow the instructions there. Make sure you install the JDK, not the JRE. Be sure you understand the licensing agreement of the Oracle JDK if you choose this path (more on this in the next chapter).

These tools *should* put Java on your `PATH`, but to be sure, run `java -version` at the command line. You should see something like this:

{lang="text", line-numbers="off"}
~~~~~~~~
java version "1.8.0_131"
Java(TM) SE Runtime Environment (build 1.8.0_131-b11)
Java HotSpot(TM) 64-Bit Server VM (build 25.131-b11, mixed mode)
~~~~~~~~

You may also want to set the `JAVA_HOME` environment variable as many tools that use Java will check there first. This also lets you install multiple versions of Java with `JAVA_HOME` pointing towards your primary version, which is particularly useful when testing a new release of Java.

### IDEs

The three most common Java IDEs are [Eclipse](http://www.eclipse.org), [IntelliJ](https://www.jetbrains.com/idea/), and [Netbeans](https://netbeans.org). I am particularly fond of IntelliJ because of its rich featureset and excellent integration with dozens of frameworks. However, of the three, it is the only one that offers a pay-for version. The free community edition is pretty good and it is what I use at home, although I highly recommend the pay-for Ultimate edition for work projects.

You may find some companies require developers to use the same IDE due to custom plugins or settings. In general, I consider this a red flag. Since Java code can technically be built from the command line, it should not matter what IDE is used to write the code.
