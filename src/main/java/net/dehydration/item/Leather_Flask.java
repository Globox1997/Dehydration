package net.dehydration.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import eu.midnightdust.puddles.Puddles;
import net.dehydration.access.ThristManagerAccess;
import net.dehydration.init.ConfigInit;
import net.dehydration.init.EffectInit;
import net.dehydration.init.SoundInit;
import net.dehydration.thirst.ThirstManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
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
import net.minecraft.util.Formatting;
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
  public int addition;

  public Leather_Flask(int waterAddition, Settings settings) {
    super(settings);
    this.addition = waterAddition;
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    ItemStack itemStack = user.getStackInHand(hand);
    CompoundTag tags = itemStack.getTag();
    HitResult hitResult = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
    BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();

    if (itemStack.hasTag() && tags.getInt("leather_flask") < 2 + addition && hitResult.getType() == HitResult.Type.BLOCK
        && world.canPlayerModifyAt(user, blockPos) && world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
      int fillLevel = 2 + addition;
      if (FabricLoader.getInstance().isModLoaded("puddles")
          && world.getBlockState(blockPos) == Puddles.Puddle.getDefaultState()) {
        if (!world.isClient) {
          world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
        }
        fillLevel = 1;
      }
      world.playSound(user, user.getX(), user.getY(), user.getZ(), SoundInit.FILL_FLASK_EVENT, SoundCategory.NEUTRAL,
          1.0F, 1.0F);
      tags.putBoolean("purified_water", false);
      tags.putInt("leather_flask", fillLevel);
      return TypedActionResult.consume(itemStack);
    }
    if (itemStack.hasTag() && tags.getInt("leather_flask") == 0) {
      return TypedActionResult.pass(itemStack);
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
            tags.putInt("leather_flask", 2 + addition);
            tags.putBoolean("purified_water", true);
            stack.setTag(tags);
          }
          tags.putInt("leather_flask", tags.getInt("leather_flask") - 1);
          ThirstManager thirstManager = ((ThristManagerAccess) playerEntity).getThirstManager(playerEntity);
          thirstManager.add(ConfigInit.CONFIG.flask_thirst_quench);

          if (!tags.getBoolean("purified_water") && world.random.nextFloat() >= 0.25F) {
            playerEntity.addStatusEffect(new StatusEffectInstance(EffectInit.THIRST, 300, 0, false, false, true));
          }
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
      tooltip.add(
          new TranslatableText("item.dehydration.leather_flask.tooltip", tags.getInt("leather_flask"), addition + 2)
              .formatted(Formatting.GRAY));
      if (tags.getInt("leather_flask") != 0) {
        String string = "Dirty";
        Formatting formatting = Formatting.DARK_AQUA;
        if (tags.getBoolean("purified_water")) {
          string = "Purified";
          formatting = Formatting.AQUA;
        }
        tooltip.add(new TranslatableText("item.dehydration.leather_flask.tooltip3", string).formatted(formatting));
      }
    } else
      tooltip.add(
          new TranslatableText("item.dehydration.leather_flask.tooltip2", addition + 2).formatted(Formatting.GRAY));
  }

}
