package org.mob.resource_capsules.block.custom.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import org.mob.resource_capsules.block.ModBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ResourceGenStructure {

    public static class Part {
        public final BlockPos offset;
        public final Supplier<Block> expectedBlock;

        public Part(int x, int y, int z, Supplier<Block> expectedBlock) {
            this.offset = new BlockPos(x, y, z);
            this.expectedBlock = expectedBlock;
        }
    }

    public static final List<Part> PARTS = new ArrayList<>();

    static {
        Supplier<Block> casing = ModBlocks.MACHINE_CASING;
        // The Controller (0, 0, 0).
        // In the middle of the front wall!

        // =========================================
        // BOTTOM LAYER (Y = -1) The Floor
        // =========================================
        PARTS.add(new Part(-1, -1, 0, casing)); // Front Left
        PARTS.add(new Part( 0, -1, 0, casing)); // Front Center (Directly Under Controller)
        PARTS.add(new Part( 1, -1, 0, casing)); // Front Right

        PARTS.add(new Part(-1, -1, 1, casing)); // Middle Left
        PARTS.add(new Part( 0, -1, 1, casing)); // Middle Center
        PARTS.add(new Part( 1, -1, 1, casing)); // Middle Right

        PARTS.add(new Part(-1, -1, 2, casing)); // Back Left
        PARTS.add(new Part( 0, -1, 2, casing)); // Back Center
        PARTS.add(new Part( 1, -1, 2, casing)); // Back Right

        // =========================================
        // MIDDLE LAYER (Y = 0) The Walls
        // =========================================
        PARTS.add(new Part(-1,  0, 0, casing)); // Front Left
        // (0, 0, 0) is the Controller, so we skip it!
        PARTS.add(new Part( 1,  0, 0, casing)); // Front Right

        PARTS.add(new Part(-1,  0, 1, casing)); // Middle Left
        // (0, 0, 1) is the Hollow Air Center, so we skip it!
        PARTS.add(new Part( 1,  0, 1, casing)); // Middle Right

        PARTS.add(new Part(-1,  0, 2, casing)); // Back Left
        PARTS.add(new Part( 0,  0, 2, casing)); // Back Center
        PARTS.add(new Part( 1,  0, 2, casing)); // Back Right

        // =========================================
        // TOP LAYER (Y = 1) The Roof
        // =========================================
        PARTS.add(new Part(-1,  1, 0, casing)); // Front Left
        PARTS.add(new Part( 0,  1, 0, casing)); // Front Center (Directly Above Controller)
        PARTS.add(new Part( 1,  1, 0, casing)); // Front Right

        PARTS.add(new Part(-1,  1, 1, casing)); // Middle Left
        PARTS.add(new Part( 0,  1, 1, casing)); // Middle Center
        PARTS.add(new Part( 1,  1, 1, casing)); // Middle Right

        PARTS.add(new Part(-1,  1, 2, casing)); // Back Left
        PARTS.add(new Part( 0,  1, 2, casing)); // Back Center
        PARTS.add(new Part( 1,  1, 2, casing)); // Back Right
    }
}