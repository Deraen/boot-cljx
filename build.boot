(set-env!
  :dependencies '[[org.clojure/clojure       "1.6.0"      :scope "provided"]
                  [boot/core                 "2.0.0-pre9" :scope "provided"]
                  [tailrecursion/boot-useful "0.1.3"      :scope "test"]
                  [com.keminglabs/cljx       "0.4.0"]])

(require '[tailrecursion.boot-useful :refer :all])

(def +version+ "0.1.0-SNAPSHOT")

(useful! +version+)

(task-options!
  pom  [:project     'deraen/boot-cljx
        :version     +version+
        :description "Boot task to compile Cljx code to Cljs and Clj code"
        :url         "https://github.com/deraen/boot-cljx"
        :scm         {:url "https://github.com/deraen/boot-cljx"}
        :license {:name "The MIT License (MIT)"
                  :url "http://opensource.org/licenses/mit-license.php"}])

(deftask dev
  "Dev process"
  []
  (comp
    (watch)
    (repl :server true)
    (build-jar)))
