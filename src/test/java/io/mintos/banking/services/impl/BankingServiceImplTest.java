package io.mintos.banking.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import io.mintos.banking.domain.Account;
import io.mintos.banking.domain.Client;
import io.mintos.banking.domain.Transactions;
import io.mintos.banking.enums.TransactionFlow;
import io.mintos.banking.exception.AccountEqualException;
import io.mintos.banking.exception.AmountExceedsException;
import io.mintos.banking.exception.CurrencyNotSupportedException;
import io.mintos.banking.records.AccountRecord;
import io.mintos.banking.records.TransactionsRecord;
import io.mintos.banking.records.TransfareRecord;
import io.mintos.banking.repos.AccountRepository;
import io.mintos.banking.repos.ClientRepository;
import io.mintos.banking.repos.TransactionsRepository;
import io.mintos.banking.utils.CurrencyConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {BankingServiceImpl.class})
@ExtendWith(SpringExtension.class)
class BankingServiceImplTest {
    @MockBean
    private AccountRepository accountRepository;

    @Autowired
    private BankingServiceImpl bankingServiceImpl;

    @MockBean
    private ClientRepository clientRepository;

    @MockBean
    private CurrencyConverter currencyConverter;

    @MockBean
    private TransactionsRepository transactionsRepository;

    /**
     * Method under test: {@link BankingServiceImpl#getAccountsByClient(String)}
     */
    @Test
    void testGetAccountsByClient() {
        // Arrange
        ArrayList<AccountRecord> accountRecordList = new ArrayList<>();
        when(clientRepository.findClientByPersonalCode(Mockito.<String>any())).thenReturn(accountRecordList);

        // Act
        List<AccountRecord> actualAccountsByClient = bankingServiceImpl.getAccountsByClient("354651611615");

        // Assert
        verify(clientRepository).findClientByPersonalCode("354651611615");
        assertTrue(actualAccountsByClient.isEmpty());
        assertSame(accountRecordList, actualAccountsByClient);
    }

    /**
     * Method under test: {@link BankingServiceImpl#getAccountsByClient(String)}
     */
    @Test
    void testGetAccountsByClient2() {
        // Arrange
        when(clientRepository.findClientByPersonalCode(Mockito.<String>any()))
                .thenThrow(new CurrencyNotSupportedException("An error occurred"));

        // Act and Assert
        assertThrows(CurrencyNotSupportedException.class, () -> bankingServiceImpl.getAccountsByClient("354651611615"));
        verify(clientRepository).findClientByPersonalCode("354651611615");
    }

    /**
     * Method under test:
     * {@link BankingServiceImpl#getTransactionHistory(String, Integer, Integer)}
     */
    @Test
    void testGetTransactionHistory() {
        // Arrange
        when(transactionsRepository.findAllByAccountNumber(Mockito.<String>any(), Mockito.<Pageable>any()))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        // Act
        List<TransactionsRecord> actualTransactionHistory = bankingServiceImpl.getTransactionHistory("42", 2, 1);

        // Assert
        verify(transactionsRepository).findAllByAccountNumber(eq("42"), Mockito.<Pageable>any());
        assertTrue(actualTransactionHistory.isEmpty());
    }

    /**
     * Method under test:
     * {@link BankingServiceImpl#getTransactionHistory(String, Integer, Integer)}
     */
    @Test
    void testGetTransactionHistory2() {
        // Arrange
        Transactions transactions = new Transactions();
        transactions.setAccountNumber("42");
        transactions.setAmount(new BigDecimal("2.3"));
        transactions.setBalance(new BigDecimal("2.3"));
        transactions.setCurrency("GBP");
        transactions.setId(1L);
        transactions.setTimestamp(OffsetDateTime.of(LocalDate.of(1970, 1, 1), LocalTime.MIDNIGHT, ZoneOffset.UTC));
        transactions.setTransactionFlow(TransactionFlow.INCOMING);

        ArrayList<Transactions> content = new ArrayList<>();
        content.add(transactions);
        PageImpl<Transactions> pageImpl = new PageImpl<>(content);
        when(transactionsRepository.findAllByAccountNumber(Mockito.<String>any(), Mockito.<Pageable>any()))
                .thenReturn(pageImpl);

        // Act
        List<TransactionsRecord> actualTransactionHistory = bankingServiceImpl.getTransactionHistory("42", 2, 1);

        // Assert
        verify(transactionsRepository).findAllByAccountNumber(eq("42"), Mockito.<Pageable>any());
        assertEquals(1, actualTransactionHistory.size());
    }

