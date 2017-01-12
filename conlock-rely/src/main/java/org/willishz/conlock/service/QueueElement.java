package org.willishz.conlock.service;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author willishz Lu
 */
public interface QueueElement extends Serializable {

    BigDecimal getLimit();

}
