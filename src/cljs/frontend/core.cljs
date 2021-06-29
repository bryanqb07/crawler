(ns frontend.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
      [reagent.core :as r]
      [reagent.dom :as d]
      [cljs-http.client :as http]
      [cljs.core.async :refer [<! >! chan put!]]))

(enable-console-print!)

;; -------------------------
;; Views
(def search-url-results (r/atom []))
(def request-chan (chan))
(def results-chan (chan))

(defn create-results-handler []
  (go-loop []
    (let [results (<! results-chan)]
      (reset! search-url-results results)
      (recur))))

(defn create-request-handler []
  (go-loop []
    (let [search-url (<! request-chan)
          response (<! (http/get "/search"
                                 {:with-credentials false
                                  :query-params {"search-url" search-url}}))
          results (get-in response [:body :results])]
      (put! results-chan results)
      (recur))))

(defn search-form []
  (let [search-url (r/atom "")]
    (fn []
      [:form.search {:on-submit (fn [e]
                                  (.preventDefault e)
                                  (reset! search-url-results ["Searching..."]) ; clear the previous results
                                  (put! request-chan @search-url))}
       [:input.searchTerm {:type :text 
                           :name :search-url
                           :value @search-url
                           :placeholder "https://w3schools.com"
                           :on-change (fn [e]
                                        (reset! search-url (-> e .-target .-value)))}
        ]
       [:button.searchButton {:type :submit}
        [:i.fa.fa-search]]])))

(defn results-list []
  [:ul
   (for [result @search-url-results]
     [:li {:key result}
      result])])   

  (defn home-page []
    (create-request-handler)
    (create-results-handler)
    [:div.wrap
     [:h2 "Mini-Crawler"]
     [:img {:src "spider.gif"}]
     [:p "Crawl all the pages on your personal website."]
     [search-form]
     [results-list]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
