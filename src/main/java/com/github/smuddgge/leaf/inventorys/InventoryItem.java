package com.github.smuddgge.leaf.inventorys;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishyconfiguration.memory.MemoryConfigurationSection;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.api.item.ItemFlag;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.kyori.adventure.text.Component;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

import java.util.*;

/**
 * Represents a custom inventory item.
 */
public class InventoryItem {

    private final ConfigurationSection section;
    private final String slot;
    private User user;

    private Map<String, String> placeholders = new HashMap<>();

    /**
     * Used to create an inventory item.
     *
     * @param section The configuration section.
     * @param slot    The slot information.
     * @param user    The item will be in context of this user.
     */
    public InventoryItem(ConfigurationSection section, String slot, User user) {
        this.section = section;
        this.slot = slot;
        this.user = user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Get an inventory item with the section combined to this
     * item's configuration section.
     *
     * @param section The instance of the configuration section.
     * @return A new inventory item instance.
     */
    public InventoryItem append(ConfigurationSection section) {
        // Create a clone.
        ConfigurationSection cloneSection = new MemoryConfigurationSection(new HashMap<>());

        // Add this sections values.
        for (String key : this.section.getKeys()) {
            cloneSection.setInSection(key, this.section.get(key));
        }

        // Add the section to append values.
        for (String key : section.getKeys()) {
            cloneSection.setInSection(key, section.get(key));
        }

        // Create the item.
        InventoryItem clone = new InventoryItem(cloneSection, this.slot, this.user);
        clone.placeholders = this.placeholders;
        return clone;
    }

    /**
     * Used to parse item placeholders on a string.
     *
     * @param string The string to parse.
     * @return The requested string.
     */
    private String parsePlaceholders(String string) {
        for (Map.Entry<String, String> entry : this.placeholders.entrySet()) {
            string = string.replace(entry.getKey(), entry.getValue());
        }
        return string;
    }

    /**
     * Used to get the list of slots.
     *
     * @param inventoryType The type of inventory the slots are in.
     * @return The requested slots
     */
    public List<Integer> getSlots(InventoryType inventoryType) {
        List<Integer> slots = new ArrayList<>();

        for (String argument : this.slot.split(",")) {
            // Check if it's an integer
            if (argument.trim().matches("^[0-9]+$")) {
                slots.add(Integer.parseInt(argument.trim()));
            }

            List<Integer> argumentsSlots = SlotManager.parseSlot(argument.trim(), inventoryType);
            for (Integer integer : argumentsSlots) {
                if (slots.contains(integer)) continue;
                slots.add(integer);
            }
        }

        return slots;
    }

    /**
     * Used to get a default item stack.
     *
     * @return The requested item stack.
     */
    public ItemStack getDefaultItemStack() {
        ItemStack item = new ItemStack(ItemType.LIGHT_GRAY_STAINED_GLASS_PANE);
        item.displayName(ChatElement.ofLegacyText("&7"));
        return item;
    }

    /**
     * Used to get this inventory item as an item stack
     * given a base item stack.
     *
     * @return The requested item stack.
     */
    public ItemStack getItemStack(ItemStack item) {
        CompoundTag compoundTag = new CompoundTag();
        Optional<Player> player = Optional.empty();
        if (this.user != null) {
            player = Leaf.getServer().getPlayer(this.user.getUniqueId());
        }

        // Set the material of the item.
        try {
            String material = PlaceholderManager.parse(
                    this.section.getString("material", "LIGHT_GRAY_STAINED_GLASS_PANE"),
                    null, this.user
            ).toUpperCase(Locale.ROOT);
            item.itemType(ItemType.valueOf(material));
        } catch (IllegalArgumentException exception) {
            MessageManager.warn("Invalid material for inventory item. Reference : &f" + this.section.getMap().toString());
        }

        // If the item has a skull value.
        if (this.section.getKeys().contains("skull")) {
            item.itemType(ItemType.PLAYER_HEAD);
            String message = this.section.getString("skull", "Steve");
            compoundTag.putString("SkullOwner", this.parsePlaceholders(PlaceholderManager.parse(message, null, this.user)));
        }

        // If the item has nbt values.
        if (this.section.getKeys().contains("nbt")) {
            for (String key : this.section.getSection("nbt").getKeys()) {
                String message = this.section.getSection("nbt").getString(key);
                compoundTag.putString(key, this.parsePlaceholders(PlaceholderManager.parse(message, null, this.user)));
            }
        }

        // If the item has model data.
        if (this.section.getKeys().contains("custom_model_data")) {
            compoundTag.putInt("CustomModelData", this.section.getInteger("custom_model_data", 0));
        }

        // Set the display name.
        Component component = MessageManager.convertAndParse(this.parsePlaceholders(
                PlaceholderManager.parse(
                        this.section.getString("name", "&7"),
                        null, this.user
                )
        ), player.orElse(null));
        item.displayName(ChatElement.of(component));

        // Set the lore.
        for (String line : this.section.getListString("lore", new ArrayList<>())) {
            Component loreComponent = MessageManager.convertAndParse(
                    this.parsePlaceholders(PlaceholderManager.parse(line, null, this.user)),
                    player.orElse(null)
            );
            item.addToLore(ChatElement.of(loreComponent));
        }

        // Set durability.
        if (this.section.getKeys().contains("durability")) {
            item.durability((short) this.section.getInteger("durability"));
        }

        // Item amount
        item.amount((byte) this.section.getInteger("amount", 1));

        // Items enchants
        if (this.section.getKeys().contains("enchants")) {
            ListTag<CompoundTag> enchants = new ListTag<>(CompoundTag.class);

            for (String key : this.section.getSection("enchants").getKeys()) {
                CompoundTag enchant = new CompoundTag();

                int value = this.section.getSection("enchants").getInteger(key);

                enchant.putString("id", key.toLowerCase(Locale.ROOT));
                enchant.putShort("lvl", (short) value);

                enchants.add(enchant);
            }

            compoundTag.put("Enchantments", enchants);
        }

        // Item enchanted
        if (this.section.getBoolean("enchanted", false)) {
            ListTag<CompoundTag> enchants = new ListTag<>(CompoundTag.class);

            CompoundTag enchant = new CompoundTag();
            enchant.putString("id", "unbreaking");
            enchant.putShort("lvl", (short) 1);
            enchants.add(enchant);

            compoundTag.put("Enchantments", enchants);

            item.itemFlag(ItemFlag.HIDE_ENCHANTMENTS, true);
        }

        // Item flags
        if (this.section.getKeys().contains("flags")) {
            for (String flag : this.section.getListString("flags", new ArrayList<>())) {
                item.itemFlag(ItemFlag.valueOf(flag.toUpperCase(Locale.ROOT)), true);
            }
        }

        // Add the nbt data.
        item.nbtData(compoundTag);
        return item;
    }

    /**
     * Used to get this inventory item as an item stack.
     *
     * @return The requested item stack.
     */
    public ItemStack getItemStack() {
        return this.getItemStack(this.getDefaultItemStack());
    }

    /**
     * Used to check if the inventory item has a command type function.
     *
     * @return True if the item has a command type function.
     */
    public boolean isFunction() {
        return this.section.getKeys().contains("function");
    }

    /**
     * Used to get the function configuration section.
     *
     * @return The requested configuration section.
     */
    public ConfigurationSection getFunctionSection() {
        return this.section.getSection("function");
    }

    /**
     * Used to get the item's configuration section.
     *
     * @return The requested configuration section.
     */
    public ConfigurationSection getSection() {
        return this.section;
    }

    /**
     * Used to add a placeholder that will get parsed in the item.
     *
     * @param key   The key to replace.
     * @param value The value to replace with.
     */
    public void addPlaceholder(String key, String value) {
        this.placeholders.put(key, value);
    }
}
