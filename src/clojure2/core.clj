
(ns clojure2.core
  (:require [clojure.core.async
    :as a
    :refer [>! <! go chan go-loop]][clojure.java.io :as io]
            [clojure.string :as str])
  (:gen-class))

(def file-as-str (slurp "src/clojure2/text.txt"))
(def lines (str/split-lines file-as-str))

(defn ch
  [c]
  (go
    (doseq [ln lines]
      (>! c ln)
    )))

(defn -main
  [& args]

  (let [c (chan)]

    (go-loop []
      (let [o (<! c)]
        (println o))
      (recur))
    
    (ch c))
)
