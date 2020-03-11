(ns clojure2.core
  (:require [clojure.core.async
             :as async
             :refer [>! <! >!! <!! put! go chan go-loop into close!]]
            [clojure.string :as str])
  (:gen-class))

(def file-as-str (slurp "src/clojure2/text.txt"))
(def read (str/split file-as-str #""))

(defn ch
  [c]
  (go
    (doseq [o read]
      (>! c o)
      ))
  )

(defn chars-to-words
  [words c]
  (
   (go-loop [counter 1]
     (>! words (<! c))
     (if (> counter 1000)
       (close! words)
       (recur (inc counter)))
     )
   )
  )


(defn -main
  [& args]

  (let [c (chan)
        n (chan)
        words (chan 100 (comp
                          (partition-by (complement #{" "}))
                          (map #(apply str %))
                          (remove #{" "})))]

    (go-loop []
      (let [o (<! words)]
        (when (not= nil o)
          (println o)))
      (recur))

    (ch c)

    (chars-to-words words c)
    ))
