package org.willishz.conlock.service;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author willishz Lu
 */
public class PurchaseAction implements Serializable {
    Integer userId;
    BigDecimal amount;

    public PurchaseAction() {
        super();
    }

    public PurchaseAction(Integer userId, BigDecimal amount) {
        this.userId = userId;
        this.amount = amount;
        System.out.println(toString());
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String toString() {
        return "userId:" + userId + " amount:" + amount.toPlainString();
    }
}
