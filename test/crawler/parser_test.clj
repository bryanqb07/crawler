(ns crawler.parser-test
  (:require  
   [clojure.test :refer :all]
   [net.cgrand.enlive-html :as html]
   [crawler.parser :refer :all]))

(def test-url-strings '(
 "https://www.wsj.com/articles/california-population-declines-for-first-time-in-more-than-a-century-11620416887"
 "from?site=wsj.com"
 "user?id=justinzollars"
 "item?id=27081754"
 "hide?id=27081754&goto=news"
 "item?id=27081754"
 "vote?id=27078207&how=up&goto=news"
 "https://download.vusec.net/papers/constantine_ccs21.pdf"
 "from?site=vusec.net"
 "user?id=sternmere"
 "item?id=27078207"
 "hide?id=27078207&goto=news"
 "item?id=27078207"
 "vote?id=27084854&how=up&goto=news"
 "item?id=27084854"
 "user?id=rigpa"
 "item?id=27084854"
 "hide?id=27084854&goto=news"
 "item?id=27084854"
 "news?p=2"
 "http://linkedin.com/ycombinator.com/" ; this should be filtered out
 "newsguidelines.html"
 "newsfaq.html"
 "lists"
 "https://github.com/HackerNews/API"
 "security.html"
 "http://www.ycombinator.com/legal/"
 "http://www.ycombinator.com/apply/"
 "mailto:hn@ycombinator.com"
))


(deftest test-parser
 (testing "filters domain-specific urls"
   (let [filter-result (filter-domain-links "ycombinator.com" test-url-strings)]
     (is (= 3 (count filter-result)))
     (is (= 2 (count (filter-external-domains "ycombinator.com" filter-result))))))
  (testing "can parse abbreviated domain name from a url"
    (is (= "ycombinator.com" (url-to-domain "https://news.ycombinator.com/"))))
  (testing "")
  )
