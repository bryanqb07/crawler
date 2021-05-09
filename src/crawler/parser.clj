(ns crawler.parser
  (:require [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io :refer [as-url]]
            [clojure.string :as str :refer [join split]]))

(def ^:dynamic *base-url* "https://www.w3schools.com/")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn get-links [html-tree] 
   (map #(get-in % [:attrs :href]) (html/select html-tree [:a])))

(defn get-links-from-url [url]
  (get-links (fetch-url url)))

; used to pass args into the #"" operator.  should probably use Java interop instead but oh well
(defn get-regexp [regex string]
   (eval (read-string (str "(re-matches #\"" regex "\" \"" string "\")"))))

(defn get-domain-name [url]
  (let [url (as-url url)] (.getHost url)))

(defn get-abbreviated-domain-name [hostname]
  (join "." (take-last 2 (split hostname #"\."))))

(defn url-to-domain [url]
  (-> url get-domain-name get-abbreviated-domain-name))

(defn filter-domain-links [domain-name url-strings] 
  (filter #(get-regexp (str "^(?!mailto)(" ".*" domain-name ".*" ")") %) url-strings))

(defn filter-external-domains [domain-name url-strings]
  (filter #(= domain-name (url-to-domain %)) url-strings))

(defn get-url-links [url]
  (let [domain-name (url-to-domain url)]
    (filter-external-domains domain-name (filter-domain-links domain-name (get-links-from-url url)))))
