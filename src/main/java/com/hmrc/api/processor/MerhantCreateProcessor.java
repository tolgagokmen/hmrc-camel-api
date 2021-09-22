package com.hmrc.api.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmrc.api.dto.MerchantDto;
import com.hmrc.api.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MerhantCreateProcessor implements Processor {

    private final MerchantService merchantService;

    @Override
    public void process(Exchange exchange) throws Exception {
        String merchantData = exchange.getIn().getBody(String.class);
        String merchantId = merchantService.createMerchant(new ObjectMapper().readValue(merchantData, MerchantDto.class));
        exchange.getIn().setBody(merchantId);
    }

}
