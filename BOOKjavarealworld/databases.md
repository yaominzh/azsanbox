# Chapter 8: Working with Databases

Knowing how to access and modify data stored in databases is critical for any Java developer. And while there is a trend towards services, most applications in production today still talk directly to the database (and of course those services have to do it eventually!).

As you would expect, Java has a myriad of options when it comes to data access. However, the choice generally comes down to whether or not you want to write SQL. If your team is comfortable with SQL, has complex data, or requires low-level SQL access, you are likely to see a lot of hand-coded SQL (hopefully) wrapped by a data framework. On the other hand, if your team has little SQL knowledge or if the data is reasonably simple, you might see an ORM solution with maybe only a few snippets of SQL for particularly gnarly problems. Obviously both approaches have their pros and cons, and you should be familiar with both flavors of data access.

## Java Database Connectivity (JDBC)

The Java Database Connectivity (or JDBC) API is the part of the Java standard library that (naturally) deals with database access. While it's obviously an option to use pure JDBC, in almost all cases you should use a data framework that abstracts away the lower-level details. However, there are a few objects you should be familiar with in the `java.sql` package:

 * `DriverManager`: Utility class that has knowledge of the available database drivers.
 * `Connection`: Represents a connection to a database and has information such as URL, username, password, etc. `Connection`s can be created from the `DriverManager`.
 * `PreparedStatement` and `CallableStatement`: These are used to send actual SQL to the database server. `Statement`s are created from `Connection`s.
 * `ResultSet`: How data is returned. A `ResultSet` is iterable and each object is a row. Data can be accessed by `getFoo(index)` or `getFoo(name)` methods where `Foo`  is the datatype, e.g. `String`, `Blob`, `Int`, etc. and `index` is the 1-indexed column number or `name` is the column name. `ResultSet`s are returned from `Statement`s.
 * `Date`, `Time` and `Timestamp`: The SQL representations of time data.

 Without going into too much detail, here is an example of how we might select all of the rows from a table and print out the information to the console using raw JDBC.

{lang="java", line-numbers="off"}
 ~~~~~~~~
// The JDBC URL tells Java where to find the database
String url = "jdbc:h2:mem:iscream;" +
                "DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
// Get a connection
try(Connection conn = DriverManager.getConnection(url, "sa", null)) {
    String sql = "select id, ingredient, unit_price from ingredient " +
            "where ingredient_type = 'ICE_CREAM'";
    // Create a statement from the connection
    try(PreparedStatement ps = conn.prepareStatement(sql)) {
        ResultSet rs = ps.executeQuery();
        // Iterate over the results
        while(rs.next()) {
            // Extract the values from each row
            Integer id = rs.getInt("id");
            String name = rs.getString("ingredient");
            Double price = rs.getDouble("unit_price");

            String msg = String.format("ID: %d, Name: %s, Price: %.2f",
                    id, name, price);

            System.out.println(msg);
        }
    }
}
~~~~~~~~

Naturally, there are better ways to work with databases!

## Spring JDBC Template

It should come as no surprise that Spring has several options for working with databases. The lowest-level option is the `JdbcTemplate` class which allows you to execute SQL without the fanfare of pure JDBC. A `JdbcTemplate` is backed by a `Datasource` and is thread-safe, so you can use the same instance throughout your application. It's usually best if you create a `Datasource` bean so that you can use `@Autowired` to automatically create the `JdbcTemplate`. At a minimum, a `Datasource` usually has a URL and a driver class name which will vary depending on the database server and driver that you are using. For my examples, I will use an in-memory H2 database which has the advantage of making the sample code immediately runnable. In a real application, you will usually be connecting to a server running MySql, PostgreSQL, SQL Server, etc. Furthermore, in a production application, it is important to configure your connection pool, but that is beyond the scope of this book. (For a great discussion on connection pools, see the [HikariCP Wiki](https://github.com/brettwooldridge/HikariCP/wiki).)

