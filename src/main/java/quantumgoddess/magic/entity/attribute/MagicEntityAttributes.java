package quantumgoddess.magic.entity.attribute;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class MagicEntityAttributes {
    public static final EntityAttribute GENERIC_MAX_MANA = new ClampedEntityAttribute("attribute.name.generic.max_mana", 20.0, 1.0, 1024.0).setTracked(true);
   
    public static void registerAll() {
        Registry.register(Registries.ATTRIBUTE, new Identifier("quantumpack-magic", "generic.max_mana"), GENERIC_MAX_MANA);
    }
}

