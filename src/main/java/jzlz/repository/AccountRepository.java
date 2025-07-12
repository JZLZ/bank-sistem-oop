package jzlz.repository;

import jzlz.exception.AccountNotFoundException;
import jzlz.exception.PixInUseException;
import jzlz.model.AccountWallet;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static jzlz.repository.CommonsRepository.checkFundsForTransaction;

@Getter
public class AccountRepository {

    private final List<AccountWallet> accounts = new ArrayList<>();

    public AccountWallet create(final List<String> pix, final long initialFunds){
        var pixInUse = accounts.stream()
                .flatMap(a -> a.getPix().stream())
                .toList();
        for (var p : pix) {
            if (pixInUse.contains(p)) {
                throw new PixInUseException("O pix " + p + " já esta em uso.");
            }
        }
        var newAccount = new AccountWallet(initialFunds, pix);
        accounts.add(newAccount);
        return newAccount;
    }

    public void deposit(final String pix, final long fundsAmount){
        var target = findByPix(pix);
        target.addMoney(fundsAmount, "deposito");
    }

    public void withdraw(final String pix, final long amount){
        var source = findByPix(pix);
        checkFundsForTransaction(source, amount);
        source.reduceMoney(amount);
    }

    public void transferMoney(final String sourcePix, final String targetPix, final long amount){
        var source = findByPix(sourcePix);
        checkFundsForTransaction(source, amount);
        var target = findByPix(targetPix);
        var message = "pix enviado de '"+ sourcePix + "' para '"+ targetPix +"'";
        target.addMoney(source.reduceMoney(amount), source.getService(), message, amount);
    }

    public AccountWallet findByPix(final String pix){
        return accounts.stream()
                .filter(a -> a.getPix().contains(pix))
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException("A conta com a chave pix '" + pix +"' não existe ou foi encerrada"));
    }

    public List<AccountWallet> list() {
        return this.accounts;
    }

}
