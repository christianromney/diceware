(ns diceware.core
  (:require [clojure.java.io :as io])
  (:import [java.security SecureRandom])
  (:gen-class))

(defn- wordlist
  "Returns a future whose value will be a map 
  of die rolls to words"
  []
  (future
    (-> "wordlist.edn" io/resource slurp read-string)))

(defn- die-rolls->word
  "Converts a sequence of 5 die rolls into a Diceware word"
  [word-list rolls]
  (let [k (apply str rolls)]
    (get @word-list k)))

(defn- die-rolls
  "Returns a lazy sequence of random die rolls"
  ([]
   (let [now (.toEpochMilli (java.time.Instant/now))
         rng (SecureRandom/getInstance "SHA1PRNG")]
     (.setSeed rng now)
     (die-rolls rng)))
  
  ([rng]
   (lazy-seq (cons (inc (.nextInt rng 5))
                   (die-rolls rng)))))

(defn passphrase
  "Generates a passphrase of n words"
  [n]
  (->> (die-rolls)
     (partition 5)
     (take n)
     (map (partial die-rolls->word (wordlist)))
     (interpose" ")
     (apply str)))

(defn -main
  "Prints a diceware passphrase"
  [& args]
  (let [num-words (Integer/parseInt (first args))]
    (println (passphrase num-words))
    (shutdown-agents)))
