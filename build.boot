(set-env!
  :resource-paths #{"src"}
  :dependencies   '[[org.clojure/clojure "1.6.0"       :scope "provided"]
                    [boot/core           "2.0.0-rc5"   :scope "provided"]
                    [adzerk/bootlaces    "0.1.8"       :scope "test"]
                    [com.keminglabs/cljx "0.5.0"       :scope "test"]])

(require '[adzerk.bootlaces :refer :all])

(def +version+ "0.2.2")

(bootlaces! +version+)

(task-options!
  pom {:project     'deraen/boot-cljx
       :version     +version+
       :description "Boot task to compile Cljx code to Cljs and Clj code"
       :url         "https://github.com/deraen/boot-cljx"
       :scm         {:url "https://github.com/deraen/boot-cljx"}
       :license     {"MIT" "http://opensource.org/licenses/mit-license.php"}})

(deftask dev
  "Dev process"
  []
  (comp
    (watch)
    (repl :server true)
    (build-jar)))
