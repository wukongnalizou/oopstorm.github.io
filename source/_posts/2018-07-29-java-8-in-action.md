---
layout: pages
title: Java 8 in Action
date: 2018.07.29
tags: bookworms
---

### Java 8 in Action | [8.5](https://book.douban.com/subject/25912747/)

本书全面介绍了Java 8 这个里程碑版本的新特性，包括Lambdas、流和函数式编程。有了函数式的编程特性，可以让代码更简洁，同时也能自动化地利用多核硬件

* Multicore CPUs have separate caches (fast memory) attached to each processor core. Locking requires these to be synchronized, requiring relatively slow cache-coherency-protocol intercore communication.

* Keep in mind the idea of language ecosystem and the consequent evolve-or-wither pressure on languages. Although Java may be supremely healthy at the moment, you can recall other healthy languages such as COBOL that failed to evolve.

* Multicore processors aren’t fully served by existing Java programming practice.

* This is what **behavior parameterization** means: the ability to tell a method to take multiple behaviors (or strategies) as parameters and use them internally to accomplish different behaviors

* An interface is still a **functional interface** if it has many default methods as long as it specifies only one abstract method.

* What can you do with functional interfaces? Lambda expressions let you provide the implementation of the abstract method of a functional interface directly inline and treat the whole expression as an instance of a functional interface (more technically speaking, an instance of a concrete implementation of the functional interface). You can achieve the same thing with an anonymous inner class, although it’s clumsier: you provide an implementation and instantiate it directly inline.

* Note that the **@FunctionalInterface** annotation isn’t mandatory, but it’s good practice to use it when an interface is designed for that purpose. You can think of it like the @Override notation to indicate that a method is overridden.

* using an **IntPredicate** **avoids a boxing operation** of the value 1000, whereas using a **Predicate<Integer>** would box the argument 1000 to an Integer object:
```
public interface IntPredicat {
   boolean test(int t);
}
IntPredicate evenNumbers = (int i) -> i % 2 == 0;
evenNumbers.test(1000); // true (no boxing)

Predicate<Integer> oddNumbers = (Integer i) -> i % 2 == 1;
oddNumbers.test(1000); // false (boxing)
```
In general, the names of functional interfaces that have a specialization for the input type parameter are preceded by the appropriate primitive type, for example, DoublePredicate, IntConsumer, LongBinaryOperator, IntFunction, and so on. **The Function interface has also variants for the output type parameter: ToIntFunction<T>, IntTo-DoubleFunction, and so on**.

* Note that **none of the functional interfaces allow for a checked exception to be thrown**. You have **two options** if you need a lambda expression to throw an exception: define your own functional interface that declares the checked exception, or wrap the lambda with a try/catch block.

* If a lambda has a statement expression as its body, it’s compatible with a function descriptor that returns void (provided the parameter list is compatible too). For example, both of the following lines are legal even though the method add of a List returns a boolean and not void as expected in the Consumer context (T -> void):
```
// Predicate has a boolean return
Predicate<String> p = s -> list.add(s);
// Consumer has a void return
Consumer<String> b = s -> list.add(s);
```

* Lambdas are allowed to capture (that is, to reference in their bodies) instance variables and static variables without restrictions. But local variables have to be explicitly declared final or are effectively final.

* You may be asking yourself **why local variables have these restrictions**. **First**, there’s a key difference in how instance and local variables are implemented behind the scenes. If a lambda could access the local variable directly and the lambda were used in a thread, then the thread using the lambda could try to access the variable after the thread that allocated the variable had deallocated it. Hence, Java implements access to a free local variable as access to a copy of it rather than access to the original variable. This makes no difference if the local variable is assigned to only once—hence the restriction. **Second**, this restriction also discourages typical imperative programming patterns (which, as we explain in later chapters, prevent easy parallelization) that mutate an outer variable.

* a **closure** is an instance of a function that can reference nonlocal variables of that function with no restrictions

* Now Java 8 lambdas and anonymous classes do something similar to closures: they can be passed as argument to methods and can access variables outside their scope. But they have a restriction: they can’t modify the content of local variables of a method in which the lambda is defined. Those variables have to be implicitly final.

* Note that the precedence of methods and and or is managed from left to right using their positions in the chain. So **a.or(b).and(c) can be seen as (a || b) && c**.

* To exploit a multicore architecture and execute this code in parallel, you need only change stream() to **parallelStream()**

* the internal iteration in the Streams library can automatically choose a data representation and implementation of parallelism to match your hardware.

* When to use **findFirst** and **findAny**
You may wonder why we have both findFirst and findAny. The answer is parallelism. Finding the first element is more constraining in parallel. If you don’t care about which element is returned, use findAny because it’s less constraining when using parallel streams.

