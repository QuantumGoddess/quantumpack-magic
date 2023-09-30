package quantumgoddess.magic.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import quantumgoddess.magic.projectile.ProjectileTypes;

public class Items {

    public static final Item BasicFireWand = new WandItem(ProjectileTypes.FIRE, 1, 1, 1, 16, 1, new FabricItemSettings());
}
