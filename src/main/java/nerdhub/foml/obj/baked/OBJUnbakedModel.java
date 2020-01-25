package nerdhub.foml.obj.baked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.mojang.datafixers.util.Pair;
import nerdhub.foml.obj.OBJBuilder;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

public class OBJUnbakedModel implements UnbakedModel {
	protected OBJBuilder builder;
	protected ModelTransformation transform;

	public OBJUnbakedModel(OBJBuilder builder, ModelTransformation transform) {
		if (transform == null) {
			transform = ModelTransformation.NONE;
		}

		this.builder = builder;
		this.transform = transform;
	}

	@Override
	public Collection<Identifier> getModelDependencies() {
		return Collections.emptySet();
	}

	@Override
	public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> function, Set<Pair<String, String>> set) {
		final List<SpriteIdentifier> sprites = new ArrayList<>();
		builder.getMtlList().forEach(mtl -> sprites.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier(mtl.getMapKd()))));

		return sprites;
	}

	@Override
	public BakedModel bake(ModelLoader modelLoader, Function<SpriteIdentifier, Sprite> function, ModelBakeSettings modelBakeSettings, Identifier identifier) {
		return new OBJBakedModel(builder, transform);
	}
}