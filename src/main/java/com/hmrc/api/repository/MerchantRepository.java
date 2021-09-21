package com.hmrc.api.repository;

import com.hmrc.api.model.Merchant;
import org.springframework.data.repository.CrudRepository;


public interface MerchantRepository extends CrudRepository<Merchant, String> {
}
