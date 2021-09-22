package com.hmrc.api;

import com.hmrc.api.dto.MerchantDto;
import com.hmrc.api.repository.MerchantRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@ContextConfiguration(classes = HmrcCamelApiApplication.class, loader = SpringBootContextLoader.class)
public class EndToEndTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MerchantRepository merchantRepository;

    @Before
    public void setup() {
        merchantRepository.deleteAll();
    }

    @Test
    public void shouldSuccessWhenCreatingMerchant() {
        // Given
        MerchantDto merchant = new MerchantDto(null, "Merchant");

        // When
        ResponseEntity<String> actual = restTemplate.postForEntity("/api/merchants", merchant, String.class);

        // Then
        assertThat(actual.getStatusCode(), is(CREATED));

    }

    @Test
    public void shouldFailWhenCreatingMerchant() {
        // Given
        MerchantDto merchant = new MerchantDto("1", null);// Name is Mantodary, Validation Error!

        // When
        ResponseEntity<String> actual = restTemplate.postForEntity("/api/merchants", merchant, String.class);

        // Then
        assertThat(actual.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertTrue(actual.getBody().contains("Validation Error!"));

    }


}
