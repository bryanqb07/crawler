(ns crawler.spider
  (:require [clojure.core.async :as a :refer [>! <! >!! <!! go chan buffer go-loop alts! timeout]]
            [crawler.parser :as parser]))


(def url-chan (chan 102400))
(def completion-chan (chan 1))
;(def site-map (atom #{}))
(def visited-urls (java.util.concurrent.ConcurrentHashMap.))
(def max-workers 1)

;; (defn create-worker []
;;   (go-loop []
;;      (let [url (<! url-chan)]
;;        (if (contains? visited-urls url)
;;          (recur)
;;          (let [url-list (get-url-links url)])
;; ))))

(defn filter-visited-links [links]
  (filter #(not (.get visited-urls %)) links))

(defn create-workers []
  (dotimes [_ max-workers]
    (go-loop [url (<! url-chan)]
      (when-not (.putIfAbsent visited-urls url true)
        (let [url-links (parser/get-url-links url)]
          (doseq [url-link (set (filter-visited-links url-links))]
            (println visited-urls)
            (println "Adding " url-link "to queue")
            (go (>! url-chan url-link)))))
      (let [[value channel] (alts! [url-chan (timeout 3000)])]
        (if (= channel url-chan)
          (recur value)
          (>! completion-chan :completed))))))


(defn seconds-since [start-time]
  (let [current-time (System/currentTimeMillis)]
    (/ (- current-time start-time) 1000.0)))

(defn crawl-url [url]
  (let [start-time (System/currentTimeMillis)]
    (create-workers)
    (>!! url-chan url)
    (println (<!! completion-chan))
    (println visited-urls)
    (println "Completed after: " (seconds-since start-time) " seconds")))
