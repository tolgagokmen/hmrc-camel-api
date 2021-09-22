package com.hmrc.api.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmrc.api.dto.MerchantDto;
import com.hmrc.api.dto.MerchantDtoList;
import com.hmrc.api.exception.MerchantNotFoundException;
import com.hmrc.api.model.Merchant;
import com.hmrc.api.service.MerchantService;
import org.apache.camel.Exchange;
import org.apache.camel.ValidationException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.springframework.http.HttpStatus.*;
import static org.apache.camel.Exchange.HTTP_RESPONSE_CODE;

@Component
public class MerchantServiceRoute extends RouteBuilder {

    @Value("${context.path}")
    private String contextPath;

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    private MerchantService merchantService;

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

        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .contextPath(contextPath)
                .port(serverPort)
                .enableCORS(true)
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "HMRC CAMEL REST API")
                .apiProperty("api.version", "v1")
                .apiContextRouteId("doc-api");

        rest("/merchants").produces("application/json").consumes("application/json")
                .get()
                .description("Get All Merchants")
                .responseMessage().code(OK.value()).responseModel(MerchantDtoList.class).message("OK").endResponseMessage()
                .route()
                .tracing()

                .log("Message body is ${body}")
                .to("bean:merchantService?method=findAllMerchants")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(OK.value()))
                .outputType(MerchantDtoList.class).description("xx");;

        rest("/merchants").produces("application/json").consumes("application/json")
                .get("/{id}/merchant")
                .description("Get Merchant By Id")
                .responseMessage().code(OK.value()).responseModel(Merchant.class).message("OK").endResponseMessage()
                .param().name("id").type(RestParamType.path).dataType("string").endParam()
                .route()
                .tracing()

                .log("Message body is ${body}")
                .to("bean:merchantService?method=findMerchant(${header.id})")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(OK.value()))
                .outputType(Merchant.class).description("xx");

        rest("/merchants").produces("application/json").consumes("application/json")
                .post().type(MerchantDto.class)
                .description("Create Merchant")
                .responseMessage().code(CREATED.value()).responseModel(String.class).message("CREATED").endResponseMessage()
                .route()
                .tracing()

                .log("Merchant request body : ${body} ")
                .marshal().json(JsonLibrary.Jackson, true)
                .to("json-validator:merchant-schema-defination.json")
                .log("Merchant request validated ${body}")

                .process(exchange -> {
                    String merchantData = exchange.getIn().getBody(String.class);
                    String merchantId = merchantService.createMerchant(new ObjectMapper().readValue(merchantData, MerchantDto.class));
                    exchange.getIn().setBody(merchantId);
                })
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(CREATED.value()))
                .outputType(String.class);

        rest("/merchants").produces("application/json").consumes("application/json")
                .put().type(MerchantDto.class)
                .description("Update Merchant")
                .responseMessage().code(NO_CONTENT.value()).responseModel(String.class).message("Success").endResponseMessage()
                .route()
                .tracing()

                .log("Merchant request body : ${body} ")
                .marshal().json(JsonLibrary.Jackson, true)
                .to("json-validator:merchant-schema-defination.json")
                .log("Merchant request  validated ${body}")

                .process(exchange -> {
                    String merchantData = exchange.getIn().getBody(String.class);
                    merchantService.updateMerchant(new ObjectMapper().readValue(merchantData, MerchantDto.class));
                })
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(NO_CONTENT.value()))
                .outputType(String.class).description("xx")
                .setBody(simple("UPDATED"));

    }
}