Q> ### But what is it, *really*?
Q>
Q> Spring JDBC Template is light-weight wrapper for the JDBC API that abstracts away most of the JDBC boilerplate. It works best if you are also using Spring Dependency Injection.

I> ### Tell Me More: H2
I>
I> H2 is a no-frills database server written in pure Java that prioritizes speed. Combined with its small size (~1.5MB), it makes a great choice for local testing. Indeed, an in-memory H2 database is usually so fast to spin up that it can make unit tests that talk to a database a reality. I also like to prototype applications with an in-memory H2 database because I can change the database schema very quickly and the included web console lets me easily inspect the data as a I work with the application. If enabled, you can access the console at `localhost:port/h2-console` while the application is running.

### A new data model for ISCream

Now that our application is going to be truly data-driven, we need to make a few changes to the model. Most notably, we need to start treating the components of an ice cream order as full objects. Here's the database schema that we will be working with throughout this chapter:

![The ISCream Schema](images/iscream_schema.png)

Accordingly, here's the changes to the Java classes:

{lang="java", title="Ingredient.java"}
<<(code/databases/spring-jdbc/src/main/java/com/letstalkdata/iscream/domain/Ingredient.java)

{lang="java", title="Flavor.java"}
<<(code/databases/spring-jdbc/src/main/java/com/letstalkdata/iscream/domain/Flavor.java)

{lang="java", title="Topping.java"}
<<(code/databases/spring-jdbc/src/main/java/com/letstalkdata/iscream/domain/Topping.java)

{lang="java", title="Order.java"}
<<(code/databases/spring-jdbc/src/main/java/com/letstalkdata/iscream/domain/Order.java)

### Querying Data

The `JdbcTemplate` class has [many different methods](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/jdbc/core/JdbcTemplate.html) depending on what kind of data you want to retrieve from the database. First you have to determine if you want to retrieve one "thing" or many "things". Then you need to determine how you want the data to be returned: as a Java built-in object (e.g. `String`), custom object, `Map`, or `SqlRowSet`. We're working with custom objects, which means we also need to implement a `RowMapper`. This sounds complicated, but it's very similar to the inside part of the loop in the JDBC section above.

{lang="java", title="IngredientService.java", starting-line-number=23, crop-start-line=23, crop-end-line=28}
<<(code/databases/spring-jdbc/src/main/java/com/letstalkdata/iscream/service/IngredientService.java)

By combining this mapper with some SQL, we can get back the flavors from the database as actual `Flavor` objects.

{lang="java", title="IngredientService.java", starting-line-number=30, crop-start-line=30, crop-end-line=34}
<<(code/databases/spring-jdbc/src/main/java/com/letstalkdata/iscream/service/IngredientService.java)

The beauty of Spring JDBC is that we don't have to worry about getting a database connection, statement, etc. Instead we focus only on the things that are specific to the application: the domain object and the SQL.

If we want to use parameterized SQL, it's as simple as passing the parameters to the method. Here's how we can retrieve one object by ID:

{lang="java", title="IngredientService.java", starting-line-number=36, crop-start-line=36, crop-end-line=42}
<<(code/databases/spring-jdbc/src/main/java/com/letstalkdata/iscream/service/IngredientService.java)

### Writing Data

For the most part, writing data with a `JdbcTemplate` is as straight-forward as reading data. For example, we could create an order like this:

{lang="java", line-numbers="off"}
~~~~~~~~
    final String sql = "insert into purchase(total_price) values (?)";
    jdbcTemplate.update(sql, BigDecimal.valueOf(4.25d));
~~~~~~~~

(Somewhat confusingly, the `update` method is used both for actual SQL `update`s and SQL `insert`s.)

But remember that our data model includes line items for each order. Moreover, those line items have to be linked appropriately using a foreign key. In order to do this properly, we need to hang on to the purchase ID after creating the purchase and use that to create the line items. We can do this using Spring's `KeyHolder`.

{lang="java", title="OrderService.java", starting-line-number=33, crop-start-line=33, crop-end-line=49}
<<(code/databases/spring-jdbc/src/main/java/com/letstalkdata/iscream/service/OrderService.java)

