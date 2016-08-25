package me.nentify.playershops.data;

import me.nentify.playershops.ShopType;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class PlayerShopDataManipulatorBuilder extends AbstractDataBuilder<PlayerShopData> implements DataManipulatorBuilder<PlayerShopData, ImmutablePlayerShopData> {

    public PlayerShopDataManipulatorBuilder() {
        super(PlayerShopData.class, 1);
    }

    @Override
    public PlayerShopData create() {
        return new PlayerShopData();
    }

    @Override
    public Optional<PlayerShopData> createFrom(DataHolder dataHolder) {
        return Optional.of(dataHolder.get(PlayerShopData.class).orElse(new PlayerShopData()));
    }

    @Override
    protected Optional<PlayerShopData> buildContent(DataView container) throws InvalidDataException {
        if (container.contains(PlayerShopKeys.SHOP_TYPE, PlayerShopKeys.ITEM, PlayerShopKeys.PRICE, PlayerShopKeys.QUANTITY)) {
            ShopType shopType = ShopType.valueOf(container.getString(PlayerShopKeys.SHOP_TYPE.getQuery()).get());
            ItemStack item = container.getSerializable(PlayerShopKeys.ITEM.getQuery(), ItemStack.class).get();
            double price = container.getDouble(PlayerShopKeys.PRICE.getQuery()).get();
            int quantity = container.getInt(PlayerShopKeys.QUANTITY.getQuery()).get();
            UUID ownerUuid = UUID.fromString(container.getString(PlayerShopKeys.OWNER_UUID.getQuery()).get());

            return Optional.of(new PlayerShopData(shopType, item, price, quantity, ownerUuid));
        }

        return Optional.empty();
    }
}
