package io.mintos.banking.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mintos.banking.records.TransfareRecord;
import io.mintos.banking.services.BankingService;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {BankingController.class})
@ExtendWith(SpringExtension.class)
class BankingControllerTest {
    @Autowired
    private BankingController bankingController;

    @MockBean
    private BankingService bankingService;

    /**
     * Method under test:
     * {@link BankingController#getTransactionHistory(String, Integer, Integer)}
     */
    @Test
    void testGetTransactionHistory() throws Exception {
        // Arrange
        when(bankingService.getTransactionHistory(Mockito.<String>any(), Mockito.<Integer>any(), Mockito.<Integer>any()))
                .thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/bank/accounts/{accountNumber}/transactions", "42");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(bankingController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    /**
     * Method under test: {@link BankingController#transferFunds(TransfareRecord)}
     */
    @Test
    void testTransferFunds() throws Exception {
        // Arrange
        doNothing().when(bankingService).transferFunds(Mockito.<TransfareRecord>any());
        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.post("/bank/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(new TransfareRecord("3", "3", new BigDecimal("2.3"), "GBP")));

        // Act and Assert
        MockMvcBuilders.standaloneSetup(bankingController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Method under test: {@link BankingController#getAccountsByClientId(String)}
     */
    @Test
    void testGetAccountsByClientId2() throws Exception {
        // Arrange
        when(bankingService.getAccountsByClient(Mockito.<String>any())).thenReturn(new ArrayList<>());
        when(bankingService.clientExists(Mockito.<String>any())).thenReturn(false);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/bank/clients/{personalCode}/accounts",
                "3546941651");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(bankingController)
                .build()
                .perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testGetAccountsByClientId3() throws Exception {
        // Arrange
        when(bankingService.getAccountsByClient(Mockito.<String>any())).thenReturn(new ArrayList<>());
        when(bankingService.clientExists(Mockito.<String>any())).thenReturn(true);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/bank/clients/{personalCode}/accounts",
                "Personal Code");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(bankingController)
                .build()
                .perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isOk());
    }
}
