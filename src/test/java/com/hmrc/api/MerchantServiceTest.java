package com.hmrc.api;

import com.hmrc.api.dto.MerchantDto;
import com.hmrc.api.dto.MerchantDtoList;
import com.hmrc.api.model.Merchant;
import com.hmrc.api.repository.MerchantRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@ContextConfiguration(classes = HmrcCamelApiApplication.class, loader = SpringBootContextLoader.class)
public class MerchantServiceTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MerchantRepository merchantRepository;

    @Test
    void contextLoads() {
        assertTrue(merchantRepository != null);
    }

    @Before
    public void setup() {
        merchantRepository.deleteAll();
    }

    @Test
    public void shouldGetAllMerchants() throws Exception {

        // Given
        merchantRepository.deleteAll();
        merchantRepository.save(new Merchant(null, "Merchant1"));
        merchantRepository.save(new Merchant(null, "Merchant2"));
        merchantRepository.save(new Merchant(null, "Merchant3"));
        // When
        ResponseEntity<MerchantDtoList> actual = restTemplate.getForEntity("/api/merchants", MerchantDtoList.class);

        // Then
        assertThat(actual.getStatusCode(), is(OK));
        assertThat(actual.getBody().getMerchantDtoList().size(), is(3));

    }

    @Test
    public void shouldGetMerchantById() throws Exception {
        // Given
        Merchant merchant = merchantRepository.save(new Merchant(null, "Merchant1"));

        // When
        ResponseEntity<MerchantDto> actual = restTemplate.getForEntity(format("/api/merchants/%s/merchant", merchant.getId()), MerchantDto.class);

        // Then
        assertThat(actual.getStatusCode(), is(OK));
        assertThat(actual.getBody().getId(), is(merchant.getId()));
    }

    @Test
    public void shouldCreateMerchant() throws Exception {
        // Given
        MerchantDto merchantDto = new MerchantDto(null, "merchant");

        // When
        ResponseEntity<String> response = restTemplate.postForEntity("/api/merchants", merchantDto, String.class);

        // Then
        assertThat(response.getStatusCode(), is(CREATED));

    }

    @Test
    public void shouldUpdateMerchant() throws Exception {
        // Given
        Merchant merchant = new Merchant(null, "merchant");
        merchant = merchantRepository.save(merchant);

        // When
        MerchantDto merchantDto = new MerchantDto(merchant.getId(), "merchant1");
        ResponseEntity<String> actual = restTemplate.exchange(format("/api/merchants/%s", merchant.getId()), HttpMethod.PUT, new HttpEntity<>(merchantDto), String.class);

        // Then
        assertThat(actual.getStatusCode(), is(NO_CONTENT));
        assertThat(merchantRepository.findById(merchant.getId()).get().getName(), is("merchant1"));

    }

}
