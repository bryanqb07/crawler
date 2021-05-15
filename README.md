-# crawler

-FIXME

##Backend Config
-
-## Prerequisites
-
-You will need [Leiningen][] 2.0.0 or above installed.
-
-[leiningen]: https://github.com/technomancy/leiningen
-
-## Running
-
-To start a web server for the application, run:
-
-    lein ring server
-
-## License
-
-Copyright © 2021 FIXME

##-Frontend Config
## Development mode
```
npm install
npx shadow-cljs watch app
```
start a ClojureScript REPL
```
npx shadow-cljs browser-repl
```
## Building for production

```
npx shadow-cljs release app
```
