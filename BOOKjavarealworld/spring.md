# Chapter 5: Spring

Perhaps no collection of tools is more well-known in the Java world than Spring. What started out as a tool to make wiring up application components more easy has grown into a variety of frameworks including frameworks for building web applications, accessing data, rapid application development, microservice configuration, mobile development, and others.

At the center of all the Spring tools is the Spring Core. We'll spend the bulk of this chapter learning the basics of Spring Core, and then explore a modern, opinionated Spring framework called Spring Boot. However, there's much more to the Spring toolkit. In later chapters, we'll discuss Spring JDBC and Spring MVC.

## Spring Core

### Dependency Injection

The term "dependency injection" (DI) is a classic example of applying complex words to a simple concept. Spring's documentation says:

> [Dependency Injection] is a process whereby objects define their dependencies, that is, the other objects they work with, only through constructor arguments, arguments to a factory method, or properties that are set on the object instance after it is constructed or returned from a factory method.[^spring-di1]

Clear as mud, right? In any application you have objects that rely on another objects to function. For example, in our IScream application, we've seen that the `Application` requires a `DailySpecialService`. We can say that `Application` *depends on* `DailySpecialService`. So far we have been creating the service explicitly in code. Spring will automatically create, or *inject*, the dependency.

Q> ### But what is it, *really*?
Q>
Q> The Spring Core manages all of the objects in your application that depend on other objects. Instead of you creating and configuring your objects manually in code, Spring automatically does it for you at runtime.

Of course with all of the hand waving comes tradeoffs. Developers can go overboard in their reliance on Spring creating complex dependency trees. Furthermore, since the dependencies are managed largely outside of code, it can sometimes make it *more* difficult to understand the application. Finally, beware that dependency injection errors often do not surface until runtime. With no assistance from the compiler, you may find yourself struggling to determine why a particular component is not properly injected.

#### XML vs. Annotations

As with many Java frameworks, when Spring was first created, it used XML for configuration. This decision was notoriously fraught. Although there are some checks to ensure that an XML document is valid, there are numerous things that cannot be checked at compile time leading to many frustrating runtime errors. Furthermore, XML documents can become long, verbose, and unwieldy artifacts that are difficult to read. In Spring 3, annotations were introduced allowing applications to be configured without XML. Annotations trade some (but not all) runtime errors for compile-time errors, and can help identify how classes are used in-line with the code. I will show both the XML and annotation styles, but keep in mind that they are not mutually exclusive. You may choose to use both XML and annotations for different purposes.

##### XML-Based Approach

To modify the IScream application to work with Spring, we're going to create a `DailySpecialPrinter` that depends on a `DailySpecialService.`

{lang="java", title="DailySpecialPrinter.java"}
<<(code/spring/core-xml/src/main/java/com/letstalkdata/iscream/DailySpecialPrinter.java)

