# Jsync
Java Async/Await support as syntactic sugar, Try to look and feel like awaiting in JavaScript and C# languages

## Description
In parallel and Thread context there are three type of programming languages:
 - Some languages (or environments) use green threads by scheduling a carrier thread between multiple virtual threads. like GoLang, Erlang & etc...
 - Some languages behave non-blocking and use the main thread in shared manner by releasing the thread on I/O delays among executing the task. like JavaScript & so on...
 - But there are some others which blocks the thread until the execution finishes. like C#, Java, Python, PHP & etc...

### The blocking programming languages
Using threads is costly in OSes as it consumes much processing time for initializing a thread or consumes much memory for retaining them, For solving this issue many solutions are getting used as best practices like using ThreadPool and etc.. which one of them is Reactor pattern (the pattern the JS uses) that specifically makes the web services not getting tightly binded to using thread-per-request and shares the threads between tasks.

### How does the Reactor pattern looks like
We are not going in depth to Reactor pattern but the Reactor pattern is the main mechanism in JavaScript and NodeJs which calls an async task and passes a callback method and keeps running over other tasks until the callback methods get triggered which means the async task got finished.
The callback method got revolutionized to another concept named Promise and finally in today world it uses a mechanism called async/await syntactic sugar.
The C# language is supporting this feature too with both of asynchronous Task and also async/await syntactic sugar.
Fortunately there is powerful features and third party projects for Java to support reactive programming using CompletableFuture, Reactor Project, RxJava and etc...
But Unfortunately the Java language do not support the flat async programming by leveraging something like async/await syntactic sugar, and it seems there is no attraction for Oracle to develop this feature.

### What we are going to do
This project is a simple try on Nightly Builds and WeekEnd tries open source module to get this feature supported not same as other languages support but look and feel like them.

## MileStones

| MileStone                                       | Status |
|-------------------------------------------------|:------:|
| Annotation Processing @async/@await             |   🔄   |
| Support ReactorProject and generate code for it |   🔄   |
| Support ReactorProject and generate code for it |   ⏸️   |



## How to use this project
The current vision for the final usage of examples looks loke the bellow code sample

Please note that this is just a raw perspective and may get changed:

```java

public class MyAsyncAwaitExample {

    public @async Integer asyncAwaitExample() {
        @await String a = this.asyncMethod();
        @await Integer b = this.anotherAsyncMethod(a);

        return b;
    }

    public @async String asyncMethod() {
        // Implementation...
    }

    public @async Integer anotherAsyncMethod(String input) {
        // Implementation...
    }
}

```


For more information please go to Wiki documents of the project (currently not enabled)






