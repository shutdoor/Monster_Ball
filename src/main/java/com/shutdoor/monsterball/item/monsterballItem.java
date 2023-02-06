package com.shutdoor.monsterball.item;

import com.shutdoor.monsterball.monsterBallMod;
import com.shutdoor.monsterball.entity.monsterballEntity;
import com.shutdoor.monsterball.util.NBTHelper;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class monsterballItem extends Item {
    public monsterballItem(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public boolean hasGlint(ItemStack stack) { return ((stack.hasNbt()) ? true: false);}

    @Override
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
        ItemStack itemStackIn = playerIn.getStackInHand(hand);
        if (!worldIn.isClient) {
            monsterballEntity monsterball = new monsterballEntity(playerIn, worldIn, itemStackIn.copy());
            monsterball.setVelocity(playerIn.getRotationVector().x, playerIn.getRotationVector().y, playerIn.getRotationVector().z, 2F, 1.5F);
            worldIn.spawnEntity(monsterball);
        }

        worldIn.playSound(playerIn, playerIn.getBlockPos(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS, 0.3F, 0.3F / (worldIn.getRandom().nextFloat() * 0.4F + 0.8F));
        playerIn.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!playerIn.isCreative() || NBTHelper.hasNbt(itemStackIn, "StoredEntity")) {
            return TypedActionResult.success(ItemStack.EMPTY);
        }

        return TypedActionResult.success(itemStackIn);
    }

    @Override
    public void appendTooltip(ItemStack stack, World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        if (NBTHelper.hasNbt(stack, "StoredEntity")) {
            NbtCompound stored = NBTHelper.getNbt(stack, "StoredEntity");

            String entityName = stored.getString("id");
            if (stored.contains("monsterball_name")) {
                entityName = stored.getString("monsterball_name");
            }
            if (stored.contains("CustomName")) {
                String s = stored.getString("CustomName");

                try {
                    MutableText customName = Text.Serializer.fromJson(stored.getString("CustomName"));
                    customName.formatted(Formatting.BLUE, Formatting.ITALIC);
                    tooltip.add(Text.translatable("tooltip.monsterball.stored_custom_name", customName, Text.translatable(entityName).formatted(Formatting.AQUA)));
                } catch (Exception exception) {
                    monsterBallMod.LOGGER.warn("Failed to parse entity custom name {}", s, exception);
                    tooltip.add(Text.translatable("tooltip.monsterball.stored", Text.translatable(entityName).formatted(Formatting.AQUA)));
                }
            } else {
                tooltip.add(Text.translatable("tooltip.monsterball.stored", Text.translatable(entityName).formatted(Formatting.AQUA)));
            }
        } else {
            tooltip.add(Text.translatable("tooltip.monsterball.empty").formatted(Formatting.GRAY));
        }
    }
}
