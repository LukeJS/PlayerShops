package me.nentify.playershops.utils;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

import java.math.BigDecimal;

public class EconomyUtils {

    public static ResultType transferWithTax(UniqueAccount from, UniqueAccount to, Currency currency, BigDecimal amount, BigDecimal tax, Cause cause) {
        TransactionResult withdraw = from.withdraw(currency, amount, cause);
        TransactionResult deposit = to.deposit(currency, amount.multiply(BigDecimal.ONE.subtract(tax)), cause);

        ResultType result = ResultType.SUCCESS;

        if (withdraw.getResult() != ResultType.SUCCESS) {
            from.deposit(currency, amount, cause);
            result = withdraw.getResult();
        }

        if (deposit.getResult() != ResultType.SUCCESS) {
            to.withdraw(currency, amount.multiply(tax), cause);
            result = deposit.getResult();
        }

        return result;
    }
}
