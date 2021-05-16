(ns crawler.spider
  (:require [clojure.core.async :as a :refer [>! <! >!! <!! go chan buffer go-loop alts! timeout]]
            [crawler.parser :as parser]))


(def url-chan (chan 102400))
(def completion-chan (chan 1))
(def log-chan (chan 1000))
;(def site-map (atom #{}))
(def visited-urls (java.util.concurrent.ConcurrentHashMap.))
(def max-workers 8)

(defn log [message]
  (go (>! log-chan message)))

(defn setup-logger []
  (go-loop []
    (println (<! log-chan))
    (recur)))

(defn filter-visited-links [links]
  (filter #(not (.get visited-urls %)) links))

(defn create-workers []
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
  (let [start-time (System/currentTimeMillis)]
    (setup-logger)
    (create-workers)
    (>!! url-chan url)
    (println (<!! completion-chan))
    (println "Completed after: " (seconds-since start-time) " seconds")
    (keys visited-urls)))