    /**
     * Method under test:
     * {@link BankingServiceImpl#getTransactionHistory(String, Integer, Integer)}
     */
    @Test
    void testGetTransactionHistory3() {
        // Arrange
        Transactions transactions = new Transactions();
        transactions.setAccountNumber("42");
        transactions.setAmount(new BigDecimal("2.3"));
        transactions.setBalance(new BigDecimal("2.3"));
        transactions.setCurrency("GBP");
        transactions.setId(1L);
        transactions.setTimestamp(OffsetDateTime.of(LocalDate.of(1970, 1, 1), LocalTime.MIDNIGHT, ZoneOffset.UTC));
        transactions.setTransactionFlow(TransactionFlow.INCOMING);

        Transactions transactions2 = new Transactions();
        transactions2.setAccountNumber("3");
        transactions2.setAmount(new BigDecimal("2.3"));
        transactions2.setBalance(new BigDecimal("2.3"));
        transactions2.setCurrency("USD");
        transactions2.setId(2L);
        transactions2.setTimestamp(OffsetDateTime.of(LocalDate.of(1970, 1, 1), LocalTime.MIDNIGHT, ZoneOffset.UTC));
        transactions2.setTransactionFlow(TransactionFlow.OUTGOING);

        ArrayList<Transactions> content = new ArrayList<>();
        content.add(transactions2);
        content.add(transactions);
        PageImpl<Transactions> pageImpl = new PageImpl<>(content);
        when(transactionsRepository.findAllByAccountNumber(Mockito.<String>any(), Mockito.<Pageable>any()))
                .thenReturn(pageImpl);

        // Act
        List<TransactionsRecord> actualTransactionHistory = bankingServiceImpl.getTransactionHistory("42", 2, 1);

        // Assert
        verify(transactionsRepository).findAllByAccountNumber(eq("42"), Mockito.<Pageable>any());
        assertEquals(2, actualTransactionHistory.size());
    }

    /**
     * Method under test:
     * {@link BankingServiceImpl#getTransactionHistory(String, Integer, Integer)}
     */
    @Test
    void testGetTransactionHistory4() {
        // Arrange
        when(transactionsRepository.findAllByAccountNumber(Mockito.<String>any(), Mockito.<Pageable>any()))
                .thenThrow(new CurrencyNotSupportedException("Transfer currency does not match receiver currency!"));

        // Act and Assert
        assertThrows(CurrencyNotSupportedException.class, () -> bankingServiceImpl.getTransactionHistory("42", 2, 1));
        verify(transactionsRepository).findAllByAccountNumber(eq("42"), Mockito.<Pageable>any());
    }

    /**
     * Method under test: {@link BankingServiceImpl#transferFunds(TransfareRecord)}
     */
    @Test
    void testTransferFunds() {
        // Arrange, Act and Assert
        AccountEqualException exception = assertThrows(AccountEqualException.class,
                () -> bankingServiceImpl.transferFunds(new TransfareRecord("386545", "386545", new BigDecimal("23"), "EUR")));
        assertAll( () -> assertNotNull(exception),
                () -> assertEquals("Sender and receiver accounts must differ!", exception.getMessage()));
    }

    /**
     * Method under test: {@link BankingServiceImpl#transferFunds(TransfareRecord)}
     */
    @Test
    void testTransferFunds2() {
        // Arrange
        Client client = new Client();
        client.setAccounts(new HashSet<>());
        client.setId(1L);
        client.setName("Name");
        client.setPersonalCode("Personal Code");
        client.setSurname("Doe");

        Account account = new Account();
        account.setAccountNumber("42");
        account.setBalance(new BigDecimal("2.3"));
        account.setClient(client);
        account.setCurrency("GBP");
        account.setId(1L);

        Client client2 = new Client();
        client2.setAccounts(new HashSet<>());
        client2.setId(1L);
        client2.setName("Name");
        client2.setPersonalCode("Personal Code");
        client2.setSurname("Doe");

        Account account2 = new Account();
        account2.setAccountNumber("42");
        account2.setBalance(new BigDecimal("2.3"));
        account2.setClient(client2);
        account2.setCurrency("GBP");
        account2.setId(1L);
        when(accountRepository.save(Mockito.<Account>any())).thenReturn(account2);
        when(accountRepository.getAccountByAccountNumber(Mockito.<String>any())).thenReturn(account);

        Transactions transactions = new Transactions();
        transactions.setAccountNumber("42");
        transactions.setAmount(new BigDecimal("2.3"));
        transactions.setBalance(new BigDecimal("2.3"));
        transactions.setCurrency("GBP");
        transactions.setId(1L);
        transactions.setTimestamp(OffsetDateTime.of(LocalDate.of(1970, 1, 1), LocalTime.MIDNIGHT, ZoneOffset.UTC));
        transactions.setTransactionFlow(TransactionFlow.INCOMING);
        when(transactionsRepository.save(Mockito.<Transactions>any())).thenReturn(transactions);

        // Act
        bankingServiceImpl.transferFunds(
                new TransfareRecord("654813545", "3", new BigDecimal("2.3"), "GBP"));

        // Assert
        verify(accountRepository, atLeast(1)).getAccountByAccountNumber(Mockito.<String>any());
        verify(accountRepository, atLeast(1)).save(Mockito.<Account>any());
        verify(transactionsRepository, atLeast(1)).save(Mockito.<Transactions>any());
    }

