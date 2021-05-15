(ns crawler.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as ring-json]
            [ring.util.response :as resp]))

(defroutes app-routes
  (GET "/search" {params :params} 
       (resp/response {:search-terms (str "you have tried to query: " params)}))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (ring-json/wrap-json-response)
      (wrap-defaults site-defaults)))


; 1) get the domain name of the app 
; 2) make a request to the uri 
; 3) parse html for links
; 4) add links to queue (channel)
; 5) 
