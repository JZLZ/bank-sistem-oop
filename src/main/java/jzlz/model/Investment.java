package jzlz.model;

public record Investment(
    long id,
    long tax,
    long initialFunds)
{
    @Override
    public String toString() {
        return String.format(
                "Investimento ID: %d | Taxa: %d%% | Valor Inicial: R$ %d",
                id,
                tax,
                initialFunds
        );
    }
}
