package jzlz.repository;

import jzlz.exception.AccountWithInvestmentException;
import jzlz.exception.InvestmentNotFoundException;
import jzlz.exception.WalletNotFoundException;
import jzlz.model.AccountWallet;
import jzlz.model.Investment;
import jzlz.model.InvestmentWallet;

import java.util.ArrayList;
import java.util.List;

import static jzlz.repository.CommonsRepository.checkFundsForTransaction;

public class InvestmentRepository {

    private long nextId;
    private final List<Investment> investments = new ArrayList<>();
    private final List<InvestmentWallet> wallets = new ArrayList<>();

    public Investment create(final long tax, final long initialFunds){
        this.nextId ++;
        var investment = new Investment(this.nextId, tax, initialFunds);
        investments.add(investment);
        return investment;
    }

    public InvestmentWallet initInvestment(final AccountWallet account, final long id){
        var accountInUse = wallets.stream().map(InvestmentWallet::getAccount).toList();
        if (accountInUse.contains(account)) {
            throw new AccountWithInvestmentException("A conta '" + account + "' já possui um investimento.");
        }

        var investment = findById(id);
        checkFundsForTransaction(account, investment.initialFunds());
        var wallet = new InvestmentWallet(investment, account, investment.initialFunds());
        wallets.add(wallet);
        return wallet;
    }

    public InvestmentWallet deposit(final String pix, final long funds){
        var wallet = findWalletByAccountPix(pix);
        wallet.addMoney(wallet.getAccount().reduceMoney(funds), wallet.getService(), "Investimento", funds);
        return wallet;
    }

    public InvestmentWallet withdraw(final String pix, final long funds){
        var wallet = findWalletByAccountPix(pix);
        checkFundsForTransaction(wallet, funds);
        wallet.getAccount().addMoney(wallet.reduceMoney(funds),wallet.getService(), "Saque de investimentos.", funds);
        if(wallet.getFunds() == 0){
            wallets.remove(wallet);
        }
        return wallet;
    }

    public void updateAmount(){
        long percent = 0;
        wallets.forEach(w -> w.updateAmount(percent));
    }

    public Investment findById(final long id) {
        return investments.stream()
                .filter(a -> a.id() == id)
                .findFirst()
                .orElseThrow(
                        () -> new InvestmentNotFoundException("O investimento '"+ id +"' não foi encontrado")
                );
    }

    public InvestmentWallet findWalletByAccountPix(final String pix){
        return wallets.stream()
                .filter(w -> w.getAccount().getPix().contains(pix))
                .findFirst()
                .orElseThrow(
                        () ->new WalletNotFoundException("A carteira não foi encontrada")
                );
    }

    public List<InvestmentWallet> listWallets(){
        return this.wallets;
    }

    public List<Investment> list() {
        return this.investments;
    }
}
