npx shadow-cljs release app && lein do clean, uberjar && heroku container:push web && heroku container:release web && heroku open