This is fine for the happy path, but what if an error happens while saving a line item? We want to avoid having bad or partial data in the database. This problem is normally solved using database transactions. You can use transactions with straight JDBC, but Spring comes to the rescue again by providing a simple `@Transactional` annotation that can be added to a method that should take place within the context of a transaction.

Here's the complete service that creates an order and its line items:

{lang="java", title="OrderService.java"}
<<(code/databases/spring-jdbc/src/main/java/com/letstalkdata/iscream/service/OrderService.java)

To see the service in action, you can look at a modified version of the ISCream web application in the sample code.

## MyBatis

MyBatis is similar to Spring JDBC in that it still relies on writing SQL. However, the important difference is that it uses reflection to more tightly couple your domain objects to database operations.

{icon=fast-backward}
G> ### Legacy Watch: iBATIS
G>
G> MyBatis has roots in an older project named "iBATIS" that was formally retired in 2010. Although it has the same core philosophy as MyBatis, it lacks many of MyBatis' features. If you're working on an iBATIS code base, you should still be able to find your way around after reading this section as the core concept of mappers is relatively unchanged. As per the usual suggestion, copy the working pattern you already see in the code base.

For all MyBatis operations, you will need a reference to a `SqlSession`. To properly create a `SqlSession`, you use a `SqlSessionFactory` which can be created using a `SqlSessionFactoryBuilder`! Most MyBatis projects will have a `mybatis-config.xml` file in the resources directory. This configuration file contains the information needed to get a connection to the database and can be passed into a `SqlSessionFactoryBuilder` to create a `SqlSessionFactory`. A new `SqlSession` should be created for each operation as it is not thread-safe.

Alternatively, you can use Spring to take care of this for you. If you have a `DataSource` bean in your application, Spring can inject a `SqlSession` wherever you need it. Furthermore, a Spring-created `SqlSession` *is* thread-safe and can be used in transactions with no additional configuration.

Q> ### But what is it, *really*?
Q>
Q> MyBatis is a tool that maps SQL to service and domain objects automatically using reflection.

### Querying Data

The primary object in a MyBatis operation is a "Mapper". The role of the Mapper is to translate between SQL and Java objects. A Mapper is a Java class backed by SQL that is stored either in an XML file or in the class itself. For example using XML:

{lang="xml", title="my-mapper.xml"}
~~~~~~~~
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.service.MyMapper">
    <select id="getEmployeeName" result="string">
        select name
        from Employee
        where id=#{id}
    </select>
</mapper>
~~~~~~~~

{lang="java", title="MyMapper.java"}
~~~~~~~
@Mapper
public interface MyMapper {
    String getEmployeeName(@Param("id") int id);
}
~~~~~~~

Or using just Java:

{lang="java", title="MyMapper.java"}
~~~~~~~~
@Mapper
public interface MyMapper {
    @Select("select name from Employee where id = #{id}")
    String getEmployeeName(@Param("id") int id);
}
~~~~~~~~

I generally prefer annotations to XML, but MyBatis is an exception. The XML mappers provide for more flexibility and options. Furthermore, I find it very helpful to be able to easily read the SQL in a mapper file and, often, copy it into a SQL editor to execute the query directly against the database.

What's really handy is that even though the Mappers are just interfaces, you don't need to manually implement them! MyBatis will create and use default implementations for you. Of course, you *can* implement them, should you need to provide a custom behavior.

{icon=thumbs-o-down}
G> ### Java Wart: Overly-layered database objects
G>
G> In some applications it makes sense to draw a distinction from your service layer and your data access layer. For example, you might choose to have a data access layer that knows only the core CRUD (create, read, update, delete) operations and a service layer that abstracts the operations into business concepts. Yet I've seen this taken to a frustrating extreme where each of those layers has an interface and exactly one implementation. This is not unheard of: A `MyServiceImpl` implements `MyService` and contains a reference to a `MyDao` which itself is implemented in `MyDaoImpl`! MyBatis can simplify this all down to one object, which I encourage unless you really need the flexibility.

