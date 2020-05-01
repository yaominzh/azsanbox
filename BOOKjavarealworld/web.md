# Chapter 6: Web Application Frameworks

Perhaps the most common use of Java in enterprise environments is web development. The API that made web development in Java viable was the "Java 2 Platform, Enterprise Edition" (or J2EE), first released in 1999. Since then literally dozens of web frameworks have risen and fallen. Even covering all the frameworks that are active today would be such a large undertaking that I am going to focus on just some of the most popular frameworks.

 * **Spring MVC:** The Spring MVC framework is by far the most dominant framework. It builds on the Spring Core that we have already seen and provides functionality for building controllers that interact with your business models and HTML views.
 * **Spring Boot:** We saw how Spring Boot could be used for a console application, but it can also be used as a wrapper for Spring MVC to make web development even easier.
 * **Java Server Faces (JSF):** JSF is a framework based on reusable UI components that can be easily linked to data and event handlers. It abstracts away most of the HTML, CSS, and JavaScript and integrates nicely with AJAX.
 * **Vaadin:** Vaadin goes one step farther than JSF and *completely* abstracts away all client-side code *and* the request-response cycle of a web application.

{icon=fast-backward}
G> ### Legacy Watch: Outdated Frameworks
G>
G> A few years ago, I found myself working on a web application built on a framework that reached its official end-of-life in 2008. It was thankfully an internal application which mitigated some of the huge security issues. But even finding decent documentation was a challenge. Sadly, this isn't uncommon in enterprise Java environments. Along the trail of Java history there are many gravestones for dead web frameworks.
G>
G> If you find yourself stuck with a legacy framework, there are a few techniques that can help you.
G>
G> * **Try to find patterns.** When adding a new feature, try to find a similar page or feature that already exists in the application and mimic it.
G> * **Cruise forums.** A lot of web forums from the early 2000s are still accessible, so sharpen your Google skills and see what you can find. Try "More from this site" often!
G> * **Ask for help.** Check with the veterans on your team. Chances are one of them had to use the framework in the past and they might be able to help you work through problems.

## Java Enterprise Edition Web API

When using Java web frameworks, you usually won't need to interact with the lower-level API of Java EE, but there are a few concepts you should be familiar with.

### Request and response

A request comes into the frameworks as an `HttpServletRequest` which provides access to the session, cookies, parameters, form data, etc. The response is sent back to the client as an `HttpServletResponse` which can be modified to include a status code, response data, headers, etc.

Sometimes you need to work with these objects directly. If so, a controller method might accept an `HttpServletRequest` as a parameter and return an `HttpServletResponse`. However, most of the time the framework will do it for you.

### JavaServer Pages

JavaServer Pages (JSP) is a technology that allows for dynamic generation of HTML. A `.jsp` file looks similar to an HTML file with some extension tags and syntax. These extensions allow access to model data and perform simple logic, such as conditionals and looping, to generate dynamic content. They are evaluated server-side and translated into the HTML that is sent to the client. Many frameworks use JSP, although we will also see alternative methods of creating web application views.

### Servlet Containers

Most Java web applications are deployed using a "servlet container." It is the servlet container's job to handle the traffic between the web server and the Java application. We will learn more about these containers in the next chapter, but for now it's only important to know that an application has to register itself with the container. This is commonly done with a `web.xml` file, although some frameworks allow it to be done in pure Java.

Most frameworks work best with the following folders:

 * **`src/main/java`:** Your application code.
 * **`src/main/resources`:** Text files, properties, etc. that are included on the classpath to aid your application code.
 * **`src/main/webapp/resources`:** *Public* resources such as CSS, JavaScript, images, etc.
 * **`src/main/webapp/WEB-INF`:** *Private* resources that the servlet container has access to.

## Spring MVC

Going into detail about the Model-View-Controller (MVC) paradigm is beyond the scope of this book, but as a quick refresher, the model is the logic and business objects, the view is what the user interacts with, and the controller is the what ties the two together. Spring's MVC framework provides a controller-focused API that makes it easy to translate requests into model objects and model objects into views.

### Model

We are going to build a trivial point-of-sale system for the ISCream company that lets an employee enter an ice cream order and calculate the cost. We'll add a few domain objects to help:

