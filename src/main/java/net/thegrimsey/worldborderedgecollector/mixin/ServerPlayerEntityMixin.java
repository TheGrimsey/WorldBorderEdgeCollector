package net.thegrimsey.worldborderedgecollector.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
	@Inject(at = @At("TAIL"), method = "playerTick()V")
	private void playerTick(CallbackInfo info) {
		ServerPlayerEntity self = (ServerPlayerEntity)(Object)(this);

		WorldBorder border = self.world.getWorldBorder();
		if (!self.isSpectator() && !self.isCreative() && !border.contains(self.getBoundingBox())) {
			double d = border.getDistanceInsideBorder(self) + self.world.getWorldBorder().getSafeZone();
			if (d < 0.0) {
				double clampedX = MathHelper.clamp(self.getX(), border.getBoundWest() + 0.5D, border.getBoundEast() - 0.5D);
				double clampedZ = MathHelper.clamp(self.getZ(), border.getBoundNorth() + 0.5D, border.getBoundSouth() - 0.5D);
				double topY = self.world.getTopY(Heightmap.Type.WORLD_SURFACE, (int)clampedX, (int)clampedZ);

				self.teleport(clampedX, topY, clampedZ);
				self.sendMessage(Text.of("You've been moved back inside the worldborder."), false);
			}
		}
	}
}
