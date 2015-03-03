(ns deraen.boot-cljx
  {:boot/export-tasks true}
  (:require
    boot.repl
    [clojure.java.io :as io]
    [boot.pod        :as pod]
    [boot.core       :as core]
    [boot.util       :as util]
    [boot.tmpdir     :as tmpd]
    [boot.from.io.aviso.ansi :as ansi]))

(def ^:private cljx-version "0.6.0")
(def ^:private deps
  [['com.keminglabs/cljx cljx-version]])

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
    (swap! boot.repl/*default-dependencies* conj ['com.keminglabs/cljx cljx-version])
    (swap! boot.repl/*default-middleware* conj 'cljx.repl-middleware/wrap-cljx)
    (core/with-pre-wrap fileset
      (let [cljx (->> fileset
                      (core/fileset-diff @last-cljx)
                      core/input-files
                      (core/by-ext [".cljx"]))]
        (reset! last-cljx fileset)
        (when (seq cljx)
          (util/info "Compiling cljx... %d changed files.\n" (count cljx))
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
