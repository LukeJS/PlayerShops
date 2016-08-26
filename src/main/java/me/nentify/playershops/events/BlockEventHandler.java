package me.nentify.playershops.events;

import me.nentify.playershops.PlayerShops;
import me.nentify.playershops.ShopType;
import me.nentify.playershops.data.PlayerShopData;
import me.nentify.playershops.utils.ChestUtils;
import me.nentify.playershops.utils.EconomyUtils;
import net.minecraft.inventory.IInventory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class BlockEventHandler {

    @Listener
    public void onBlockPlace(ChangeBlockEvent.Place event, @Root Player player) {
        for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            BlockSnapshot blockSnapshot = transaction.getFinal();
            Optional<Location<World>> blockLocation = blockSnapshot.getLocation();

            if (blockLocation.isPresent()) {
                Optional<TileEntity> tileEntity = blockLocation.get().getTileEntity();

                if (tileEntity.isPresent() && tileEntity.get() instanceof Sign) {
                    Sign sign = (Sign) tileEntity.get();

                    Optional<PlayerShopData> playerShopDataOptional = PlayerShops.takePlayerShopData(player.getUniqueId());

                    if (playerShopDataOptional.isPresent()) {
                        if (!PlayerShops.instance.configuration.limitToWorlds.contains(event.getTargetWorld().getName())) {
                            player.sendMessage(Text.of(TextColors.RED, "You can not create a shop in this world"));
                            return;
                        }

                        PlayerShopData playerShopData = playerShopDataOptional.get();

                        ShopType shopType = playerShopData.getShopType();
                        int quantity = playerShopData.quantity().get();
                        ItemStack itemStack = playerShopData.item().get();
                        BigDecimal price = BigDecimal.valueOf(playerShopData.price().get());

                        SignData signData = sign.getOrCreate(SignData.class).get();

                        Currency defaultCurrency = PlayerShops.instance.economyService.getDefaultCurrency();

                        ListValue<Text> lines = signData.lines();
                        lines.set(0, Text.of(TextColors.DARK_BLUE, "[", shopType.getName(), "]"));
                        lines.set(1, Text.of(quantity));
                        lines.set(2, Text.of(itemStack));
                        lines.set(3, Text.of(defaultCurrency.getSymbol(), price));

                        double creationCost = PlayerShops.instance.configuration.creationCost;

                        if (creationCost > 0.0) {
                            Optional<UniqueAccount> account = PlayerShops.instance.economyService.getOrCreateAccount(player.getUniqueId());

                            if (account.isPresent()) {
                                TransactionResult result = account.get().withdraw(
                                        defaultCurrency,
                                        BigDecimal.valueOf(creationCost),
                                        Cause.source(PlayerShops.instance).build()
                                );

                                if (result.getResult() == ResultType.ACCOUNT_NO_FUNDS) {
                                    player.sendMessage(Text.of(TextColors.RED, "You need ", defaultCurrency.format(BigDecimal.valueOf(creationCost)) ," to create a shop"));
                                    return;
                                } else if (result.getResult() != ResultType.SUCCESS) {
                                    player.sendMessage(Text.of(TextColors.RED, "Error during transaction"));
                                    return;
                                }
                            }
                        }

                        sign.offer(playerShopData);
                        sign.offer(lines);

                        player.sendMessage(Text.of(TextColors.GREEN, "Successfully created ", shopType.toString().toLowerCase(), " shop for ", quantity, " ", itemStack, " for ", defaultCurrency.format(price)));
                    }
                }
            }
        }
    }

    @Listener
    public void onSignChange(ChangeSignEvent event) {
        Optional<PlayerShopData> playerShopDataOptional = event.getTargetTile().get(PlayerShopData.class);

        if (playerShopDataOptional.isPresent())
            event.setCancelled(true);
    }

    @Listener
    public void onBlockInteract(InteractBlockEvent.Secondary.MainHand event, @Root Player player) {
        Optional<Location<World>> blockLoc = event.getTargetBlock().getLocation();

        if (blockLoc.isPresent()) {
            Optional<TileEntity> tileEntity = blockLoc.get().getTileEntity();

            if (tileEntity.isPresent() && tileEntity.get() instanceof Sign) {
                Sign sign = (Sign) tileEntity.get();

                Optional<PlayerShopData> playerShopDataOptional = sign.get(PlayerShopData.class);

                if (playerShopDataOptional.isPresent()) {
                    PlayerShopData playerShopData = playerShopDataOptional.get();

                    ShopType shopType = playerShopData.getShopType();
                    ItemStack item = playerShopData.item().get().copy();
                    BigDecimal price = BigDecimal.valueOf(playerShopData.price().get());
                    int quantity = playerShopData.quantity().get();
                    UUID ownerUuid = playerShopData.getOwnerUuid();

                    item.setQuantity(quantity);

                    Optional<UniqueAccount> account = PlayerShops.instance.economyService.getOrCreateAccount(player.getUniqueId());
                    Optional<UniqueAccount> ownerAccount = PlayerShops.instance.economyService.getOrCreateAccount(ownerUuid);

                    if (account.isPresent() && ownerAccount.isPresent()) {
                        Currency defaultCurrency = PlayerShops.instance.economyService.getDefaultCurrency();

                        UserStorageService userStorage = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
                        Optional<User> ownerOptional = userStorage.get(ownerUuid);

                        if (ownerOptional.isPresent()) {
                            User owner = ownerOptional.get();

                            if (player.get(Keys.IS_SNEAKING).orElse(false)) {
                                player.sendMessage(Text.of(TextColors.AQUA, "This shop is owned by ", owner.getName()));
                            } else {
                                Location<World> blockDown = blockLoc.get().getBlockRelative(Direction.DOWN);
                                Optional<TileEntity> te = blockDown.getTileEntity();

                                if (te.isPresent() && te.get() instanceof IInventory) {
                                    IInventory inv = (IInventory) te.get();

                                    if (shopType == ShopType.BUY) {
                                        if (ChestUtils.hasQuantityOfItems(inv, item, quantity)) {
                                            if ((player.getInventory().size() < player.getInventory().capacity())) {
                                                ResultType result = EconomyUtils.transferWithTax(
                                                        account.get(),
                                                        ownerAccount.get(),
                                                        defaultCurrency,
                                                        price,
                                                        BigDecimal.valueOf(PlayerShops.instance.configuration.tax),
                                                        Cause.source(PlayerShops.instance).build()
                                                );

                                                if (result == ResultType.SUCCESS) {
                                                    ChestUtils.removeItems(inv, item);
                                                    player.getInventory().offer(item);
                                                    player.sendMessage(Text.of(TextColors.GREEN, "Bought ", quantity, " ", item, " for ", defaultCurrency.format(price), " from ", owner.getName()));
                                                } else if (result == ResultType.ACCOUNT_NO_FUNDS) {
                                                    player.sendMessage(Text.of(TextColors.RED, "You don't have enough money to buy this"));
                                                } else if (result == ResultType.ACCOUNT_NO_SPACE) {
                                                    player.sendMessage(Text.of(TextColors.RED, owner.getName(), "'s account is full"));
                                                } else {
                                                    player.sendMessage(Text.of(TextColors.RED, "Error during transaction"));
                                                }
                                            } else {
                                                player.sendMessage(Text.of(TextColors.RED, "You do not have enough inventory space to buy from ", owner.getName(), "'s shop"));
                                            }
                                        } else {
                                            player.sendMessage(Text.of(TextColors.RED, owner.getName(), " has run out of stock"));
                                        }
                                    } else {
                                        Inventory inventoryStacks = player.getInventory().query(item);
                                        Optional<ItemStack> peek = inventoryStacks.peek(quantity);

                                        if (peek.isPresent() && peek.get().getQuantity() >= quantity) {
                                            Optional<Integer> slotWithSpace = ChestUtils.getSlotWithSpaceForItem(inv, item, quantity);

                                            if (slotWithSpace.isPresent()) {
                                                BigDecimal tax = BigDecimal.valueOf(PlayerShops.instance.configuration.tax);

                                                ResultType result = EconomyUtils.transferWithTax(
                                                        account.get(),
                                                        ownerAccount.get(),
                                                        defaultCurrency,
                                                        price,
                                                        tax,
                                                        Cause.source(PlayerShops.instance).build()
                                                );

                                                if (result == ResultType.SUCCESS) {
                                                    inventoryStacks.poll(quantity);
                                                    ChestUtils.addItemsToSlot(inv, slotWithSpace.get(), item);
                                                    player.sendMessage(Text.of(TextColors.GREEN, "Sold ", quantity, " ", item, " for ", defaultCurrency.format(price.multiply(BigDecimal.ONE.subtract(tax))), " to ", owner.getName()));
                                                } else if (result == ResultType.ACCOUNT_NO_FUNDS) {
                                                    player.sendMessage(Text.of(TextColors.RED, "The shop owner has run out of money"));
                                                } else if (result == ResultType.ACCOUNT_NO_SPACE) {
                                                    player.sendMessage(Text.of(TextColors.RED, "Your account is full!"));
                                                } else {
                                                    player.sendMessage(Text.of(TextColors.RED, "Error during transaction"));
                                                }
                                            } else {
                                                player.sendMessage(Text.of(TextColors.RED, owner.getName(), "'s shop has run out of space"));
                                            }
                                        } else {
                                            player.sendMessage(Text.of(TextColors.RED, "You do not have ", quantity, " ", item, " to sell"));
                                        }
                                    }
                                } else {
                                    player.sendMessage(Text.of(TextColors.RED, "No chest was found under the sign for ", owner.getName(), "'s shop"));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
