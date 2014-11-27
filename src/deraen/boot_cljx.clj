(ns deraen.boot-cljx
  {:boot/export-tasks true}
  (:require
   [clojure.java.io :as io]
   [boot.pod        :as pod]
   [boot.core       :as core]
   [boot.util       :as util]
   [boot.tmpdir     :as tmpd]))

(def ^:private deps
  '[[com.keminglabs/cljx "0.4.0"]])

(def ^:private last-cljx (atom {}))

(core/deftask cljx
  "Compile Cljx code."
  []
  (let [rules       [:clj :cljs]
        tmp         (core/temp-dir!)
        base-opts   {:output-path (.getPath tmp)}
        builds      (map (partial assoc base-opts :rules) rules)
        p           (-> (core/get-env)
                        (update-in [:dependencies] into deps)
                        pod/make-pod
                        future)]
    (core/with-pre-wrap
      fileset
      (let [srcs     (core/input-files fileset)
            cljx     (core/by-ext [".cljx"] srcs)
            c        (reduce #(assoc %1 %2 (.lastModified (tmpd/file %2))) {} cljx)
            ; Find the cljx files which have changed since last run
            cljx*    (filter #(not= (get @last-cljx %) (get c %)) cljx)]
        (println @last-cljx c cljx cljx*)
        (reset! last-cljx c)
        (if (seq cljx*)
          (do
            (util/info (str "Compiling cljx... " (count cljx*) " changed files."))
            (doseq [r rules
                    f cljx*]
              (pod/call-in*
                @p
                `(deraen.boot-cljx.impl/cljx-compile
                   ~r
                   ~(.getPath (tmpd/file f))
                   ~(tmpd/path f))))
            (-> fileset
                (core/add-resource tmp)
                core/commit!))
          fileset)))))
