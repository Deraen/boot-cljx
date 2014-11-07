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

  (let [; FIXME:
        rules       [:clj :cljs]
        tmp-dir     (core/mktmpdir!)
        stage-dir   (core/mktgtdir!)
        base-opts   {; FIXME:
                     :source-paths ["src/cljx"]
                     :output-path (.getPath tmp-dir)}
        builds      (map (partial assoc base-opts :rules) rules)
        p           (-> (core/get-env)
                        (update-in [:dependencies] into deps)
                        pod/make-pod future)]
    (core/with-pre-wrap
      (io/make-parents tmp-dir)
      (let [srcs     (core/src-files+)
            cljx     (->> srcs (core/by-ext [".cljx"]))
            lastc    (->> cljx (reduce #(assoc %1 %2 (.lastModified %2)) {}))
            dirty-c? (not= @last-cljx (reset! last-cljx lastc))]
        (when dirty-c?
          (util/info "Compiling cljx...\n")
          (pod/call-in @p `(cljx.core/cljx-compile ~builds)))
        (core/sync! stage-dir tmp-dir)
        ; ??
        #_
        (doseq [f cljx]
          (core/consume-file! f))))))
