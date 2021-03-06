package vazkii.quark.building.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.LeashKnotModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.LightType;
import vazkii.quark.building.entity.LeashKnot2Entity;

public class LeashKnot2Renderer extends EntityRenderer<LeashKnot2Entity> {

	private static final ResourceLocation LEASH_KNOT_TEXTURES = new ResourceLocation("textures/entity/lead_knot.png");
	private final LeashKnotModel<LeashKnotEntity> leashKnotModel = new LeashKnotModel<>();

	public LeashKnot2Renderer(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public boolean shouldRender(LeashKnot2Entity livingEntityIn, ClippingHelper camera, double camX, double camY, double camZ) {
		if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ))
			return true;
		else if (livingEntityIn.getLeashed()) {
			Entity entity = livingEntityIn.getLeashHolder();
			return camera.isBoundingBoxInFrustum(entity.getRenderBoundingBox());
		} else return false;
	}

	@Override
	public void render(LeashKnot2Entity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);

		matrixStackIn.push();
		matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
//		this.leashKnotModel.setRotationAngles(entityIn, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.leashKnotModel.getRenderType(LEASH_KNOT_TEXTURES));
		
		matrixStackIn.translate(0, -1.0 / 8, 0);
		this.leashKnotModel.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		matrixStackIn.pop();

		Entity entity = entityIn.getLeashHolder();
		if (entity != null)
			this.renderLeash(entityIn, partialTicks, matrixStackIn, bufferIn, entity);
	}

	private <E extends Entity> void renderLeash(LeashKnot2Entity entityLivingIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, E leashHolder) {
		matrixStackIn.push();
		double d0 = (double)(MathHelper.lerp(partialTicks * 0.5F, leashHolder.rotationYaw, leashHolder.prevRotationYaw) * ((float)Math.PI / 180F));
		double d1 = (double)(MathHelper.lerp(partialTicks * 0.5F, leashHolder.rotationPitch, leashHolder.prevRotationPitch) * ((float)Math.PI / 180F));
		double d2 = Math.cos(d0);
		double d3 = Math.sin(d0);
		double d4 = Math.sin(d1);
		if (leashHolder instanceof HangingEntity) {
			d2 = 0.0D;
			d3 = 0.0D;
			d4 = -1.0D;
		}

		double d5 = Math.cos(d1);
		double d6 = MathHelper.lerp((double)partialTicks, leashHolder.prevPosX, leashHolder.getPosX()) - d2 * 0.7D - d3 * 0.5D * d5;
		double d7 = MathHelper.lerp((double)partialTicks, leashHolder.prevPosY + (double)leashHolder.getEyeHeight() * 0.7D, leashHolder.getPosY() + (double)leashHolder.getEyeHeight() * 0.7D) - d4 * 0.5D - 0.25D;
		double d8 = MathHelper.lerp((double)partialTicks, leashHolder.prevPosZ, leashHolder.getPosZ()) - d3 * 0.7D + d2 * 0.5D * d5;
		double d9 = (double)(MathHelper.lerp(partialTicks, entityLivingIn.renderYawOffset, entityLivingIn.prevRenderYawOffset) * ((float)Math.PI / 180F)) + (Math.PI / 2D);
		Vector3d vector3d = new Vector3d(0.0D, 0.32F, 0F);
		
		d2 = Math.cos(d9) * vector3d.z + Math.sin(d9) * vector3d.x;
		d3 = Math.sin(d9) * vector3d.z - Math.cos(d9) * vector3d.x;
		double d10 = MathHelper.lerp((double)partialTicks, entityLivingIn.prevPosX, entityLivingIn.getPosX()) + d2;
		double d11 = MathHelper.lerp((double)partialTicks, entityLivingIn.prevPosY, entityLivingIn.getPosY()) + vector3d.y;
		double d12 = MathHelper.lerp((double)partialTicks, entityLivingIn.prevPosZ, entityLivingIn.getPosZ()) + d3;
		matrixStackIn.translate(d2, vector3d.y, d3);
		float f = (float)(d6 - d10);
		float f1 = (float)(d7 - d11);
		float f2 = (float)(d8 - d12);
		IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getLeash());
		Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
		float f4 = MathHelper.fastInvSqrt(f * f + f2 * f2) * 0.025F / 2.0F;
		float f5 = f2 * f4;
		float f6 = f * f4;
		BlockPos blockpos = new BlockPos(entityLivingIn.getEyePosition(partialTicks));
		BlockPos blockpos1 = new BlockPos(leashHolder.getEyePosition(partialTicks));
		int i = this.getBlockLight(entityLivingIn, blockpos);
		int j = 0;
		int k = entityLivingIn.world.getLightFor(LightType.SKY, blockpos);
		int l = entityLivingIn.world.getLightFor(LightType.SKY, blockpos1);
		
		renderSide(ivertexbuilder, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.025F, f5, f6);
		renderSide(ivertexbuilder, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.0F, f5, f6);
		matrixStackIn.pop();
	}

	public static void renderSide(IVertexBuilder bufferIn, Matrix4f matrixIn, float p_229119_2_, float p_229119_3_, float p_229119_4_, int blockLight, int holderBlockLight, int skyLight, int holderSkyLight, float p_229119_9_, float p_229119_10_, float p_229119_11_, float p_229119_12_) {
		int i = 24;

		for(int j = 0; j < 24; ++j) {
			float f = (float)j / 23.0F;
			int k = (int)MathHelper.lerp(f, (float)blockLight, (float)holderBlockLight);
			int l = (int)MathHelper.lerp(f, (float)skyLight, (float)holderSkyLight);
			int i1 = LightTexture.packLight(k, l);
			addVertexPair(bufferIn, matrixIn, i1, p_229119_2_, p_229119_3_, p_229119_4_, p_229119_9_, p_229119_10_, 24, j, false, p_229119_11_, p_229119_12_);
			addVertexPair(bufferIn, matrixIn, i1, p_229119_2_, p_229119_3_, p_229119_4_, p_229119_9_, p_229119_10_, 24, j + 1, true, p_229119_11_, p_229119_12_);
		}

	}

	public static void addVertexPair(IVertexBuilder bufferIn, Matrix4f matrixIn, int packedLight, float p_229120_3_, float p_229120_4_, float p_229120_5_, float p_229120_6_, float p_229120_7_, int p_229120_8_, int p_229120_9_, boolean p_229120_10_, float p_229120_11_, float p_229120_12_) {
		float f = 0.5F;
		float f1 = 0.4F;
		float f2 = 0.3F;
		if (p_229120_9_ % 2 == 0) {
			f *= 0.7F;
			f1 *= 0.7F;
			f2 *= 0.7F;
		}

		float f3 = (float)p_229120_9_ / (float)p_229120_8_;
		float f4 = p_229120_3_ * f3;
		float f5 = p_229120_4_ > 0.0F ? p_229120_4_ * f3 * f3 : p_229120_4_ - p_229120_4_ * (1.0F - f3) * (1.0F - f3);
		float f6 = p_229120_5_ * f3;
		if (!p_229120_10_) {
			bufferIn.pos(matrixIn, f4 + p_229120_11_, f5 + p_229120_6_ - p_229120_7_, f6 - p_229120_12_).color(f, f1, f2, 1.0F).lightmap(packedLight).endVertex();
		}

		bufferIn.pos(matrixIn, f4 - p_229120_11_, f5 + p_229120_7_, f6 + p_229120_12_).color(f, f1, f2, 1.0F).lightmap(packedLight).endVertex();
		if (p_229120_10_) {
			bufferIn.pos(matrixIn, f4 + p_229120_11_, f5 + p_229120_6_ - p_229120_7_, f6 - p_229120_12_).color(f, f1, f2, 1.0F).lightmap(packedLight).endVertex();
		}

	}

	@Override
	public ResourceLocation getEntityTexture(LeashKnot2Entity entity) {
		return LEASH_KNOT_TEXTURES;
	}

}
