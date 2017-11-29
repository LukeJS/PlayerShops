package me.nentify.playershops.utils;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

import java.math.BigDecimal;

public class EconomyUtils {

    // this could do with a better solution in future
    public static ResultType transferWithTax(UniqueAccount from, UniqueAccount to, Currency currency, BigDecimal amount, BigDecimal tax, Cause cause) {
        TransactionResult transfer = from.transfer(to, currency, amount, cause);

        // should succeed. if it doesn't not a big deal
        if (transfer.getResult() == ResultType.SUCCESS) {
            // take tax from recipient
            to.withdraw(currency, amount.multiply(tax), cause);
        }

        return transfer.getResult();
    }
}
