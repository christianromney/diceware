(ns diceware.core
  (:require [clojure.java.io :as io])
  (:import [java.security SecureRandom])
  (:gen-class))

(defn- wordlist
  "Returns a future whose value will be a map 
  of die rolls to words"
  []
  (-> "wordlist.edn" io/resource slurp read-string))

(defn- die-rolls->word
  "Converts a sequence of 5 die rolls into a Diceware word"
  [word-list rolls]
  (let [k (apply str rolls)]
    (get @word-list k)))

(defn- infinite-die-rolls
  "Returns a lazy sequence of random die rolls"
  ([]
   (let [now (.toEpochMilli (java.time.Instant/now))
         rng (SecureRandom/getInstance "SHA1PRNG")]
     (.setSeed rng now)
     (infinite-die-rolls rng)))
  
  ([rng]
   (lazy-seq (cons (inc (.nextInt rng 6))
                   (infinite-die-rolls rng)))))

(defn generate-passphrase
  "Generates a passphrase of n words"
  [n]
  (->> (inifinite-die-rolls)
       (partition 5)
       (take n)
       (map (partial die-rolls->word (future-call wordlist)))
       (interpose " ")
       (apply str)))

(defn requested-phrase-length
  "Return the number of requested words in the phrase"
  [args]
  (Integer/parseInt (first args)))

(defn -main
  "Prints a diceware passphrase"
  [& args]
  (let [num-words (requested-phrase-length args)]
    (println (generate-passphrase num-words))
    (shutdown-agents)))
