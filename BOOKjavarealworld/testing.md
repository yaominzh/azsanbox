# Chapter 4: Testing

Few things are a better predictor of successful production code than good tests. Thankfully, Java has a rich set of tools and libraries for testing. By far, the most used testing library is JUnit. It can be used as a stand-alone library or in combination with other libraries that provide additional functionality. All the modern IDEs can run JUnit tests without much fuss, and both Maven and Gradle support the `test` command which runs all of the detected JUnit tests. (Ant has a `junit` target). An alternative to JUnit is TestNG which has historically provided functionality missing from JUnit. It too has wide support, but in some cases requires additional plugins.

{icon=fast-backward}
G> ### Legacy Watch: No tests!
G>
G> Whether it's due to lack of knowledge, poor management, or just developer hubris, it seems there's no shortage of code without automated tests. In particular if you are working with a legacy Java application, you might find yourself lost in untested spaghetti code. I've learned that if you can isolate some subsection of the code and create a few broad tests it can at least help you find your bearings. For more details, take a look at Michael Feather's discussion of test harnesses in *Working Effectively with Legacy Code* (Prentice Hall, 2004).

## Adding a Service to the IScream Application

Using the IScream store application again, let's change the `DailySpecialService` to query a web API endpoint instead of returning a static list of specials. We'll assume that the service returns a JSON response and also that we don't know about JSON parsing libraries!

{lang="java",title="DailySpecialService.java"}
<<(code/testing/icecream-app-junit/src/main/java/com/letstalkdata/iscream/service/DailySpecialService.java)

