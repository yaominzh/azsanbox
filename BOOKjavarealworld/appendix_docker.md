# Appendix A: Docker

Docker is a containerization tool that allows applications to run in isolated environments on a host system. The concept is similar to a virtual machine, but with a key difference: Docker containers do not contain a entire operating system. Instead, they are slim bundles of software that delegate kernel operations to the host. The result is they are small (usually a few hundred MB), fast to spin up/down, and efficient.

In the Java world, Docker allows us to bundle all of the supporting software for an application and deploy it in an isolated part of the server. For example, we could have a `.war` file application deployed to Tomcat talking to a Postgres database all inside of a container. And removing or redeploying the application is as simple as issuing a single Docker command.

{icon=fast-forward}
G> ### Hipster Watch: Docker
G>
G> Docker is not terribly new, but many companies simply have not planned their architecture to support containerization. It's a novel and powerful new approach to application development and deployment and the industry is definitely trending towards containers. But your company might not have the velocity to adopt it yet. If you are looking to ease into Docker, consider trying it out in a development capacity for research spikes and developer testing.

## Building Docker images

The first step to deploying a Docker container is to create a Docker image. An image contains all the components for your application and is persistent inside the Docker instance. Importantly, images can be layered on top of one another. So a common pattern is to start with a publicly available image and make slight tweaks for your custom application. This is done using an extensionless `Dockerfile`.

A `Dockerfile` usually starts with a `FROM` command to instruct Docker which base image to start with. Here are some base images useful to Java developers:

 * **`openjdk`**: A Java JDK
 * **`azul/zulu-openjdk`**: Another JDK
 * **`tomcat`**: The Tomcat application server
 * **`jboss/wildfly`**: The Wildfly application server
 * **`alpine`**: A tiny Linux kernel

Specific versions of images may be referred to using the `image:tag` syntax, such as `openjdk:8-jdk-alpine`.

A number of [additional commands](https://docs.docker.com/engine/reference/builder/) can be included in a `Dockerfile`, such as `ADD` to include resources, `ENV` to set environment variables, etc. For example, this `Dockerfile` is used to create a Spring Boot web application image.

{lang="text", title="Dockerfile"}
<<(code/docker/spring-mvc-boot/Dockerfile)

The `ENTRYPOINT` command tells Docker what to run as soon as the container starts. This is important because otherwise the container would just boot up the tiny Alpine Linux and do nothing else.

To actually create the image, we can run `docker build --tag=iscream .`. The `tag` is optional, but without it, Docker will give the image a random name. Building an image for the first time might take a minute or so because all of the base images need to be downloaded from the internet. But subsequent builds will be faster because Docker saves the base images.

## Deploying a Docker container

Once an image has been built, it's possible to create one or more containers from that image. Containers should generally be thought of as (relatively) short-lived and disposable. If they need to persist data, that is done with Docker volumes. Containers are scrapped when a new version of your code needs to deployed or when you need to reconfigure the application with different parameters. It's even possible to create highly disposable containers that are automatically deleted by Docker when their process finishes. This pattern is useful for batch or ETL jobs that have no need to linger after their task is complete.

A very simple container can be created using `docker run -d -p 8080:8080 iscream`. This will start an instance of the `iscream` image in the background (`-d`), assign it a random name, and expose the internal port `8080` to the host port `8080` (`-p`). After issuing that command, you can verify the program is running by navigating to the familiar `http://localhost:8080/orders/new` page.

You can run `docker ps` to see the name of the container and then run `docker stop container_name` to stop the container and `docker rm container_name` to delete it.

Of course, there are [many parameters to the run command](https://docs.docker.com/engine/reference/run/), but here are a few examples to get you started:

 * `docker run -it -p 8080:8080 iscream`: Run a container in "interactive" mode
 * `docker run -d --rm -p 8080:8080 iscream`: Remove a container automatically when it stops
 * `docker run -d --name iscream_dev -p 8080:8080 iscream`: Name the container instance `iscream_dev`

## Caveats

### Memory

If you are running multiple containers on a Docker host (which you should be!), it's a good idea to put some bounds on how much memory the containers can use. Otherwise, a memory leak in one application could negatively impact others. Docker allows you to limit the memory a container uses with the `-m` flag, but it is critical that you also limit the memory at the JVM level! (For more details on why, see ["Java Inside Docker: What You Must Know to Not FAIL"](https://dzone.com/articles/java-inside-docker-what-you-must-know-to-not-fail) by Rafael Benevides). This requires two steps.

First, modify your `Dockerfile` to pass `$JAVA_OPTS` to the `java` command as in:

{lang="text", line-numbers="off"}
~~~~~~~~
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app.jar" ]
~~~~~~~~

Second, when creating a container, limit the container memory *and* set `$JAVA_OPTS` using `-e` as in:

{lang="text", line-numbers="off"}
~~~~~~~~
docker run -d -p 8080:8080 -m 1536M -e JAVA_OPTS='-Xmx1024m' iscream
~~~~~~~~

### JDKs

If you remember all the way back to Chapter 1, you will recall that there are actually multiple JDKs for Java. Due to Oracle's licensing, it is likely [illegal to use an Oracle JDK inside of Docker](http://blog.takipi.com/running-java-on-docker-youre-breaking-the-law/). The alternative is to use a JDK with permissive licensing such as OpenJDK. This will be suitable for most situations. However, due to variations in tag names, you might accidentally use a version of the JDK that you did not intend to use. For example, `8-jdk-alpine` does not specify a specific commit and could be *any* version of Java 8.

One alternative is to specify a more specific tag name such as `8u131-jdk-alpine` (although this still does not point to a specific JDK commit). Or you could use a different JDK such as Zulu which is formally tested and has tags pegged to specific commits such as `8u144-8.23.0.3`.

For what it's worth, I still use OpenJDK most of the time and don't find the version uncertainty too concerning. I trust that OpenJDK is reasonably well-tested. However, I do not work with mission-critical software that cannot tolerate an occasional (and rare!) JDK bug. You should choose a JDK appropriate to your situation.

## Suggested Resources

"Docker Documentation" Docker, 2017. https://docs.docker.com.

Krochmalski, Jaroslaw. *Docker and Kubernetes for Java Developers.* Packt 2017.  
https://www.packtpub.com/virtualization-and-cloud/docker-and-kubernetes-java-developers
