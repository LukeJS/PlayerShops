package me.nentify.playershops.data;

import com.google.common.base.Objects;
import me.nentify.playershops.ShopType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class PlayerShopData extends AbstractData<PlayerShopData, ImmutablePlayerShopData> {

    private ShopType shopType;
    private ItemStack item;
    private double price;
    private int quantity;
    private UUID ownerUuid;

    public PlayerShopData() {
        this(null, null, 0.0, 0, null);
    }

    public PlayerShopData(ShopType shopType, ItemStack item, double price, int quantity, UUID ownerUuid) {
        this.shopType = shopType;
        this.item = item;
        this.price = price;
        this.quantity = quantity;
        this.ownerUuid = ownerUuid;
    }

    public Value<String> shopType() {
        return Sponge.getRegistry().getValueFactory().createValue(PlayerShopKeys.SHOP_TYPE, shopType.toString());
    }

    public Value<ItemStack> item() {
        return Sponge.getRegistry().getValueFactory().createValue(PlayerShopKeys.ITEM, item);
    }

    public Value<Double> price() {
        return Sponge.getRegistry().getValueFactory().createValue(PlayerShopKeys.PRICE, price);
    }

    public Value<Integer> quantity() {
        return Sponge.getRegistry().getValueFactory().createValue(PlayerShopKeys.QUANTITY, quantity);
    }

    public Value<String> ownerUuid() {
        return Sponge.getRegistry().getValueFactory().createValue(PlayerShopKeys.OWNER_UUID, ownerUuid.toString());
    }

    @Override
    protected void registerGettersAndSetters() {
        registerFieldGetter(PlayerShopKeys.SHOP_TYPE, () -> shopType);
        registerFieldSetter(PlayerShopKeys.SHOP_TYPE, value -> shopType = ShopType.valueOf(value));
        registerKeyValue(PlayerShopKeys.SHOP_TYPE, this::shopType);

        registerFieldGetter(PlayerShopKeys.ITEM, () -> item);
        registerFieldSetter(PlayerShopKeys.ITEM, value -> item = value);
        registerKeyValue(PlayerShopKeys.ITEM, this::item);

        registerFieldGetter(PlayerShopKeys.PRICE, () -> price);
        registerFieldSetter(PlayerShopKeys.PRICE, value -> price = value);
        registerKeyValue(PlayerShopKeys.PRICE, this::price);

        registerFieldGetter(PlayerShopKeys.QUANTITY, () -> quantity);
        registerFieldSetter(PlayerShopKeys.QUANTITY, value -> quantity = value);
        registerKeyValue(PlayerShopKeys.QUANTITY, this::quantity);

        registerFieldGetter(PlayerShopKeys.OWNER_UUID, () -> ownerUuid);
        registerFieldSetter(PlayerShopKeys.OWNER_UUID, value -> ownerUuid = UUID.fromString(value));
        registerKeyValue(PlayerShopKeys.OWNER_UUID, this::ownerUuid);
    }

    @Override
    public Optional<PlayerShopData> fill(DataHolder dataHolder, MergeFunction overlap) {
        return Optional.empty();
    }

    @Override
    public Optional<PlayerShopData> from(DataContainer container) {
        if (!container.contains(
                PlayerShopKeys.ITEM.getQuery(),
                PlayerShopKeys.PRICE.getQuery(),
                PlayerShopKeys.QUANTITY.getQuery(),
                PlayerShopKeys.SHOP_TYPE.getQuery(),
                PlayerShopKeys.OWNER_UUID.getQuery()
        ))
            return Optional.empty();

        this.shopType = ShopType.valueOf(container.getString(PlayerShopKeys.SHOP_TYPE.getQuery()).get());
        this.item = container.getSerializable(PlayerShopKeys.ITEM.getQuery(), ItemStack.class).get();
        this.price = container.getDouble(PlayerShopKeys.PRICE.getQuery()).get();
        this.quantity = container.getInt(PlayerShopKeys.QUANTITY.getQuery()).get();
        this.ownerUuid = UUID.fromString(container.getString(PlayerShopKeys.OWNER_UUID.getQuery()).get());

        return Optional.of(this);
    }

    @Override
    public PlayerShopData copy() {
        return new PlayerShopData(shopType, item, price, quantity, ownerUuid);
    }

    @Override
    public ImmutablePlayerShopData asImmutable() {
        return new ImmutablePlayerShopData(shopType, item, price, quantity, ownerUuid);
    }

    @Override
    public int compareTo(PlayerShopData o) {
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

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("shopType", shopType)
                .add("item", item)
                .add("price", price)
                .add("quantity", quantity)
                .add("ownerUuid", ownerUuid)
                .toString();
    }

    public ShopType getShopType() {
        return shopType;
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }
}
