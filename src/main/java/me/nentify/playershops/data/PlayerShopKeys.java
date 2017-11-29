package me.nentify.playershops.data;

import com.google.common.reflect.TypeToken;
import me.nentify.playershops.PlayerShops;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.UUID;

public class PlayerShopKeys {

    public static final Key<Value<String>> SHOP_TYPE = KeyFactory.makeSingleKey(TypeToken.of(String.class), new TypeToken<Value<String>>() {}, DataQuery.of("ShopType"), PlayerShops.PLUGIN_ID + ":shop_type", "shop_type");
    public static final Key<Value<ItemStack>> ITEM = KeyFactory.makeSingleKey(TypeToken.of(ItemStack.class), new TypeToken<Value<ItemStack>>() {}, DataQuery.of("ItemStack"), PlayerShops.PLUGIN_ID + ":item_stack", "item_stack");
    public static final Key<Value<Double>> PRICE = KeyFactory.makeSingleKey(TypeToken.of(Double.class), new TypeToken<Value<Double>>() {}, DataQuery.of("Price"), PlayerShops.PLUGIN_ID + ":price", "price");
    public static final Key<Value<Integer>> QUANTITY = KeyFactory.makeSingleKey(TypeToken.of(Integer.class), new TypeToken<Value<Integer>>() {}, DataQuery.of("Quantity"), PlayerShops.PLUGIN_ID + ":quantity", "quantity");
    public static final Key<Value<String>> OWNER_UUID = KeyFactory.makeSingleKey(TypeToken.of(String.class), new TypeToken<Value<String>>() {}, DataQuery.of("OwnerUuid"), PlayerShops.PLUGIN_ID + ":owner_uuid", "owner_uuid");
}
