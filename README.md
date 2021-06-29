<h1>Clojure Crawler</h1>
<p>A single domain crawler for your personal site. Built with Clojure & Clojurescript (Reagent, shadow-cljs)</p>

<h3>Backend Config</h3>
<p>You will need [Leiningen][] 2.0.0 or above installed.</p>
<p>[leiningen]: https://github.com/technomancy/leiningen</p>
<p>Use <code>lein run</code> to start the server</p>

<h3>Frontend Config</h3>
<p><code>npm install</code></p>
<p><code>npx shadow-cljs watch app</code></p>
<p>Run <code>npx shadow-cljs release app</code> for release version</p>

<h3>Docker Version</h3>
<p>Build uberjar in base dir with <code>lein do clean, uberjar</code></p>
<p><code>docker build . -t crawler</code></p>
<p><code>docker run -p 3000:3000 crawler</code></p>
