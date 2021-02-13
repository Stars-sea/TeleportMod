package com.github.star_sea.teleport.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PosText extends StringTextComponent {
    public static final IFormattableTextComponent BackCommandText = new StringTextComponent("/back")
            .modifyStyle(style -> style.setFormatting(TextFormatting.GREEN)
                    .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/back"))
                    .setHoverEvent(PosText.ClickToTPEvent));

    public static final IFormattableTextComponent BackErrorText =
            new TranslationTextComponent("msg.teleport.back_err").mergeStyle(TextFormatting.RED);

    protected static final HoverEvent ClickToTPEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
            new TranslationTextComponent("msg.teleport.click_to_tp"));

    public PosText(@Nullable PosContainer container) {
        super(container != null ? container.toString() : "");
        mergeStyle(TextFormatting.GREEN);
        if (container != null)
            modifyStyle(style -> style.setHoverEvent(ClickToTPEvent)
                    .setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, container.pos.getTpCommand())));
    }

    public IFormattableTextComponent backTip() {
        return new TranslationTextComponent("msg.teleport.back_tip", PosText.BackCommandText, this);
    }

    public IFormattableTextComponent tpSuccess() {
        return new TranslationTextComponent("msg.teleport.tp_succeed", this);
    }

    public IFormattableTextComponent tpFail() {
        return new TranslationTextComponent("msg.teleport.tp_failed", this).mergeStyle(TextFormatting.RED);
    }

    public IFormattableTextComponent saveSucceed(ItemStack stack) {
        return new TranslationTextComponent("msg.teleport.save_succeed", this, stack.getTextComponent());
    }

    public IFormattableTextComponent saveFailed(ItemStack stack) {
        return new TranslationTextComponent("msg.teleport.save_failed", this, stack.getTextComponent());
    }
}
