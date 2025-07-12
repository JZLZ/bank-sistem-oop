package jzlz;

import jzlz.exception.AccountNotFoundException;
import jzlz.exception.NoFundsEnoughException;
import jzlz.exception.PixInUseException;
import jzlz.model.AccountWallet;
import jzlz.repository.AccountRepository;
import jzlz.repository.InvestmentRepository;

import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.SECONDS;

public class Main {

    private final static AccountRepository accountRepository = new AccountRepository();
    private final static InvestmentRepository investmentRepository = new InvestmentRepository();

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Olá. Seja bem vindo ao Bank.");
        while (true){
            System.out.println("Selecione a operação desejada.");
            System.out.println("1 - Criar uma conta");
            System.out.println("2 - Criar um investimento");
            System.out.println("3 - Fazer um investimento");
            System.out.println("4 - Depositar na conta");
            System.out.println("5 - Sacar");
            System.out.println("6 - Transferencia");
            System.out.println("7 - Investir");
            System.out.println("8 - Sacar investimento");
            System.out.println("9 - Listar contas");
            System.out.println("10 - Listar investimentos");
            System.out.println("11 - Listar carteiras de investimento");
            System.out.println("12 - Atualizar investimentos");
            System.out.println("13 - Historico de conta");
            System.out.println("14 - Sair");
            var option = scanner.nextInt();
            scanner.nextLine(); // limpa o \n

            switch (option){
                case 1 -> createAccount();
                case 2 -> createInvestment();
                case 3 -> createWalletInvestment();
                case 4 -> deposit();
                case 5 -> withdraw();
                case 6 -> transferToAccount();
                case 7 -> incInvestment();
                case 8 -> rescueInvestment();
                case 9 -> accountRepository.list().forEach(System.out::println);
                case 10 -> investmentRepository.list().forEach(System.out::println);
                case 11 -> investmentRepository.listWallets().forEach(System.out::println);
                case 12 -> {
                    investmentRepository.updateAmount();
                    System.out.println("Investimentos atualizados");
                }
                case 13 -> checkHistory();
                case 14 -> System.exit(0);
                default -> System.out.println("Opção invalida");
            }
        }
    }
    /* 1- Criar Conta */
    private static void createAccount() {
        try {
            System.out.println("Informe as chaves pix (separadas por ';'):");
            var inputPix = scanner.nextLine().trim();
            if (inputPix.isBlank()) {
                System.out.println("Você precisa informar pelo menos uma chave Pix.");
                return;
            }

            var pix = Arrays.stream(inputPix.split(";"))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .toList();

            System.out.println("Informe o valor de depósito inicial:");
            var inputFunds = scanner.nextLine().trim();
            var initialFunds = Long.parseLong(inputFunds);

            if (initialFunds <= 0) {
                System.out.println("O valor inicial deve ser maior que zero.");
                return;
            }

            var wallet = accountRepository.create(pix, initialFunds);
            System.out.println("Conta criada com Pix: " + String.join(", ", pix) + " | Saldo inicial: " + initialFunds);
        } catch (NumberFormatException e) {
            System.out.println("Erro: valor inválido. Certifique-se de digitar apenas números.");
        } catch (PixInUseException ex) {
            System.out.println("Erro: " + ex.getMessage());
        }
    }

    /* 2- Criar Investimento */
    private static void createInvestment(){
        System.out.println("Informe a taxa do investimento");
        var tax = scanner.nextInt();
        System.out.println("Informe o valor de deposito inicial");
        var initialFunds = scanner.nextLong();
        var investment = investmentRepository.create(tax, initialFunds);
        System.out.println( investment + " realizado com sucesso");
    }
    /* 3- Fazer Investimento */
    private static void createWalletInvestment(){
        System.out.println("Informe a chave pix da conta:");
        var pix = scanner.next();
        var account = accountRepository.findByPix(pix);
        System.out.println("Informe o ID do investimento:");
        var investmentId = scanner.nextInt();
        try {
            var investmentWallet = investmentRepository.initInvestment(account, investmentId);
            System.out.println(investmentWallet);
            System.out.println("Operação realizada com sucesso!");
        }catch (AccountNotFoundException | NoFundsEnoughException ex) {
            System.out.println(ex.getMessage());

        }
    }
    /* 4- Depositar */
    private static void deposit(){
        System.out.println("Informe a chave pix da conta para deposito: ");
        var pix = scanner.next();
        System.out.println("Informe o valor que será depositado:");
        var amount = scanner.nextLong();
        try {
            accountRepository.deposit(pix, amount);
            System.out.println("Deposito realizado com sucesso.");
        }catch (AccountNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
    /* 5- Sacar */
    private static void withdraw() {
        System.out.println("Informe a chave pix da conta para saque: ");
        var pix = scanner.next();
        System.out.println("Informe o valor que será sacado:");
        var amount = scanner.nextLong();
        try {
            accountRepository.withdraw(pix, amount);
            System.out.println("Operação realizada com sucesso!");
        }catch (NoFundsEnoughException | AccountNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
    /* 6- Transferencia */
    private static void transferToAccount(){
        System.out.println("Informe a chave pix da conta origem:");
        var source = scanner.next();
        System.out.println("Informe a chave pix da conta de destino:");
        var target = scanner.next();
        System.out.println("Informe o valor que será transferido:");
        var amount = scanner.nextLong();
        try{
            accountRepository.transferMoney(source, target, amount);
            System.out.println("Operação realizada com sucesso!");
        } catch (AccountNotFoundException | NoFundsEnoughException ex){
            System.out.println(ex.getMessage());
        }
    }
    /* 7- Investir */
    private static void incInvestment(){
        System.out.println("Informe a chave pix da conta para investimento: ");
        var pix = scanner.nextLine().trim();

        try {
            // Primeiro, tenta encontrar a carteira de investimento para esse Pix
            var wallet = investmentRepository.findWalletByAccountPix(pix);

            System.out.println("Informe o valor que será investido:");
            var amount = Long.parseLong(scanner.nextLine().trim());

            // Faz o depósito na carteira
            investmentRepository.deposit(pix, amount);

            System.out.println("Investimento realizado com sucesso.");

        } catch (jzlz.exception.WalletNotFoundException e) {
            System.out.println("Carteira de investimento não encontrada para o Pix informado.");
            System.out.println("Por favor, crie uma carteira de investimento antes de investir.");
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido. Por favor, informe um número válido.");
        } catch (Exception e) {
            System.out.println("Erro inesperado: " + e.getMessage());
        }
    }

    /* 8- Sacar investimento */
    private static void rescueInvestment() {
        System.out.println("Informe a chave pix da conta para resgate do investimento: ");
        var pix = scanner.next();
        System.out.println("Informe o valor que será sacado:");
        var amount = scanner.nextLong();
        try {
            investmentRepository.withdraw(pix, amount);
        }catch (NoFundsEnoughException | AccountNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
    /* 13- Historico de conta */
    private static void checkHistory() {
        System.out.println("Informe a chave pix da conta para verificar extrato:");
        var pix = scanner.next().trim();

        try {
            var wallet = accountRepository.findByPix(pix);
            var transactions = wallet.getFinancialTransaction();

            if (transactions.isEmpty()) {
                System.out.println("Nenhuma transação encontrada.");
                return;
            }

            System.out.println("Histórico da conta com Pix: " + pix);
            transactions.forEach(t -> {
                System.out.printf(
                        "- [%s] R$ %d | %s%n",
                        t.createdAt(), t.amount(), t.description()
                );
            });

        } catch (AccountNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
