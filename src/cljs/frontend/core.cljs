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

(defn respond-to-search [response]
  (prn response))

;; (defn create-request-handler []
;;   (let [search-url (go (<! request-chan))
;;         response (go (<! (http/get "/"
;;                                    {:with-credentials false
;;                                     :query-params {"search-url" search-url}})))]
;;     (respond-to-search response)
;;     ))
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
      [:form {:on-submit (fn [e]
                           (.preventDefault e)
                           (put! request-chan @search-url))}
       [:input {:type :text 
                :name :search-url
                :value @search-url
                :on-change (fn [e]
                             (reset! search-url (-> e .-target .-value)))}
        ]
       [:button {:type :submit} "Crawl!"]])))

(defn results-list []
  [:ul
   (for [result @search-url-results]
     [:li {:key result}
      result])])   

  (defn home-page []
    (create-request-handler)
    (create-results-handler)
    [:div 
     [:h2 "Clojure Web Crawler"]
     [search-form]
     [results-list]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))