(set-env!
  ; :source-paths #{"src"}
  :resource-paths #{"src"}
  :dependencies '[[org.clojure/clojure       "1.6.0"      :scope "provided"]
                  [boot/core                 "2.0.0-pre27" :scope "provided"]
                  [tailrecursion/boot-useful "0.1.3"      :scope "test"]
                  [com.keminglabs/cljx       "0.4.0"]])

; (require '[tailrecursion.boot-useful :refer :all])
(require '[boot.core :as core])

(def +version+ "0.2.0-SNAPSHOT")

; (useful! +version+)

(task-options!
  pom  [:project     'deraen/boot-cljx
        :version     +version+
        :description "Boot task to compile Cljx code to Cljs and Clj code"
        :url         "https://github.com/deraen/boot-cljx"
        :scm         {:url "https://github.com/deraen/boot-cljx"}
        :license {:name "The MIT License (MIT)"
                  :url "http://opensource.org/licenses/mit-license.php"}])

(deftask add-src
  ""
  []
  (core/with-pre-wrap
    fileset
    (reduce core/add-resource fileset (core/input-dirs fileset))))

(deftask dev
  "Dev process"
  []
  (comp
    (add-src)
    (watch)
    (repl :server true)
    (pom)
    (jar)
    (install)))
