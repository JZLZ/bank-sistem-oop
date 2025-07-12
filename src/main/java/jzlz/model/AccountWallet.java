package jzlz.model;

import lombok.Getter;
import lombok.ToString;

import java.util.List;
import static jzlz.model.BankService.ACCOUNT;

@Getter
public class AccountWallet extends Wallet{

    private final List<String> pix;

    public AccountWallet(final List<String> pix) {
        super(ACCOUNT);
        this.pix = pix;
    }

    public AccountWallet(final long amount, final List<String> pix) {
        super(ACCOUNT);
        this.pix = pix;
        addMoney(amount, "valor de criação da conta");
    }

    public void addMoney(final long amount, final String description){
        var money = generateMoney(amount, description);
        addMoney(money, getService(), description, amount);
    }
    @Override
    public String toString() {
        return String.format(
                "Conta Pix: %s | Saldo: R$ %d | Transações: %d",
                String.join(", ", pix),
                getFunds(),
                getFinancialTransaction().size()
        );
    }
}
