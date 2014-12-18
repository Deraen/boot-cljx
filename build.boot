(set-env! :resource-paths #{"src"})

(require '[deraen.boot-cljx :refer [cljx-version]])
(def +version+ (str cljx-version "-SNAPSHOT"))

(set-env!
  :dependencies   `[[org.clojure/clojure "1.6.0"       :scope "provided"]
                    [boot/core           "2.0.0-pre28" :scope "provided"]
                    [adzerk/bootlaces    "0.1.5"       :scope "test"]
                    [com.keminglabs/cljx ~cljx-version :scope "test"]])

(require '[adzerk.bootlaces :refer :all])

(bootlaces! +version+)

(task-options!
  pom {:project     'deraen/boot-cljx
       :version     +version+
       :description "Boot task to compile Cljx code to Cljs and Clj code"
       :url         "https://github.com/deraen/boot-cljx"
       :scm         {:url "https://github.com/deraen/boot-cljx"}
       :license     {:name "The MIT License (MIT)"
                     :url "http://opensource.org/licenses/mit-license.php"}})

(deftask dev
  "Dev process"
  []
  (comp
    (watch)
    (repl :server true)
    (build-jar)))
