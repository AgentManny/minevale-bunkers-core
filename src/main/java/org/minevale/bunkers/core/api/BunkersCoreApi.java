package org.minevale.bunkers.core.api;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.api.exception.InsufficientFundsException;
import org.minevale.bunkers.core.api.exception.InventoryFullException;
import org.minevale.bunkers.core.player.PlayerData;
import org.minevale.bunkers.core.player.balance.Balance;
import org.minevale.bunkers.core.util.InventoryUtils;

@RequiredArgsConstructor
public class BunkersCoreApi implements BunkersApi {

    private final BunkersCore bunkersCore;

    @Override
    public PlayerData getPlayerData(String playerName) {
        return bunkersCore.getPlayerDataManager().getPlayerData(playerName);
    }

    @Override
    public PlayerData getPlayerData(Player player) {
        return bunkersCore.getPlayerDataManager().getPlayerData(player);
    }

    @Override
    public int getBalance(PlayerData player) {
        return player.getBalance();
    }

    @Override
    public int clearBalance(PlayerData player) {
        int balanceBefore = player.getBalance();

        for (Balance balance : Balance.values()) {
            while (player.getInventoryData().contains(balance.getType())) {
                player.getInventoryData().remove(balance.getType());
            }
        }

        Player bukkitPlayer = player.getPlayer();
        if (bukkitPlayer != null) {
            bukkitPlayer.updateInventory();
        }
        return balanceBefore;
    }

    @Override
    public int clearBalance(PlayerData player, Balance balance) {
        int balanceBefore = player.getBalance(balance);

        while (player.getInventoryData().contains(balance.getType())) {
            player.getInventoryData().remove(balance.getType());
        }

        Player bukkitPlayer = player.getPlayer();
        if (bukkitPlayer != null) {
            bukkitPlayer.updateInventory();
        }
        return balanceBefore;
    }

    @Override
    public int addCurrency(PlayerData player, Balance balance, int amount) throws InventoryFullException {
        ItemStack item = balance.getItem().clone();
        item.setAmount(amount);
        if (InventoryUtils.fits(item, player.getInventoryData())) {
            player.getInventoryData().addItem(item);
            player.getInventoryData().update(player);
            return amount;
        }
        throw new InventoryFullException(player, item);
    }

    @Override
    public int removeCurrency(PlayerData player, Balance balance, int amount) throws InsufficientFundsException {
        int contains = player.getBalance(balance);
        if (contains < amount) {
            throw new InsufficientFundsException(player, balance, amount);
        }

        player.getInventoryData().removeAmount(balance.getType(), amount);
        return amount;
    }

    @Override
    public double getCoins(PlayerData player) {
        return player.getBalance(Balance.COINS);
    }

    @Override
    public double addCoins(PlayerData player, int amount) {
        player.getInventoryData().addItem(new ItemStack(Balance.COINS.getType(), amount));
        return player.getBalance(Balance.COINS);
    }

    @Override
    public double removeCoins(PlayerData player, int amount) {
        player.getInventoryData().removeAmount(Balance.COINS.getType(), amount);
        return player.getBalance(Balance.COINS);
    }

    @Override
    public double addNuggets(PlayerData player, int amount) {
        player.getInventoryData().addItem(new ItemStack(Balance.NUGGETS.getType(), amount));
        return player.getBalance(Balance.NUGGETS);
    }

    @Override
    public double removeNuggets(PlayerData player, int amount) {
        player.getInventoryData().removeAmount(Balance.NUGGETS.getType(), amount);
        return player.getBalance(Balance.NUGGETS);
    }

    @Override
    public double addBars(PlayerData player, int amount) {
        player.getInventoryData().addItem(new ItemStack(Balance.BARS.getType(), amount));
        return player.getBalance(Balance.BARS);
    }

    @Override
    public double removeBars(PlayerData player, int amount) {
        player.getInventoryData().removeAmount(Balance.BARS.getType(), amount);
        return player.getBalance(Balance.BARS);
    }
}
