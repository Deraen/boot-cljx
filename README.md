# boot-cljx
[![Clojars Project](http://clojars.org/deraen/boot-cljx/latest-version.svg)](http://clojars.org/deraen/boot-cljx)

[Boot](https://github.com/boot-clj/boot) task to compile Cljx.

* Provides the `cljx` task
* Reads `.cljx` files and creates corresponding `.clj` and `.cljs` files.
  Resulting files will be available to others tasks and on classpath.
* Adds cljx nrepl middleware to repl task default settings
  * Make sure your cljx task is run before repl task

## Use

```clojure
; All files (.clj, .cljx, .cljs) could be on the same directory,
; but I like to have separate directories per filetype.
(set-env! :src-paths #{"src/cljx" "src/clj" "src/cljs"})

; Run cljx before cljs
; $ boot cljx cljs ...
(deftask package
  "Package the app"
  []
  (comp
    (cljx)
    (cljs)
    ...))
```

## License

Copyright (C) 2014-2015 Juho Teperi

Distributed under the Eclipse Public License, the same as Clojure.