{icon=thumbs-o-down}
G> ### Java Wart: Getters and Setters
G>
G> The pattern of having a `private` class property with `public` accessors is pervasive in most Java codebases. The "official" reason for this is that by relying on getter and setter methods, you do not have to worry about the underlying implementation changing. But in reality, getter and setter methods rarely do anything other than the "get" or "set". Perhaps more annoyingly, they make classes fully mutable.
G>
G> So why do we keep creating them? The best reason I have is that many frameworks expect them. The Spring XML approach will try to use setters to inject dependencies. (It's possible to use constructors, but it takes more work for the developer.) We'll see more frameworks that also expect getters and setters, although some frameworks will automatically fall back to finding class properties if accessors aren't present.

To add Spring to the project using XML, we're going to add a file that is named `applicationContext.xml` by convention. If you're using Maven or Gradle, it should go in `src/main/resources`. This file defines "beans" which you can think of as the components of your application.

{lang="xml", title="applicationContext.xml"}
<<(code/spring/core-xml/src/main/resources/applicationContext.xml)

I> ### Tell Me More: Java Beans
I>
I> A JavaBean is a Java object that meets a set of certain requirements, with the intention of being reused in or across applications. The requirements are that the class must:
I>
I> * Have a `public` zero-argument constructor
I> * Expose its properties via getters and setters
I> * Be serializable (i.e. implement `java.io.Serializable`)
I>
I> Some frameworks require JavaBeans or at least work best with JavaBeans. Although Spring calls its managed objects beans, it is flexible on the requirements. Thus a Spring "bean" may not be a true JavaBean.

The first bean we define is the service. The `id` is any name you choose and the `class` is, of course, the type of object you want to create.

{lang="xml", title="applicationContext.xml", starting-line-number=7, crop-start-line=7, crop-end-line=8}
<<(code/spring/core-xml/src/main/resources/applicationContext.xml)

Next, we create the Printer which includes a `property` in the definition. The `name` of the property must match the name in the actual Java class (i.e. `dailySpecialService`) and the `ref` refers to another bean in the XML file (i.e. `dailySpecialServiceBean`).

{lang="xml", title="applicationContext.xml", starting-line-number=10, crop-start-line=10, crop-end-line=13}
<<(code/spring/core-xml/src/main/resources/applicationContext.xml)

The last step to prod Spring into configuring the application is to get a reference to the Application Context in the `main` method and create the Printer using the context. This is referred to as "bootstrapping".

{lang="java", title="Application.java"}
<<(code/spring/core-xml/src/main/java/com/letstalkdata/iscream/Application.java)

Using Spring's `getBean` method is usually not the correct thing to do in an application--you should configure Spring to create the objects automatically. But if you think of the dependencies in an application as a tree, we still need something to create the root object. Therefore, it's common practice to use `getBean` once in `main` for bootstrapping.

Now if you run the application, it will work automatically even though you never explicitly created a `DailySpecialService`!

##### Annotation-Based Approach

Just as we did with the XML method, we will create a `DailySpecialPrinter` that depends on a `DailySpecialService.` Instead of using `applicationContext.xml` we'll use the annotations `@Component` and `@Autowired`. Putting `@Component` on a class means it will be detected by Spring. The `@Autowired` annotation is used to mark something that you want Spring to inject. Here's the annotated Printer:

{lang="java", title="DailySpecialPrinter.java"}
<<(code/spring/core-annotation/src/main/java/com/letstalkdata/iscream/DailySpecialPrinter.java)

You could also autowire a setter method:

{lang="java", line-numbers="off"}
~~~~~~~~
private DailySpecialService dailySpecialService;

@Autowired
public void setDailySpecialService(DailySpecialService service) {
    this.dailySpecialService = service;
}
~~~~~~~~

...or a field:

{lang="java", line-numbers="off"}
~~~~~~~~
@Autowired
private DailySpecialService dailySpecialService;
~~~~~~~~

As a guideline, Spring recommends that you use constructor injection for dependencies that are required and setter methods for dependencies that are optional.[^spring-di2] So what about field injection? It has some problems. By using a field injector you are *requiring* a class to be created using Spring. However, there is a time and place for everything and I admit that the cleanliness of field-injected properties is sometimes a big asset. Just keep in mind that too many dependencies can indicate design issues. For a more in-depth discussion, see ["Why field injection is evil"](http://olivergierke.de/2013/11/why-field-injection-is-evil/).

Since we want Spring to inject the `DailySpecialService`, we need to make the class visible to Spring. As discussed, this can be done with the `@Component` annotation. However, I prefer to use the `@Service` annotation. It makes no functional difference, but is a semantic marker for the class that is more specific than just `@Component`.

{lang="java", line-numbers="off"}
~~~~~~~~
@Service
public class DailySpecialService {
  // Class body omitted for brevity
}
~~~~~~~~

Finally we need to adjust the `Application` class. As with the XML approach, we need to bootstrap Spring, but we will use a different flavor of `ApplicationContext`: the `AnnotationConfigApplicationContext` (get used to long class names in Spring!). We also need to annotate the class with `@ComponentScan` which tells Spring to start at this class and descend recursively through the packages to find classes that Spring can instantiate, i.e. those annotated `@Component` or `@Service` (or one of the other Spring Component annotations).

{lang="java", title="Application.java"}
<<(code/spring/core-annotation/src/main/java/com/letstalkdata/iscream/Application.java)

Now you can run the application and, again, it works without explicitly creating a `DailySpecialService`.

### Properties

It's often a good idea to externalize some values of your application outside of the code. This can make configuration and deployment much easier, especially if you need to maintain multiple instances of an application that must be configured differently.

Let's imagine for our application that we want to run it in a User Acceptance Testing (UAT) environment that is connected to a special test web service, while at the same time maintain the production application that points to the production web service. That means we can no longer hardcode the URL in the `DailySpecialService`.

We create the following files somewhere on the file system:

{lang="text", title="application.uat.properties", line-numbers="off"}
<<(code/spring/core-properties/src/main/config/application.uat.properties)

{lang="text", title="application.prod.properties", line-numbers="off"}
<<(code/spring/core-properties/src/main/config/application.prod.properties)

I> ### Tell Me More: `.properties` files
I>
I> Plain text files ending in `.properties` are commonly used to configure Java applications or frameworks that require only minimal setup. They contain simple key-value pairs and most parsers will intelligently recognize strings, numbers, and booleans. By convention, dots are used to separate words. So, for example, a file may contain `app.directory.input`, `app.directory.output`, `app.db.username`, `app.db.password`, etc.

Now we need to tell Spring how to access those files. We can do this with the `@PropertySource` annotation on the `Application` (or a `PropertyPlaceholderConfigurer` in the XML file, if using XML).

{lang="java", title="Application.java", starting-line-number=8, crop-start-line=8, crop-end-line=10}
<<(code/spring/core-properties/src/main/java/com/letstalkdata/iscream/Application.java)

Remember that we need our application to work in multiple environments. To keep us from constantly recompiling the code, we can make the application dynamically access its properties. Spring has an expression language that allows for string interpolation. This lets us make some sections of the file path dynamic. Spring will look for Java System properties or environment variables for `propPath` and `env` and replace `${propPath}` and `${env}` in the file path. Next, `@PropertySource` will read the file and load it into the Spring context, making the properties available to the application. This is extremely powerful--you now have the ability to configure an application on the fly without recompilation!

To make use of the application properties, you can use Spring's `@Value` annotation, or reference them directly in the `property` of a bean in the XML file. Here's how to access the URL in the `DailySpecialService`:

{lang="java", title="DailySpecialService.java", starting-line-number=23, crop-start-line=23, crop-end-line=27}
<<(code/spring/core-properties/src/main/java/com/letstalkdata/iscream/service/DailySpecialService.java)

Again, we use the Spring expression language to refer to the specific application property we want to use. Just as objects can be injected via field, constructor, or setter method, so can properties. The same guidelines apply, so it's generally best to put your `@Value` annotations in the constructor.

After setting the `propPath` and `env` variables, you can run the application. For testing, I find it easiest to configure my IDE to pass in the Java System (or "VM") properties (e.g. `-Denv=uat`).

I> ### Tell Me More: System Properties
I>
I> When starting the JVM, it is possible to include certain variables that remain set for the entire duration the JVM is running. This is done with the syntax `java -Dkey=value` where `key` and `value` are the actual property name and value. These are known as "System Properties" or "System Variables". Some properties are defined by the JVM specification, but custom properties can be set too.

## Spring Boot

The Spring group has done a wonderful job making sure that their components play nicely together. However, developers have a knack of making things more difficult and it's not uncommon to come across tortured `applicationContext.xml` files. Following the Spring guidance helps, but it can still be laborious to correctly configure a Spring application, even with annotations.

In 2014 the Spring team released Spring Boot which is an opinionated tool designed for rapid application development with the Spring platform. It favors convention over configuration and makes it very easy to skip the rigmarole of creating a new application.

Spring Boot applications are built from one or more "starters" which are included in a Maven or Gradle dependency list. For example a web application that communicates with an LDAP server would use `spring-boot-starter-web` and `spring-boot-starter-data-ldap`. A complete list of starters can be found on the [Spring Boot GitHub repository](https://github.com/spring-projects/spring-boot/tree/master/spring-boot-starters).

### Running a Spring Boot Application

Like any Java program, the entry to a Spring Boot program is a class containing a `main` method. However, it requires some special configuration for it to run using Spring Boot.

First, the class is annotated `@SpringBootApplication`. This is a handy shortcut annotation that combines `@Configuration`, `@EnableAutoConfiguration` and `@ComponentScan`. We've seen `@ComponentScan` earlier in the chapter, but the other two annotations are used to configure the application based on:

  - The settings you add to this class (`@Configuration`)
  - What is on your classpath (`@EnableAutoConfiguration`)

Usually the `main` method is very simple. For example if the class is named `Application.java`:

{lang="java", line-numbers="off"}
~~~~~~~~
public static void main(String[] args) throws Exception {
    SpringApplication.run(Application.class, args);
}
~~~~~~~~

The static `run()` method takes care of the bootstrapping.

Spring Boot projects can be launched from an IDE or compiled into `.jar` files using Maven or Gradle and ran normally. There's also shortcut tasks `mvn spring-boot:run` and `gradle bootRun` to compile and run the application from the command line.

### Configuration

As we've seen so far, Spring can be a big help in making configuration easier. Spring Boot formalizes the concept of multiple configurations into "profiles". One or more profiles can be active for the runtime of an application making it easy to mix and match configurations for different runtime environments.

Spring Boot configuration files have the name `application.properties` or `application.yml` and profiles are named `application-foo.properties` or `application-foo.yml` where `foo` is the profile name. When using `yml` files, property names are "unpacked" such that `spring.foo.bar=baz` becomes:

{lang="yaml", line-numbers="off"}
~~~~~~~~
spring:
  foo:
    bar: baz
~~~~~~~~

Taking the examples from the previous section on properties, here's how you could create the profiles to toggle between different IScream web services:

{lang="yaml", title="application.yml", line-numbers="off"}
~~~~~~~~
spring:
  profiles:
    active: uat
~~~~~~~~

{lang="yaml", title="application-uat.yml", line-numbers="off"}
~~~~~~~~
specials:
  url: http://www.mocky.io/v2/590401621000003d034f66dc
~~~~~~~~

{lang="yaml", title="application-prod.yml", line-numbers="off"}
~~~~~~~~
specials:
  url: http://www.example.com/some-prod-url
~~~~~~~~

This configuration sets the default profile to `uat`. However, this can be overridden with a Java system variable `spring.profiles.active` as in `java -jar -Dspring.profiles.active=prod my-app.jar` or with the environment variable `SPRING_PROFILES_ACTIVE`. In fact, any Spring Boot property can be overriden using these two methods. As mentioned, profiles can be mixed simply by separating each profile name with a comma.

{icon=fast-forward}
G> ### Hipster Watch: Spring Cloud Config
G>
G> If you are running distributed software or need to support many environments, application profile files can become cumbersome. Spring's solution is [Spring Cloud Config](https://cloud.spring.io/spring-cloud-config/spring-cloud-config.html) which allows applications to query a web service in order to determine their properties. By default, properties are stored in Git repositories which are cloned by the Spring Cloud Server and passed along to the application making the request.

## Summary

We've only scratched the surface of Spring, and unfortunately it can be used in so many different ways that it's difficult to give advice that applies globally. However, we can at least always keep in mind that the goal of Spring Core is to assemble an application with all of its properly configured dependencies at runtime. As mentioned before, the Spring set of tools are so pervasive that we will see it pop up again in the coming chapters.

Spring Boot is a new approach to Spring applications that frees developers to spend more time writing code and less time with configuration. You can see an example included in the code for this chapter, but I will go into more depth in Chapter 6, which discusses web frameworks.

## Suggested Resources

"Guides". *Spring*, https://spring.io/guides. Accessed 18 June 2017.

Deinum, Marten. *Spring Recipes: A Problem-Solution Approach.* Apress, 2014.  
http://www.apress.com/br/book/9781430259084

Walls, Craig. *Spring Boot in Action*. Manning, 2015.  
https://www.manning.com/books/spring-boot-in-action

Walls, Craig. *Spring in Action*. Manning, 2014.  
https://www.manning.com/books/spring-in-action-fourth-edition

Yong, Mook Kim. "Spring Tutorial". *Mkyong.com*,  
http://www.mkyong.com/tutorials/spring-tutorials. Accessed 18 June 2017.

[^spring-di1]: "The IoC container". *Spring Framework Reference*, https://docs.spring.io/spring/docs/current/spring-framework-reference/html/beans.html. Accessed 10 June 2017.

[^spring-di2]: "The IoC container". *Spring Framework Reference*, https://docs.spring.io/spring/docs/current/spring-framework-reference/html/beans.html. Accessed 10 June 2017.
