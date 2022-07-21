This repository is closed. The ideas and partly the source code was migrated to real implementation that you can found at [Proxycian repository](https://github.com/FgForrest/Proxycian) and also published to Maven Central for general usage.

# Java Dynamic Proxy generation in runtime

This project is a demo / sandbox project for my presentation at 
[Java Days CZ in autumn 2016](https://novoj.github.io/reveal.js/runtime-class-generation.html#/). It is in no way meant
to be used as production code but only as a demonstration of a technique using Java dynamic proxies for automatic 
implementation of the logic behind standard [DAO classes](https://en.wikipedia.org/wiki/Data_access_object). Dynamic
proxies are used to solve much more scenarios than this and things you'll learn in this demo can be of use even for
different use-cases. Typical use-cases of dynamic proxies are:

- lazy delegation (delegate calls to something you'll know later)
- transaction handling
- security handling
- bridge patterns
- and much more

You'll find them in [Spring AOP](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/aop.html), 
[Spring data](http://projects.spring.io/spring-data/), [Hibernate](http://hibernate.org/), [MyBatis](http://www.mybatis.org/mybatis-3/) ...
 
## What you'll find in this demo

Example implementation of automatic DAO implementation upon Java Person "POJO" collection including PASSING! tests.
Except DAO there is also automatically implemented "POJO" class that is aggregation of several "traits" and is also
created as dynamic proxy. This is something that is not usually done because it has performance implications. POJO
dynamic generation is there mainly because of "tutorial" reasons - so that my talk could start with something more
ease to understand.

### Used implementations

Demo contains the same logic and functionality implemented with:

- [JDK Proxy](https://docs.oracle.com/javase/8/docs/technotes/guides/reflection/proxy.html)
- [CgLib](https://github.com/cglib/cglib)
- [Javassist](http://jboss-javassist.github.io/javassist/)
- [ByteBuddy](http://bytebuddy.net/#/)

While ByteBuddy is nowadays a rising star I would recommend to use this library for your potential work.
 
### What you'll need

- Java 8
- Maven
- IDE :)

If you use IntelliJ Idea and install [Remote Call Plugin](https://plugins.jetbrains.com/plugin/6027) you may navigate 
directly from [presentation](https://novoj.github.io/reveal.js/runtime-class-generation.html#/) to the code. 

#Questions

... are welcomed. Contact me via GitHub issues.