    /**
     * Method under test: {@link BankingServiceImpl#transferFunds(TransfareRecord)}
     */
    @Test
    void testTransferFunds3() {
        // Arrange
        Client client = new Client();
        client.setAccounts(new HashSet<>());
        client.setId(1L);
        client.setName("Name");
        client.setPersonalCode("Personal Code");
        client.setSurname("Doe");

        Account account = new Account();
        account.setAccountNumber("42");
        account.setBalance(new BigDecimal("2.3"));
        account.setClient(client);
        account.setCurrency("GBP");
        account.setId(1L);

        Client client2 = new Client();
        client2.setAccounts(new HashSet<>());
        client2.setId(1L);
        client2.setName("Name");
        client2.setPersonalCode("Personal Code");
        client2.setSurname("Doe");

        Account account2 = new Account();
        account2.setAccountNumber("345782452");
        account2.setBalance(new BigDecimal("2.3"));
        account2.setClient(client2);
        account2.setCurrency("GBP");
        account2.setId(1L);

        when(accountRepository.getAccountByAccountNumber(Mockito.<String>any())).thenReturn(account);
        when(transactionsRepository.save(Mockito.<Transactions>any()))
                .thenThrow(new CurrencyNotSupportedException("Transfer currency does not match receiver currency!"));

        // Act and Assert
        CurrencyNotSupportedException exception = assertThrows(CurrencyNotSupportedException.class, () -> bankingServiceImpl.transferFunds(
                new TransfareRecord("48461816", "345782452", new BigDecimal("2.3"), "USD")));

        assertAll( () -> assertNotNull(exception),
                () -> assertEquals("Transfer currency does not match receiver currency!", exception.getMessage()));

        verify(accountRepository, atLeast(0)).getAccountByAccountNumber(Mockito.<String>any());
    }

    /**
     * Method under test: {@link BankingServiceImpl#clientExists(String)}
     */
    @Test
    void testClientExists() {
        // Arrange
        when(clientRepository.existsByPersonalCode(Mockito.<String>any())).thenReturn(true);

        // Act
        boolean actualClientExistsResult = bankingServiceImpl.clientExists("345345258");

        // Assert
        verify(clientRepository).existsByPersonalCode("345345258");
        assertTrue(actualClientExistsResult);
    }

    /**
     * Method under test: {@link BankingServiceImpl#clientExists(String)}
     */
    @Test
    void testClientExists2() {
        // Arrange
        when(clientRepository.existsByPersonalCode(Mockito.<String>any())).thenReturn(false);

        // Act
        boolean actualClientExistsResult = bankingServiceImpl.clientExists("345248842");

        // Assert
        verify(clientRepository).existsByPersonalCode("345248842");
        assertFalse(actualClientExistsResult);
    }

    /**
     * Method under test: {@link BankingServiceImpl#clientExists(String)}
     */
    @Test
    void testClientExists3() {
        // Arrange
        when(clientRepository.existsByPersonalCode(Mockito.<String>any()))
                .thenThrow(new CurrencyNotSupportedException("Transfer currency does not match receiver currency!"));

        // Act and Assert
        assertThrows(CurrencyNotSupportedException.class, () -> bankingServiceImpl.clientExists("345452782"));
        verify(clientRepository).existsByPersonalCode("345452782");
    }

    @Test
    void testValidateAmount() {
        // Arrange
        TransfareRecord transfareRecord = new TransfareRecord("3", "3", new BigDecimal("2.3"), "GBP");

        Client client = new Client();
        client.setAccounts(new HashSet<>());
        client.setId(1L);
        client.setName("Name");
        client.setPersonalCode("Personal Code");
        client.setSurname("Doe");

        Account senderAccount = new Account();
        senderAccount.setAccountNumber("42");
        senderAccount.setBalance(new BigDecimal("2.3"));
        senderAccount.setClient(client);
        senderAccount.setCurrency("GBP");
        senderAccount.setId(1L);

        // Act
        bankingServiceImpl.validateAmount(transfareRecord, senderAccount);

        // Assert that nothing has changed
        assertEquals("42", senderAccount.getAccountNumber());
        assertEquals("GBP", senderAccount.getCurrency());
        assertEquals(1L, senderAccount.getId().longValue());
        BigDecimal expectedBalance = new BigDecimal("2.3");
        assertEquals(expectedBalance, senderAccount.getBalance());
        assertSame(client, senderAccount.getClient());
    }

