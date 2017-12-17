package se.kth.id1212.currencyConvert.controller;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import se.kth.id1212.currencyConvert.integration.CurrencyDAO;
import se.kth.id1212.currencyConvert.integration.CurrencyRate;
import se.kth.id1212.currencyConvert.model.CurrencyConvertBody;

@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateless
public class Controller {
    @EJB CurrencyDAO currencyRateDB;
    
    public CurrencyConvertBody convert(CurrencyConvertBody ccb) {
        if (!currencyRateDB.rateTableLoaded()){
            currencyRateDB.loadRateTable();
        }
        CurrencyRate currRate = currencyRateDB.findAllRate(ccb.getFromCurrency());
        ccb.convert(currRate);
        return ccb;
    } 
}
