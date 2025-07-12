package jzlz.repository;

import jzlz.exception.NoFundsEnoughException;
import jzlz.model.AccountWallet;
import jzlz.model.Money;
import jzlz.model.MoneyAudit;
import jzlz.model.Wallet;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static jzlz.model.BankService.ACCOUNT;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class CommonsRepository {

    public static void checkFundsForTransaction(final Wallet source, final long amount){
        if(source.getFunds() < amount){
            throw new NoFundsEnoughException("Sua conta não tem dinheiro suficiente para realizar essa transação.");
        }
    }

    public static List<Money> generateMoney(final UUID transactionID, final long funds, final String description){
        var history = new MoneyAudit(transactionID, ACCOUNT, description, funds, OffsetDateTime.now());
        return Stream.generate(() -> new Money(history, 1L)).limit(funds).toList();
    }

}
