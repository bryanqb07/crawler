(ns crawler.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as ring-json]
            [ring.adapter.jetty :refer :all]
            [ring.util.response :as resp]
            [crawler.spider :as spider])
  (:gen-class))

(defroutes app-routes
  (GET "/" [] (resp/redirect "/index.html"))
  (GET "/search" {{search-url :search-url} :params} 
       (resp/response {:results (spider/crawl-url search-url)}))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (ring-json/wrap-json-response)
      (wrap-defaults site-defaults)))

;; by including jetty above we can override the default
(defn -main []
  (let [port (or (Integer.(System/getenv "PORT")) 3000)]
    (println (str "App starting on port: " port))
    (run-jetty app {:port port})))
