(ns deraen.boot-cljx
  {:boot/export-tasks true}
  (:require
   [clojure.java.io :as io]
   [boot.pod        :as pod]
   [boot.core       :as core]
   [boot.util       :as util]))

(def ^:private deps
  '[[com.keminglabs/cljx "0.4.0"]])

(def ^:private last-cljx (atom {}))

(core/deftask cljx
  "Compile Cljx code."
  []

  (let [rules       [:clj :cljs]
        tmp-dir     (core/mktmpdir!)
        stage-dir   (core/mktgtdir!)
        base-opts   {:output-path (.getPath tmp-dir)}
        builds      (map (partial assoc base-opts :rules) rules)
        p           (-> (core/get-env)
                        (update-in [:dependencies] into deps)
                        pod/make-pod
                        future)]
    (core/with-pre-wrap
      (io/make-parents tmp-dir)
      (let [srcs     (core/src-files+)
            cljx     (core/by-ext [".cljx"] srcs)
            ; This is passed to the pod so pass strings instead of Files.
            ; result is eg. [["src/cljx/foo/bar.cljx" "foo/bar.cljx"]]
            ; The first path is used for reading ths source and second for
            ; writing the result to proper path
            cljx*    (map (juxt #(.getPath %) core/resource-path) cljx)
            lastc    (reduce #(assoc %1 %2 (.lastModified %2)) {} cljx)
            dirty-c? (not= @last-cljx (reset! last-cljx lastc))]
        (when dirty-c?
          (util/info "Compiling cljx...\n")
          (pod/call-in @p `(deraen.boot-cljx.impl/cljx-compile ~cljx* ~builds)))
        (core/sync! stage-dir tmp-dir)
        ; Consume the .cljx files
        ; This means that the following tasks don't see the .cljx files in src-files
        (doseq [f cljx]
          (core/consume-file! f))))))
