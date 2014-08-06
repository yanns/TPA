About this project
==================

This project aims at showing how to structure a play application with the cake pattern.

It was used in a presentation at [Ping Conf](http://www.ping-conf.com/) in 2014:
- video of the presentation: http://www.ustream.tv/recorded/42775808
- slides: https://speakerdeck.com/yanns/structure-a-play-application-with-the-cake-pattern-and-test-it


Structure
=========

This [frontend application](/frontend/) uses two backend: a [micro-service about players](backends/PlayerService/) and a [video streaming app](backends/VideoService/)

The cake pattern in progressively introduces in a frontend application, from [TBA_01](frontend/TBA_01) to [TBA_06](frontend/TBA_06). The final version is [TBA_05_final](frontend/TBA_05_final).


How to run this application
===========================
- start the backends:
```bash
cd backends/PlayerService/
./sbt.sh run
```

in another terminal:
```bash
cd backends/VideoService/
./sbt.sh run
```

- start one frontend, for example the final version:
```bash
cd frontend/TBA_05_final/
sbt run
```

- check the tests with:
```bash
sbt test
```

Further developments
====================

Some new versions of the frontend application were added after the presentation to explore other ways to structure an application:
- [TBA_07](frontend/TBA_07) does not use any components nor any dependency injection. The testing is done only with component tests and is explained in a [blog post](http://yanns.github.io/blog/2014/05/30/enlarge-your-test-scope/).
- [TBA_macwire](frontend/TBA_macwire) uses simple constructors to express dependencies and [MacWire](https://github.com/adamw/macwire) to "inject" them. It is like a dependency injection at compile time.
