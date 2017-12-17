package se.kth.id1212.currencyConvert.integration;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CurrencyRate implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
  
    private String fromCurrency;
    private double rateToSEK;
    private double rateToEUR;
    private double rateToUSD;
    private double rateToGBP;
    

    public CurrencyRate() {
    }

    public CurrencyRate(String fromCurr, double rateToSEK, double rateToEUR,
            double rateToUSD, double rateToGBP) {
        this.fromCurrency = fromCurr;
        this.rateToSEK = rateToSEK;
        this.rateToEUR = rateToEUR;
        this.rateToUSD = rateToUSD;
        this.rateToGBP = rateToGBP;
    }
       
    
    public double getRateToSEK() {
        return this.rateToSEK;
    }
    
    public double getRateToEUR() {
        return this.rateToEUR;
    }   

    public double getRateToUSD() {
        return this.rateToUSD;
    }
    
    public double getRateToGBP() {
        return this.rateToGBP;
    }
    
    
}
