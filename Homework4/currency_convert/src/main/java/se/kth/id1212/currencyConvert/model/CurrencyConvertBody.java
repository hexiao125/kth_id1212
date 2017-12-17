package se.kth.id1212.currencyConvert.model;

import se.kth.id1212.currencyConvert.integration.CurrencyRate;
import java.io.Serializable;

public class CurrencyConvertBody implements CurrencyConvertDTO, Serializable {

    private String fromCurrency;
    private String toCurrency;
    private Double amountFromCurrency;
    private Double amountToCurrency;
    private String convertedResult;
    
    
    public CurrencyConvertBody() {
    }

       
    public CurrencyConvertBody(String fromCurr, String toCurr, Double amountFromCurr) {
        this.fromCurrency = fromCurr;
        this.toCurrency = toCurr;
        this.amountFromCurrency = amountFromCurr;
    }
    
    public void convert(CurrencyRate currRate){
        Double rate;
        switch (this.toCurrency){
            case "SEK": rate = currRate.getRateToSEK(); break;
            case "EUR": rate = currRate.getRateToEUR(); break;
            case "USD": rate = currRate.getRateToUSD(); break;
            case "GBP": rate = currRate.getRateToGBP(); break;
            default: rate = 0.0;
        }
        this.amountToCurrency = rate * this.amountFromCurrency;
        genResult();
        
    }
    
    private void genResult(){
        this.convertedResult = this.amountFromCurrency.toString() + " " + this.fromCurrency
                + " = " + this.amountToCurrency.toString() + " " + this.toCurrency;
    }
    
    public String getFromCurrency() {
        return fromCurrency;
    }
     
    public Double getAmountToCurrency() {
        return amountToCurrency;
    }
    
    @Override
    public String getConvertedResult() {
        return this.convertedResult;
    }
}
