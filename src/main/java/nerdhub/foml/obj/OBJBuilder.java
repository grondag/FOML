package nerdhub.foml.obj;

import java.util.List;
import java.util.Map;

import de.javagl.obj.FloatTuple;
import de.javagl.obj.Mtl;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjSplitting;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;

public class OBJBuilder {
	private final MeshBuilder meshBuilder;
	private final QuadEmitter quadEmitter;

	private final Obj obj;
	private final List<Mtl> mtlList;

	public OBJBuilder(Obj obj, List<Mtl> mtlList) {
		meshBuilder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
		quadEmitter = meshBuilder.getEmitter();
		this.obj = obj;
		this.mtlList = mtlList;
	}

	private void addVertex (int faceIndex, int vertIndex, FloatTuple vertex, FloatTuple normal, QuadEmitter emitter,
			Sprite mtlSprite, Obj matGroup, boolean degenerate) {
		int textureCoordIndex = vertIndex;
		if (degenerate) {
			textureCoordIndex --;
		}

		quadEmitter.pos   (vertIndex, vertex.getX(), vertex.getY(), vertex.getZ());
		quadEmitter.normal(vertIndex, normal.getX(), normal.getY(), normal.getZ());

		if(obj.getNumTexCoords() > 0) {
			final FloatTuple text = matGroup.getTexCoord(matGroup.getFace(faceIndex).getTexCoordIndex(textureCoordIndex));

			quadEmitter.sprite(vertIndex, 0, text.getX(), text.getY());
		}else {
			quadEmitter.nominalFace(Direction.getFacing(normal.getX(), normal.getY(), normal.getZ()));
		}
	}

	public Mesh build() {
		final Map<String, Obj> materialGroups = ObjSplitting.splitByMaterialGroups(obj);

		for (final Map.Entry<String, Obj> entry : materialGroups.entrySet()) {
			final String matName = entry.getKey();
			final Obj matGroupObj = entry.getValue();


			final Mtl mtl = findMtlForName(matName);
			FloatTuple diffuseColor = null;
			FloatTuple specularColor = null;
			Sprite mtlSprite = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEX).apply(MissingSprite.getMissingSpriteId());

			if(mtl != null) {
				diffuseColor = mtl.getKd();
				specularColor = mtl.getKs();
				mtlSprite = getMtlSprite(mtl.getMapKd());
			}

			for (int i = 0; i < matGroupObj.getNumFaces(); i++) {
				FloatTuple vertex = null;
				FloatTuple normal = null;
				int v;
				for (v = 0; v < matGroupObj.getFace(i).getNumVertices(); v++) {
					vertex = matGroupObj.getVertex(matGroupObj.getFace(i).getVertexIndex(v));
					normal = matGroupObj.getNormal(matGroupObj.getFace(i).getNormalIndex(v));

					addVertex (i, v, vertex, normal, quadEmitter, mtlSprite, matGroupObj, false);
				}

				// Special conversion of triangles to quads: re-add third vertex as the fourth vertex
				if (matGroupObj.getFace(i).getNumVertices() == 3) {
					addVertex (i, 3, vertex, normal, quadEmitter, mtlSprite, matGroupObj, true);
				}

				quadEmitter.spriteColor(0, -1, -1, -1, -1);
				quadEmitter.material(RendererAccess.INSTANCE.getRenderer().materialFinder().find());
				quadEmitter.colorIndex(1);
				quadEmitter.spriteBake(0, mtlSprite, MutableQuadView.BAKE_NORMALIZED);

				quadEmitter.emit();
			}
		}

		return meshBuilder.build();
	}

	public List<Mtl> getMtlList() {
		return mtlList;
	}

	public Mtl findMtlForName(String name) {
		for (final Mtl mtl : mtlList) {
			if(mtl.getName().equals(name)) {
				return mtl;
			}
		}

		return null;
	}

	public Sprite getMtlSprite(String name) {
		return MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEX).apply(new Identifier(name));
	}
}
