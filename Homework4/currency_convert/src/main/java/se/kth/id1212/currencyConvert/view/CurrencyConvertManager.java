package se.kth.id1212.currencyConvert.view;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import se.kth.id1212.currencyConvert.controller.Controller;
import se.kth.id1212.currencyConvert.model.CurrencyConvertBody;
import se.kth.id1212.currencyConvert.model.CurrencyConvertDTO;


@Named("CurrencyConvertManager")
@ConversationScoped
public class CurrencyConvertManager implements Serializable {
    @EJB
    private Controller controller;
    private CurrencyConvertBody currentConvert;
    private Exception sessionFail;
    
    private String fromCurrency;
    private String toCurrency;
    private Double amountFromCurrency;
    
    
    @Inject
    private Conversation conversation;

    private void startConversation() {
        if (conversation.isTransient()) {
            conversation.begin();
        }
    }

    private void stopConversation() {
        if (!conversation.isTransient()) {
            conversation.end();
        }
    }

    private void handleException(Exception e) {
        stopConversation();
        e.printStackTrace(System.err);
        sessionFail = e;
    }


    public boolean getSuccess() {
        return sessionFail == null;
    }

    public Exception getException() {
        return sessionFail;
    }


    public CurrencyConvertDTO getCurrentConvert() {
        return currentConvert;
    }
    

    public void convert() {
        try {
            startConversation();
            sessionFail = null;
            currentConvert = new CurrencyConvertBody(this.fromCurrency, this.toCurrency, this.amountFromCurrency);
            currentConvert = controller.convert(this.currentConvert);
        } catch (Exception e) {
            handleException(e);
        }
    }
    
    
    public void setFromCurrency(String fromCurr) {
        this.fromCurrency = fromCurr;
    }

    public String getFromCurrency() {
        return null;
    }
    
    public void setToCurrency (String toCurr) {
        this.toCurrency = toCurr;
    }

    public String getToCurrency() {
        return null;
    }
    
    public void setAmountFromCurrency(Double amountFromCurr) {
        this.amountFromCurrency = amountFromCurr;
    }

    public Double getAmountFromCurrency() {
        return null;
    }
    
}