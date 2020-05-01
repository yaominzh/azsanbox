# Chapter 10: Useful Third-Party libraries

While the Java standard library is quite robust, there are a few notable augmentations that the open source community has contributed that you should be familiar with. Since there's no support for JSON in the standard library, we'll first look at Google's Gson and FasterXML's Jackson. Next we'll review some general purpose libraries: Google's Guava and Apache Commons. Finally a brief Legacy Watch about Joda time, the (sane) pre-Java 8 way of working with dates and times.

## JSON Support

In order to demonstrate the translation between POJOs and JSON, we're going to add two new controller routes to the ISCream web application. First will be an endpoint that shows all of the orders in the database and second is an endpoint for creating orders via JSON. However, these are examples for learning how to work with JSON libraries, **they are not examples of RESTful web services**! For information on how to build a true web service in Spring Boot, see ["Building a RESTful Web Service"](https://spring.io/guides/gs/rest-service/).

### Google Gson

All Gson operations rely on having an instance of `com.google.gson.Gson` available. `Gson` objects are thread-safe, so typically you only need to create one for the whole application. Although you can use `new Gson()`, the defaults are almost always not what you want. Instead, you can customize the object using a `GsonBuilder`. Here's one example:

{lang="java",title="OrderController.java", starting-line-number=64, crop-start-line=64,crop-end-line=67}
<<(code/third-party/gson/src/main/java/com/letstalkdata/iscream/controller/OrderController.java)

One you have the `Gson` object you can use the `fromJson` and `toJson` methods to translate between POJOs and JSON. These methods are overloaded to work with different objects, but I find myself most often using `Gson.fromJson(String json, Class<T> classOfT)` and `Gson.toJson(Object src)`.

Here's the new controller routes:

{lang="java",title="OrderController.java", starting-line-number=69,crop-start-line=69,crop-end-line=85}
<<(code/third-party/gson/src/main/java/com/letstalkdata/iscream/controller/OrderController.java)

You can use the UI to create an order (`/orders/new`) and then navigate to the new endpoint to see your order.

{lang="javascript", line-numbers="off"}
~~~~~~~~
[
  {
    "id": 1,
    "flavor": {
      "id": 2,
      "name": "Chocolate",
      "unit_price": 1.5000
    },
    "scoops": 2,
    "toppings": [
      {
        "id": 5,
        "name": "Cherry",
        "unit_price": 0.2500
      },
      {
        "id": 8,
        "name": "Sprinkles",
        "unit_price": 0.2500
      }
    ]
  }
]
~~~~~~~~

You can also use a tool like Postman or `curl` to `POST` an order like this to the database at `/orders/fromJson`.

{lang="javascript", line-numbers="off"}
~~~~~~~~
{
  "flavor": {
    "id": 2,
    "name": "Chocolate",
    "unit_price": 1.5000
  },
  "scoops": 2,
  "toppings": [
    {
      "id": 5,
      "name": "Cherry",
      "unit_price": 0.2500
    },
    {
      "id": 7,
      "name": "Peanuts",
      "unit_price": 0.2500
    }
  ]
}
~~~~~~~~

If you look at the example code you'll see that I did not use Hibernate. The reason for that is Gson does not have a simple way to ignore a single property and there is a circular reference between an `Order` and its `OrderLineItems`s. In the next section, we'll see how Jackson plays a little nicer with Hibernate.

### Jackson

Jackson is similar to Gson in that JSON operations use a single thread-safe object to do all of the work. For Jackson, this is called a `com.fasterxml.jackson.databind.ObjectMapper`. But unlike Gson, the configuration for (de)serialization is generally not done at the `ObjectMapper` level, it is done on each object.

Like many of the other tools we've looked at, Jackson uses annotations for configuration. By default, getters and setters are used to determine what fields an object should have and therefore no annotations are required if you are OK with the default settings. However, most applications that use JSON will have some special requirements about how the JSON should be formed, especially web services. We will use the `@JsonProperty(String name)` and `@JsonIgnore` annotations, but keep in mind there are [many more](https://github.com/FasterXML/jackson-annotations/wiki/Jackson-Annotations) to suit your needs.

