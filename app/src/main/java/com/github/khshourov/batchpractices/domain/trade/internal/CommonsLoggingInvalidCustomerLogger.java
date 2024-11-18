package com.github.khshourov.batchpractices.domain.trade.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommonsLoggingInvalidCustomerLogger implements InvalidCustomerLogger {

  protected static final Log LOG = LogFactory.getLog(CommonsLoggingInvalidCustomerLogger.class);

  @Override
  public void log(CustomerUpdate customerUpdate) {
    LOG.error("invalid customer encountered: [ " + customerUpdate + "]");
  }
}
