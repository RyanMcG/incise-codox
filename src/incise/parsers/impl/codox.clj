(ns incise.parsers.impl.codox
  (:require [incise.parsers.core :refer [register]]
            [incise.config :as conf]
            [incise.utils :refer [delete-recursively directory?]]
            [clojure.edn :as edn]
            [clojure.java.io :refer [file reader]]
            [hiccup.util :refer [with-base-url]]
            [codox.main :refer [generate-docs]])
  (:import [java.io PushbackReader]))

(defn- read-edn-file [a-file] (edn/read (PushbackReader. (reader a-file))))

(defn parse-codox
  "Interpret the given file as edn information to be used as config for codox."
  [a-file]
  (let [opts (read-edn-file a-file)
        output-dir-path (or (opts :output-dir) "")
        out-dir (-> :out-dir (conf/get) (file output-dir-path))
        options (assoc opts :output-dir (.getPath out-dir))]
    (delay
      (delete-recursively out-dir)
      (with-base-url output-dir-path (generate-docs options))
      (remove directory? (file-seq out-dir)))))

(register :codox #'parse-codox)
