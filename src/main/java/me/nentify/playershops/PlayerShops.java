package me.nentify.playershops;

import com.google.inject.Inject;
import me.nentify.playershops.commands.ShopCommand;
import me.nentify.playershops.data.ImmutablePlayerShopData;
import me.nentify.playershops.data.PlayerShopData;
import me.nentify.playershops.data.PlayerShopDataManipulatorBuilder;
import me.nentify.playershops.events.BlockEventHandler;
import org.slf4j.Logger;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Plugin(id = PlayerShops.PLUGIN_ID, name = PlayerShops.PLUGIN_NAME, version = PlayerShops.PLUGIN_VERSION)
public class PlayerShops {

    public static final String PLUGIN_ID = "playershops";
    public static final String PLUGIN_NAME = "Player Shops";
    public static final String PLUGIN_VERSION = "1.10.2-1.0.0";

    public static PlayerShops instance;

    @Inject
    public Logger logger;

    @Inject
    public GameRegistry gameRegistry;

    public EconomyService economyService;

    // Stores shop data after a player uses the /shop command to be later put on to a sign placed by the player
    public static Map<UUID, PlayerShopData> playerShopData = new HashMap<>();

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        logger.info("Starting " + PLUGIN_NAME + " v" + PLUGIN_VERSION);

        instance = this;

        Sponge.getDataManager().register(PlayerShopData.class, ImmutablePlayerShopData.class, new PlayerShopDataManipulatorBuilder());

        Sponge.getGame().getEventManager().registerListeners(this, new BlockEventHandler());

        // Permissions?
        CommandSpec shopCommand = CommandSpec.builder()
                .description(Text.of("Create a shop"))
                .arguments(
                        GenericArguments.enumValue(Text.of("shopType"), ShopType.class),
                        GenericArguments.doubleNum(Text.of("price")),
                        GenericArguments.optional(GenericArguments.integer(Text.of("quantity")))
                )
                .executor(new ShopCommand())
                .build();

        Sponge.getCommandManager().register(this, shopCommand, "shop");
    }

    @Listener
    public void onChangeServiceProvider(ChangeServiceProviderEvent event) {
        if (event.getService().equals(EconomyService.class)) {
            economyService = (EconomyService) event.getNewProviderRegistration().getProvider();
        }
    }

    public static void addPlayerShopData(UUID uuid, PlayerShopData data) {
        playerShopData.put(uuid, data);
    }

    public static Optional<PlayerShopData> takePlayerShopData(UUID uuid) {
        if (playerShopData.containsKey(uuid)) {
            PlayerShopData data = playerShopData.get(uuid);
            playerShopData.remove(uuid);
            return Optional.of(data);
        }

        return Optional.empty();
    }
}