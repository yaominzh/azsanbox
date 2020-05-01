# Chapter 7: Web Application Deployment

Depending on the size of your company, you may never actually have to deploy your code. It's possible that a deployment team takes care of it for you. Nonetheless, you should still be familiar with how the code is deployed, if only so that you can properly maintain the project build files. On the other hand if you are on a smaller team, it's quite possible you will need to know how to deploy the projects you build.

Unlike console applications, Java web applications aren't executed from the command line. (Although we'll see an exception to this.) Instead, they run inside of application servers that are responsible for processing requests to your code and responses back to the web server.

## Packaging

There are two common ways to package Java applications: WAR files and EAR files.

A WAR file (**W**eb **A**pplication a**R**chive) is really the same as a JAR file except with a different extension to easily identify it as a web application. When used as a deployment artifact, it contains all of the code required to run the application including domain objects, controllers, view files, etc.

An EAR file (**E**nterprise **A**pplication a**R**chive) is a different type of deployment archive that includes both WAR and JAR files. Typically when an EAR file is used, the WAR files *only* contain the code related to a web application. Domain objects, services, etc. (which are called "Enterprise Java Beans" in this context) are packaged in JAR files. Theoretically this means that different teams could work on separate code bases (the web code and the domain code). Furthermore, you could have multiple web applications that share the same domain objects.

The example code for the book contains a project that gets packaged as a WAR and a project that gets packaged as an EAR. Let's compare the archive file structures:

{lang="text", title="A WAR File Structure", line-numbers="off"}
~~~~~~~~
.
├── META-INF
│   └── MANIFEST.MF
└── WEB-INF
    ├── classes
    │   └── com
    │       └── letstalkdata
    │           └── iscream
    │               ├── controller
    │               │   ├── OrderController.class
    │               │   └── WelcomeController.class
    │               └── domain
    │                   ├── Flavor.class
    │                   ├── Order.class
    │                   └── Topping.class
    ├── dispatcher-servlet.xml
    ├── lib
    │   ├── commons-logging-1.2.jar
    │   ├── jstl-1.2.jar
    │   ├── spring-aop-4.3.9.RELEASE.jar
    │   ├── spring-beans-4.3.9.RELEASE.jar
    │   ├── spring-context-4.3.9.RELEASE.jar
    │   ├── spring-core-4.3.9.RELEASE.jar
    │   ├── spring-expression-4.3.9.RELEASE.jar
    │   ├── spring-web-4.3.9.RELEASE.jar
    │   └── spring-webmvc-4.3.9.RELEASE.jar
    ├── views
    │   └── jsp
    │       ├── new-order.jsp
    │       ├── order-success.jsp
    │       └── welcome.jsp
    └── web.xml
~~~~~~~~

{lang="text", title="An EAR File Structure", line-numbers="off"}
~~~~~~~~
.
├── META-INF
│   ├── MANIFEST.MF
│   └── application.xml
├── iscream-ejb.jar
│   ├── META-INF
│   │   └── MANIFEST.MF
│   └── com
│       └── letstalkdata
│           └── iscream
│               └── domain
│                   ├── Flavor.class
│                   ├── Order.class
│                   └── Topping.class
└──  iscream-web.war
    ├── META-INF
    │   └── MANIFEST.MF
    └── WEB-INF
        ├── classes
        │   └── com
        │       └── letstalkdata
        │           └── iscream
        │               ├── WebAppInitializer.class
        │               ├── WebConfig.class
        │               └── controller
        │                   ├── OrderController.class
        │                   └── WelcomeController.class
        ├── lib
        │   ├── commons-logging-1.2.jar
        │   ├── spring-aop-4.3.9.RELEASE.jar
        │   ├── spring-beans-4.3.9.RELEASE.jar
        │   ├── spring-context-4.3.9.RELEASE.jar
        │   ├── spring-core-4.3.9.RELEASE.jar
        │   ├── spring-expression-4.3.9.RELEASE.jar
        │   ├── spring-web-4.3.9.RELEASE.jar
        │   └── spring-webmvc-4.3.9.RELEASE.jar
        └── views
            └── jsp
                ├── new-order.jsp
                ├── order-success.jsp
                └── welcome.jsp
~~~~~~~~

As you can see, the WAR deployment contains everything needed for the application: controllers, views, and domain objects. The EAR deployment contains a WAR file that does not have any domain objects. Instead, the domain objects have been moved to `iscream-ejb.jar`. The EAR also contains an `application.xml` file that provides instructions to the deployment container. It's possible to create this by hand, but it's usually better to let your build tool create it.

