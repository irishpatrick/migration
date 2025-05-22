(ns migration.core
  (:require [clojure.java.io :as io]
            [migration.parser :as parser]
            [next.jdbc :as jdbc])
  (:import (java.io File)))

(def default-resource-dir "migrations")

(defn get-migration-files [resource-dir]
  (.listFiles (File. (.getFile (io/resource resource-dir)))))

(defn run-migrations [db & {:keys [migration-files
                                   current-revision-fn
                                   update-revision-fn
                                   init-fn
                                   target-revision]
                            :or {init-fn (fn []),
                                 target-revision Long/MAX_VALUE,
                                 migration-files (get-migration-files default-resource-dir)}}]
  {:pre [(some? db) (some? current-revision-fn) (some? update-revision-fn)]}
  (jdbc/with-transaction [tx db]
                         (init-fn)
                         (let [current-rev (current-revision-fn)
                               migrations (parser/parse-migrations (parser/get-relevant-migrations migration-files current-rev target-revision))]
                           (loop [to-run migrations]
                             (if (seq to-run)
                               (let
                                 [running (first to-run)
                                  operation-key (parser/get-migration-operation current-rev target-revision)]
                                 (println "running " operation-key " migration " (:revision running))
                                 (jdbc/execute! tx [(operation-key running)])
                                 (update-revision-fn (+ (:revision running) (operation-key {:up 0 :down -1})))
                                 (recur (rest to-run))))))))
