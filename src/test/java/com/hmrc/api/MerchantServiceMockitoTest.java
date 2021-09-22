package com.hmrc.api;


import com.hmrc.api.dto.MerchantDto;
import com.hmrc.api.dto.MerchantDtoList;
import com.hmrc.api.exception.MerchantNotFoundException;
import com.hmrc.api.model.Merchant;
import com.hmrc.api.repository.MerchantRepository;
import com.hmrc.api.service.MerchantService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class MerchantServiceMockitoTest {

    @InjectMocks
    private MerchantService merchantService;

    @Mock
    private MerchantRepository merchantRepository;

    @Test
    public void shouldGetAllMerchants() {
        // Given
        List<Merchant> merchants = newArrayList(
                new Merchant("1", "merchant1"),
                new Merchant("2", "merchant2"));
        given(merchantRepository.findAll()).willReturn(merchants);

        // When
        MerchantDtoList actual = merchantService.findAllMerchants();

        // Then
        assertThat(actual.getMerchantDtoList().get(0).getId(), is(merchants.get(0).getId()));
        assertThat(actual.getMerchantDtoList().get(1).getId(), is(merchants.get(1).getId()));
    }

    @Test
    public void shouldGetOneMerchant() {
        // Given
        List<Merchant> order = newArrayList(new Merchant("1", "Merchant1"), new Merchant("2", "Merchant2"));
        given(merchantRepository.findById("1")).willReturn(Optional.of(order.get(0)));

        // When
        Merchant actual = merchantService.findMerchant("1");

        // Then
        assertThat(actual.getId(), is("1"));
    }

    @Test(expected = MerchantNotFoundException.class)
    public void merchantNotFound() {
        merchantService.findMerchant("3");
    }

    @Test
    public void shouldCreateMerchant() {
        // Given
        Merchant merchant = new Merchant(null, "Merchant");
        Merchant merchantWithId = new Merchant("1", "Merchant");
        given(merchantRepository.save(merchant)).willReturn(merchantWithId);

        // When
        String createdMerchant = merchantService.createMerchant(new MerchantDto(null, "Merchant"));

        // Then
        assertThat(createdMerchant, is(merchantWithId.getId()));
    }

    @Test
    public void shouldUpdateMerchant() {
        // Given
        Merchant merchantWithId = new Merchant("1", "Merchant");

        // When
        merchantService.updateMerchant(new MerchantDto("1", "Merchant"));

        // Then
        verify(merchantRepository, times(1)).save(merchantWithId);
    }

    @Test
    public void shouldThrowExceptionIfIdIsNulL() {
        // When
        try {
            merchantService.updateMerchant(new MerchantDto(null, "Merchant"));
            fail("Runtime exception should have been thrown");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Merchant ID Mandatory"));
        }

        // Then
        verify(merchantRepository, never()).save(any());
    }

}
