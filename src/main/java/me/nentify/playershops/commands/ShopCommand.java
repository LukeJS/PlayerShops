package me.nentify.playershops.commands;

import me.nentify.playershops.PlayerShops;
import me.nentify.playershops.ShopType;
import me.nentify.playershops.data.PlayerShopData;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.Optional;

public class ShopCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        ShopType shopType = args.<ShopType>getOne("shopType").get();
        double price = args.<Double>getOne("price").get();
        Optional<Integer> quantityOptional = args.getOne("quantity");

        if (src instanceof Player) {
            Player player = (Player) src;

            Optional<ItemStack> itemStackOptional = player.getItemInHand(HandTypes.MAIN_HAND);

            if (itemStackOptional.isPresent()) {
                ItemStack itemStack = itemStackOptional.get();

                int quantity;

                if (quantityOptional.isPresent())
                    quantity = quantityOptional.get();
                else
                    quantity = itemStack.getQuantity();

                PlayerShopData serverShopData = new PlayerShopData(shopType, itemStack, price, quantity, player.getUniqueId());
                PlayerShops.addPlayerShopData(player.getUniqueId(), serverShopData);

                Currency defaultCurrency = PlayerShops.instance.economyService.getDefaultCurrency();
                player.sendMessage(Text.of(TextColors.GREEN, "Creating ", shopType.toString().toLowerCase(), " shop for ", quantity, " ", itemStack, " for ", defaultCurrency.format(BigDecimal.valueOf(price))));
                player.sendMessage(Text.of(TextColors.YELLOW, "Place a sign to finish creating your ", shopType.toString().toLowerCase(), " shop"));
            } else {
                player.sendMessage(Text.of(TextColors.RED, "You must be holding the item you wish to buy or sell"));
            }
        } else {
            src.sendMessage(Text.of(TextColors.RED, "You must be a player to use this command"));
        }

        return CommandResult.success();
    }
}