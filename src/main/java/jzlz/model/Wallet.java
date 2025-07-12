package jzlz.model;

import jzlz.exception.NoFundsEnoughException;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@ToString
public abstract class Wallet {

    @Getter
    private final BankService service;

    protected final List<Money> money;

    public Wallet(final BankService serviceType) {
        this.service = serviceType;
        this.money = new ArrayList<>();
    }

    /**
     * Gera uma lista de objetos Money com o mesmo histórico.
     */
    protected List<Money> generateMoney(final long amount, final String description){
        var history = new MoneyAudit(UUID.randomUUID(), service, description, amount, OffsetDateTime.now());
        return List.of(new Money(history,amount));
    }

    public long getFunds() {
        return money.stream().mapToLong(Money::getAmount).sum();
    }

    /**
     * Adiciona uma lista de objetos Money e insere um novo histórico de auditoria a todos.
     */
    public void addMoney(final List<Money> money, final BankService service, final String description, final long amount) {
        var history = new MoneyAudit(UUID.randomUUID(), service, description, amount, OffsetDateTime.now());
        money.forEach(m -> m.addHistory(history));
        this.money.addAll(money);
    }

    public List<Money> reduceMoney(final long amount) {
        List<Money> result = new ArrayList<>();
        long remaining = amount;

        var iterator = money.iterator();
        while (iterator.hasNext() && remaining > 0) {
            Money m = iterator.next();
            if (m.getAmount() <= remaining) {
                result.add(m);
                remaining -= m.getAmount();
                iterator.remove();
            } else {
                // Divide o Money
                Money partial = new Money(new MoneyAudit(service, "divisão para saque", remaining), remaining);
                m.addHistory(new MoneyAudit(service, "valor reduzido", remaining));
                m.amount -= remaining;
                result.add(partial);
                remaining = 0;
            }
        }

        return result;
    }



    /**
     * Retorna o histórico completo de transações da carteira.
     */
    public List<MoneyAudit> getFinancialTransaction() {
        return money.stream().flatMap(m -> m.getHistory().stream()).toList();
    }
}
