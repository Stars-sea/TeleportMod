package com.github.star_sea.teleport.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ParametersAreNonnullByDefault
public class PosContainer implements INBTSerializable<CompoundNBT> {
    public String alia;
    public Pos pos;

    public PosContainer(String alia, Pos pos) {
        this.alia = alia;
        this.pos  = pos;
    }

    public PosContainer(PlayerEntity player) {
        this(String.format("%s[%s] recorded at %s", player.getDisplayName(), player.getUniqueID(),
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), new Pos(player));
    }

    public PosContainer(Pos pos) { this(pos.toSimpleString(), pos); }

    public PosContainer(CompoundNBT nbt) { deserializeNBT(nbt); }

    @Nonnull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("alia", alia);
        nbt.put("pos", pos.serializeNBT());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        alia = nbt.getString("alia");
        pos  = new Pos(nbt.getCompound("pos"));
    }

    @Override
    public String toString() {
        String simpleString = pos.toSimpleString();
        return alia.equals(simpleString) || alia.isEmpty() ? simpleString : String.format("%s [%s]", alia, simpleString);
    }

    public PosText getText() { return new PosText(this); }
}
