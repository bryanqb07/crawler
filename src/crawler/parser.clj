(ns crawler.parser
  (:require [net.cgrand.enlive-html :as html]))

(def ^:dynamic *base-url* "https://news.ycombinator.com/")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn get-links [html-tree] 
   (map #(get-in % [:attrs :href]) (html/select html-tree [:a])))

; used to pass args into the #"" operator.  should probably use Java interop instead but oh well
(defn get-regexp [regex string]
   (eval (read-string (str "(re-matches #\"" regex "\" \"" string "\")")))
)

(defn filter-domain-links [domain-name url-strings] 
  (filter #(get-regexp (str "^(?!mailto)(" ".*" domain-name ".*" ")") %) url-strings))
