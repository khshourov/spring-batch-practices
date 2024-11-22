package com.github.khshourov.batchpractices.patternmatching;

import com.github.khshourov.batchpractices.patternmatching.models.Address;
import com.github.khshourov.batchpractices.patternmatching.models.BillingInfo;
import com.github.khshourov.batchpractices.patternmatching.models.Customer;
import com.github.khshourov.batchpractices.patternmatching.models.LineItem;
import com.github.khshourov.batchpractices.patternmatching.models.Order;
import com.github.khshourov.batchpractices.patternmatching.models.ShippingInfo;
import java.util.ArrayList;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class OrderItemReader implements ItemReader<Order> {
  private ItemReader<FieldSet> delegate;

  private FieldSetMapper<Order> orderFieldSetMapper;
  private FieldSetMapper<Customer> customerFieldSetMapper;
  private FieldSetMapper<Address> addressFieldSetMapper;
  private FieldSetMapper<BillingInfo> billingInfoFieldSetMapper;
  private FieldSetMapper<ShippingInfo> shippingInfoFieldSetMapper;
  private FieldSetMapper<LineItem> lineItemFieldSetMapper;

  @Override
  public Order read()
      throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
    Order order = null;

    while (true) {
      FieldSet line = this.delegate.read();
      if (line == null) {
        return null;
      }
      if (Order.LINE_ID_HEADER.equals(line.readString(0))) {
        order = orderFieldSetMapper.mapFieldSet(line);
      }

      assert order != null;

      switch (line.readString(0)) {
        case Order.LINE_ID_FOOTER -> {
          order.setTotalPrice(line.readBigDecimal("TOTAL_PRICE"));
          order.setTotalLines(line.readInt("TOTAL_LINE_ITEMS"));
          order.setTotalItems(line.readInt("TOTAL_ITEMS"));

          return order;
        }
        case Customer.LINE_ID_BUSINESS_CUST -> {
          if (order.getCustomer() == null) {
            order.setCustomer(customerFieldSetMapper.mapFieldSet(line));
            order.getCustomer().setBusinessCustomer(true);
          }
        }
        case Customer.LINE_ID_NON_BUSINESS_CUST -> {
          if (order.getCustomer() == null) {
            order.setCustomer(customerFieldSetMapper.mapFieldSet(line));
            order.getCustomer().setBusinessCustomer(false);
          }
        }
        case Address.LINE_ID_BILLING_ADDR -> {
          order.setBillingAddress(addressFieldSetMapper.mapFieldSet(line));
        }
        case Address.LINE_ID_SHIPPING_ADDR -> {
          order.setShippingAddress(addressFieldSetMapper.mapFieldSet(line));
        }
        case BillingInfo.LINE_ID_BILLING_INFO -> {
          order.setBilling(billingInfoFieldSetMapper.mapFieldSet(line));
        }
        case ShippingInfo.LINE_ID_SHIPPING_INFO -> {
          order.setShipping(shippingInfoFieldSetMapper.mapFieldSet(line));
        }
        case LineItem.LINE_ID_ITEM -> {
          if (order.getLineItems() == null) {
            order.setLineItems(new ArrayList<>());
          }

          order.getLineItems().add(lineItemFieldSetMapper.mapFieldSet(line));
        }
        default -> {
          throw new IllegalArgumentException(
              String.format("%s is not valid token", line.readString(0)));
        }
      }
    }
  }

  public void setDelegate(ItemReader<FieldSet> delegate) {
    this.delegate = delegate;
  }

  public void setOrderFieldSetMapper(FieldSetMapper<Order> orderFieldSetMapper) {
    this.orderFieldSetMapper = orderFieldSetMapper;
  }

  public void setCustomerFieldSetMapper(FieldSetMapper<Customer> customerFieldSetMapper) {
    this.customerFieldSetMapper = customerFieldSetMapper;
  }

  public void setAddressFieldSetMapper(FieldSetMapper<Address> addressFieldSetMapper) {
    this.addressFieldSetMapper = addressFieldSetMapper;
  }

  public void setBillingInfoFieldSetMapper(FieldSetMapper<BillingInfo> billingInfoFieldSetMapper) {
    this.billingInfoFieldSetMapper = billingInfoFieldSetMapper;
  }

  public void setShippingInfoFieldSetMapper(
      FieldSetMapper<ShippingInfo> shippingInfoFieldSetMapper) {
    this.shippingInfoFieldSetMapper = shippingInfoFieldSetMapper;
  }

  public void setLineItemFieldSetMapper(FieldSetMapper<LineItem> lineItemFieldSetMapper) {
    this.lineItemFieldSetMapper = lineItemFieldSetMapper;
  }
}
