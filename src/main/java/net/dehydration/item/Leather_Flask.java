package net.dehydration.item;

import java.util.List;

import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.annotation.Nullable;
import net.dehydration.access.ThristManagerAccess;
import net.dehydration.init.ConfigInit;
import net.dehydration.init.SoundInit;
import net.dehydration.thirst.ThirstManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

// Thanks to Pois1x for the texture

public class Leather_Flask extends Item {
  private static int flaskThirstQuench = ConfigInit.CONFIG.flask_thirst_quench;

  public Leather_Flask(Settings settings) {
    super(settings);
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    ItemStack itemStack = user.getStackInHand(hand);
    CompoundTag tags = itemStack.getTag();
    if (itemStack.hasTag() && tags.getInt("leather_flask") == 0) {
      HitResult hitResult = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
      if (hitResult.getType() == HitResult.Type.MISS) {
        return TypedActionResult.pass(itemStack);
      } else {
        if (hitResult.getType() == HitResult.Type.BLOCK) {
          BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
          if (!world.canPlayerModifyAt(user, blockPos)) {
            return TypedActionResult.pass(itemStack);
          }
          if (world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
            world.playSound(user, user.getX(), user.getY(), user.getZ(), SoundInit.FILL_FLASK_EVENT,
                SoundCategory.NEUTRAL, 1.0F, 1.0F);
            tags.putInt("leather_flask", 2);
            return TypedActionResult.consume(itemStack);
          }
        }
        return TypedActionResult.pass(itemStack);
      }
    } else {
      return ItemUsage.consumeHeldItem(world, user, hand);
    }
  }

  @Override
  public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
    PlayerEntity playerEntity = user instanceof PlayerEntity ? (PlayerEntity) user : null;
    CompoundTag tags = stack.getTag();
    if (!stack.hasTag() || tags != null && tags.getInt("leather_flask") > 0) {
      if (playerEntity instanceof ServerPlayerEntity) {
        Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity) playerEntity, stack);
      }
      if (playerEntity != null) {
        playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!playerEntity.abilities.creativeMode) {
          if (!stack.hasTag()) {
            tags = new CompoundTag();
            tags.putInt("leather_flask", 2);
            stack.setTag(tags);
          }
          if (tags.getInt("leather_flask") == 1) {
            tags.putInt("leather_flask", 0);
          } else if (tags.getInt("leather_flask") == 2) {
            tags.putInt("leather_flask", 1);
          }
          ThirstManager thirstManager = ((ThristManagerAccess) playerEntity).getThirstManager(playerEntity);
          thirstManager.add(flaskThirstQuench);
        }
      }
    }
    return stack;
  }

  @Override
  public int getMaxUseTime(ItemStack stack) {
    return 32;
  }

  @Override
  public UseAction getUseAction(ItemStack stack) {
    CompoundTag tags = stack.getTag();
    if (!stack.hasTag() || (tags != null && tags.getInt("leather_flask") > 0)) {
      return UseAction.DRINK;
    } else
      return UseAction.NONE;
  }

  @Override
  public void onCraft(ItemStack stack, World world, PlayerEntity player) {
    if (!stack.hasTag()) {
      CompoundTag tags = new CompoundTag();
      tags.putInt("leather_flask", 0);
      stack.setTag(tags);
    }
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    CompoundTag tags = stack.getTag();
    if (tags != null) {
      if (tags.getInt("leather_flask") == 2) {
        tooltip.add(new TranslatableText("item.dehydration.leather_flask.tooltip"));
      }
      if (tags.getInt("leather_flask") == 1) {
        tooltip.add(new TranslatableText("item.dehydration.leather_flask_half_full.tooltip"));
      }

    }
  }

}
