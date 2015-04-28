(set-env!
  :resource-paths #{"src"}
  :dependencies   '[[org.clojure/clojure "1.6.0"       :scope "provided"]
                    [boot/core           "2.0.0-rc14"  :scope "provided"]
                    [adzerk/bootlaces    "0.1.11"      :scope "test"]
                    [com.keminglabs/cljx "0.6.0"       :scope "test"]])

(require '[adzerk.bootlaces :refer :all])

(def +version+ "0.3.0")

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
