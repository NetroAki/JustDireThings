package com.direwolf20.justdirethings.common.items.data;

import com.direwolf20.justdirethings.setup.Registration;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Small utility for reading/writing attachment-backed item data using plain NBT.
 * <p>
 * Every stack receives an attachment (registered in {@link Registration}) that holds a {@link CompoundTag}
 * dedicated to JustDireThings. All helper methods work inside that root tag so callers only have to supply keys.
 */
public final class ItemDataHelper {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String ROOT_TAG = "justdirethings";

    private ItemDataHelper() {
    }

    private static CompoundTag getAttachment(ItemStack stack) {
        return stack.getData(Registration.ITEM_DATA.get());
    }

    private static CompoundTag getOrCreateRoot(ItemStack stack) {
        CompoundTag attachment = getAttachment(stack);
        if (!attachment.contains(ROOT_TAG, Tag.TAG_COMPOUND)) {
            attachment.put(ROOT_TAG, new CompoundTag());
        }
        return attachment.getCompound(ROOT_TAG);
    }

    private static Optional<CompoundTag> getRootOptional(ItemStack stack) {
        CompoundTag attachment = getAttachment(stack);
        if (attachment.contains(ROOT_TAG, Tag.TAG_COMPOUND)) {
            return Optional.of(attachment.getCompound(ROOT_TAG));
        }
        return Optional.empty();
    }

    /* Basic scalar helpers */

    public static void setInt(ItemStack stack, String key, int value) {
        getOrCreateRoot(stack).putInt(key, value);
    }

    public static int getInt(ItemStack stack, String key, int fallback) {
        return getRootOptional(stack).map(root -> root.contains(key, Tag.TAG_INT) ? root.getInt(key) : fallback).orElse(fallback);
    }

    public static void setDouble(ItemStack stack, String key, double value) {
        getOrCreateRoot(stack).putDouble(key, value);
    }

    public static double getDouble(ItemStack stack, String key, double fallback) {
        return getRootOptional(stack).map(root -> root.contains(key, DoubleTag.TAG_DOUBLE) ? root.getDouble(key) : fallback).orElse(fallback);
    }

    public static void setBoolean(ItemStack stack, String key, boolean value) {
        getOrCreateRoot(stack).putBoolean(key, value);
    }

    public static boolean getBoolean(ItemStack stack, String key, boolean fallback) {
        return getRootOptional(stack).map(root -> root.contains(key, Tag.TAG_BYTE) ? root.getBoolean(key) : fallback).orElse(fallback);
    }

    public static boolean toggleBoolean(ItemStack stack, String key, boolean fallback) {
        boolean current = getBoolean(stack, key, fallback);
        boolean updated = !current;
        setBoolean(stack, key, updated);
        return updated;
    }

    public static void setString(ItemStack stack, String key, String value) {
        getOrCreateRoot(stack).putString(key, value);
    }

    public static String getString(ItemStack stack, String key, String fallback) {
        return getRootOptional(stack).map(root -> root.contains(key, Tag.TAG_STRING) ? root.getString(key) : fallback).orElse(fallback);
    }

    public static void setUuid(ItemStack stack, String key, UUID uuid) {
        getOrCreateRoot(stack).putUUID(key, uuid);
    }

    public static UUID getUuid(ItemStack stack, String key, @Nullable UUID fallback) {
        return getRootOptional(stack).map(root -> root.contains(key, Tag.TAG_INT_ARRAY) ? root.getUUID(key) : fallback).orElse(fallback);
    }

    /* Structured helpers */

