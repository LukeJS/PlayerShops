package me.nentify.playershops.data;

import me.nentify.playershops.ShopType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.UUID;

public class ImmutablePlayerShopData extends AbstractImmutableData<ImmutablePlayerShopData, PlayerShopData> {

    private ShopType shopType;
    private ItemStack item;
    private double price;
    private int quantity;
    private UUID ownerUuid;

    public ImmutablePlayerShopData() {
        this(null, null, 0.0, 0, null);
    }

    public ImmutablePlayerShopData(ShopType shopType, ItemStack item, double price, int quantity, UUID ownerUuid) {
        this.shopType = shopType;
        this.item = item;
        this.price = price;
        this.quantity = quantity;
        this.ownerUuid = ownerUuid;
    }

    public ImmutableValue<String> shopType() {
        return Sponge.getRegistry().getValueFactory().createValue(PlayerShopKeys.SHOP_TYPE, shopType.toString()).asImmutable();
    }

    public ImmutableValue<ItemStack> item() {
        return Sponge.getRegistry().getValueFactory().createValue(PlayerShopKeys.ITEM, item).asImmutable();
    }

    public ImmutableValue<Double> price() {
        return Sponge.getRegistry().getValueFactory().createValue(PlayerShopKeys.PRICE, price).asImmutable();
    }

    public ImmutableValue<Integer> quantity() {
        return Sponge.getRegistry().getValueFactory().createValue(PlayerShopKeys.QUANTITY, quantity).asImmutable();
    }

    public ImmutableValue<String> ownerUuid() {
        return Sponge.getRegistry().getValueFactory().createValue(PlayerShopKeys.OWNER_UUID, ownerUuid.toString()).asImmutable();
    }

    @Override
    protected void registerGetters() {
        registerFieldGetter(PlayerShopKeys.SHOP_TYPE, () -> shopType);
        registerKeyValue(PlayerShopKeys.SHOP_TYPE, this::shopType);

        registerFieldGetter(PlayerShopKeys.ITEM, () -> item);
        registerKeyValue(PlayerShopKeys.ITEM, this::item);

        registerFieldGetter(PlayerShopKeys.PRICE, () -> price);
        registerKeyValue(PlayerShopKeys.PRICE, this::price);

        registerFieldGetter(PlayerShopKeys.QUANTITY, () -> quantity);
        registerKeyValue(PlayerShopKeys.QUANTITY, this::quantity);

        registerFieldGetter(PlayerShopKeys.OWNER_UUID, () -> ownerUuid);
        registerKeyValue(PlayerShopKeys.OWNER_UUID, this::ownerUuid);
    }

    @Override
    public PlayerShopData asMutable() {
        return new PlayerShopData(shopType, item, price, quantity, ownerUuid);
    }

    @Override
    public int compareTo(ImmutablePlayerShopData o) {
        return 0;
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer()
                .set(PlayerShopKeys.SHOP_TYPE, shopType.toString())
                .set(PlayerShopKeys.ITEM, item)
                .set(PlayerShopKeys.PRICE, price)
                .set(PlayerShopKeys.QUANTITY, quantity)
                .set(PlayerShopKeys.OWNER_UUID, ownerUuid.toString());
    }
}
