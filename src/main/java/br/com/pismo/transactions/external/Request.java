package br.com.pismo.transactions.external;

public class Request {

    private CreditLimit availableCreditLimit;
    private WithdrawalLimit availableWithdrawalLimit;

    public CreditLimit getAvailableCreditLimit() {
        return availableCreditLimit;
    }

    public void setAvailableCreditLimit(CreditLimit availableCreditLimit) {
        this.availableCreditLimit = availableCreditLimit;
    }

    public WithdrawalLimit getAvailableWithdrawalLimit() {
        return availableWithdrawalLimit;
    }

    public void setAvailableWithdrawalLimit(WithdrawalLimit availableWithdrawalLimit) {
        this.availableWithdrawalLimit = availableWithdrawalLimit;
    }
}