In the `OrderLineItem` class we need to ignore the `Order` reference due to the circular reference mentioned above.

{lang="java",title="OrderLineItem.java", starting-line-number=17, crop-start-line=17,crop-end-line=20}
<<(code/third-party/jackson/src/main/java/com/letstalkdata/iscream/domain/OrderLineItem.java)

And since Jackson uses getters to determine which properties to serialize, we also need to ignore the calculated `getLineItemCost` method.

{lang="java",title="OrderLineItem.java", starting-line-number=71, crop-start-line=71,crop-end-line=76}
<<(code/third-party/jackson/src/main/java/com/letstalkdata/iscream/domain/OrderLineItem.java)

I prefer to use `snake_case` for JSON, so by using `@JsonProperty` I can alter the name of certain properties[^snake-case]. For example:

{lang="java",title="Order.java", starting-line-number=77, crop-start-line=77,crop-end-line=78}
<<(code/third-party/jackson/src/main/java/com/letstalkdata/iscream/domain/Order.java)

The `ObjectMapper` has several `readValue` and `writeValue` methods that are analogous to Gson's `fromJson` and `toJson`. This is how they are used in the controller:

{lang="java",title="OrderController.java", starting-line-number=67, crop-start-line=64,crop-end-line=86}
<<(code/third-party/jackson/src/main/java/com/letstalkdata/iscream/controller/OrderController.java)

Deciding between the two libraries mostly comes down to what features you will need. To me, Gson is simpler to set up, but lacks the flexibility of Jackson. And if performance is a concern, you should know that Jackson generally out-performs for large files and Gson generally out performs for small files.[^json-perf]

## Utility Libraries

There are many general purpose utility libraries for Java and indeed, your company may have one of its own. Two that I will highlight are Guava and Apache Commons.

### Guava

#### Collections

Guava provides many handy utility methods for creating collections. Consider how often you have had to create a small collection of objects. Instead of this...

{lang="java", line-numbers="off"}
~~~~~~~~
List<String> myList = Arrays.asList("blue", "green", "yellow");

Set<String> mySet = new HashSet<>();
mySet.add("blue");
mySet.add("green");
mySet.add("yellow");
~~~~~~~~

...you can do this:

{lang="java", line-numbers="off", crop-start-line=15, crop-end-line=16}
<<(code/third-party/guava/src/test/java/com/letstalkdata/Collections.java)

Of course, if you are using Java 9, you might as well use the built-in `List.of()` method.

New collection types are also included in the library, such as Multiset, BiMap, and Table.

{lang="java", line-numbers="off", crop-start-line=21, crop-end-line=43}
<<(code/third-party/guava/src/test/java/com/letstalkdata/Collections.java)

#### Strings

Splitting strings can usually be done with `String.split()`, but its behavior isn't consistent and can be frustrating to use with user-provided data. The `Splitter` class is explicit in how it splits strings because you configure it.

{lang="java", line-numbers="off", crop-start-line=14, crop-end-line=24}
<<(code/third-party/guava/src/test/java/com/letstalkdata/Strings.java)

There are also utility methods to convert between casing:

{lang="java", line-numbers="off", crop-start-line=29, crop-end-line=30}
<<(code/third-party/guava/src/test/java/com/letstalkdata/Strings.java)

#### Caches

You've probably heard this before: "There are only two hard things in Computer Science: cache invalidation and naming things." Well, Guava tries to make the first a little easier. Caches are handy for numerous things, but are tricky to implement correctly. Letting Guava do the heavy lifting means you are less likely to make a mistake.

{lang="java", line-numbers="off", crop-start-line=15, crop-end-line=33}
<<(code/third-party/guava/src/test/java/com/letstalkdata/Cache.java)

#### Other

