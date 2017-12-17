package se.kth.id1212.currencyConvert.integration;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Stateless

public class CurrencyDAO {
    @PersistenceContext(unitName = "currencyRatePU")
    private EntityManager em;
    private boolean rateTableLoaded = false; 

    
    public void loadRateTable(){
        CurrencyRate crSEK = new CurrencyRate("SEK",  1.0,   0.1,  0.12,  0.0884); 
        CurrencyRate crEUR = new CurrencyRate("EUR",  9.94,  1.0,  1.18,  0.88  ); 
        CurrencyRate crUSD = new CurrencyRate("USD",  8.54,  0.85, 1.0,   0.75  ); 
        CurrencyRate crGBP = new CurrencyRate("GBP", 11.31,  1.14, 1.34,  1.0   ); 
        em.persist(crSEK);
        em.persist(crEUR);
        em.persist(crUSD);
        em.persist(crGBP);
        this.rateTableLoaded = true;
    }
    
    public boolean rateTableLoaded(){
        return this.rateTableLoaded;
    }
   
    public CurrencyRate findAllRate(String fromCurr) {
        return em.find(CurrencyRate.class, fromCurr);
    }
}
