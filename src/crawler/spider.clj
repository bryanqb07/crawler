(ns crawler.spider
  (:require [clojure.core.async :as a :refer [>! <! >!! <!! go chan buffer go-loop alts!! timeout]]
            [crawler.parser :as http]))

(defn worker [url-chan url]
  (try
    (let [url-links (http/get-url-links url)]
      (go (>! url-chan url-links)))
    (catch Exception e
      (println "Error retrieving url: " url))))

(defn crawl [url-chan]
  (let [visited-urls (atom {})]
      (loop [urls (<!! url-chan)]
        (doseq [url urls] 
          (when-not (contains? @visited-urls url)
            (println "retrieving..." url)
            (swap! visited-urls assoc url true)
            (worker url-chan url))) 
        (let [[next-urls channel] (alts!! [url-chan (timeout 3000)])]
          (if (= channel url-chan)
            (recur next-urls)
            (keys @visited-urls))))))

(defn crawl-url [url]
  (let [url-chan (chan)]
    (go (>! url-chan (conj [] url)))
    (crawl url-chan)))
