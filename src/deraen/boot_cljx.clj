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

(core/deftask cljx
  "Compile Cljx code."
  []
  (let [rules       [:clj :cljs]
        tmp         (core/temp-dir!)
        p           (-> (core/get-env)
                        (update-in [:dependencies] into deps)
                        pod/make-pod
                        future)
        last-cljx   (atom nil)]
    (core/with-pre-wrap fileset
      (let [cljx (->> fileset
                      (core/fileset-diff @last-cljx)
                      core/input-files
                      (core/by-ext [".cljx"]))]
        (reset! last-cljx fileset)
        (when (seq cljx)
          (util/info (str "Compiling cljx... " (count cljx) " changed files."))
          (doseq [r rules
                  f cljx]
            (pod/with-call-in @p
              (deraen.boot-cljx.impl/cljx-compile
                ~r
                ~(.getPath (tmpd/file f))
                ~(.getPath tmp)
                ~(tmpd/path f)))))
        (-> fileset
            (core/add-resource tmp)
            core/commit!)))))