There's a lot more to Guava than I covered here, so it is definitely worth poking around the [Wiki](https://github.com/google/guava/wiki). Bear in mind that some of the classes in Guava are not very helpful if you are using the latest version of Java. For example, `com.google.common.base.Optional` is largely replaced by `java.util.Optional`.

### Apache Commons

The Apache Commons libraries are a vast collection of tools to help Java developers with common tasks. In fact, we already saw one library in the Logging chapter. There are some other utilities sprinkled throughout, of which I will highlight a few.

#### `commons.lang` Utils

The `commons.lang` library is designed to extend the classes in the `java.lang` package. The `StringUtils` class extends many of the methods in `String` to be null-safe. For example:

{lang="java", line-numbers="off", crop-start-line=21, crop-end-line=22}
<<(code/third-party/commons/src/test/java/com/letstalkdata/LangUtils.java)

It also has helpful methods for common `String` tasks:

{lang="java", line-numbers="off", crop-start-line=26, crop-end-line=36}
<<(code/third-party/commons/src/test/java/com/letstalkdata/LangUtils.java)

For the life of me, I do not know why `java.util.Random` doesn't have a method for generating random numbers in a range. `RandomUtils` adds those methods.

{lang="java", line-numbers="off", crop-start-line=41, crop-end-line=42}
<<(code/third-party/commons/src/test/java/com/letstalkdata/LangUtils.java)

The `ClassUtils` class eases working with actual classes--and it doesn't use reflection so it's fast.

{lang="java", line-numbers="off", crop-start-line=47, crop-end-line=48}
<<(code/third-party/commons/src/test/java/com/letstalkdata/LangUtils.java)

#### `commons.collections`

The Commons Collections library has a few new collection types and utilities. Like Guava, there's a bi-directional map:

{lang="java", line-numbers="off", crop-start-line=15, crop-end-line=19}
<<(code/third-party/commons/src/test/java/com/letstalkdata/Collections.java)

There are also `SetUtils`, `ListUtils`, and `MapUtils` classes for working with their respective collection types. Here's an example of a set utility operation:

{lang="java", line-numbers="off", crop-start-line=27, crop-end-line=32}
<<(code/third-party/commons/src/test/java/com/letstalkdata/Collections.java)

#### `commons.io` Utils

Java IO operations are notoriously verbose. Some of this was improved with the `nio` package released in Java 7, but many common tasks are still tedious. The Apache Commons IO library abstracts much of the boilerplate code for you. This library is particularly useful when you are working with another library that doesn't give you data in the way you want. For example, let's suppose you are given an `InputStream` but you want a `String`.

{lang="java", line-numbers="off", crop-start-line=16, crop-end-line=23}
<<(code/third-party/commons/src/test/java/com/letstalkdata/InputOutputUtils.java)

Similarly, sometimes you want to copy streams to `Writer`s or `Reader`s.

{lang="java", line-numbers="off", crop-start-line=25, crop-end-line=30}
<<(code/third-party/commons/src/test/java/com/letstalkdata/InputOutputUtils.java)

The `FileUtils` class gives some helpful methods for working with files and directories. For example, we can pull a `File` into a `String` with one line:

{lang="java", line-numbers="off", crop-start-line=35, crop-end-line=40}
<<(code/third-party/commons/src/test/java/com/letstalkdata/InputOutputUtils.java)

You can also walk through a directory and optionally ignore certain files or directories:

{lang="java", line-numbers="off", crop-start-line=44, crop-end-line=49}
<<(code/third-party/commons/src/test/java/com/letstalkdata/InputOutputUtils.java)

#### Other

Like Guava, the Apache Commons is vast and I couldn't possibly cover all of the helpful things. Take some time to glance over the [other packages](https://commons.apache.org) and keep them in mind for the next project you work on.

## Joda Time

