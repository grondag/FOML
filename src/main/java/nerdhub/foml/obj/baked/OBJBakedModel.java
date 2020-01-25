package nerdhub.foml.obj.baked;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import nerdhub.foml.obj.OBJBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;

public class OBJBakedModel implements BakedModel, FabricBakedModel {

	private final Mesh mesh;
	private final ModelTransformation transformation;

	public OBJBakedModel(OBJBuilder builder, ModelTransformation transformation) {
		mesh = builder.build();
		this.transformation = transformation;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState blockState, Direction direction, Random random) {
		final List<BakedQuad>[] bakedQuads = ModelHelper.toQuadLists(mesh);
		return bakedQuads[direction == null ? 6 : direction.getId()];
	}

	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		if(mesh != null) {
			context.meshConsumer().accept(mesh);
		}
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
		if(mesh != null) {
			context.meshConsumer().accept(mesh);
		}
	}

	@Override
	public boolean useAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean hasDepth() {
		return true;
	}

	@Override
	public boolean isSideLit() {
		return true;
	}

	@Override
	public boolean isBuiltin() {
		return false;
	}

	@Override
	public Sprite getSprite() {
		return MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEX).apply(MissingSprite.getMissingSpriteId());
	}

	@Override
	public ModelTransformation getTransformation() {
		return transformation;
	}

	@Override
	public ModelItemPropertyOverrideList getItemPropertyOverrides() {
		return ModelItemPropertyOverrideList.EMPTY;
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}
}
