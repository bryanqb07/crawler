(ns frontend.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
      [reagent.core :as r]
      [reagent.dom :as d]
      [cljs-http.client :as http]
      [cljs.core.async :refer [<! >! chan]]))

(enable-console-print!)

;; -------------------------
;; Views
(def search-url-results (r/atom {}))
(def request-chan (chan))

(defn respond-to-search [response]
  (prn response))

(defn create-request-handler []
  (let [search-url (<! request-chan)
        response (<! (http/get "/"
                               {:with-credentials false
                                :query-params {"search-url" search-url}}))]
    (respond-to-search response)
    ))


(defn search-form []
  (let [search-url (r/atom "")]
    (fn []
      [:form {:on-submit (fn [e]
                           (.preventDefault e)
                           (>! chan @search-url))}
       [:input {:type :text 
                :name :search-url
                :value @search-url
                :on-change (fn [e]
                             (reset! search-url (-> e .-target .-value)))}
        ]
       [:button {:type :submit} "Crawl!"]])))

(defn home-page []
  [:div 
   [:h2 "Clojure Web Crawler"]
   [search-form]
   ])

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