I> ### Tell Me More: Mocky
I>
I> If you ever need to quickly stub a web service that is actually available over HTTP, I recommend [Mocky](http://www.mocky.io). You provide a response code, content type, headers, and body and instantly get a permanent URL that always returns your inputs. Just be aware it does not use HTTPS, so don't send anything sensitive.

The method we're most interested in is `parseFlavorsFromJson` and it's what we'll be testing. In a real application we would of course not do this by hand! (See Chapter 10 for a discussion on JSON libraries.) To start, we'll create `DailySpecialServiceTest.java` in the folder `src/test/java/com/letstalkdata/iscream/service/`. Notice that this parallels the structure of the actual class. There are three good reasons for this:

1. It keeps your test code reasonably organized.
1. If you are using a build tool, you can exclude anything in the `test` folder from being included in your build artifacts.
1. If you are using a build tool, identically named files in the `test` folder override files in the `main` folder. We'll see an example of how this can aid testing shortly.

When working with JSON, I like to store the test data as static files outside of the code for cleanliness. These three samples are included in the `src/test/resources/json-samples` folder[^resources]:


{lang="javascript",title="no-specials.json", line-numbers="off"}
<<(code/testing/icecream-app-junit/src/test/resources/json-samples/no_specials.json)

{lang="javascript",title="one-special.json", line-numbers="off"}
<<(code/testing/icecream-app-junit/src/test/resources/json-samples/one_special.json)

{lang="javascript",title="three-specials.json", line-numbers="off"}
<<(code/testing/icecream-app-junit/src/test/resources/json-samples/three_specials.json)

In the Java test, I created this helper to read in the JSON:

{lang="java", title="DailySpecialServiceTest.java", starting-line-number=19, crop-start-line=19, crop-end-line=25}
<<(code/testing/icecream-app-junit/src/test/java/com/letstalkdata/iscream/service/DailySpecialServiceTest.java)

Now that we have the skeleton in place, lets add some actual tests.

## Writing Tests

### JUnit

JUnit is the most commonly used testing framework in the Java world. It follows in the xUnit tradition of testing. So if you have used something like NUnit, PHPUnit, CppUnit, etc., you'll be right at home. If not, the syntax is easy to pick up.

All JUnit tests are `public void` methods labelled with the `@Test` annotation. Of course, to make the test meaningful, you need to add an assertion. It's common practice to statically import `org.junit.Assert.*` in test classes in order to make tests more succinct. I find myself most often using `assertEquals`, `assertTrue`, and `assertFalse`, but there are a few other methods that can come in handy. Here's some tests to validate the parsing method using the three mocked up JSON files.

{lang="java", title="DailySpecialServiceTest.java", starting-line-number=27,crop-start-line=27, crop-end-line=41}
<<(code/testing/icecream-app-junit/src/test/java/com/letstalkdata/iscream/service/DailySpecialServiceTest.java)

JUnit includes some matchers from the Hamcrest testing framework which can be accessed by adding `import static org.hamcrest.CoreMatchers.*`. Here's how an assertion can be written using Hamcrest:

{lang="java", title="DailySpecialServiceTest.java", starting-line-number=43,crop-start-line=43, crop-end-line=49}
<<(code/testing/icecream-app-junit/src/test/java/com/letstalkdata/iscream/service/DailySpecialServiceTest.java)

Some people prefer this "fluent" style of writing tests, but they always feel a little awkward to me in Java. If you're intrigued, you can include the full set of Hamcrest matchers by adding the `hamcrest-library.jar` library to your application.

### TestNG

In many instances, TestNG was the first on the scene with features now present in both TestNG and JUnit. For example: annotations, test groups, and parameterized tests. As such, TestNG has many similarities to JUnit. It is also an xUnit framework and tests are `public void` methods labelled with the `@Test` annotation. Many of the same assertions are in the framework, and you typically include `import static org.testng.Assert.*` in the class.

Because of these similarities, this test looks (almost) exactly the same:

{lang="java", title="DailySpecialServiceTest.java", starting-line-number=44,crop-start-line=44, crop-end-line=50}
<<(code/testing/icecream-app-testng/src/test/java/com/letstalkdata/iscream/service/DailySpecialServiceTest.java)

(I said "almost" because TestNG and JUnit flip the order of the parameters in `assertEquals()`).

Until relatively recently, JUnit did not support test groups. Test groups allow you to execute different sets of tests at different times. For example, you may have a handful of slow tests that you don't need to run as frequently so you can group them separately from your fast unit tests. Here are two sample tests, one of which is in a group:

{lang="java", line-numbers="off"}
~~~~~~~~
@Test(groups = "db-integration")
public void employeeCanBeSaved() {
  EmployeeService service = new EmployeeService();
  Employee employee = new Employee("Allie");
  service.save(employee);

  assertEquals(service.getById(1).getName(), "Allie");
}

@Test
public void employeeFullNameIsGenerated() {
  Employee employee = new Employee();
  employee.setFirstName("Allie");
  employee.setLastName("Park")

  assertEquals(employee.getFullName(), "Allie Park")
}
~~~~~~~~

Now that `employeeCanBeSaved` has been put into a group, we can run that group on demand. For example, in Gradle, we could create a task to run the slow database integration tests.

{lang="groovy", line-numbers="off"}
~~~~~~~~
task dbTest(type: Test, dependsOn: 'test') {
  useTestNG() {
    includeGroups 'db-integration'
  }
}
~~~~~~~~

## Running Tests

As mentioned previously, JUnit and TestNG tests are known to most of the Java toolset. During development, you will probably find it easiest to run tests via the IDE. Here's how some JUnit tests look when I run them from IntelliJ.

![Running tests in the IntelliJ IDE](images/intellij_tests.png)

Build tools know about tests too. For example, here's what a failing test looks like in Gradle:

{lang="text",line-numbers="off"}
~~~~~~~~
$ gradle test
:compileJava UP-TO-DATE
:processResources NO-SOURCE
:classes UP-TO-DATE
:compileTestJava
:processTestResources UP-TO-DATE
:testClasses
:test

com.letstalkdata.iscream.service.DailySpecialServiceTest >  
GivenZeroSpecials_EmptyListIstReturned FAILED
    java.lang.AssertionError at DailySpecialServiceTest.java:31

3 tests completed, 1 failed
:test FAILED

FAILURE: Build failed with an exception.
~~~~~~~~

Maven and Gradle will also create (slightly different) HTML test reports which can be particularly useful for large applications with dozens of tests. You'll find the test results in the `build` folder for Gradle or the `target` folder for Maven. Here's what the Gradle test report looks like for the previous build.

![A Gradle test report](images/gradle_test_report.png)

## Using Test Doubles

Sometimes when writing tests, we run into objects that are cumbersome to work with. Typically these are objects that interact with the outside world--database connectors, file system readers, web context objects, etc. Test doubles are objects that stand in for the complex objects in order to make writing tests easier. If this concept is new to you, I highly recommend ["Mocks Aren't Stubs"](https://martinfowler.com/articles/mocksArentStubs.html) by Martin Fowler. Naturally, Java has many frameworks for working with test doubles: EasyMock, Mockito, JMockit, jMock, PowerMock, ... . I'll be covering three of them.

 * **EasyMock** was one of the first on the scene and tends to be more common in older projects.
 * Today, **Mockito** is the [most-used mocking library](http://blog.takipi.com/we-analyzed-60678-libraries-on-github-here-are-the-top-100/).
 * **PowerMock** sits on top of either EasyMock or Mockito to provide additional functionality

### Modifying IScream for Mockable Services

Right now our application is set up to make a web service call out to the internet to determine the daily specials. In the context of unit tests, this is slow and unreliable. An HTTP call could take a few seconds and there's no guarantee that the service will even be available. Unless we are specially testing the web service integration (i.e. in an *integration* test), we would really like to avoid actually talking to the internet. Mocks to the rescue.

I added a new class to the code to make our tests a little easier to write:

{lang="java",title="MenuCreator.java"}
<<(code/testing/icecream-app-mock/src/main/java/com/letstalkdata/iscream/MenuCreator.java)

The `MenuCreator` constructor contains a `DailySpecialService` instance because the `MenuCreator` *depends* on it. And when that instance is passed in, we say it is *injected*. (The term "dependency injection" will be especially relevant when we get to the chapter on Spring!) Now that `MenuCreator` is set up, we can inject a mock `DailySpecialService` in a test.

### Creating a Test with Mocks

The basic steps for using a mock are as follows:

1. Create the mock object
2. Set the expectations for the mock object
3. Inject the mock object into the test object
4. Invoke the test object
5. Verify that the mock met the expectations

Steps one, two, and five are where the mocking frameworks are actually used. So theoretically, if you wanted to switch mocking frameworks in a codebase, you would only need to partially modify your tests.

### EasyMock

EasyMock has a somewhat confusing terminology of "record, replay, verify", so  I find it easier to think of it as "set up, enable, verify". Here is a test that uses EasyMock:

{lang="java", title="MenuCreatorTestEasyMock.java", starting-line-number=16, crop-start-line=16, crop-end-line=35}
<<(code/testing/icecream-app-mock/src/test/java/com/letstalkdata/iscream/MenuCreatorTestEasyMock.java)

Referring back to the general steps of mocking, the `EasyMock.createMock()` method is step one and `EasyMock.expect()` is step two. Our expectations are that the service is called only once, and that when it is called it returns the specials (in this case, just an empty list). The `EasyMock.replay()` method is next and it enables the mock object to be used. Steps three and four have nothing to do with EasyMock and they would be the same regardless of what framework we used. Finally, `EasyMock.verify()` asserts that the mock behaved as expected. When you run the test it should pass without any connection to the internet!

EasyMock has three different types of mocks: default, nice, and strict. The mock we used was a default mock, but there are times when you want a different type. Here's a comparison:

| Mock Type | Unexpected method call | Method call order | API method   |
|-----------|:----------------------:|:-----------------:|--------------|
| Default   | Disallowed             | Unenforced        | `mock`       |
| Nice      | Allowed                | Unenforced        | `niceMock`   |
| Strict    | Disallowed             | Enforced          | `strictMock` |

Nice mocks will also return default values for all non-mocked methods, i.e. `0`, `null`, or `false`.

### Mockito

Because Mockito started out as a fork of EasyMock, its concepts and terminology are similar. However, likely due to its simpler syntax and extended feature set, it has surpassed its predecessor in popularity.

{lang="java", title="MenuCreatorTestMockito.java", starting-line-number=23, crop-start-line=23, crop-end-line=42}
<<(code/testing/icecream-app-mock/src/test/java/com/letstalkdata/iscream/MenuCreatorTestMockito.java)

If you read the section on EasyMock, this should look very familiar. Referring back to the general steps of mocking, the `Mocktio.mock()` method is step one and `Mocktio.when()` is step two. We want the mock to return the specials (in this case, just an empty list) *when* it is called. As promised, steps three and four have nothing to do with mocking and thus remain unchanged when switching to Mockito. Finally, `Mocktio.verify()` asserts that the mock behaved as expected and `Mockito.times(1)` asserts that the mock method was called once[^mockito-times-default]. Again, this test passes without actually querying the web service.

You can control the mock behavior by passing one of the following constants into the `mock(Class, Answer)` method.

| Behavior | Description |
| -------- | ----------- |
| `CALLS_REAL_METHODS`  | Does not mock anything implicitly. |
| `RETURNS_DEEP_STUBS`  | Allows chaining mock calls, e.g. `mockEmployee.getManager().getName()`. |
| `RETURNS_DEFAULTS`    | **Default behavior**. A "nice" mock that returns `null`, empty, `0`, etc. |
| `RETURNS_MOCKS`       | Like `RETURNS_DEFAULTS`, but will also delegate down to return another mock. |
| `RETURNS_SELF`        | Returns the mock itself. Helpful for objects created with the builder pattern. |
| `RETURNS_SMART_NULLS` | Returns an "enhanced" `null` that does not throw a `NullPointerException`. |

Mockito also has some annotations that can make creating stubs very easy. A stub is different from a mock because a stub is not verified. Consider the following code:

{lang="java", title="MenuCreatorTestMockito.java", starting-line-number=43, crop-start-line=43, crop-end-line=58}
<<(code/testing/icecream-app-mock/src/test/java/com/letstalkdata/iscream/MenuCreatorTestMockito.java)

Here we don't care about the behavior of the `DailySpecialService`, we just need to create a stub so that the test `MenuCreator` doesn't try to make an HTTP call during unit testing. The `@Mock` annotation creates a nice (i.e. `RETURNS_DEFAULTS`) stub that returns convenient defaults.

In rare instances, you want a test double to call some of its real methods and some stubbed methods. For this, you can use the `@Spy` annotation. Beware, even the official documentation recommends avoiding overuse: "Real spies should be used carefully and occasionally, for example when dealing with legacy code... I wouldn't use partial mocks for new, test-driven & well-designed code."[^mockito-spies]

### PowerMock

If during testing you find yourself backed into a corner during where you need to simulate the behavior of static methods, you might consider reaching for the PowerMock tool. It sits on top of either EasyMock or Mockito and provides some additional functionality.

Here's how we can stub a method using EasyMock:

{lang="java", title="DailySpecialServiceTestPowerMock.java", starting-line-number=23, crop-start-line=23, crop-end-line=32}
<<(code/testing/icecream-app-mock/src/test/java/com/letstalkdata/iscream/service/DailySpecialServiceTestPowerMock.java)

And the same test using Mockito:

{lang="java", title="DailySpecialServiceTestPowerMock.java", starting-line-number=34,crop-start-line=34, crop-end-line=41}
<<(code/testing/icecream-app-mock/src/test/java/com/letstalkdata/iscream/service/DailySpecialServiceTestPowerMock.java)

Granted, neither of these tests are too imaginative. Normally you only use PowerMock when you are working with third-party or legacy code that cannot be properly refactored to avoid the static method calls. For example, imagine this (psuedo-code) situation where a web controller talks to a third-party session context:

{lang="java", line-numbers="off"}
~~~~~~~~
public class MyWebController {
  public void getHomePage() {
    WebApplicationSession.setVariable("user", new User());
    // ...
  }
}

public class MyWebControllerTest {
  @Test
  public void someTest() {
    MyWebController controller = new MyWebController();
    controller.getHomePage();
    //...
  }
}
~~~~~~~~

It may be desirable to statically stub `WebApplicationSession` to facilitate easier testing of the controller itself.

PowerMock can also mock final methods and classes, and private methods. For more details, check out the [PowerMock wiki](https://github.com/powermock/powermock/wiki).

## Summary

Clearly, the Java testing world is vast and diverse. Indeed there are still more frameworks out there that I did not mention which may suit your needs better. Having said that, JUnit dominates and it is what you are most likely to see in existing applications. TestNG has at times provided more features than JUnit, although now both frameworks are comparable. For test doubles (mocks, stubs, spies), Mockito is a good choice, although EasyMock is nearly as good, if a bit more verbose.

We've also seen that these frameworks integrate nicely with build tools making test execution just another step of the build process. For well-maintained codebases, you should be able to check out the code from source control and run the tests using Maven, Gradle, or Ant. If, on the other hand, you are working on legacy code, there's a good chance it will have no tests or a few spotty tests from a well-meaning developer who tried their best to introduce testing. If you want to add your own tests, you may find PowerMock useful to stub out difficult parts of the application to keep making forward progress.

## Suggested Resources

### General testing

Fowler, Martin. "Mocks Aren't Stubs." 2 January 2007.  
https://martinfowler.com/articles/mocksArentStubs.html. Accessed 5 May 2017.

Koskela, Lasse. *Test Driven: Practical TDD and Acceptance TDD for Java Developers*. Manning, 2007.  
https://www.manning.com/books/test-driven

Osherove, Roy. *The Art of Unit Testing*. Manning, 2013.  
https://www.manning.com/books/the-art-of-unit-testing-second-edition. (Note: This book uses C# for its examples, but the content is great, even if you just skim the code examples.)

### Framework-specific

Acharya, Sujoy, *Mastering Unit Testing Using Mockito and JUnit*. Packt, 2014.  
https://www.packtpub.com/application-development/mastering-unit-testing-using-mockito-and-junit

Shah, Deep. *Instant Mock Testing with PowerMock*. Packt, 2013.  
https://www.packtpub.com/application-development/instant-mock-testing-powermock-instant

Tahchiev, Petar, et. al. *JUnit in Action*. Manning, 2010.  
https://www.manning.com/books/junit-in-action-second-edition

[^resources]: Remember, `src/main/resources` and `src/test/resources` are automatically included in the classpath when using Maven or Gradle, so we don't have to do any extra work to use the files in those folders.

[^mockito-times-default]: Actually, the default assertion is that the method was called once so this can be omitted.

[^mockito-spies]: "Class Mockito". *Mockito 2.7.22 API*, https://static.javadoc.io/org.mockito/mockito-core/2.7.22/org/mockito/Mockito.html#spy. Accessed 14 May 2017.