{lang="java", title="Flavor.java"}
<<(code/web/spring-mvc-xml/src/main/java/com/letstalkdata/iscream/domain/Flavor.java)

{lang="java", title="Topping.java"}
<<(code/web/spring-mvc-xml/src/main/java/com/letstalkdata/iscream/domain/Topping.java)

{lang="java", title="Order.java"}
<<(code/web/spring-mvc-xml/src/main/java/com/letstalkdata/iscream/domain/Order.java)

It is worth noting that nothing identifies these objects as part of a web application. That's intentional. Spring uses "Plain Old Java Objects" (POJO) as much as possible.

### View

Our application will have two views: one to let the employee enter the order data and one to return the price. Spring MVC can use various view technologies, but we will use JSP for now.

As mentioned a `.jsp` file looks similar to an HTML file and can use all the tags available in HTML. We can use other tags by adding a `taglib` at the top of the file. Most JSP files will use at least the standard JSP tag library. Additionally, the [JSP Expression Language](https://docs.oracle.com/javaee/7/tutorial/jsf-el.htm) lets us inject dynamic content. Its syntax is `${expression}` where `expression` can contain variables, literals, operators, and even certain Java method calls.

Here's how we can dynamically inject the flavors and toppings into the view:

{lang="html",line-numbers="off"}
~~~~~~~~
<select name="flavor">
  <option value="" selected></option>
  <c:forEach items="${flavors}" var="flavor">
    <option value="${flavor}">${flavor.toString()}</option>
  </c:forEach>
</select>
~~~~~~~~

We'll see how the flavors actually get passed in shortly, but assuming they do, here's what the rendered HTML looks like to the client:

{lang="html",line-numbers="off"}
~~~~~~~~
<select name="flavor">
  <option value="" selected></option>
  <option value="VANILLA">Vanilla</option>
  <option value="CHOCOLATE">Chocolate</option>
  <option value="STRAWBERRY">Strawberry</option>
</select>
~~~~~~~~

We can also inject the toppings and price in a similar manner, which you can see in the example code for this chapter.

### Controller

Spring MVC controllers are both powerful and versatile, making them easy to fit almost any need. At the same time, there's usually several different ways to solve the same problem, which can be confusing when you are first learning the framework.

Controller classes are annotated `@Controller` and sometimes also have a `@RequestMapping` annotation to designate the base route for the whole controller. For example, an `EmployeeController` could have a base route (`/employee`).

Routes are defined by annotating a method with `@RequestMapping` and include the actual route (e.g. `/new`) and the HTTP methods (e.g. `POST`). Beyond that, route methods are extremely flexible. There's more than [20 types of parameter objects](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html#mvc-ann-arguments) and over [15 valid return types](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html#mvc-ann-return-types)! Bearing that in mind, here is one *possible* way to define the route for the New Order page of the application.

{lang="java", title="OrderController.java", starting-line-number=20, crop-start-line=20, crop-end-line=27}
<<(code/web/spring-mvc-xml/src/main/java/com/letstalkdata/iscream/controller/OrderController.java)

A `Model` is passed in so that we can modify (or "enrich") it to include the list of flavors and list of toppings. For example, setting the `flavors` attribute allows that data to be accessed in the view with the `${flavors}` expression. Finally, the return type is a `String` that identifies the view that should be used.

In order to process the order, we need to allow `POST` requests to be made to the same route. We can do this by creating another method in the controller.

{lang="java", title="OrderController.java", starting-line-number=29, crop-start-line=29, crop-end-line=36}
<<(code/web/spring-mvc-xml/src/main/java/com/letstalkdata/iscream/controller/OrderController.java)

Seemingly by magic, the request is automatically coerced into an `Order` object by using the `@ModelAttribute` annotation. Spring MVC will use the form data attributes in the `POST` request to call the matching setters in the `Order` class. (Here's another example of a framework expecting getters and setters!) Again, we also include a `Model` in order to enrich it with the calculated price and we return a `String` to identify the view that should be used for the response.

These examples just scratch the surface of what is possible with Spring MVC controllers. In your application, you will likely find more variety in order to accommodate more complex request-response cycles. This can be overwhelming at first, so take it easy!

### Configuration

As a Spring tool, Spring MVC can be configured using an XML file or annotations. A minimal application such as this requires minimal configuration. Here's how you can configure the same settings using both methods.

{lang="xml", title="dispatcher-servlet.xml"}
<<(code/web/spring-mvc-xml/src/main/webapp/WEB-INF/dispatcher-servlet.xml)

{lang="java", title="WebConfig.java"}
<<(code/web/spring-mvc-annotation/src/main/java/com/letstalkdata/iscream/WebConfig.java)

This configuration is required to tell Spring MVC how to locate the views for your application.

Finally, we need to configure the application to run in a servlet container. As mentioned before, it is common to do this via a `web.xml` file in the root of the `WEB-INF` directory. This file defines a servlet that will be registered in the container along with other configuration options.

{lang="xml", title="web.xml"}
<<(code/web/spring-mvc-xml/src/main/webapp/WEB-INF/web.xml)

If you so choose, you may do this with a Java object instead.

{lang="java", title="WebApplicationInitializer.java"}
<<(code/web/spring-mvc-annotation/src/main/java/com/letstalkdata/iscream/WebAppInitializer.java)

With that in place, it is now possible to deploy the application to a servlet container. However, if you would like to try out the example code, you can run `gradle appRun` which will actually start a very small servlet container (Gretty) on your computer and deploy the application to `http://localhost:8080`.

## Spring Boot {#spring-boot-anchor}

We got an introduction to Spring Boot in the previous chapter and saw how it can be used for rapid application development. By adding another starter, we can use Spring Boot to build Spring MVC web applications.

The major advantage is that you do not need to do any configuration. A Spring Boot web application has no `web.xml`, nor a `WebApplicationInitializer` class. Furthermore, there's no need to create a `@WebConfig` class or `dispatcher-servlet.xml`. The presence of the the `spring-boot-starter-web` starter on the classpath is enough to configure the application.

### Thymeleaf

Although it is possible to use JSP in a Spring Boot application, the recommended way to create views is by using the Thymeleaf template engine. As long as Thymeleaf is on the classpath, it will automatically be used as the view renderer for the application. The Thymeleaf engine is similar to JSP technology, with one notable difference: Thymeleaf views are (X)HTML files that use custom attributes instead of tags. For comparison, this how you would load the flavors into the view using Thymeleaf:

{lang="html",line-numbers="off"}
~~~~~~~~
<select class="form-control" id="flavor" name="flavor">
  <option value="" selected="selected"></option>
  <option th:each="flavor : ${flavors}"
    th:value="${flavor.name()}"
    th:text="${flavor}"></option>
</select>
~~~~~~~~

You can see that this generally looks like HTML and certain `th` attributes provide the logic. By convention, `th` is the alias for `http://www.thymeleaf.org`.

Since you are working directly with HTML, the views can be loaded into a browser without running the application. For example:

![A Thymeleaf view in the browser](images/thymeleaf_template_html.png)

Obviously none of the server-side data is rendered into the view, but it can give you a general idea of the layout and styling, making front-end design iterations faster. For a more in-depth discussion of Thymeleaf, see [the official documentation](http://www.thymeleaf.org).

### Running a Spring Boot Web App

I've mentioned before that Java web applications run in a servlet container. Deploying to a container takes a couple minutes (longer for more complex applications), which can be annoying during development. To speed things up, you can use a plugin to deploy your application to a light-weight local container. In this chapter, I used Gretty which can be invoked with `gradle appRun`. Spring Boot takes a similar approach, but instead of treating the container and application as separate entities, Spring Boot embeds a container (Tomcat) into your application. The embedded container automatically starts when you run your application from the command line using `java -jar ...` or when running the `main` class from your IDE. This makes local development and production deployment equally easy.

## JavaServer Faces

JavaServer Faces (JSF) is a web application technology that focuses more on the UI of an application and less on the backend wiring. Indeed the controller is mostly hidden from the developer and development is spent on the model and view. Of the web application tools we've discussed so far, it is perhaps the most divisive in the community. Some criticize it for hiding too much from the developer, adding statefulness, and coupling the view and model too tightly. On the other hand, some developers praise its reusability and productivity.

### Managed Beans

Unlike Spring, when using JSF we don't manually inject things into the view. Instead, the JSF application will create and access "managed beans" to interact with the model. Creating a managed bean is as simple as adding the `@ManagedBean` annotation to a class. Additionally, a scope should be applied to the bean that tells the application how long to hold onto the bean. You can see the complete list of scopes [here](http://docs.oracle.com/javaee/6/tutorial/doc/girch.html), but some common ones are:

 * **`ApplicationScoped`:** The bean is maintained for the whole run time of the application. This scope is typically used for global beans that many users access.
 * **`SessionScoped`:** The bean is maintained for the duration of the user's session.
 * **`RequestScoped`:** The bean is maintained for one request-response cycle (this is also the default scope).

For the ISCream application, I created an `IngredientService` that can be used by all users and made the `Order` class request-scoped.

Managed beans can also have managed properties which can be accessed by the view too. Because of the statefulness of JSF, I created a few managed properties in the `Order` class. We will see how these are used shortly.

{lang="java", title="IngredientService.java"}
<<(code/web/jsf/src/main/java/com/letstalkdata/iscream/service/IngredientsService.java)

{lang="java", title="Order.java"}
<<(code/web/jsf/src/main/java/com/letstalkdata/iscream/domain/Order.java)

### JSF Views

Like Thymeleaf, JSF views are XHTML files that use custom extensions to instruct the server on how the response should be constructed. However, JSF relies much more heavily on custom tags. Indeed, most JSF XHTML files use almost exclusively custom tags. This is how to create the now-familiar "flavors" drop down in JSF:

{lang="html",line-numbers="off"}
~~~~~~~~
<h:selectOneMenu id="flavor" class="form-control" h:value="#{order.flavor}">
  <f:selectItems value="#{ingredientsService.flavors}" var="flavor"
                  itemLabel="#{flavor}" itemValue="#{flavor}" />
</h:selectOneMenu>
~~~~~~~~

Here we see the managed bean `ingredientsService` used to populate the items of the `select` list. Importantly, we also see `h:value="#{order.flavor}"`. This is how we bind view data back to a model. The `order` bean is managed and we can access the `flavor` value because we have a `setFlavor` method.

After binding the data into the model, we need to display the calculated cost to the user. Instead of doing a page redirect, we can use the JSF built-in support for AJAX. This is also where we take advantage of the managed properties on the `order` bean. This is the JSP code to handle the AJAX request:

{lang="html", title="new-order.xhtml", starting-line-number=33, crop-start-line=33, crop-end-line=51}
<<(code/web/jsf/src/main/webapp/new-order.xhtml)

When the page loads, the order has no details so its price cannot be calculated. But because `order` is a managed bean, when the user submits the AJAX request, the details are injected into the order via setters. Additionally, the `save()` method is executed because it is assigned to the `action` attribute of the `commandButton`. The `save()` method then toggles the `saved` property. Since the `panelGroup`'s visibility is determined via the boolean value of the `saved`  property, it is now visible. Finally, `formattedPrice` is accessed and rendered onto the page.

To run the demo application, you can again use `gradle appRun`. However, when using JSF you typically don't define the routes. Instead, the routes are based on the file structure inside of `webapp`. So to access the page, you would go to `http://localhost:8080/new-order.xhtml`.

## Vaadin

A common challenge for web application developers is that they need to have a solid understanding of a server-side language and the front end technologies: JavaScript, CSS, and HTML. In recent years this has led to server-side JavaScript becoming more popular. But for Java web developers, this barrier still exists. Even for experienced developers who are comfortable with all of the web technologies, a considerable amount of time is spent marrying the front-end to the back-end. In response to this, Google created the Google Web Toolkit which translates pure Java code into HTML and JavaScript. Indeed GWT is still a viable web framework today. Vaadin is built on-top of GWT with the goal of simplifying and abstracting some of the trickier parts of GWT.

Like JSF, Vaadin hides the request-response cycle from the developer and has no explicit concept of a controller. However, it goes one step farther because with Vaadin, you do not create any HTML view files[^vaadin-designer]. Most web applications can be designed entirely in Java, although if you need custom styling or advanced client-side components, you can extend Vaadin with your own CSS or JavaScript.

### Layouts and Components

In general, views in Vaadin are built by adding `Component`s to `Layout`s. Components are objects like text fields, select lists, headers, grids, buttons, or even other layouts. To create the New Order screen, we'll create a `VerticalLayout` that contains a `FormLayout` for the order details and another `VerticalLayout` to display the price.

{lang="java", title="OrderScreen.java"}
<<(code/web/vaadin/src/main/java/com/letstalkdata/ui/OrderScreen.java)

Since there is no explicit controller or reference to a request, we can populate the flavor and toppings controls at compile time. Similarly, we are not concerned with *how* the application responds to a user clicking the submit button, we just code *what* we want to happen when the user clicks the button.

### Vaadin `UI`s

Creating a `UI` is as close as we get to defining routes in a Vaadin application. However, unlike traditional web applications, most Vaadin applications only have have a handful of routes--many only have one. A Vaadin `UI` is a single-page application (SPA), although different "pages" can be displayed to the user by implementing the `View` interface.

In the ISCream UI, we create an instance of the `OrderScreen` and set it as the root of the UI.

{lang="java", title="OrderUI.java"}
<<(code/web/vaadin/src/main/java/com/letstalkdata/ui/OrderUI.java)

### Themes

Each UI can have its own theme which is created using [SASS](http://sass-lang.com), a CSS extension. Unless you absolutely need your own custom style, it's usually best to simply build on top of one of Vaadin's pre-designed themes. For this application, I'm using the Valo theme as my base:

{lang="sass", title="mytheme.scss", starting-line-number=1, crop-start-line=32, crop-end-line=38}
<<(code/web/vaadin/src/main/webapp/VAADIN/themes/mytheme/mytheme.scss)

I didn't need any customizations, but if you do, you would add them here. You would also need to compile the SCSS theme into CSS, which is easy to do if you are using the Maven Vaadin plugin (see `README.md` in the example code for details).

### Running the Application

The Vaadin Maven plugin includes a simple web server to run the application during development. Run `mvn jetty:run` and navigate to `http://localhost:8080/`. If you define multiple `UI`s, they would be accessed at `http://localhost:8080/my-main-ui`, `http://localhost:8080/my-other-ui` etc.

## Summary

As mentioned, I only presented a sampling of the Java web frameworks in use today. However, I intentionally tried to show the wide range of approaches used to develop Java web applications.

On one end of the spectrum is Spring MVC--a very traditional framework with routes and direct knowledge of requests and responses. We saw how Spring Boot can be used to develop a Spring MVC application more easily and in less time. On the other end of the spectrum is Vaadin which, for the most part, feels like developing a desktop application. Somewhere in the middle is JSF which focuses on building a view while hiding the controller from the developer. Each of these have their advantages and as such are all in common use today.

We also saw a variety of view technologies. JSP is the oldest and uses an expression language for basic logic. Spring provides extensions to the expression language to make working with Spring applications a bit easier. JSF and Thymeleaf use XHTML, but with slightly different approaches. Thymeleaf views use mostly HTML tags with special attributes while JSF views use almost exclusively custom tags. Finally, Vaadin drops view files instead opting for defining views using Java.

In many ways Java web applications are the most complex applications you're likely to work on at traditional companies. And unfortunately, best practices are not always followed and the flexibility of some frameworks can be maddening. It's important to spend the time to learn which frameworks your company uses and even more so, *how* the developers use them.

## Suggested Resources

Ganeshan, Amuthan. *Spring MVC: Beginner's Guide - Second Edition*. Packt, 2016.  
https://www.packtpub.com/application-development/spring-mvc-beginners-guide-second-edition

Leonard, Anghel. *Mastering JavaServer Faces 2.2*. Packt, 2014.  
https://www.packtpub.com/application-development/mastering-javaserver-faces-22

Walls, Craig. *Spring Boot in Action*. Manning, 2015.  
https://www.manning.com/books/spring-boot-in-action

"Vaadin Docs". *Vaadin*, 2017, https://vaadin.com/docs/. Accessed 23 July 2017.

[^vaadin-designer]: The commercial version of Vaadin gives access to the Designer tool which lets you build views using a GUI. The designer does create an HTML file, but you rarely interact with the file directly.
