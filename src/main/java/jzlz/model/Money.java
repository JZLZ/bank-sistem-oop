package jzlz.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
@ToString
@Getter
public class Money {
    private final List<MoneyAudit> history = new ArrayList<>();
    public long amount;

    public Money(final MoneyAudit history, long amount){
        this.history.add(history);
        this.amount = amount;
    }

    public void addHistory(final MoneyAudit history){
        this.history.add(history);
    }

    // Método para diminuir o valor do amount
    public void subtractAmount(long value) {
        if (value > this.amount) {
            throw new IllegalArgumentException("Valor a subtrair maior que o amount disponível");
        }
        this.amount -= value;
    }
}
