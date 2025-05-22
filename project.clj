(defproject org.clojars.irishpatrick/migration "0.1.1-SNAPSHOT"
  :description "Run database migrations from a list of SQL files."
  :url "http://github.com/irishpatrick/migration"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [com.github.seancorfield/next.jdbc "1.3.1002"]]
  :repl-options {:init-ns migration.core})