    /**
     * Method under test:
     * {@link BankingServiceImpl#validateAmount(TransfareRecord, Account)}
     */
    @Test
    void testValidateAmount2() {
        // Arrange
        TransfareRecord transfareRecord = new TransfareRecord("3", "3", new BigDecimal("4.5"), "GBP");

        Client client = new Client();
        client.setAccounts(new HashSet<>());
        client.setId(1L);
        client.setName("Name");
        client.setPersonalCode("Personal Code");
        client.setSurname("Doe");

        Account senderAccount = new Account();
        senderAccount.setAccountNumber("42");
        senderAccount.setBalance(new BigDecimal("2.3"));
        senderAccount.setClient(client);
        senderAccount.setCurrency("GBP");
        senderAccount.setId(1L);

        // Act and Assert
        assertThrows(AmountExceedsException.class, () -> bankingServiceImpl.validateAmount(transfareRecord, senderAccount));
    }

    @Test
    void testConvert() {
        // Arrange
        BigDecimal amount = new BigDecimal("2.3");
        TransfareRecord transfareRecord = new TransfareRecord("3", "3", amount, "GBP");

        Client client = new Client();
        client.setAccounts(new HashSet<>());
        client.setId(1L);
        client.setName("Name");
        client.setPersonalCode("Personal Code");
        client.setSurname("Doe");

        Account senderAccount = new Account();
        senderAccount.setAccountNumber("42");
        senderAccount.setBalance(new BigDecimal("2.3"));
        senderAccount.setClient(client);
        senderAccount.setCurrency("GBP");
        senderAccount.setId(1L);

        // Act
        BigDecimal actualConvertResult = bankingServiceImpl.convert(transfareRecord, senderAccount);

        // Assert
        assertEquals(new BigDecimal("2.3"), actualConvertResult);
        assertSame(amount, actualConvertResult);
    }

    /**
     * Method under test:
     * {@link BankingServiceImpl#convert(TransfareRecord, Account)}
     */
    @Test
    void testConvert2() {
        // Arrange
        BigDecimal bigDecimal = new BigDecimal("2.3");
        when(currencyConverter.convert(Mockito.<String>any(), Mockito.<String>any(), Mockito.<BigDecimal>any()))
                .thenReturn(bigDecimal);
        TransfareRecord transfareRecord = new TransfareRecord("3", "3", new BigDecimal("2.3"), "USD");

        Client client = new Client();
        client.setAccounts(new HashSet<>());
        client.setId(1L);
        client.setName("Name");
        client.setPersonalCode("Personal Code");
        client.setSurname("Doe");

        Account senderAccount = new Account();
        senderAccount.setAccountNumber("42");
        senderAccount.setBalance(new BigDecimal("2.3"));
        senderAccount.setClient(client);
        senderAccount.setCurrency("GBP");
        senderAccount.setId(1L);

        // Act
        BigDecimal actualConvertResult = bankingServiceImpl.convert(transfareRecord, senderAccount);

        // Assert
        verify(currencyConverter).convert(eq("GBP"), eq("USD"), Mockito.<BigDecimal>any());
        assertEquals(new BigDecimal("2.3"), actualConvertResult);
        assertSame(bigDecimal, actualConvertResult);
    }

    /**
     * Method under test:
     * {@link BankingServiceImpl#convert(TransfareRecord, Account)}
     */
    @Test
    void testConvert3() {
        // Arrange
        when(currencyConverter.convert(Mockito.<String>any(), Mockito.<String>any(), Mockito.<BigDecimal>any()))
                .thenThrow(new CurrencyNotSupportedException("An error occurred"));
        TransfareRecord transfareRecord = new TransfareRecord("3", "3", new BigDecimal("2.3"), "USD");

        Client client = new Client();
        client.setAccounts(new HashSet<>());
        client.setId(1L);
        client.setName("Name");
        client.setPersonalCode("Personal Code");
        client.setSurname("Doe");

        Account senderAccount = new Account();
        senderAccount.setAccountNumber("42");
        senderAccount.setBalance(new BigDecimal("2.3"));
        senderAccount.setClient(client);
        senderAccount.setCurrency("GBP");
        senderAccount.setId(1L);

        // Act and Assert
        assertThrows(CurrencyNotSupportedException.class, () -> bankingServiceImpl.convert(transfareRecord, senderAccount));
        verify(currencyConverter).convert(eq("GBP"), eq("USD"), Mockito.<BigDecimal>any());
    }

    @Test
    void testValidateCurrency() {
        // Arrange, Act and Assert
        assertThrows(CurrencyNotSupportedException.class, () -> bankingServiceImpl.validateCurrency("Currency"));
    }
}
