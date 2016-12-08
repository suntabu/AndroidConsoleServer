 
 
 
 ![](https://coding.net/u/suntabu/p/AndroidConsoleServer/git/raw/master/help.png)
 
 Using a NanoHTTPD Server on app side for serving the console command and request.
 
 - TODO:
    - list external app directory infos
    - clear external app directory files
    - format activity info string

HOW TO USE
-----
```java
// init with application context
ACS.init(context);

// start listen on port 8443
ACS.startAndroidWebServer();

    
    
```
Then you can use open console web page at your phone's ip:port with any browsers you like.


TIPS: your phone's ip must be reachable！
===

LICENCE
-----
This is using the [NanoHTTPD](https://github.com/NanoHttpd/nanohttpd) library.
**AndroidConsoleServer** based on [Lopez Mikhael](http://mikhaellopez.com/)'s  [AndroidWebServer](https://github.com/lopspower/AndroidWebServer) and is licensed under a [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).
