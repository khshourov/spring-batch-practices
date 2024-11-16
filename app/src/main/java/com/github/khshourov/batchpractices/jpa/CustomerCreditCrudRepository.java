package com.github.khshourov.batchpractices.jpa;

import com.github.khshourov.batchpractices.domain.trade.CustomerCredit;
import org.springframework.data.repository.CrudRepository;

public interface CustomerCreditCrudRepository extends CrudRepository<CustomerCredit, Long> {}