    public static void setBlockPos(ItemStack stack, String key, BlockPos pos) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());
        getOrCreateRoot(stack).put(key, tag);
    }

    public static BlockPos getBlockPos(ItemStack stack, String key, BlockPos fallback) {
        return getRootOptional(stack)
                .filter(root -> root.contains(key, Tag.TAG_COMPOUND))
                .map(root -> root.getCompound(key))
                .map(tag -> new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z")))
                .orElse(fallback);
    }

    public static void setGlobalPos(ItemStack stack, String key, GlobalPos pos) {
        CompoundTag tag = new CompoundTag();
        tag.putString("dimension", pos.dimension().location().toString());
        BlockPos blockPos = pos.pos();
        tag.putInt("x", blockPos.getX());
        tag.putInt("y", blockPos.getY());
        tag.putInt("z", blockPos.getZ());
        getOrCreateRoot(stack).put(key, tag);
    }

    @Nullable
    public static GlobalPos getGlobalPos(ItemStack stack, String key) {
        Optional<CompoundTag> optional = getRootOptional(stack);
        if (optional.isEmpty()) {
            return null;
        }
        CompoundTag root = optional.get();
        if (!root.contains(key, Tag.TAG_COMPOUND)) {
            return null;
        }
        CompoundTag tag = root.getCompound(key);
        if (!tag.contains("dimension", Tag.TAG_STRING)) {
            return null;
        }
        ResourceLocation id = ResourceLocation.tryParse(tag.getString("dimension"));
        if (id == null) {
            LOGGER.warn("Invalid dimension id stored for '{}'", key);
            return null;
        }
        ResourceKey<Level> levelKey = ResourceKey.create(Registries.DIMENSION, id);
        BlockPos pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
        return GlobalPos.of(levelKey, pos);
    }

    public static void setVec3(ItemStack stack, String key, Vec3 vec3) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("x", vec3.x());
        tag.putDouble("y", vec3.y());
        tag.putDouble("z", vec3.z());
        getOrCreateRoot(stack).put(key, tag);
    }

    @Nullable
    public static Vec3 getVec3(ItemStack stack, String key) {
        return getRootOptional(stack)
                .filter(root -> root.contains(key, Tag.TAG_COMPOUND))
                .map(root -> root.getCompound(key))
                .map(tag -> new Vec3(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z")))
                .orElse(null);
    }

    public static void setCompound(ItemStack stack, String key, CompoundTag value) {
        getOrCreateRoot(stack).put(key, value);
    }

    public static CompoundTag getCompound(ItemStack stack, String key) {
        return getRootOptional(stack)
                .filter(root -> root.contains(key, Tag.TAG_COMPOUND))
                .map(root -> root.getCompound(key))
                .map(CompoundTag::copy)
                .orElseGet(CompoundTag::new);
    }

    public static void setStringList(ItemStack stack, String key, List<String> entries) {
        ListTag listTag = new ListTag();
        for (String entry : entries) {
            listTag.add(StringTag.valueOf(entry));
        }
        getOrCreateRoot(stack).put(key, listTag);
    }

    public static List<String> getStringList(ItemStack stack, String key) {
        return getRootOptional(stack)
                .filter(root -> root.contains(key, Tag.TAG_LIST))
                .map(root -> root.getList(key, Tag.TAG_STRING))
                .map(list -> {
                    List<String> strings = new ArrayList<>(list.size());
                    for (int i = 0; i < list.size(); i++) {
                        strings.add(list.getString(i));
                    }
                    return strings;
                })
                .orElseGet(ArrayList::new);
    }

    public static void setList(ItemStack stack, String key, ListTag listTag) {
        getOrCreateRoot(stack).put(key, listTag);
    }

    public static ListTag getList(ItemStack stack, String key, int elementType) {
        return getRootOptional(stack)
                .filter(root -> root.contains(key, Tag.TAG_LIST))
                .map(root -> root.getList(key, elementType))
                .map(ListTag::copy)
                .orElseGet(ListTag::new);
    }

    public static void remove(ItemStack stack, String key) {
        getRootOptional(stack).ifPresent(root -> root.remove(key));
    }

    public static boolean has(ItemStack stack, String key) {
        return getRootOptional(stack).map(root -> root.contains(key)).orElse(false);
    }
}
