(ns crawler.spider
  (:require [clojure.core.async :as a :refer [>! <! >!! <!! go chan buffer]]
            [crawler.parser :as parser]))


(def url-chan (chan))
(def visited-urls (atom #{}))


(defn crawl-url [url])
