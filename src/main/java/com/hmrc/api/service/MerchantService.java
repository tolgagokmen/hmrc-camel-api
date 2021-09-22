package com.hmrc.api.service;


import com.hmrc.api.dto.MerchantDto;
import com.hmrc.api.dto.MerchantDtoList;
import com.hmrc.api.exception.MerchantNotFoundException;
import com.hmrc.api.model.Merchant;
import com.hmrc.api.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MerchantService {

    private final MerchantRepository merchantRepository;

    public Merchant findMerchant(String merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(MerchantNotFoundException::new);
        return merchant;
    }

    public MerchantDtoList findAllMerchants() {
        return new MerchantDtoList(
                newArrayList(merchantRepository.findAll()).stream()
                        .map(merchant -> new MerchantDto(merchant.getId(), merchant.getName())).collect(Collectors.toList())
        );
    }

    public String createMerchant(MerchantDto newMerchant) {
        Merchant merchant = merchantRepository.save(new Merchant(null, newMerchant.getName()));
        return merchant.getId();
    }

    public void updateMerchant(MerchantDto merchantDto) {
        if (merchantDto.getId() == null) throw new RuntimeException("Merchant ID Mandatory");
        merchantRepository.save(new Merchant(merchantDto.getId(), merchantDto.getName()));
    }
}
