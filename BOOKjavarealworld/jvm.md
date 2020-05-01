# Chapter 2: The Java Virtual Machine

## What is the Java Virtual Machine?
One of Java's selling points over the years has been its support for multiple platforms. For example, I could write and test my Java code on a Mac, then deploy it on a Windows server and expect it to run. The reason this works is because a Java Virtual Machine (or JVM) sits between your compiled code and the operating system, and it knows how to translate Java into native system calls. To be perfectly precise, this would be *an instance of a* JVM. The term can also refer to the specification or its implementation.

## JVM Versions
Periodically, the maintainers of Java make updates to the [JVM specification](https://docs.oracle.com/javase/specs/jvms/se8/html/). Naturally, this allows them to release new features and improvements. As of this writing, the most current production version is Java 1.9. For convenience, Java 1.9 is usually just called "Java 9", Java 1.8 is "Java 8", etc.

{icon=fast-backward}
G> ### Legacy Watch: Out-dated JVMs
G>
G> Unfortunately, some companies are slow to adopt technology. The financial industry in particular is known for being behind the curve. So while learning materials, blogs, news, etc. may assume you are using Java 8 or Java 9, you may actually be stuck using Java 7 or even Java 6.

It's important to know what version of the JVM your application will run under, so be sure to ask before you start any development. The version will determine what language features you can take advantage of. However, regardless of which version is used in production, you can usually install the latest version on your development machine. This is because Java is extremely backwards compatible (some would say to a fault), and you can run Java code written in 1.6 style on a 1.8 JVM, for example.

To try this out, here are two sample programs that do the same thing:

{title="NamesOld.java"}
<<(code/jvm/NamesOld.java)

{title="NamesNew.java"}
<<(code/jvm/NamesNew.java)

If you try to compile the programs targeting two different versions, this is what you should see:

|               | `javac -source 1.6` | `javac -source 1.8` |
|---------------|---------------------|---------------------|
|`NamesOld.java`| No errors           | No errors           |
|`NamesNew.java`| Two compiler errors | No errors           |

{icon=thumbs-o-down}
G> ### Java Wart: Backwards Compatibility
G> The decision by Java's maintainers to ensure compatibility across versions is perhaps one of the reasons why Java has remained so ubiquitous. Yet with that decision comes a lot of baggage. To name a few things:
G>
G> - Some methods in `java.util.Date` have been deprecated since 1997!
G> - For that matter, `java.util.Date` as a whole has largely been replaced by `java.time.*`.
G> - Generic types are only used for compile time checks. They are erased at runtime.
G> - The keywords `const` and `goto` are reserved but not implemented
G> - There are wrapper classes for all the primatives, e.g. `int` and `java.lang.Integer`, `boolean` and `java.lang.Boolean`, etc.
G> - The collections `Hashtable` and `Vector` should usually be avoided, instead preferring `HashMap` and `ArrayList`. If you need collections with concurrency benefits, you should look at the options in `java.util.concurrent`.

## JVM Varieties
It may be surprising to learn that Oracle actually has two JVM products: the Oracle JVM and the OpenJDK JVM. For almost all situations, it honestly does not matter which one you choose. I suggest using whichever is easier to install on your platform. However, you should be aware that the Oracle JVM has licensing restrictions not present in OpenJDK. If you have specific concerns, check with your company's legal department.

It is also worth noting that because the JVM specification is public, anyone can make their own JVM. In fact, some people and companies have done just that. To name a few there are IBM's J9, Azul's Zing, and Excelsior's JET. Third-party JVM implementations each have their own marketing pitch but it usually comes down to targeting different operating systems, improving performance, or adding features.

A> ### So what are those differences between the Oracle JVMs?
A> Not much. According to a blog on Oracle's website:
A>
A> > ...our build process for Oracle JDK releases builds on OpenJDK 7 by adding just a couple of pieces, like the deployment code, which includes Oracle's implementation of the Java Plugin and Java WebStart, as well as some closed source third party components like a graphics rasterizer, some open source third party components, like Rhino, and a few bits and pieces here and there, like additional documentation or third party fonts.
A>
A> \- Henrik Stahl "[Java 7 Questions & Answers](https://blogs.oracle.com/henrik/entry/java_7_questions_answers)" 11 August 2011
