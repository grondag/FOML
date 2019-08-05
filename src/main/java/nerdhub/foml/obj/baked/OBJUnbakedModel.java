package nerdhub.foml.obj.baked;

import nerdhub.foml.obj.OBJBuilder;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Function;

public class OBJUnbakedModel implements UnbakedModel {

    private OBJBuilder builder;

    public OBJUnbakedModel(OBJBuilder builder) {
        this.builder = builder;
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        return Collections.emptySet();
    }

    @Override
    public Collection<Identifier> getTextureDependencies(Function<Identifier, UnbakedModel> var1, Set<String> var2) {
        List<Identifier> sprites = new ArrayList<>();
        builder.getMtlList().forEach(mtl -> sprites.add(new Identifier(mtl.getMapKd())));

        return sprites;
    }

    @Override
    public BakedModel bake(ModelLoader var1, Function<Identifier, Sprite> var2, ModelBakeSettings var3) {
        return new OBJBakedModel(builder);
    }
}