{icon=fast-backward}
G> ### Legacy Watch
G>
G> Prior to Java 8, the Java date and time libraries were error-prone and cumbersome to use. This was the motivation for the creators of Joda Time to create a user-friendly and safe datetime library. Java 8 introduced the `java.time` package (which borrows heavily from Joda Time) and fixes the problems with the old `java.util.Date` class. However, even if your organization has migrated to Java 8, you might still find Joda Time if the project pre-dates Java 8.

At the core of Joda Time is the `Instant` class which represents a point in time along the unix epoch timeline. You typically won't need to create `Instant`s, but you might use them to convert Joda Time objects into other Joda Time objects.

{lang="java", line-numbers="off", crop-start-line=10, crop-end-line=16}
<<(code/third-party/joda-time/src/test/java/com/letstalkdata/JodaTime.java)

The `DateTime` object is extremely powerful and is likely the most common object you'll use in most applications. It can be created manually from integers, parsed from strings, translated from `Instant`s, etc.

{lang="java", line-numbers="off", crop-start-line=21, crop-end-line=25}
<<(code/third-party/joda-time/src/test/java/com/letstalkdata/JodaTime.java)

A `Period` is a duration between two time points and is convenient for determining how far apart events are. It's not millisecond-precise, so it is usually used for differences humans experience such as the number of days until an event or calculating someone's age from their birth date. (If you need millisecond-precision, use `Duration`.) Importantly it handles Daylight Savings Time and leap years properly! While `Period` is a concrete class, it's often more convenient to work with the `Years`, `Months`, `Days`, etc. subclasses.

{lang="java", line-numbers="off", crop-start-line=30, crop-end-line=33}
<<(code/third-party/joda-time/src/test/java/com/letstalkdata/JodaTime.java)

Handling time zones can be annoying, but Joda Time doesn't consider them an after-thought. `DateTime` objects will use the system's time zone unless otherwise specified. For clarity, when creating a `DateTime`, it's usually best to use one of the overloaded constructors that accepts a time zone, even if that zone is always the same.

{lang="java", line-numbers="off", crop-start-line=38, crop-end-line=44}
<<(code/third-party/joda-time/src/test/java/com/letstalkdata/JodaTime.java)

A final nice feature of Joda Time is that all of its objects are immutable and thread-safe, unless you explicitly choose to work with `MutableDateTime`.

{icon=thumbs-o-down}
G> ### Java Wart: `java.util.Date`
G>
G> Just what's so bad about the old `Date` class that motivated Joda Time? Well, a lot. Jon Skeet has a [nice summary on his blog](https://codeblog.jonskeet.uk/2017/04/23/all-about-java-util-date/), but perhaps most frustrating is the disconnect between the class names and semantics. A `java.util.Date` is not a date at all--it's an instant in time. As such operations like `getMonth()` or `toGMTString()` don't make sense. And in fact, those methods (and many others) have been deprecated accordingly.

## Summary

Guava and Apache Commons can help reduce boilerplate, extend the functionality of the core Java language, and reduce errors. You shouldn't need these libraries for every project, but they can be very helpful for large codebases. Also keep in mind that you can very easily grow a project just by adding a couple of commons dependencies. So use the libraries judiciously and make sure you're really going to benefit from them before adding them to the project. Finally if you are working on a legacy (pre-Java 8) application, Joda Time should almost always be preferred to the standard library date classes.

## Suggested Resources

"User Guide", *Google Guava*, 2016 October 24.  
https://github.com/google/guava/wiki.

"User Guide", *Joda Time*, 2017 March 23.  
http://www.joda.org/joda-time/userguide.html

"Welcome to Apache Commons", *Apache Commons*, 2017 August 1.  
https://commons.apache.org.

[^snake-case]: You could also use `MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)` to set the behavior globally.

[^json-perf]: Dreyfuss, Josh. "The Ultimate JSON Library: JSON.simple vs GSON vs Jackson vs JSONP", *Takipi Blog*, 28 May 2015. http://blog.takipi.com/the-ultimate-json-library-json-simple-vs-gson-vs-jackson-vs-json/. Accessed 9 September 2017.
