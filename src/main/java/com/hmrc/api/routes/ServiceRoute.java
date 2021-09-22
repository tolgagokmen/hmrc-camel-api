package com.hmrc.api.routes;

import com.hmrc.api.dto.MerchantDtoList;
import com.hmrc.api.exception.MerchantNotFoundException;
import com.hmrc.api.model.Merchant;
import com.hmrc.api.processor.MerhantCreateProcessor;
import com.hmrc.api.processor.MerhantUpdateProcessor;
import com.hmrc.api.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.ValidationException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.apache.camel.Exchange.HTTP_RESPONSE_CODE;
import static org.springframework.http.HttpStatus.*;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ServiceRoute extends RouteBuilder {

    private final MerchantService merchantService;

    private final MerhantUpdateProcessor merhantUpdateProcessor;

    private final MerhantCreateProcessor merhantCreateProcessor;

    @Override
    public void configure() throws Exception {
        onException(ValidationException.class)
                .handled(true)
                .setHeader(HTTP_RESPONSE_CODE, constant(BAD_REQUEST.value()))
                .log("Error : ${exception.message} ")
                .setBody(simple("Validation Error!"));

        onException(MerchantNotFoundException.class)
                .handled(true)
                .setHeader(HTTP_RESPONSE_CODE, constant(NOT_FOUND.value()))
                .log("Error : ${exception.message} ")
                .setBody(simple("Merchant Not Found  ${body}"));

        onException(RuntimeException.class)
                .handled(true)
                .setHeader(HTTP_RESPONSE_CODE, constant(INTERNAL_SERVER_ERROR.value()))
                .log("Error : ${exception.message} ")
                .setBody(simple("Internal Server Error  ${body}"));

        onException(RuntimeException.class)
                .handled(true)
                .setHeader(HTTP_RESPONSE_CODE, constant(INTERNAL_SERVER_ERROR.value()))
                .log("Error : ${exception.message} ")
                .setBody(simple("Exception  ${body}"));


        from("direct:getAllMerchants")
                .tracing()
                .to("bean:merchantService?method=findAllMerchants")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(OK.value()))
                .outputType(MerchantDtoList.class);

        from("direct:getMerchantById")
                .tracing()
                .to("bean:merchantService?method=findMerchant(${header.id})")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(OK.value()))
                .outputType(Merchant.class);

        from("direct:createMerchant")
                .tracing()
                .log("Merchant request body : ${body} ")
                .marshal().json(JsonLibrary.Jackson, true)
                .to("json-validator:merchant-schema-defination.json")
                .log("Merchant request validated ${body}")

                .process(merhantCreateProcessor)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(CREATED.value()))
                .outputType(String.class);

        from("direct:updateMerchant")
                .tracing()
                .log("Merchant request body : ${body} ")
                .marshal().json(JsonLibrary.Jackson, true)
                .to("json-validator:merchant-schema-defination.json")
                .log("Merchant request  validated ${body}")

                .process(merhantUpdateProcessor)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(NO_CONTENT.value()));

    }
}