* Note that in the previous example, there are no guarantees about what type of Set is returned. But by using **toCollection**, you can have more control. For example, you can ask for a HashSet by passing a constructor reference to it:
```
Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType =
menu.stream().collect(
  groupingBy(Dish::getType, mapping(
   dish -> { if (dish.getCalories() <= 400) return CaloricLevel.DIET;
           else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
         else return CaloricLevel.FAT; },
   toCollection(HashSet::new) )));
```

* For example, you could do something like the following:
```
stream.parallel()
     .filter(...)
     .sequential()
     .map(...)
     .parallel()
     .reduce();
```
But **the last call to parallel or sequential wins and affects the pipeline globally**. In this example, the pipeline will be executed in parallel because that’s the **last call** in the pipeline.

* Parallel streams internally use the default ForkJoinPool (you’ll learn more about the fork/join framework in section 7.2), which **by default has as many threads as you have processors**, as returned by Runtime.getRuntime().availableProcessors().

* But you can change the size of this pool using the system property java.util.concurrent.ForkJoinPool.common.parallelism, as in the following example:
```
System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "12");
```
This is a **global setting**, so it will affect all the parallel streams in your code.

* This is quite disappointing: the parallel version of the summing method is much slower than the sequential one. How can you explain this unexpected result? There are actually two issues mixed together:
1. iterate generates boxed objects, which have to be unboxed to numbers before they can be added.
2. iterate is difficult to divide into independent chunks to execute in parallel.

* This demonstrates how parallel programming can be tricky and sometimes counterintuitive. When misused (for example, using an operation that’s not parallel-friendly, like iterate) it can actually worsen the overall performance of your programs, so it’s mandatory to understand what happens behind the scenes when you invoke that apparently magic parallel method.

* This also demonstrates that **using the right data structure** and then making it work in parallel guarantees the best performance.

* Watch out for boxing. **Automatic boxing and unboxing operations can dramatically hurt performance**. Java 8 includes primitive streams (IntStream, LongStream, and DoubleStream) to avoid such operations, so use them when possible.

* operations such as **limit** and **findFirst** that rely on the **order** of the elements are **expensive in a parallel stream**.

* Note that in a real-world application, it doesn’t make sense to use more than one **ForkJoinPool**. For this reason, what you typically should do is instantiate it only once and keep this instance in a static field, making it a **singleton**, so it could be conveniently reused by any part of your software.

* Note that the availableProcessors method, despite its name, in reality returns the number of available cores, including any virtual ones due to hyperthreading.