As mentioned, MyBatis can work more closely with Java objects than JDBC. This means you can pass custom domain objects as parameters into a query and receive back custom domain objects as the result. This mapper shows how the ISCream application can get back the flavors and toppings as actual objects--no manual mapping required.

{lang="xml", title="ingredient-mapper.xml"}
<<(code/databases/mybatis/src/main/resources/ingredient-mapper.xml)

By aliasing the columns in the query to match the properties of the `Ingredient` class, MyBatis can use reflection to automatically create the correct object and call the correct setters. (You can also use a custom constructor instead of setters, but that requires some [additional configuration](http://www.mybatis.org/mybatis-3/sqlmap-xml.html) in the mapper file.) Naturally, the mapper XML file corresponds to a Mapper service object in the Java code. Again, no implementation required.

{lang="java", title="IngredientService.java"}
<<(code/databases/mybatis/src/main/java/com/letstalkdata/iscream/service/IngredientService.java)

### Writing Data

In the same way that Java data types and objects can be returned from a MyBatis query, Java data types and objects can be passed *into* MyBatis queries. This makes it relatively easy to save, update, or delete domain objects.

Again working with a mapper file, we create the following insert statements to save an order:

{lang="xml", title="purchase-mapper.xml"}
<<(code/databases/mybatis/src/main/resources/purchase-mapper.xml)

Again the `#{property}` syntax is used which will find the matching getter on a POJO or will be used as a key in a `Map`. Remembering that retaining the ID of the purchase is important, we can use the `keyColumn` and `keyProperty` attributes to set the ID on the POJO after the insert succeeds.

Because the process to create an order is somewhat complex, I chose to provide an implementation for the Purchase Mapper. This requires getting access to a `SqlSession`. Although I chose to use Spring, remember that it can be created manually from a `SqlSessionFactory`.

{lang="java", title="OrderService.java"}
<<(code/databases/mybatis/src/main/java/com/letstalkdata/iscream/service/OrderService.java)

Notice the use of Spring's `@Transactional` here. If Spring creates the `SqlSession` it can be used in transactions automatically. If you are not using Spring, you can turn off auto-commit and use `SqlSession.commit()` to perform operations inside of a transaction.

### Dynamic SQL

Another great feature of MyBatis is its support for dynamic SQL. A fairly common business need is for a user to select one or more items from a list and retrieve data about those items. In order to hit the SQL database once, you need to write a `where x in ...` query. I've seen lots of bad code where a developer tried to implement this manually using string concatenation. Usually the code opens up SQL injection vulnerabilities, doesn't properly handle trailing commas, doesn't handle strings, etc. MyBatis makes this easy:

{lang="xml", line-numbers="off"}
~~~~~~~~
<select id="getOrdersById" resultType="com.letstalkdata.iscream.domain.Order">
  select * from purchase
  where id in
  <foreach item="id" index="index" collection="orderIds"
      open="(" separator="," close=")">
        #{id}
  </foreach>
</select>
~~~~~~~~

Another similarly treacherous feature is to make a query with optional search parameters. Again, MyBatis makes this easy:

{lang="xml", line-numbers="off"}
~~~~~~~~
<select id="findIngredientsSearch"
     resultType="com.letstalkdata.iscream.domain.Ingredient">
  select * from ingredient
  <where>
    <if test="name != null">
        ingredient = #{name}
    </if>
    <if test="type != null">
        and ingredient_type = #{type}
    </if>
  </where>
</select>
~~~~~~~~

You see the full list of MyBatis dynamic SQL features on the [documentation website](http://www.mybatis.org/mybatis-3/dynamic-sql.html).

## Hibernate

We just saw how MyBatis can bind POJO domain objects to SQL statements, but there was still some overhead in manually writing out relatively basic SQL. Additionally, relationships between objects were annoying to work with. Object relational mappers (ORM) attempt to solve these problems by abstracting away all SQL from the developer. Theoretically, given enough information about the objects, it is possible for all SQL to be generated programatically.

Hibernate was one of the first ORM tools for Java. It was not until a few years later when persistence was standardized with the Java Persistence API (JPA). The JPA provides no implementation--rather it allows third-party tools to leverage a common API. And it just so happens that Hibernate is the most commonly used third-party implementation. Having said that, it is still possible to use Hibernate *without* using JPA. The sample code for this chapter includes two Hibernate projects: one that is more modern and leverages the JPA annotations and one that is an older style that uses XML for its configuration. To further complicate matters, you can use JPA interfaces or Hibernate's native classes to interact with the database. For convenience, the annotations code example uses JPA interfaces while the XML code example uses Hibernate native objects, but it's possible to mix and match. You should be familiar with all of these options as they are all still popular.

### Domain POJO adjustments

When using an ORM, the closer your domain objects match the database the less work you will need to do. While it is certainly possible to [handle abstract classes](http://docs.oracle.com/javaee/6/tutorial/doc/bnbqn.html#bnbqr) (e.g. an `ingredient` table with both `Flavor` and `Topping` classes), I don't want to muddy your understanding of the JPA with those details. Instead, let's change our domain objects to match the database tables.

![The ISCream Domain Objects](images/iscream_hibernate_domain.png)

### JPA Annotations

The JPA provides a set of annotations that are used to mark up domain classes to describe how they map to the underlying database. In general, classes are annotated `@Entity` and `@Table(name = "myDatabaseTableName")`, while properties are annotated `@Column(name = "myColumnName")`. Additional annotations are added to "special" columns such as ID columns and foreign key columns. Importantly, foreign keys are not represented as integers but rather as the objects themselves. For example, the `OrderLineItem` class does not have a `purchase_id` property, it has an `Order` property. Here's the complete annotated properties of that class.

{lang="java", title="OrderLineItem.java", starting-line-number=10, crop-start-line=10, crop-end-line=24}
<<(code/databases/hibernate-annotations/src/main/java/com/letstalkdata/iscream/domain/OrderLineItem.java)

Focusing on the two foreign-keyed properties, notice that `@JoinColumn` is used to signify it is used in a SQL join and that the relationship is described as `@ManyToOne` since there are many `OrderLineItem`s for each `Order`. The `fetch = FetchType.Lazy` instructs Hibernate (or more accurately, any JPA provider you're using) to not load the actual `Order` object until it is explicitly asked for. `Lazy`'s opposite is `FetchType.Eager` which would load the `Order` as soon as the `OrderLineItem` is returned from the database.

It's not required, but I chose to create a bi-directional relationship between an order and its line items. Here's how `Order` is annotated.

{lang="java", title="Order.java", starting-line-number=14, crop-start-line=14, crop-end-line=26}
<<(code/databases/hibernate-annotations/src/main/java/com/letstalkdata/iscream/domain/Order.java)

The new annotation here is `@OneToMany`: there is one `Order` for many `OrderLineItem`s. The `mappedBy` attribute tells Hibernate that it should use the value `order` to find a setter, i.e. `setOrder` when returning `OrderLineItem`s, and the attribute `cascade` is used to describe the behavior of child objects when an operation occurs on the parent. Using `CascadeType.ALL` means updates, inserts, and deletes on an `Order` will cascade down to child `OrderLineItem`s.

As you can imagine, there are **many** annotations to fit the wide variety of relationships in databases. You might consider browsing the [JPA JavaDoc](http://docs.oracle.com/javaee/7/api/javax/persistence/package-summary.html) to get a feeling for what is possible.

I> ### Tell Me More: JPA implementations
I>
I> While Hibernate is far and away the most popular JPA implementation, it's not the only one. The other main options are [EclipseLink](http://www.eclipse.org/eclipselink/#jpa) and [OpenJPA](http://openjpa.apache.org). All the implementations include their own annotations that provide advanced features beyond the JPA. So while Hibernate is usually the default choice, you might choose another implementation if you need access to a particular feature.

When using JPA, Hibernate is usually configured using a `persistence.xml` file or programatically with Java. Neither of these options are proprietary, and therefore could be used with other JPA providers. For more details, see "Bootstrapping" in the [official documentation](http://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html). Finally, if you use Spring Boot, the configuration is mostly taken care of automatically with just a few values set in `application.yml` / `application.properties`.

### XML Mappings

The older method of mapping objects for Hibernate was via--you guessed it--XML. You will have one `MyClassName.hbm.xml` file for each entity class in the application and it will need to be on the classpath. Each file lists the properties of the class (e.g. `<property ...`) and the relationships (e.g. `<one-to-many ...`). Here's the mappings for `Order` and `OrderLineItem`. If you compare them to the JPA-annotated classes, you should notice similar terminology and structure, although some of the vocabulary does not match.

{lang="xml", title="Order.hbm.xml"}
<<(code/databases/hibernate-xml/src/main/resources/Order.hbm.xml)

{lang="xml", title="OrderLineItem.hbm.xml"}
<<(code/databases/hibernate-xml/src/main/resources/OrderLineItem.hbm.xml)

When using the mappings, you have to register them with Hibernate via configuration. In most instances, Hibernate is configured programatically with Java, a `hibernate.properties` file, or a `hibernate.cfg.xml` file. For more details on hibernate configuration see "Legacy Bootstrapping" in the [official documentation](http://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html#appendix-legacy-bootstrap).

### Writing Data

#### JPA

As you can imagine, the `OrderService` for JPA is going to look considerably different from what we have seen so far. The JPA object that does the heavy lifting is an `EntityManager`. Similar to MyBatis, one way to obtain an `EntityManager` is via `EntityManagerFactoryBuilder > EntityManagerFactory > EntityManager`. The `EntityManager` has three methods that *loosely* map to "insert", "update", and "delete": `persist`, `merge`, and `remove`.[^persist-merge]

Here's where the real power of ORMs is revealed: no SQL was written, but using the annotations, the JPA provider is able to create the proper inserts to save the `Order` *and* its child `OrderLineItem`s! This means saving an object is usually just one line of code:

{lang="java", title="OrderService.java", starting-line-number=20, crop-start-line=20, crop-end-line=23}
<<(code/databases/hibernate-annotations/src/main/java/com/letstalkdata/iscream/service/OrderService.java)

#### Native Hibernate

If you are not using JPA, the worker class is a Hibernate `SessionFactory` which can be used to create `Session`s. One Hibernate `Session` is used per unit-of-work. This is deliberately vague--a unit of work is typically larger than a single database round-trip and may contain a few tightly related operations. For example, a unit of work might be one user request in a web application.

In addition to `merge` and `persist`, a Hibernate session also has some operations like `save`, `update`, and `saveOrUpdate`. Sadly, these are also non-trivial. For a brief discussion, see ["Hibernate: save, persist, update, merge, saveOrUpdate"](http://www.baeldung.com/hibernate-save-persist-update-merge-saveorupdate) on Baeldung.

The code to save an object using native Hibernate is still relatively short, but remember to (auto) close the `Session`.

{lang="java", title="OrderService.java", starting-line-number=20, crop-start-line=20, crop-end-line=26}
<<(code/databases/hibernate-xml/src/main/java/com/letstalkdata/iscream/service/OrderService.java)

### Reading Data

Using an ORM, it's possible to say "Save this object!" because the object has annotations, but saying "Get an object!" is not straight-forward. What filters should be applied? Do you want one object or many? Do you want child objects? Etc. Because of these questions, you need to construct a query.

With Hibernate, you have a few options: use a `Criteria` object, write Hibernate Query Language (HQL), or write native SQL. (In general, you should avoid native SQL, since that really defeats the purpose of an ORM framework. But there are definitely situations where it cannot be (easily) avoided.) Here's how the three methods compare:

**Criteria**:

{lang="java", line-numbers="off"}
~~~~~~~~
private List<Ingredient> getIngredients(Ingredient.Type type) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Ingredient> criteriaQuery =
            cb.createQuery(Ingredient.class);
    Root ingredient = criteriaQuery.from(Ingredient.class);
    criteriaQuery.where(cb.equal(ingredient.get("type"), type));
    TypedQuery<Ingredient> query = entityManager.createQuery(criteriaQuery);
    return query.getResultList();
}
~~~~~~~~

**HQL**:

{lang="java", line-numbers="off"}
~~~~~~~~
private List<Ingredient> getIngredients(Ingredient.Type type) {
    String hql = "select i from Ingredient i where type =:type";
    Query query = entityManager.createQuery(hql);
    query.setParameter("type", type);
    @SuppressWarnings("unchecked")
    List<Ingredient> ingredients =
            (List<Ingredient>) query.getResultList();
    return ingredients;
}
~~~~~~~~

**Native SQL**:

{lang="java", line-numbers="off"}
~~~~~~~~
private List<Ingredient> getIngredients(Ingredient.Type type) {
    String sql = "select * from ingredient where ingredient_type = ?";
    Query query = entityManager.createNativeQuery(sql, Ingredient.class);
    query.setParameter(1, type.name());
    @SuppressWarnings("unchecked")
    List<Ingredient> ingredients =
            (List<Ingredient>) query.getResultList();
    return ingredients;
}
~~~~~~~~

If you are using native Hibernate instead of JPA, the code is largely the same except that a `Query` is an `org.hibernate.Query`, not a `javax.persistence.Query` and is created from `Session.createQuery()`.

Deciding which method to use isn't always straight forward. I already mentioned that native SQL should probably be avoided. Using native SQL prevents Hibernate from doing any checks, and you are saying, "Trust me, I know what I'm doing, just execute the SQL!". However, there are legitimate reasons to use native SQL. If you have a complex query or if you need to retrieve data that doesn't map to an `Entity`, it's your best choice. On the other end of the spectrum is the `CriteriaBuilder` which lets you retrieve data without writing SQL and is completely type-safe. The down side is that it is fairly verbose and difficult to read. I personally find myself using the middle ground of HQL most often. HQL looks similar to SQL, which I find easier to read than the criteria, at the cost of some type safety.

## Summary

Many Java applications interact with a database and the Java standard library provides only the most basic database connection tools. While some codebases still rely on pure JDBC, there's a range of frameworks available to make database interaction more pleasant.

Spring JDBC provides some handy functionality on top of JDBC, but still relies on manually translating between domain objects and SQL. MyBatis handles most of that translation on its own and supports storing SQL outside of the code. Finally, Hibernate attempts to abstract away all (or at least most) SQL from the developer. Most modern Hibernate projects will take advantage of JPA annotations, while older projects may still be using XML mappings.

Many words have been written on which data solution is best, but the reality is a case can be made for all of them. As such, it's important to familiarize yourself with the different approaches. You're likely to come across all of them sooner or later.

## Suggested Resources

Bauer, Christian, Gavin King, and Gary Gregory. *Java Persistence with Hibernate, Second Edition*. Manning, 2015.  
https://www.manning.com/books/java-persistence-with-hibernate-second-edition.

"Lesson: JDBC Basics". Oracle, 2015.  
https://docs.oracle.com/javase/tutorial/jdbc/basics/.

Mihalcea, Vlad. *High-Performance Java Persistence*. Leanpub, 2017.  
https://leanpub.com/high-performance-java-persistence.

Prajapati, Yogesh and Vishal Ranapariya. *Java Hibernate Cookbook*. Packt, 2015.  
https://www.packtpub.com/application-development/java-hibernate-cookbook.

Reddy, K. Siva Prasad. *Java Persistence with MyBatis 3*. Packt, 2013.  
https://www.packtpub.com/application-development/java-persistence-mybatis-3.

[^persist-merge]: The `persist` and `merge` methods are actually non-trivial and [this StackOverflow answer](https://stackoverflow.com/questions/1069992/jpa-entitymanager-why-use-persist-over-merge) does a good job discussing the differences.
