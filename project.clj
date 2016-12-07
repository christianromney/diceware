(defproject diceware "0.1.0-SNAPSHOT"
  :description "Diceware passphrase generator in Clojure"
  :url "https://github.com/christianromney/diceware"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :resource-paths ["resources"]
  :main diceware.core
  :aot [diceware.core]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