* The work-stealing algorithm used by the fork/join framework
![](https://user-images.githubusercontent.com/968283/44899423-545bb100-ad34-11e8-9c20-8f74f1085e2a.png)

* First, the meanings of **this** and **super** are different for anonymous classes and lambda expressions. Inside an anonymous class, this refers to the anonymous class itself, but inside a lambda it refers to the enclosing class. Second, anonymous classes are allowed to shadow variables from the enclosing class. Lambda expressions can’t (they’ll cause a compile error)

* You can solve the ambiguity by providing an explicit cast (Task):
```
doSomething((Task)() -> System.out.println("Danger danger!!"));
```

* built-in Java Logger class:
```
if (logger.isLoggable(Log.FINER)){
   logger.finer("Problem: " + generateDiagnostic());
}
logger.log(Level.FINER, "Problem: " + generateDiagnostic());
```
This is where lambda expressions can help. What you need is a way to **defer** the construction of the message so it can be generated only under a given condition (here, when the logger level is set to FINER).
```
logger.log(Level.FINER, () -> "Problem: " + generateDiagnostic());
```

* What’s the takeaway from the story? If you see yourself querying the state of an object many times in client code (for example, the state of the logger), only to call some method on this object with arguments (for example, log a message), then consider introducing a new method that calls that method (passed as a lambda or method reference) only after internally checking the state of the object. Your code will be more readable (less clutter) and better encapsulated (the state of the object isn’t exposed in client code)!

* Note that if a method reference refers to a method declared in the same class as where it’s used, then it will appear in the stack trace. For instance, in the following example
```
import java.util.*;

public class Debugging{
   public static void main(String[] args) {
       List<Integer> numbers = Arrays.asList(1, 2, 3);
       numbers.stream().map(Debugging::divideByZero).forEach(System
            .out::println);
   }

   public static int divideByZero(int n){
       return n / 0;
   }
}
```

* In the following code, you use peek to print the intermediate value before and after each operation in the stream pipeline:
![](https://user-images.githubusercontent.com/968283/44899442-5f164600-ad34-11e8-8bac-1d0e8c1014d3.png)
This will produce a useful output at each step of the pipeline:
```
from stream: 2
after map: 19
from stream: 3
after map: 20
after filter: 20
after limit: 20
from stream: 4
after map: 21
from stream: 5
after map: 22
after filter: 22
after limit: 22
```

* Static methods and interfaces
A common pattern in Java is to define both an interface and a utility companion class defining many static methods for working with instances of the interface. For example, Collections is a companion class to deal with Collection objects. Now that static methods can exist inside interfaces, such utility classes in your code can go away and their static methods can be moved inside an interface. These companion classes will remain in the Java API in order to preserve backward compatibility.

* Abstract classes vs. interfaces in Java 8
So what’s the difference between an abstract class and an interface? They both can contain abstract methods and methods with a body.

**First**, a class can extend only from one abstract class, but a class can implement multiple interfaces.

**Second**, an abstract class can enforce a common state through instance variables (fields). An interface can’t have instance variables.

* **Three resolution rules to know**
There are three rules to follow when a class inherits a method with the same signature from multiple places (such as another class or interface):
1.  Classes always win. A method declaration in the class or a superclass takes priority over any default method declaration.
2.  Otherwise, sub-interfaces win: the method with the same signature in the most specific default-providing interface is selected. (If B extends A, B is more specific than A)
3.  Finally, if the choice is still ambiguous, the class inheriting from multiple interfaces has to explicitly select which default method implementation to use by overriding it and calling the desired method explicitly.

* Java 8 introduces the new syntax **X.super.m(...)** where X is the superinterface whose method m you want to call. For example, if you want C to use the default method from B, it looks like this:
```
public class C implements B, A {
   void hello() {
       B.super.hello(); // Explicityly choosing to call the method from interface B
   }
}
```

* **Because the Optional class wasn’t intended for use as a field type, it also doesn’t implement the Serializable interface**. For this reason, using Optionals in your domain model could break applications using tools or frameworks that require a serializable model to work. Nevertheless, we believe that we showed why using Optionals as a proper type in your domain is a good idea, especially when you have to traverse a graph of objects that could be, all or in part, potentially not present. Alternatively, if you need to have a serializable domain model, we suggest you at least provide a method allowing access also to any possibly missing value as an optional, as in the following example:
```
public class Person {
   private Car car;
   public Optional<Car> getCarAsOptional() {
       return Optional.ofNullable(car);
   }
}
```

* you can think of an optional as a stream containing at most a single element

* Optional.**of**: Returns an Optional wrapping the given value or throws a NullPointerException if this value is null

* Note that, like streams, optionals also have primitive counterparts—**OptionalInt, OptionalLong, and OptionalDouble**—so the method in listing 10.6 could have returned an OptionalInt instead of Optional<Integer>

* We **discourage** using primitive optionals because they lack the map, flatMap, and filter methods

* it can invoke get on the Future. By doing so the client either unwraps the value contained in the Future (if the asynchronous task is already finished) or remains blocked until that value is available.

* It’s a good practice to always use a **timeout** to avoid similar situations elsewhere in your code

* To make the client aware of the reason the shop wasn’t able to provide the price of the requested product, you have to propagate the Exception that caused the problem inside the CompletableFuture through its **completeExceptionally** method.
![](https://user-images.githubusercontent.com/968283/44899460-689fae00-ad34-11e8-8df2-6fa8be1b76e2.png)

* the right pool size to approximate a desired CPU utilization rate can be calculated with the following formula:
```
Nthreads = NCPU * UCPU * (1 + W/C)
```
where

NCPU is the number of cores, available through Runtime.getRuntime().availableProcessors()
UCPU is the target CPU utilization (between 0 and 1), and
W/C is the ratio of wait time to compute time

* A Java program can’t terminate or exit while a normal thread is executing, so a leftover thread waiting for a never-satisfiable event causes problems. By contrast, marking a thread as a **daemon** means it can be killed on program termination.

* You’ve now seen **two different ways to do parallel computing on a collection**: either convert it to a parallel stream and use operations like map on it, or iterate over the collection and spawn operations within a CompletableFuture. The latter provides more control using resizing of thread pools, which helps ensure that your overall computation doesn’t block just because all of your fixed number of threads are waiting for I/O.

* Our advice for using these APIs is as follows:
1. If you’re doing computation-heavy operations with no I/O, then the Stream interface gives the simplest implementation and one likely to be the most efficient (if all threads are compute-bound, then there’s no point in having more threads than processor cores).
2. On the other hand, if your parallel units of work involve waiting for I/O (including network connections), then CompletableFutures give more flexibility and the ability to match the number of threads to the wait/computer, or W/C, ratio as discussed previously. Another reason to avoid using parallel streams when I/O waits are involved in the stream-processing pipeline is that the laziness of streams can make it harder to reason about when the waits actually happen.

* Note that using the **thenApply** method doesn’t block your code until the Completable-Future on which you’re invoking it is completed.

* The Java 8 CompletableFutures API provides the **thenCompose** method specifically for this purpose, allowing you to **pipeline two asynchronous operations**, passing the result of the first operation to the second operation when it becomes available. In other words, you can compose two CompletableFutures by invoking the thenCompose method on the first CompletableFuture and passing to it a Function. This Function has as argument the value returned by that first CompletableFuture when it completes, and it returns a second CompletableFuture that uses the result of the first as input for its computation.

* The allOf factory method takes as input an array of CompletableFutures and returns a CompletableFuture<Void> that’s completed only when all the CompletableFutures passed have completed. This means that invoking join on the CompletableFuture returned by the allOf method provides an easy way to wait for the completion of all the CompletableFutures in the original stream.
```
CompletableFuture.allOf(futures).join();
```

* You can **asynchronously consume from a synchronous API** by simply wrapping its invocation in a CompletableFuture.

* The class **LocalDate** is probably the first one you’ll come across when you start using the new Date and Time API. An instance of this class is an **immutable** object representing just a plain date without the time of day. In particular, it doesn’t carry any information about the time zone.

* **Instant** is intended for use only by a **machine**. It consists of a number of seconds and nanoseconds.

* If you want to define the **TemporalAdjuster** with a lambda expression, it’s preferable to do it using the **ofDateAdjuster** static factory of the TemporalAdjusters class that accepts a UnaryOperator<LocalDate> as follows:
```
TemporalAdjuster nextWorkingDay = TemporalAdjusters.ofDateAdjuster(
   temporal -> {
       DayOfWeek dow =
               DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
       int dayToAdd = 1;
       if (dow == DayOfWeek.FRIDAY) dayToAdd = 3;
       if (dow == DayOfWeek.SATURDAY) dayToAdd = 2;
       return temporal.plus(dayToAdd, ChronoUnit.DAYS);
   });
date = date.with(nextWorkingDay);
```

* In comparison to the old java.util.DateFormat class, all the DateTimeFormatter instances are **thread-safe**.

* The new `java.time.ZoneId` class is the replacement for the old java.util.TimeZone class.

* The region IDs are all in the format “{area}/{city}” and the set of available locations is the one supplied by the **IANA Time Zone Database**.

* Making sense of a ZonedDateTime
![ZonedDateTime](https://user-images.githubusercontent.com/968283/44899475-748b7000-ad34-11e8-9497-f8af329f045b.png)

* **it’s recommended to use LocalDate throughout your application, including all storage, manipulation, and interpretation of business rules, whereas you should employ Chrono-LocalDate only when you need to localize the input or output of your program**.

* The date-time objects of the new Date and Time API are all **immutable**.

* **TemporalAdjusters** allow you to manipulate a date in a more complex way than just changing one of its values, and you can define and use your own custom date transformations.

* The restrictions on “no visible side-effects” (no mutating structure visible to callers, no I/O, no exceptions) encode the concept of **referential transparency**. A function is referentially transparent if it always returns the same result value when called with the same argument value. The method String.replace is referentially transparent because "raoul".replace('r', 'R') will always produce the same result (the method replace returns a new String with all lowercase 'r' replaced with uppercase 'R') rather than updating its this object so it can be considered a function.

* Figures 13.5 and 13.6 illustrate the difference between the recursive and tail-recursive definitions of factorial.
![Figures 13.5](https://user-images.githubusercontent.com/968283/44899483-7bb27e00-ad34-11e8-8a9e-1d8108e5a61b.png)
![Figures 13.6](https://user-images.githubusercontent.com/968283/44899487-810fc880-ad34-11e8-9371-99b3a93c5092.png)


* The bad news is that **Java doesn’t support this kind of optimization**. But adopting tail recursion may be a better practice than classic recursion because it opens the way to eventual compiler optimization. Many modern JVM languages such as Scala and Groovy can optimize those uses of recursion, which are equivalent to iteration (they’ll execute at the same speed). This means that pure-functional adherents can have their purity cake and eat it efficiently too.

* In Java 8 it’s perfectly valid to store the method Integer.parseInt in a variable by using a method reference as follows:
```
Function<String, Integer> strToInt = Integer::parseInt;
```

* Functions (like Comparator.comparing) that can do at least one of the following are called **higher-order functions** within the functional programming community:
1. Take one or more functions as parameter
2. Return a function as result

* No existing data structure was harmed during the making of this update to the Tree.
![Figure 14.4.](https://user-images.githubusercontent.com/968283/44899501-8705a980-ad34-11e8-85cb-0b294fc99c0c.png)
