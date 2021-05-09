(ns crawler.parser
  (:require [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io :refer [as-url]]
            [clojure.string :as str :refer [join split ends-with?]]))

(def ^:dynamic *base-url* "https://www.w3schools.com/")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn get-links [html-tree] 
   (map #(get-in % [:attrs :href]) (html/select html-tree [:a])))

(defn get-links-from-url [url]
  (get-links (fetch-url url)))

; for urls that end with / , remove the dash when preparing to append relative links
(defn remove-trailing-slash-url [url]
  (if (ends-with? url "/")
      (join "" (butlast url))
      url))

(defn convert-relative-links [base-url url-strings]
  (reduce 
   (fn [acc url]
     (if 
      (re-matches #"^\/.+" url) ; if it's a relative link replace with full ex. /posts => facebook.com/posts
       (cons (str (remove-trailing-slash-url base-url) url) acc)
       (cons url acc))) ;; otherwise jsut leave it as it is
   '()
   url-strings)
)

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

(defn get-full-links-from-url [url]
  (let [url-strings (remove nil? (get-links-from-url url))]
    (convert-relative-links url url-strings)))

; 1) grab the URL and all of link tag elements href attrs
; 2) convert the relative href's into full url ex. /posts  => facebook.com/posts
; 3) filter out any url that doesn't match domain name
; 4) filter out any url that is prefaced by a site that doesn't match the domain-name linkedin.com/facebook.com/

(defn get-url-links [url]
  (let [domain-name (url-to-domain url)]
    (filter-external-domains domain-name (filter-domain-links domain-name (get-full-links-from-url url)))))
