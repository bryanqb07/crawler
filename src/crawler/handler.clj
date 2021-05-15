(ns crawler.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))


; 1) get the domain name of the app 
; 2) make a request to the uri 
; 3) parse html for links
; 4) add links to queue (channel)
; 5) 
