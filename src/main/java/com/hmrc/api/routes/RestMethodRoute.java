package com.hmrc.api.routes;

import com.hmrc.api.dto.MerchantDto;
import com.hmrc.api.dto.MerchantDtoList;
import com.hmrc.api.model.Merchant;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import static org.springframework.http.HttpStatus.*;

@Component
public class RestMethodRoute extends RouteBuilder {

    @Value("${context.path}")
    private String contextPath;

    @Value("${server.port}")
    private String serverPort;

    @Override
    public void configure() throws Exception {

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

        rest("/merchants")
                .get().to("direct:getAllMerchants")
                    .produces(MediaType.APPLICATION_JSON_VALUE)
                    .responseMessage().code(OK.value()).responseModel(MerchantDtoList.class).message("OK").endResponseMessage()
                    .description("Get All Merchants")

                .get("/{id}/merchant").to("direct:getMerchantById")
                    .produces(MediaType.APPLICATION_JSON_VALUE)
                    .responseMessage().code(OK.value()).responseModel(Merchant.class).message("OK").endResponseMessage()
                    .param().name("id").type(RestParamType.path).dataType("string").endParam()
                    .description("Get Merchant By Id")

                .post().to("direct:createMerchant")
                    .produces(MediaType.APPLICATION_JSON_VALUE).consumes(MediaType.APPLICATION_JSON_VALUE)
                    .type(Merchant.class)
                    .description("Create Merchant")
                    .responseMessage().code(CREATED.value()).responseModel(String.class).message("CREATED").endResponseMessage()

                .put().to("direct:updateMerchant").consumes(MediaType.APPLICATION_JSON_VALUE)
                    .type(MerchantDto.class)
                    .description("Update Merchant")
                    .responseMessage().code(NO_CONTENT.value()).endResponseMessage();


    }
}
