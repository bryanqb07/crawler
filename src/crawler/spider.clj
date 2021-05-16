(ns crawler.spider
  (:require [clojure.core.async :as a :refer [>! <! >!! <!! go chan buffer go-loop alts! timeout]]
            [crawler.parser :as parser]))

(def log-chan (chan 1000))
;; ;(def site-map (atom #{}))

(defn log [message]
  (go (>! log-chan message)))

(defn setup-logger []
  (go-loop []
    (println (<! log-chan))
    (recur)))

(defn create-workers [visited-urls url-chan completion-chan max-workers]
  (dotimes [_ max-workers]
    (go-loop [url (<! url-chan)]
      (when-not (.putIfAbsent visited-urls url true)
        (log (str "retreiving... " url))
        (try
          (let [url-links (parser/get-url-links url)]
            (doseq [url-link (set url-links)]
              (go (>! url-chan url-link))))
          (catch Exception e
            (log (str "error retreiving url: " url)))))
      (let [[value channel] (alts! [url-chan (timeout 3000)])]
        (if (= channel url-chan)
          (recur value)
          (>! completion-chan :completed))))))


(defn seconds-since [start-time]
  (let [current-time (System/currentTimeMillis)]
    (/ (- current-time start-time) 1000.0)))

(defn crawl-url [url]
  (let [start-time (System/currentTimeMillis)
        url-chan (chan 102400)
        completion-chan (chan 1)
        visited-urls (java.util.concurrent.ConcurrentHashMap.)
        ]
    (setup-logger)
    (create-workers visited-urls url-chan completion-chan 8)
    (>!! url-chan url)
    (println (<!! completion-chan))
    (println "Completed after: " (seconds-since start-time) " seconds")
    (keys visited-urls)))