## Deployment

The most common application servers are Tomcat and JBoss/WildFly[^common-as]. Both can be used to deploy WAR files, but Tomcat cannot deploy EAR files. There are of course other options including Glassfish, Jetty, JOnAS, Resin, and others, but they are much less commonly encountered.

There are a few common ways that applications are deployed (and undeployed) to application severs, although not all tools support all options.

1. **Folder drop**. If the application server "watches" a folder while running, any Java archive that is dropped into the folder will be deployed automatically. This can be particularly helpful during development where you may want to just quickly deploy something to a development server.

1. **GUI**. Usually you can access a web interface to the application server at the root context (e.g. `http://my-server:8080/`). Here you can see what applications are running and deploy a new application.

1. **Console**. If the application server has a command line component, you can run the CLI and manage applications there.

1. **API**. Deploying artifacts using an API can be particularly useful if you have a continuous integration server that builds and tests your application. You might choose to automatically deploy your code if it builds successfully.

For practice, you can install one or more application servers on your computer and build the sample projects to create artifacts. Alternatively, you can use Docker to avoid installing anything to your computer directly. I discuss Docker in more detail in Appendix A, but in short, it is a utility that allows you to easily create and destroy isolated "containers" on your computer. The sample projects contain handy `docker-build.sh` and `docker-run.sh` scripts as a convenience. Both of these scripts use the "folder drop" method of placing the build artifact into a special folder that is checked on server startup. For Tomcat it is `$TOMCAT_INSTALL_DIR/webapps` and for WildFly it is `$WILDFLY_INSTALL_DIR/standalone/deployments`. Here's a summary of the different methods for these two application servers (standard ports assumed):

**Folder Watch**

* **Tomcat:** `$INSTALL_DIR/webapps`
* **WildFly:** `$INSTALL_DIR/standalone/deployments`

**GUI**

* **Tomcat:**
  1. Navigate to `http://server:8080/manager`
  1. Click "Choose File" to select your `.war`
  1. Click "Upload"
* **WildFly:**
  1. Navigate to `http://server:9990/console`
  1. Click "Start" under "Deployments"
  1. Follow the wizard

**Console**

* **Tomcat:** N/A
* **WildFly:**
  1. Run `$INSTALL_DIR/bin/jboss-cli.sh -c`
  1. Run the command `deploy path/to/war/iscream-ejb.war`

**API**

* **Tomcat:** Issue an `HTTP PUT` to  `http://server:8080/manager/text/deploy?path=/iscream`.
* **WildFly:**
  1. Send the file via `HTTP POST` to `http://server:9990/management/add-content`
  1. Note the `BYTES_VALUE` in the response
  1. `POST` this JSON to `http://localhost:9990/management`

    {lang="javascript", line-numbers="off"}
    ~~~~~~~~
    {  
       "content":[{"hash":{"BYTES_VALUE":"YOUR VALUE HERE"}}],
       "address":[{"deployment":"iscream-ejb.ear"}],
       "operation":"add",
       "enabled":"true"
    }
    ~~~~~~~~

## Embedded Servers

A final option to run a web application is to include a Java Application Server *with* the web application. Jetty and Tomcat are two common choices because there are embedded versions of both tools. By including the application server in your code, you can treat the code just like a command line application and run it using `java -jar`. Indeed, this is what Spring Boot does for its web applications (see [Spring Boot in Chapter 6](#spring-boot-anchor)).

## Suggested Resources

"Apache Tomcat 8". *The Apache Software Foundation*, 21 June 2017.  
http://tomcat.apache.org/tomcat-8.5-doc/index.html. Accessed 23 July 2017.

Long, Josh. "Deploying Spring Boot Applications". *Spring*, 7 March 2014.  
https://spring.io/blog/2014/03/07/deploying-spring-boot-applications. Accessed 23 July 2017.

Stancapiano, Luca. *Mastering Java EE Development with WildFly*. Packt, 2017.  
http://shop.oreilly.com/product/9781787287174.do

[^common-as]: Salnikov-Tarnovski, Nikita. "Most popular Java application servers: 2016 edition". *Plumbr*, 21 April 2016, https://plumbr.eu/blog/java/most-popular-java-ee-servers-2016-edition. Accessed 22 July 2017.
