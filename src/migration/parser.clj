(ns migration.parser
  (:require [clojure.set :as set]
            [clojure.string :as string]))


(defn parse-migration-number [file]
  (let [found (re-find #"[0-9]+" file)]
    (if (string? found)
      (parse-long found))))

(defn get-relevant-migrations [files current-ver target-ver]
  (if (< current-ver target-ver)
    (filter (fn [x]
              (let [ver (parse-migration-number (.getName x))]
                (and (> ver current-ver) (<= ver target-ver)))) files)
    (filter (fn [x]
              (let [ver (parse-migration-number (.getName x))]
                (and (<= ver current-ver) (> ver target-ver)))) files)))

(defn get-migration-operation [current-ver target-ver]
  (if (< current-ver target-ver)
    :up
    :down))

(defn line-concat [lines]
  (loop [lines lines fused ""]
    (if-not (seq lines)
      fused
      (recur (rest lines) (str fused (first lines) "\n")))))

(defn parse-up-down [data]
  (let [lines (string/split-lines data)
        content (map-indexed list lines)
        seeking {:up-tag (partial re-matches #"--\s*up.*")
                 :down-tag (partial re-matches #"--\s*down.*")
                 :fixtures-tag (partial re-matches #"--\s*fixtures.*")}
        start 0
        end (count content)
        up-tag (filter #((:up-tag seeking) (second %)) content)
        down-tag (filter #((:down-tag seeking) (second %)) content)
        fixtures-tag (filter #((:fixtures-tag seeking) (second %)) content)
        structure (dissoc (sorted-map
                            start :start
                            end :end
                            (first (first up-tag)) :up
                            (first (first down-tag)) :down
                            (first (first fixtures-tag)) :fixtures) nil)
        inverse (set/map-invert structure)
        ranges {:up [(:up inverse) (first (first (filter #(> (first %) (:up inverse)) structure)))]
                :down [(:down inverse) (first (first (filter #(> (first %) (:down inverse)) structure)))]
                :fixtures [(:fixtures inverse) (first (first (filter #(> (first %) (:fixtures inverse)) structure)))]}]
    {:up (line-concat (subvec lines (first (:up ranges)) (last (:up ranges))))
     :down (line-concat (subvec lines (first (:down ranges)) (last (:down ranges))))
     :fixtures (line-concat (subvec lines (first (:fixtures ranges)) (last (:fixtures ranges))))}))

(defn parse-migrations [files]
  (loop [files files parsed []]
    (let [fp (first files)]
      (if-not (seq files)
        parsed
        (recur (rest files) (conj parsed (merge {:revision (parse-migration-number (.getName fp))} (parse-up-down (slurp fp)))))))))

