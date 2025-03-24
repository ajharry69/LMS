package com.github.ajharry69.lms.services.loan.integration;

import com.github.ajharry69.lms.config.LmsProperties;
import com.github.ajharry69.lms.services.loan.exception.TransactionRetrievalException;
import com.github.ajharry69.lms.services.loan.integration.transaction.wsdl.TransactionsRequest;
import com.github.ajharry69.lms.services.loan.integration.transaction.wsdl.TransactionsResponse;
import com.github.ajharry69.lms.services.loan.model.Transaction;
import com.github.ajharry69.lms.utils.soap.LmsWebServiceGatewaySupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.WebServiceTransportException;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class TransactionApiClient extends LmsWebServiceGatewaySupport {
    public TransactionApiClient(
            LmsProperties lmsProperties,
            @Qualifier(value = "transactionMarshaller")
            Jaxb2Marshaller marshaller
    ) {
        super(lmsProperties, marshaller, "https://trxapitest.credable.io/service/");
    }

    public List<Transaction> getTransactions(String customerNumber) {
        log.info("Fetching transactions from Transaction API for customer number: {}", customerNumber);
        var request = new TransactionsRequest();
        request.setCustomerNumber(customerNumber);

        TransactionsResponse response;
        try {
            response = (TransactionsResponse) getWebServiceTemplate()
                    .marshalSendAndReceive(request);
        } catch (WebServiceTransportException e) {
            log.error("Failed to retrieve transactions from Transaction API.", e);
            throw new TransactionRetrievalException();
        }

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