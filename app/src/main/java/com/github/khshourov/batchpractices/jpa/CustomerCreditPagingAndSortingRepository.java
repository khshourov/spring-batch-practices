package com.github.khshourov.batchpractices.jpa;

import com.github.khshourov.batchpractices.domain.trade.CustomerCredit;
import java.math.BigDecimal;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CustomerCreditPagingAndSortingRepository
    extends PagingAndSortingRepository<CustomerCredit, Long> {

  Slice<CustomerCredit> findByCreditGreaterThan(BigDecimal credit, Pageable request);
}
