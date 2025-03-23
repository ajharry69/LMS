package com.github.ajharry69.lms.services.loan.integration;

import com.github.ajharry69.lms.config.LmsProperties;
import com.github.ajharry69.lms.services.loan.integration.transaction.wsdl.TransactionsRequest;
import com.github.ajharry69.lms.services.loan.integration.transaction.wsdl.TransactionsResponse;
import com.github.ajharry69.lms.services.loan.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;

import static java.util.Base64.getEncoder;

@Component
@Slf4j
public class TransactionApiClient extends WebServiceGatewaySupport {
    private final LmsProperties lmsProperties;

    public TransactionApiClient(
            LmsProperties lmsProperties,
            @Qualifier(value = "transactionMarshaller")
            Jaxb2Marshaller marshaller
    ) {
        this.lmsProperties = lmsProperties;
        setMarshaller(marshaller);
        setUnmarshaller(marshaller);
    }

    public List<Transaction> getTransactions(String customerNumber) {
        log.info("Fetching transactions from Transaction API for customer number: {}", customerNumber);

        // Set credentials for Basic Authentication in the SOAP request
        var messageSender = new HttpUrlConnectionMessageSender() {
            @Override
            protected void prepareConnection(HttpURLConnection connection) throws IOException {
                super.prepareConnection(connection);
                String authString = lmsProperties.username() + ":" + lmsProperties.password();
                String authEncoded = getEncoder().encodeToString(authString.getBytes());
                connection.setRequestProperty("Authorization", "Basic " + authEncoded);
            }
        };
        getWebServiceTemplate().setMessageSender(messageSender);

        var request = new TransactionsRequest();
        request.setCustomerNumber(customerNumber);

        var response = (TransactionsResponse) getWebServiceTemplate().marshalSendAndReceive(
                "https://trxapitest.credable.io/service/transactionWsdl.wsdl",
                request,
                new SoapActionCallback("https://transaction.credable.com/TransactionsRequest")
        );

        if (response == null || response.getTransactions() == null) {
            log.error("Failed to retrieve transactions from Transaction API.");
            return Collections.emptyList();
        }

        log.info("Successfully retrieved transactions from Transaction API.");
        return response.getTransactions()
                .stream()
                .map(transaction -> Transaction.builder()
                        .accountNumber(transaction.getAccountNumber())
                        .alternativechanneltrnscrAmount(transaction.getAlternativechanneltrnscrAmount())
                        .alternativechanneltrnscrNumber(transaction.getAlternativechanneltrnscrNumber())
                        .alternativechanneltrnsdebitAmount(transaction.getAlternativechanneltrnsdebitAmount())
                        .alternativechanneltrnsdebitNumber(transaction.getAlternativechanneltrnsdebitNumber())
                        .atmTransactionsNumber(transaction.getAtmTransactionsNumber())
                        .atmtransactionsAmount(transaction.getAtmtransactionsAmount())
                        .bouncedChequesDebitNumber(transaction.getBouncedChequesDebitNumber())
                        .bouncedchequescreditNumber(transaction.getBouncedchequescreditNumber())
                        .bouncedchequetransactionscrAmount(transaction.getBouncedchequetransactionscrAmount())
                        .bouncedchequetransactionsdrAmount(transaction.getBouncedchequetransactionsdrAmount())
                        .chequeDebitTransactionsAmount(transaction.getChequeDebitTransactionsAmount())
                        .chequeDebitTransactionsNumber(transaction.getChequeDebitTransactionsNumber())
                        .createdAt(transaction.getCreatedAt().toGregorianCalendar().getTimeInMillis())
                        .createdDate(transaction.getCreatedDate().toGregorianCalendar().getTimeInMillis())
                        .credittransactionsAmount(transaction.getCredittransactionsAmount())
                        .debitcardpostransactionsAmount(transaction.getDebitcardpostransactionsAmount())
                        .debitcardpostransactionsNumber(transaction.getDebitcardpostransactionsNumber())
                        .fincominglocaltransactioncrAmount(transaction.getFincominglocaltransactioncrAmount())
                        .id(transaction.getId())
                        .incominginternationaltrncrAmount(transaction.getIncominginternationaltrncrAmount())
                        .incominginternationaltrncrNumber(transaction.getIncominginternationaltrncrNumber())
                        .incominglocaltransactioncrNumber(transaction.getIncominglocaltransactioncrNumber())
                        .intrestAmount(transaction.getIntrestAmount())
                        .lastTransactionDate(transaction.getLastTransactionDate().toGregorianCalendar().getTimeInMillis())
                        .lastTransactionType(transaction.getLastTransactionType())
                        .lastTransactionValue(transaction.getLastTransactionValue())
                        .maxAtmTransactions(transaction.getMaxAtmTransactions())
                        .maxMonthlyBebitTransactions(transaction.getMaxMonthlyBebitTransactions())
                        .maxalternativechanneltrnscr(transaction.getMaxalternativechanneltrnscr())
                        .maxalternativechanneltrnsdebit(transaction.getMaxalternativechanneltrnsdebit())
                        .maxbouncedchequetransactionscr(transaction.getMaxbouncedchequetransactionscr())
                        .maxchequedebittransactions(transaction.getMaxchequedebittransactions())
                        .maxdebitcardpostransactions(transaction.getMaxdebitcardpostransactions())
                        .maxincominginternationaltrncr(transaction.getMaxincominginternationaltrncr())
                        .maxincominglocaltransactioncr(transaction.getMaxincominglocaltransactioncr())
                        .maxmobilemoneycredittrn(transaction.getMaxmobilemoneycredittrn())
                        .maxmobilemoneydebittransaction(transaction.getMaxmobilemoneydebittransaction())
                        .maxmonthlycredittransactions(transaction.getMaxmonthlycredittransactions())
                        .maxoutgoinginttrndebit(transaction.getMaxoutgoinginttrndebit())
                        .maxoutgoinglocaltrndebit(transaction.getMaxoutgoinglocaltrndebit())
                        .maxoverthecounterwithdrawals(transaction.getMaxoverthecounterwithdrawals())
                        .minAtmTransactions(transaction.getMinAtmTransactions())
                        .minMonthlyDebitTransactions(transaction.getMinMonthlyDebitTransactions())
                        .minalternativechanneltrnscr(transaction.getMinalternativechanneltrnscr())
                        .minalternativechanneltrnsdebit(transaction.getMinalternativechanneltrnsdebit())
                        .minbouncedchequetransactionscr(transaction.getMinbouncedchequetransactionscr())
                        .minchequedebittransactions(transaction.getMinchequedebittransactions())
                        .mindebitcardpostransactions(transaction.getMindebitcardpostransactions())
                        .minincominginternationaltrncr(transaction.getMinincominginternationaltrncr())
                        .minincominglocaltransactioncr(transaction.getMinincominglocaltransactioncr())
                        .minmobilemoneycredittrn(transaction.getMinmobilemoneycredittrn())
                        .minmobilemoneydebittransaction(transaction.getMinmobilemoneydebittransaction())
                        .minmonthlycredittransactions(transaction.getMinmonthlycredittransactions())
                        .minoutgoinginttrndebit(transaction.getMinoutgoinginttrndebit())
                        .minoutgoinglocaltrndebit(transaction.getMinoutgoinglocaltrndebit())
                        .minoverthecounterwithdrawals(transaction.getMinoverthecounterwithdrawals())
                        .mobilemoneycredittransactionAmount(transaction.getMobilemoneycredittransactionAmount())
                        .mobilemoneycredittransactionNumber(transaction.getMobilemoneycredittransactionNumber())
                        .mobilemoneydebittransactionAmount(transaction.getMobilemoneydebittransactionAmount())
                        .mobilemoneydebittransactionNumber(transaction.getMobilemoneydebittransactionNumber())
                        .monthlyBalance(transaction.getMonthlyBalance())
                        .monthlydebittransactionsAmount(transaction.getMonthlydebittransactionsAmount())
                        .outgoinginttransactiondebitAmount(transaction.getOutgoinginttransactiondebitAmount())
                        .outgoinginttrndebitNumber(transaction.getOutgoinginttrndebitNumber())
                        .outgoinglocaltransactiondebitAmount(transaction.getOutgoinglocaltransactiondebitAmount())
                        .outgoinglocaltransactiondebitNumber(transaction.getOutgoinglocaltransactiondebitNumber())
                        .overdraftLimit(transaction.getOverdraftLimit())
                        .overthecounterwithdrawalsAmount(transaction.getOverthecounterwithdrawalsAmount())
                        .overthecounterwithdrawalsNumber(transaction.getOverthecounterwithdrawalsNumber())
                        .transactionValue(transaction.getTransactionValue())
                        .updatedAt(transaction.getUpdatedAt().toGregorianCalendar().getTimeInMillis())
                        .build()
                )
                .toList();
    }
}