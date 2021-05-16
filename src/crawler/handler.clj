(ns crawler.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as ring-json]
            [ring.util.response :as resp]))

(defroutes app-routes
  (GET "/" [] (resp/redirect "/index.html"))
  (GET "/search" {params :params} 
       (resp/response {:results ["test.com" "test.com/something" "test.com/about" ]}))
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
