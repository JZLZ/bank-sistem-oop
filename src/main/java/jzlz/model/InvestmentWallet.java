package jzlz.model;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static jzlz.model.BankService.INVESTMENT;

@Getter
public class InvestmentWallet  extends Wallet{

    private final Investment investment;
    private final AccountWallet account;

    public InvestmentWallet(final Investment investment, AccountWallet account, final long amount) {
        super(INVESTMENT);
        this.investment = investment;
        this.account = account;
        addMoney(account.reduceMoney(amount), getService(), "investimento", amount);
    }

    public void updateAmount(final long percent){
        var amount = getFunds() * percent / 100;
        var history = new MoneyAudit(UUID.randomUUID(),getService(), "rendimentos", amount, OffsetDateTime.now());
        var money = Stream.generate(() -> new Money(history, 1L)). limit(amount). toList();
        this.money.addAll(money);
    }
    @Override
    public String toString() {
        return String.format(
                "Carteira de Investimento [ID Investimento: %d | Pix da Conta: %s | Saldo: R$ %d]",
                investment.id(),
                String.join(", ", account.getPix()),
                getFunds()
        );
    }
}